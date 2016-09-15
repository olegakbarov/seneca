
(ns nexus.handlers
    (:require [re-frame.core :as re-frame]
              [nexus.db :as db]
              [nexus.helpers.core :refer [log]]
              [nexus.templates.editor.dnd :refer [dnd-store]]))

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
;; const hoverClientY = clientOffset.y - hoverBoundingRect.top;

(defn rearrange [arr a b]
  "`a` and `b` are both indexes; arr is a vector"
  (let [right (if (> a b) a b)
        left (if (> a b) b a)
        l-item (nth arr left)
        r-item (nth arr right)]
    (swap! dnd-store assoc :drag-id b)
    (if (= a b)
        arr
        (let [b4-a (subvec arr 0 left)
              btwn (subvec arr (+ left 1) right)
              aftr-b (subvec arr (+ right 1))]
           (into [] (concat b4-a btwn [r-item] [l-item] aftr-b))))))

(defn should-rearrange? [drag-id hover client-y]
  (let [dix drag-id
        hix (:index hover)
        hover-mid (/ (- (:brect-bottom hover) (:brect-top hover)) 2)
        hov-cli-y (- client-y (:brect-top hover))]
    ; (log (str dix " " hix " " hover-mid " " hov-cli-y))
    ; (log hover-mid)
    (cond
      (and (< dix hix) (< hov-cli-y hover-mid)) true
      (and (> dix hix) (> hov-cli-y hover-mid)) true
      (nil? hover) false
      :else false)))

(re-frame/reg-event-db
  :reorder_msg
  (fn [db [_ drag-id hover client-y]]
    (let [course (:curr-course db)
          day (:curr-day db)
          msgs (get-in db [:courses course :days day :messages])
          hover-index (:index hover)]
      (log (should-rearrange? drag-id hover client-y))
      (if (should-rearrange? drag-id hover client-y)
          (assoc-in db [:courses course :days day :messages] (rearrange msgs drag-id hover-index))
          db))))
;;
