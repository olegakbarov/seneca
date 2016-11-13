
(ns nexus.subs
  (:require [re-frame.core :as re-frame]
            [nexus.db :as db]
            [re-frame.core :refer [subscribe]]))

(re-frame/reg-sub
  :active-panel
  (fn [db _]
   (-> db :router :current)))


;;-------------------------
;; AUTH

(re-frame/reg-sub
  :auth/token
  (fn [db _]
   (-> db :auth :token)))

;;-------------------------
;; CURRENTS

(re-frame/reg-sub
  :curr-day
  (fn [db [_]]
   (:curr-day db)))

(re-frame/reg-sub
  :curr-days
  (let [course (:curr-course db)]
    (get-in db [:courses course :days])))

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
  :form
  (fn [db [_]]
    (-> db :form)))

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
  :ui/curr-thread
  (fn [db [_]]
    (-> db :ui :curr-thread)))

(re-frame/reg-sub
  :ui/msgs-state
  (fn [db [_]]
    (-> db :ui :msgs)))

(re-frame/reg-sub
  :ui/hidden-msgs
  (fn [db [_]]
    (-> db :ui :msgs :hidden)))

(re-frame/reg-sub
  :ui/active-msgs
  (fn [db [_]]
    (-> db :ui :msgs :active)))

(re-frame/reg-sub
  :ui/is-editing-id
  (fn [db [_]]
    (-> db :ui :is-editing-id)))

(re-frame/reg-sub
  :ui/text
  (fn [db [_ uid]]
    (let [course (:curr-course db)
          day (:curr-day db)
          msgs (get-in db [:courses course :days day :messages])
          one (->> msgs
                   (filter #(= (:uid %) uid)))]
      (:text (first one)))))
