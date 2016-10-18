
(ns nexus.templates.editor.messages
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:import [goog.events EventType])
  (:require
    [reagent.core :as r]
    [cljs.core.async :refer [<! put! chan timeout]]
    [goog.events :as events]
    ; [nexus.helpers.core :refer [log]]
    [nexus.helpers.uids :refer [gen-uid]]
    [nexus.templates.editor.dnd :refer [state
                                        on-drag-start
                                        ; on-drag
                                        ; on-drag-enter
                                        on-drag-over
                                        on-drag-end]]

    [nexus.templates.editor.add_msg :refer [add-msg]]
    [re-frame.core :refer [reg-event-db
                           path
                           reg-sub
                           subscribe]]))


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

(defn render-qr [btns]
  [:div.lister_msg_item_wrap
    (doall
      (map-indexed
        (fn [ix item]
          ^{:key ix}
          [:div.lister_msg_item_qr
            (:text item)])
        btns))])

(defn render-item
  "Wraps each msg in draggable container"
  [ix msg & items]
  (let [{:keys [type]} msg]
    [:li.list_message
      items]))

;; M&Ms

(defmulti render-msg
  (fn [ix item]
    (:type item)))

(defmethod render-msg :default [ix item] "KEK")

(defmethod render-msg "text-message" [ix item]
  (let [text (:text item)]
    ^{:key ix}
    [render-item ix item
       ^{:key ix}
      [render-text text]]))

(defmethod render-msg "quick-reply" [ix item]
  (let [text (:text item)
        btns (:buttons item)]
    ^{:key ix}
    [render-item ix item
      ^{:key text}
      [render-text text]
      ^{:key btns}
      [render-qr btns]]))

(defmethod render-msg "button-template" [ix item]
  (let [text (:text item)
        btns (:buttons item)]
    ^{:key ix}
    [render-item ix item
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

(defn msg-tools [ix msg]
  [:div.lister_msg_tools {:on-drag-start #(.stopPropagation %)
                          :class (if (= @reveal ix) "" "hidden")}
    [:div.edit   "✍🏼"]
      ;; TODO on-click
    [:div.copy   "📑"]
      ;; TODO on-click
    [:div.remove "💥"]])
      ;; TODO on-click

(def mouse-chan (chan))

(defn on-hover [e]
  ;; currentTarget 'cause dealing with synthetic event
  (let [x (-> e .-currentTarget .-dataset .-uid)]
    (reset! reveal x)))

(defn on-unhover [e]
  (reset! reveal nil))

(defn drag-hook [ix]
  [:div.msg_drag-hook
    {:class (if (= @reveal ix) "" "hidden")}
    "🖐🏻"])

(defn render-msg-container [msg]
  (let [{:keys [type uid order]} msg]
    (fn []
      [:div.lister_msg_container
        {:draggable true
         :data-dragtype "MSG_TYPE"
         :class (if (= uid (:dix @state)) "msg_dragged" "")
         :on-drag-start  on-drag-start
         :on-drag-over   on-drag-over
        ;  :on-drag-enter  on-drag-enter
        ;  :on-drag        on-drag
         :on-drag-end    on-drag-end
         ;;
         :on-mouse-enter on-hover
         :on-mouse-leave on-unhover
         ;;
         :data-uid uid
         :data-dragindex order
         :data-type type}
        [drag-hook uid]
        [render-msg uid msg]
        [msg-tools uid msg]])))

(defn lister []
  (fn []
    (let [msgs (subscribe [:curr-msgs])]
      (if (= 0 (count @msgs))
        [empty-day]
        [:div#msg_wrapper
          [:ul.list_messages
            (doall
              (for [item (sort-by :order @msgs)]
                  ^{:key (:uid item)}
                  [render-msg-container item]))]]))))
