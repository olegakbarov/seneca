
(ns nexus.templates.editor.core
  (:require-macros [reagent.ratom  :refer [reaction]])
  (:require
    [reagent.core :as r]
    [nexus.templates.editor.days_row :refer [days-row]]
    [nexus.templates.editor.messages :refer [lister]]
    [nexus.templates.editor.tools :refer [tools-list]]
    [nexus.templates.header :refer [header]]
    [re-frame.core :refer [dispatch
                           subscribe]]))

;; ---------------------------------
;; Editor


(defn editor-templ []
 (fn []
   (let [state (subscribe [:ui/msgs-state])
         courses (subscribe [:courses])
         course (subscribe [:curr-course])
         day (subscribe [:curr-day])
         msgs (get-in @courses [@course :days @day :messages])]

     (js/console.log msgs)

     (when (> (count msgs) 0)
           (dispatch [:ui/create-msgs-state]))
     [:div
       [header]
       [:div.content]
       [:div.editor
         [days-row]
         [:div.editor_wrapper
           [:div#editor_messenger_wrapper ;; TODO: need for proper on-scroll
            [:div.editor_messenger
             (if (> (count msgs) 0)
               [lister msgs @state])]]
           [tools-list]]]])))

(defn editor []
   (r/create-class
    {:component-did-mount
        #(dispatch [:courses-fetch])
     :display-name  "courses-container"
     :render (fn []
                [editor-templ])}))
