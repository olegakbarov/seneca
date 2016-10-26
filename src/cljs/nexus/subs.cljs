
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
    (let [course-id (:curr-course db)]
      (-> db
          (get-in [:courses course-id :days])
          vals))))

(re-frame/reg-sub
  :curr-msgs
  (fn [db [_]]
    (let [course-id (:curr-course db)
          day-id (:curr-day db)]
      (-> db
          (get-in [:courses course-id :days day-id :messages])))))

(re-frame/reg-sub
  :my-bots
  (fn [db [_ course-id]]
    (-> db :bots)))

;; ---------------------------------------------
;; FORMS

(re-frame/reg-sub
  :form/email
  (fn [db [_]]
    (-> db :form :email)))

(re-frame/reg-sub
  :form/password
  (fn [db [_]]
    (-> db :form :password)))

(re-frame/reg-sub
  :form/password-again
  (fn [db [_]]
    (-> db :form :password-again)))

(re-frame/reg-sub
  :form/passwords-match
  (fn [db [_]]
    (let [pwd (-> db :form :password)
          pwd2 (-> db :form :password-again)]
      (= pwd pwd2))))


;; ---------------------------------------------
;; UI

(re-frame/reg-sub
  :ui/is-editing-id
  (fn [db [_]]
    (-> db :ui :is-editing-id)))

(re-frame/reg-sub
  :ui/is-editing-msg-text
  (fn [db [_]]
    (let [course (:curr-course db)
          day (:curr-day db)
          id (subscribe [:ui/is-editing-id])]
      (get-in db [:courses course :days day :messages @id :text]))))

(re-frame/reg-sub
  :ui/curr-thread
  (fn [db [_]]
    (-> db :ui :curr-thread)))


 ; (register-sub
 ;  :sorted-items      ;; the query id  (the name of the query)
 ;  (fn [db [_]]       ;; the handler for the subscription
 ;    (reaction
 ;       (let [items      (get-in @db [:items])     ;; extract items from db
 ;             sort-attr  (get-in @db [:sort-by])]  ;; extract sort key from db
 ;           (sort-by sort-attr items))))))        ;; return them sorted
