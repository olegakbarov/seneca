
(ns nexus.handlers
    (:require [re-frame.core :as re-frame]
              [nexus.db :as db]
              [nexus.helpers.core :refer [log]]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/state))

;; naīve logging
(re-frame/reg-event-db
  :show_state
  (fn [db [_]]
    (log db)
    db))

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
    (inc (apply max (map key arr)))))

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


;; DND magic ---------------

; (defn swap-items [msgs order]
;   (let [next (first (filter (fn [x] (= (:order x) (inc order))) msgs))
;         current (first (filter fn [x] (= (:order x) (inc order)) msgs))
;         step-one (conj msgs)]))

(re-frame/reg-event-db
  :reorder_msg
  (fn [db [_ order type]]
    (prn "lets reorder!")
    (let [course (:curr-course db)
          day (:curr-day db)
          msgs (get-in db [:courses course :days day :messages])]
      (condp = type
        :inc (assoc-in db [:courses course :days day :messages]
               (map (fn [item]
                      (cond
                        (= (:order item) order) (update-in item [:order] inc)
                        (= (:order item) (inc order)) (update-in item [:order] dec)
                        :else item))
                msgs))
        :dec (assoc-in db [:courses course :days day :messages]
               (map (fn [item]
                      (cond
                        (= (:order item) order) (update-in item [:order] dec)
                        (= (:order item) (inc order)) (update-in item [:order] inc)
                        :else item))
                msgs))))))

;;
