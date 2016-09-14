
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

(defn rearrange [arr a b]
  "a and b are both indexes in arr is vector"
  (let [right (if (> a b) a b)
        left (if (> a b) b a)
        l-item (nth arr left)
        r-item (nth arr right)]
    (if (= a b)
        arr
        (let [b4-a (subvec arr 0 left)
              btwn (subvec arr (+ left 1) right)
              aftr-b (subvec arr (+ right 1))]
           (into [] (concat b4-a btwn [r-item] [l-item] aftr-b))))))

(defn should-rearrange? [drag hover]
  (let [dix (:index drag)
        hix (:index hover)
        dy (:middle-y drag)
        dh (:middle-y hover)]
    (cond
      (and (< dix hix) (< dy dh)) false
      (and (> dix hix) (> dy dh)) false
      :else true)))

(re-frame/reg-event-db
  :reorder_msg
  (fn [db [_ drag hover]]
    (let [course (:curr-course db)
          day (:curr-day db)
          msgs (get-in db [:courses course :days day :messages])
          drag-index (:index drag)
          hover-index (:index hover)
          updated (rearrange msgs drag-index hover-index)]
      (log (should-rearrange? drag hover))
      (if (should-rearrange? drag hover)
        (assoc-in db [:courses course :days day :messages] updated)
        db))))

;;
