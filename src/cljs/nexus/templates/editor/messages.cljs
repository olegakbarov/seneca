
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

(defn lister []
  (fn []
    (let [msgs (subscribe [:msgs 123 1])]
      [:div#msg_wrapper.list_messages
        ; {:on-drag-enter on-event
        ;  :on-drag-leave on-event}
        ; [add-msg]
        (doall
          (map-indexed
            (fn [ix, item]
              (let [{:keys [title]} item]
                 ^{:key ix}
                 [:div.list_message
                  {:draggable true
                   :class (if (= ix (:dix @state)) "msg_dragged" "")
                   :droppable true
                   :data-index ix
                   :data-dragtype "msg"}
                  title])) @msgs))])))
