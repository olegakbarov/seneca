
(ns nexus.templates.editor.messages
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:import [goog.events EventType])
  (:require
    [reagent.core :as r]
    [npm-packages]
    [re-frame.core :refer [reg-event-db path reg-sub subscribe dispatch]]
    [cljs.core.async :refer [<! put! chan timeout]]
    [goog.events :as events]
    [nexus.helpers.uids :refer [gen-uid]]
    [nexus.templates.editor.dnd :refer [state
                                        on-drag-start
                                        on-drag-over
                                        on-drag-end
                                        on-drag-enter]]))

(def reveal (r/atom nil))

(def dnd-types ["text-message"
                "button-template"
                "quick-reply"
                "generic-template"
                "media"])

(defn render-text [uid & last]
  (let [text (subscribe [:ui/text uid])]
    ^{:key @text}
    [:div.lister_msg_item_text
      {:class (if (not (nil? last)) "last_txt" "")}
      @text]))

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
  (fn []
    (let [btns (:payload msg)]
      [:div.lister_msg_item_wrap
        (doall
          (map-indexed
            (fn [ix item]
              (let [hidden (subscribe [:ui/hidden-msgs])
                    active (subscribe [:ui/active-msgs])
                    id (:uid msg)
                    next (-> item :next)
                    selected (contains? @active next)
                    classes (str
                              (if next "" "qr_error")
                              (if selected " selected" ""))]
                ^{:key ix}
                [:div.lister_msg_item_qr
                  {:class classes
                   :on-click #(dispatch [:ui/toggle-expanded-id [id next]])}
                  (:text item)]))
            btns))])))

;; ------------------------------------
;; EDITABALES
;; ------------------------------------

(defn render-editable-text [uid]
  (fn []
    (let [text (subscribe [:ui/text uid])]
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
                 :on-click #(.stopPropagation %)}])}))))

;; ------------------------------------
;; RENDER MSG BY TYPE
;; ------------------------------------

(defn render-msg [ix item]
  (fn []
    (let [is-editing-id (subscribe [:ui/is-editing-id])
          is-editing (= ix @is-editing-id)]
      (condp = (:type item)
        "text-message"
          (fn [ix item is-editing]
            (let [{:keys [text uid]} item]
              ^{:key ix}
              [:div.message_content
                (if is-editing
                  ^{:key ix} [render-editable-text uid]
                  ^{:key ix} [render-text uid])]))

        "quick-reply"
          (fn [ix item is-editing]
            (let [{:keys [text thread]} item
                  btns (:payload item)]
              ^{:key ix}
              [:div.message_content
                ^{:key text}
                [render-text ix]
                ^{:key btns}
                [render-qr (merge item {:uid ix})] thread]))

        "button-template"
          (fn [ix item is-editing]
            (let [text (:text item)
                  btns (:payload item)]
              ^{:key ix}
              [:div.message_content
                ^{:key text}
                [render-text text "last"]
                ^{:key btns}
                [render-buttons btns]]))))))

(defn empty-day []
  [:div.lister_msg_empty
    {:on-drag-enter #(do
                       (dispatch [:add-msg (@state :adding-type) 0]) ;; adding msg with index 0
                       (reset! state (merge @state {:msg-added true})))
     :on-drag-end on-drag-end}
    "Drag & drop here one of elements from the right panel"])

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
  (let [visible (= @reveal ix)]
    [:div.msg_drag-hook
      {:class (if visible "" "hidden")}
      "🖐🏻"]))

(defn msg-tools [ix msg is-editing]
  [:div.lister_msg_tools {:on-drag-start #(.stopPropagation %)
                          :class (if (= @reveal ix) "" "hidden")}
    [:div.edit
      {:on-click #(dispatch [:set-is-editing-id ix])}
      "✍🏼"]
    [:div.copy "📑"]
      ; {:on-click #(dispatch [:clone-msg ix])}]])
    [:div.remove "💥"]])
      ; {:on-click #(dispatch [:remove-msg ix])}]])

;; ------------------------------------
;; WRAPPER over MSGS
;; ------------------------------------

(defn render-msg-container [msg order]
  (fn []
    (let [{:keys [type uid]} msg
          is-editing-id (subscribe [:ui/is-editing-id])
          active-thread-id (subscribe [:ui/curr-thread])
          is-editing (= (:uid msg) @is-editing-id) ;; if current msg editable
          ; overlayed? (if (nil? @active-thread-id)
          ;                false
          ;                (not= @active-thread-id (:thread msg)))
          dragged? (when (= uid (:dix @state)) "msg_dragged")
          classes (str dragged?)]
        [:li.list_message_container
          ; (when overlayed?
          ;   [:div.msg_overlay
          ;     {:on-mouse-enter #(.stopPropagation %)
          ;      :on-click #(dispatch [:ui/set-active-thread thread])}])
          [:div.msg_inner_container
            (if-not is-editing
              {:draggable true
               :data-dragtype "MSG_TYPE"
               :style {:cursor "move"}
               :class classes
               :on-drag-start  on-drag-start
               :on-drag-over   on-drag-over
               :on-drag-end    on-drag-end
               :on-drag-enter  on-drag-enter
               :on-mouse-enter on-hover
               :on-mouse-leave on-unhover
               :data-uid uid
               :data-dragindex order
               :data-type type})
            [drag-hook uid is-editing]
            [render-msg uid msg is-editing]
            [msg-tools uid msg is-editing]]])))

; (def textarea-autosize
;   (r/adapt-react-class
;     (aget js/npm "react-textarea-autosize")))

(defn list-component [items]
  (let [dropzone (> 0 (count items))]
    [:div#msg_wrapper
      [:ul.list_messages;
        (doall
          (map-indexed
            (fn [index item]
              ^{:key (:uid item)} [render-msg-container item index])
            items))]
      (if dropzone
        [:div.msg_wrapper_dropzone])]))

(defn lister []
  (r/create-class
     {:component-will-mount
       (fn []
         (dispatch [:ui/create-msgs-state]))
      :render
        (fn []
          (let [msgs (subscribe [:curr-msgs])
                state (subscribe [:ui/msgs-state])
                processed (reduce
                            (fn [acc item]
                             (if-not (contains? (:hidden @state) (:uid item))
                               (conj acc item)
                               acc))
                            []
                            @msgs)]
            (if (= 0 (count processed))
                [empty-day]
                [list-component processed])))}))
