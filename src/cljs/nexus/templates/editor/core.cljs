
(ns nexus.templates.editor.core
  (:require
    [reagent.core :as r]
    [nexus.helpers.core :refer [log]]
    [nexus.templates.editor.days_row :refer [days-row]]
    [nexus.templates.editor.messages :refer [lister]]
    [nexus.templates.editor.tools :refer [tools-list]]))

;; ---------------------------------
;; Init

(defn add-to-list [x]
  (swap! s-days assoc-in x {:id x :order x :text (str "kek-" x)}))

(defn show-state []
  [:input {:type "submit"
           :value "state"
           :on-click #(.log js/console (clj->js @state))}])

;; ---------------------------------
;; Editor

(defn editor []
  (fn []
    [:div.editor
      [days-row]
      [:div.editor_wrapper
        [:div.editor_messenger_wrapper
          ; [show-state]
          [:div.editor_messenger
            [lister]]]
        [tools-list]]]))
