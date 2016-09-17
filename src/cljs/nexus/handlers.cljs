
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

;; DND STARTS HERE

(defn rearrange [arr a b]
  "`a` and `b` are both indexes; arr is a vector"
  (prn (str "a "))
  (prn (str "b "))
  (let [right (if (> a b) a b)
        left (if (> a b) b a)
        l-item (nth arr left)
        r-item (nth arr right)]
    (swap! dnd-store assoc :drag-index b)
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
    ; (prn "drag-id" drag-id)
    ; (prn "hover" hover)
    (let [course (:curr-course db)
          day (:curr-day db)
          msgs (get-in db [:courses course :days day :messages])
          hover-index (:index hover)]
      (if (should-rearrange? drag-id hover client-y)
          (assoc-in db [:courses course :days day :messages] (rearrange msgs drag-id hover-index))
          db))))

;; ADD CARD
(defn- insert-at [v item index]
  (let [left (subvec v 0 index)
        right (subvec v index)]
      (into [] (concat left [item] right))))

(re-frame/reg-event-db
  :add_msg
  (log "add msg!")
  (fn [db [_ type drag-index hover client-y]]
    (log drag-index)
    (let [course (:curr-course db)
          day (:curr-day db)
          msgs (get-in db [:courses course :days day :messages])
          hover-index (:index hover)
          updated (insert-at msgs {:title "New!" :id 55} (:index hover))]
        (assoc-in db [:courses course :days day :messages] updated))))

(defn remove-at [v i]
  "Removes item with index `i` from vector `v`"
  (if (or (>= i (count v)) (< i 0))
    "Index out of bounds"
    (if (= 0 i)
      (subvec v 1)
      (let [left (subvec v 0 i)
            right (subvec v (+ i 1))]
        (into [] (concat left right))))))

(re-frame/reg-event-db
  :remove_msg
  (log "remove msg!")
  (fn [db [_ index]]
    (let [course (:curr-course db)
          day (:curr-day db)
          msgs (get-in db [:courses course :days day :messages])
          updated (remove-at msgs (:drag-index @dnd-store))]
      (log @dnd-store)
      (assoc-in db [:courses course :days day :messages] updated))))

;;
