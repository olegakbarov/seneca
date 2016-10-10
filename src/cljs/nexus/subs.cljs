(ns nexus.subs
  (:require [re-frame.core :as re-frame]
            [nexus.db :as db]
            [nexus.helpers.core :refer [log]]))

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
  :current-msgs
  (fn [db [_ course-id day-id]]
    (let [course (-> db :curr-course)
          curr-day-id (-> db :curr-day)
          days (vals (get-in db [:courses course :days]))
          current-day (filter
                        (fn [day] (= curr-day-id (:uuid day)))
                       days)]
      (-> current-day
          first
          :messages))))

(re-frame/reg-sub
  :days
  (fn [db [_ course-id]]
    (get-in db [:courses course-id :days])))

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
