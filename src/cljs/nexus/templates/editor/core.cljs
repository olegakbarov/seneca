
(ns nexus.templates.editor.core
  (:require
    [reagent.core :as r]
    [nexus.templates.editor.days_row :refer [days-row]]
    [nexus.helpers.core :refer [log]]
    [nexus.templates.tools :refer [tools]]))

;; ---------------------------------
;; State

(def state (r/atom {:auth {}
                    ; :bots {:courses {:days {}}}
                    :days {}}))

(def s-days (r/cursor state [:days]))

;; ---------------------------------
;; Init

(defn add-to-list [x]
  (swap! s-days assoc-in x {:id x :order x :text (str "kek-" x)}))

(defn show-state []
  [:input {:type "submit"
           :value "state"
           :on-click #(.log js/console (clj->js @state))}])

;; ---------------------------------
;; Courses

(defn msg-list [items]
  [:div.list_messages
    (for [id items]
       ^{:key id}
       [:div.list_message
         id])])

(defn lister-msg []
   [msg-list (range 7)])

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
            [lister-msg]]]
        [tools]]]))
