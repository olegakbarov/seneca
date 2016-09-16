
(ns nexus.templates.editor.messages
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [reagent.core :as r]
    [cljs.core.async :refer [<! put! chan timeout]]
    [nexus.helpers.core :refer [log]]
    [nexus.templates.editor.dnd :refer [on-drag-start
                                        on-drag-over
                                        ; on-drop
                                        on-drag-leave
                                        on-drag-enter
                                        dnd-store]]
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
        {:on-drag-enter on-drag-enter
         :on-drag-leave on-drag-leave}
        ; [add-msg]
        (doall
          (map-indexed
            (fn [ix, item]
              (let [{:keys [title]} item]
                 ^{:key ix}
                 [:div.list_message
                  {:draggable true
                   :class (if (= ix (:drag-index @dnd-store)) "msg_dragged" "")
                   :droppable true
                   :data-index ix
                   :data-dragtype "msg"}
                  title])) @msgs))])))

; (defn draggable []
;   (reagent/create-class {:reagent-render draggable-render
;                          :component-did-mount draggable-did-mount}))
