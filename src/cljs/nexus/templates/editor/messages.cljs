
(ns nexus.templates.editor.messages
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [reagent.core :as r]
    [cljs.core.async :refer [<! put! chan timeout]]
    [nexus.helpers.core :refer [log]]
    [nexus.templates.editor.dnd :refer [on-event
                                        state]]
    [nexus.templates.editor.add_msg :refer [add-msg]]
    [re-frame.core :refer [reg-event-db
                           path
                           reg-sub
                           dispatch
                           dispatch-sync
                           subscribe]]))

(def dnd-types ["text-message"
                "button-template"
                "quick-reply"
                "generic-template"
                "media"])

(defn render-text [text & last]
  ^{:key text}
  [:div.lister_msg_item_text
    {:class (if (not (nil? last)) "last_txt" "")}
    ; {:draggable false
    ;  :on-drag-start #(.preventDefault %)} ;; TODO think about it!
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
        {:draggable true
         :class (if (= ix (:dix @state)) "msg_dragged" "")
         :on-drag-enter on-event
        ;  :on-drag-leave on-event
         :on-drag-over  on-event
         :data-index ix
         :data-type type}
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

;; LISTER

(defn lister []
  (fn []
    (let [msgs (subscribe [:msgs 123 1])]
      [:div#msg_wrapper
        [:ul.list_messages
          (doall
            (map-indexed
              (fn [ix item]
                ^{:key ix}
                [render-msg ix item])
              @msgs))]])))







;;
