(ns nexus.handlers
    (:require [re-frame.core :as re-frame]
              [nexus.db :as db :refer [current-route]]
              [nexus.helpers.core :refer [log]]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/state))

(re-frame/reg-event-db
  :set-active-panel
  (fn [db [_ active-panel]]
    (assoc-in db [:router :current] active-panel)))


 ; (re-frame/reg-event-db
 ;  :set-active-panel
 ;  (fn [db [_ active-panel]]
 ;    (assoc db :active-panel active-panel)))))
