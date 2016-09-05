
(ns nexus.templates.editor
  (:require
    [reagent.core :as r]
    [nexus.helpers.core :refer [log]]))

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
   [msg-list (range 4)])

;; ---------------------------------
;; Days row

(defn days-list [items]
  [:div.days_wrapper
    (for [id items]
       ^{:key id}
       [:div.day_item
         id])])

(defn days-row []
    [days-list (range 7)])

;; ---------------------------------
;; Editor

(defn editor []
  (fn []
    [:div.editor
      [days-row]
      [:div.editor_wrapper
        [:div.editor_messenger_wrapper
          [show-state]
          [:div.editor_messenger
            [lister-msg]]]
        [:div.editor_tools_wrapper
          [:div.editor_tools
            "top"]]]]))
