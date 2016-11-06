
(ns nexus.templates.editor.messages
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:import [goog.events EventType])
  (:require
    [reagent.core :as r]
    [re-frame.core :refer [reg-event-db path reg-sub subscribe dispatch]]
    [cljs.core.async :refer [<! put! chan timeout]]
    [goog.events :as events]
    [nexus.helpers.uids :refer [gen-uid]]
    [nexus.dbutils :refer [add-thread-info
                           shallow-deps
                           make-deps-tree
                           keywordize-ids
                           build-tree]]
    [nexus.templates.editor.dnd :refer [state on-drag-start on-drag-over on-drag-end]]
    [nexus.templates.editor.add_msg :refer [add-msg]]))

(def reveal (r/atom nil))

(def dnd-types ["text-message"
                "button-template"
                "quick-reply"
                "generic-template"
                "media"])

(defn render-text [text & last]
  ^{:key text}
  [:div.lister_msg_item_text
    {:class (if (not (nil? last)) "last_txt" "")}
    text])

(defn render-buttons [btns]
  [:ul
    (doall
      (map-indexed
        (fn [ix item]
          ^{:key ix}
          [:li.lister_msg_item_btns
            (:text item)])
        btns))])

(defn render-qr [msg thread]
  (let [expanded (subscribe [:ui/expanded-msgs])
        btns (:buttons msg)]
    [:div.lister_msg_item_wrap
      (doall
        (map-indexed
          (fn [ix item]
            (let [classes (str
                            (if (:payload item) "" "qr_error")
                            (if (contains? @expanded (:payload item)) " selected" ""))]
              ^{:key ix}
              [:div.lister_msg_item_qr
                {:class classes
                 :on-click #(do
                              (js/console.log "Toggling :payload with id " (:payload item))
                              (dispatch [:ui/toggle-expanded-id (:payload item)]))}
                (:text item)]))
          btns))]))

;; ------------------------------------
;; EDITABALES
;; ------------------------------------

(defn render-editable-text [uid]
  (let [text (subscribe [:ui/is-editing-msg-text])]
    (r/create-class
       {:component-did-mount
         (fn [this]
           (let [node (reagent.dom/dom-node this)]
             (-> node .focus)))
        :render
          (fn []
            [:textarea.lister_msg_item_text
              {:value @text
               :on-change #(dispatch [:edit-msg-text uid (-> % .-target .-value)])
               :on-click #(.stopPropagation %)}])})))

;; ------------------------------------
;; RENDER MSG BY TYPE
;; ------------------------------------

(defmulti render-msg
  (fn [ix item is-editing]
    (:type item)))

(defmethod render-msg :default [ix item is-editing] "KEK")

(defmethod render-msg "text-message" [ix item is-editing]
  (let [{:keys [text uid]} item]
    ^{:key ix}
    [:div.message_content
      (if is-editing
        ^{:key ix} [render-editable-text uid]
        ^{:key ix} [render-text text])]))

(defmethod render-msg "quick-reply" [ix item is-editing]
  (let [{:keys [text thread]} item
        btns (:buttons item)]
    ^{:key ix}
    [:div.message_content
      ^{:key text}
      [render-text text]
      ^{:key btns}
      [render-qr (merge item {:uid ix}) thread]]))

(defmethod render-msg "button-template" [ix item is-editing]
  (let [text (:text item)
        btns (:buttons item)]
    ^{:key ix}
    [:div.message_content
      ^{:key text}
      [render-text text "last"]
      ^{:key btns}
      [render-buttons btns]]))

(defn empty-day []
  [:div.lister_msg_empty
    ;; TODO remove
    {:on-click #(prn (gen-uid "msg"))}
    (str "Drag & drop here one of elements"
         " from the right panel")])

;; ------------------------------------
;; ON-HOVER BUSINESS
;; ------------------------------------

(def mouse-chan (chan))

(defn on-hover [e]
  ;; currentTarget 'cause dealing with synthetic event
  (let [x (-> e .-currentTarget .-dataset .-uid)]
    (reset! reveal x)))

(defn on-unhover [e]
  (reset! reveal nil))

(defn drag-hook [ix is-editing]
  (let [visible (or is-editing (= @reveal ix))]
    [:div.msg_drag-hook
      {:class (if visible "" "hidden")}
      "🖐🏻"]))

(defn msg-tools [ix msg is-editing]
  [:div.lister_msg_tools {:on-drag-start #(.stopPropagation %)
                          :class (if (= @reveal ix) "" "hidden")}
    [:div.edit
      {:on-click #(dispatch [:set-is-editing-id ix])}
      "✍🏼"]
    [:div.copy   "📑"]
      ;; TODO on-click
    [:div.remove "💥"]])
      ;; TODO on-click

;; ------------------------------------
;; WRAPPER over MSGS
;; ------------------------------------

(defn render-msg-container [msg]
  (fn []
    (let [{:keys [type uid order thread]} msg
          is-editing-id (subscribe [:ui/is-editing-id])
          active-thread-id (subscribe [:ui/curr-thread])
          is-editing (= (:uid msg) @is-editing-id) ;; if current msg editable
          overlayed? (if (nil? @active-thread-id)
                         false
                         (not= @active-thread-id (:thread msg)))
          dragged? (when (= uid (:dix @state)) "msg_dragged")
          classes (str dragged?)]
        [:li.list_message_container
          (when overlayed?
            [:div.msg_overlay
              {:on-mouse-enter #(.stopPropagation %)
               :on-click #(dispatch [:ui/set-active-thread thread])}])
          [:div.msg_inner_container
            (if-not is-editing
              {:draggable true
               :data-dragtype "MSG_TYPE"
               :style {:cursor "move"}
               :class classes
               :on-drag-start  on-drag-start
               :on-drag-over   on-drag-over
               :on-drag-end    on-drag-end
               :on-mouse-enter on-hover
               :on-mouse-leave on-unhover
               :data-uid uid
               :data-dragindex order
               :data-type type})
            [drag-hook uid is-editing]
            [render-msg uid msg is-editing]
            [msg-tools uid msg is-editing]]])))


(defn lister []
  (fn []
    (let [msgs (subscribe [:curr-msgs])
          dropzone (> (count @msgs) 1)
          m (keywordize-ids @msgs)
          tree (map
                 (fn [item]
                   (build-tree item m))
                 m)
          msg-state (atom {:hidden (shallow-deps m)
                           :deps (make-deps-tree tree)})
          hidden (:hidden @msg-state)
          processed (reduce
                     (fn [acc [key val]]
                       (if (contains? hidden key)
                         acc
                         (assoc acc key val))) {} m)]
      (if (= 0 (count @msgs))
        [empty-day]
        [:div#msg_wrapper
          [:ul.list_messages
            (doall
              (for [[key item] processed]
                ^{:key key} [render-msg-container item]))]
         (if dropzone
          [:div.msg_wrapper_dropzone])]))))
