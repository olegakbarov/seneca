
(ns nexus.templates.editor.messages
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [reagent.core :as r]
    [cljs.core.async :refer [<! put! chan timeout]]
    [nexus.helpers.core :refer [log]]
    [nexus.templates.editor.dnd :refer [on-drag-start drag-id]]
    [re-frame.core :refer [reg-event-db
                           path
                           reg-sub
                           dispatch
                           dispatch-sync
                           subscribe]]))

(def is_editable (r/atom false))
(def title (r/atom ""))

(defn toggle-edit []
  ; (log @is_editable)
  (reset! is_editable (not @is_editable)))

(defn save-msg []
  (toggle-edit)
  (dispatch [:add_msg @title]))


(defn add-msg []
  (fn []
    (let [title @title])
    (if @is_editable
      [:div.msgs_add_msg
        [:input {:type "text"
                 :value @title
                 :on-change #(reset! title (-> % .-target .-value))}]
        [:input {:type "button"
                 :value "submit"
                 :on-click save-msg}]]
      [:div.msgs_add_msg
        [:input {:type "button"
                 :value "+"
                 :on-click toggle-edit}]])))


(defn lister []
  (fn []
    (let [msgs (subscribe [:msgs 123 1])]
          ; current-course (subscribe :course)
          ; curr-day (subscribe (current-course))
      [:div#msg_wrapper.list_messages
        [add-msg]
        (doall
          (for [o @msgs]
            (let [{:keys [id title]} o]
               ^{:key id}
               [:div.list_message
                {:draggable true
                 :class (if (= id @drag-id) "msg_dragged" "")
                 :data-id id
                 :on-drag-start on-drag-start}
                ;  :on-drag-end on-drag-end}
                ;  :on-mouse-down mouse-down-handler}
                title])))])))
