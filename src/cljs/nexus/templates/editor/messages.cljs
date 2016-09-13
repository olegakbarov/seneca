
(ns nexus.templates.editor.messages
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [reagent.core :as r]
    [cljs.core.async :refer [<! put! chan timeout]]
    [nexus.helpers.core :refer [log]]
    [nexus.templates.editor.dnd :refer [on-drag-start drag-id]]
    [nexus.templates.editor.add_msg :refer [add-msg]]
    [re-frame.core :refer [reg-event-db
                           path
                           reg-sub
                           dispatch
                           dispatch-sync
                           subscribe]]))

(defn lister []
  (fn []
    (let [msgs (subscribe [:msgs 123 1])]
          ; current-course (subscribe :course)
          ; curr-day (subscribe (current-course))
      [:div#msg_wrapper.list_messages
        [add-msg]
        (doall
          (for [sorted (sort-by :order @msgs)
                :let [{:keys [title order]} sorted]]
             ^{:key order}
             [:div.list_message
              {:draggable true
               :class (if (= order @drag-id) "msg_dragged" "")
               :data-order order}
              ;  :on-drag-start on-drag-start}
              ;  :on-drag-end on-drag-end}
              ;  :on-mouse-down mouse-down-handler}
              title]))])))
