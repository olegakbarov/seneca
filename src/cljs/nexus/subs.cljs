
(ns nexus.subs
  (:require [re-frame.core :as re-frame]
            [nexus.db :as db]
            [nexus.helpers.core :refer [log]]
            [re-frame.core :refer [subscribe]]))

(re-frame/reg-sub
  :active-panel
  (fn [db _]
   (-> db :router :current)))

(re-frame/reg-sub
  :curr-day
  (fn [db [_]]
   (:curr-day db)))

(re-frame/reg-sub
 :curr-course
 (fn [db [_]]
   (:curr-course db)))

(re-frame/reg-sub
  :curr-days
  (fn [db [_]]
    (let [course-id (:curr-course db)
          all-courses (get-in db [:courses])]
      (->> all-courses
           (filter (fn [c] (= (:uid c) course-id)))
           first
           :days))))

(re-frame/reg-sub
  :current-msgs
  (fn [db [_]]
    (let [days (subscribe [:curr-days])
          day-id (:curr-day db)]
      (->> @days
           (filter (fn [c] (= (:uid c) day-id)))
           first
           :messages))))


(re-frame/reg-sub
  :my-bots
  (fn [db [_ course-id]]
    (-> db :bots)))

 ; (register-sub
 ;  :sorted-items      ;; the query id  (the name of the query)
 ;  (fn [db [_]]       ;; the handler for the subscription
 ;    (reaction
 ;       (let [items      (get-in @db [:items])     ;; extract items from db
 ;             sort-attr  (get-in @db [:sort-by])]  ;; extract sort key from db
 ;           (sort-by sort-attr items)))))))          ;; return them sorted
