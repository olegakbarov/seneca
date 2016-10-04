
(ns nexus.templates.editor.core
  (:require
    [reagent.core :as r]
    [nexus.helpers.core :refer [log]]
    [nexus.templates.editor.days_row :refer [days-row]]
    [nexus.templates.editor.messages :refer [lister]]
    [nexus.templates.editor.tools :refer [tools-list]]))

;; ---------------------------------
;; Editor

(defn editor []
  (fn []
    [:div.editor
      [days-row]
      [:div.editor_wrapper
        [:div#editor_messenger_wrapper ;; need for proper on-scroll
          [:div.editor_messenger
            [lister]]]
        [tools-list]]]))
