(ns nexus.subs
  (:require [re-frame.core :as re-frame]
            [nexus.db :as db]
            [nexus.helpers.core :refer [log]]))

(re-frame/reg-sub
 :active-panel
 (fn [db _]
  ;  (log "SUBS:")
  ;  (log (-> db :router :current))
   (-> db :router :current)))


  ;  (register-sub
  ;   :sorted-items      ;; the query id  (the name of the query)
  ;   (fn [db [_]]       ;; the handler for the subscription
  ;     (reaction
  ;        (let [items      (get-in @db [:items])     ;; extract items from db
  ;              sort-attr  (get-in @db [:sort-by])]  ;; extract sort key from db
  ;            (sort-by sort-attr items)))))))          ;; return them sorted
