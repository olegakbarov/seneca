(ns nexus.handlers
    (:require [re-frame.core :as re-frame]
              [nexus.db :as db]
              [nexus.helpers.core :refer [log]]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/state))

(re-frame/reg-event-db
  :set-active-panel
  (fn [db [_ active-panel]]
    (assoc-in db [:router :current] active-panel)))

;; just for lulz, should do it on the databases
(defn inc-prop [db key]
  (let [c (:courses db)
        cc (get-in c [(:curr-course db)])
        d (:days cc)
        cd (get-in d [(:curr-day db)])
        arr (:messages cd)]
    (inc (max (map key arr)))))

(re-frame/reg-event-db
  :add_msg
  (fn [db [_ title]]
    (let [course (:curr-course db)
          day (:curr-day db)]
      (update-in db
        [:courses course :days day :messages]
        conj
        {:id (inc-prop db :id)
         :order (inc-prop db :order)
         :title title}))))

;; naīve logging
(re-frame/reg-event-db
  :show_state
  (fn [db [_]]
    (log db)
    db))
