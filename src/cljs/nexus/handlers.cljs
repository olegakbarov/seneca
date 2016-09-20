
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

;; DND STARTS HERE

; (defn reorder [arr a b]
;   "`a` and `b` are both indexes; arr is a vector"
;   (let [right (if (> a b) a b)
;         left (if (> a b) b a)
;         l-item (nth arr left)
;         r-item (nth arr right)]
;     (if (= a b)
;         arr
;         (let [b4-a (subvec arr 0 left)
;               btwn (subvec arr (+ left 1) right)
;               aftr-b (subvec arr (+ right 1))]
;            (into [] (concat b4-a btwn [r-item] [l-item] aftr-b))))))

(defn reorder [v a b]
  "`a` and `b` are both indexes; v is a vector"
  (let [right (if (> a b) a b)
        left (if (> a b) b a)
        l-item (nth v left)
        r-item (nth v right)]
    (if (= a b)
        v
        (assoc v right l-item left r-item)))) ;; TODO CHECK!

(re-frame/reg-event-db
  :reorder_msg
  (fn [db [_ dix hix]]
    (let [course (:curr-course db)
          day (:curr-day db)
          msgs (get-in db [:courses course :days day :messages])
          updated (reorder msgs dix hix)]
      (assoc-in db [:courses course :days day :messages] updated))))

;; ADD MSG
(defn- insert-at [v item index]
  (if (= index (+ 1 (count v)))
    (concat v [item])
    (let [left (subvec v 0 index)
          right (subvec v index)]
        (into [] (concat left [item] right)))))

(re-frame/reg-event-db
  :add_msg
  (log "add msg!")
  (fn [db [_ type hix]]
    (prn (str "hix: " hix))
    (let [course (:curr-course db)
          day (:curr-day db)
          msgs (get-in db [:courses course :days day :messages])
          updated (insert-at msgs {:title "New!" :id 55} hix)]
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
  (fn [db [_ i]]
    (let [course (:curr-course db)
          day (:curr-day db)
          msgs (get-in db [:courses course :days day :messages])
          updated (remove-at msgs i)]
      (assoc-in db [:courses course :days day :messages] updated))))

;;
