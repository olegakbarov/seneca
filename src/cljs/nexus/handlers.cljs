
(ns nexus.handlers
  (:require [re-frame.core :refer [reg-event-db
                                   subscribe]]
            [re-frame.core :as re-frame]
            [nexus.db :as db]
            [nexus.helpers.uids :refer [gen-uid]]
            [ajax.core :as ajax]
            [nexus.localstorage :as ls]))

(reg-event-db
 :initialize-db
 (fn  [_ _]
   db/state))


;;---------------------------
;; ROUTER

(reg-event-db
 :set-active-panel
 (fn [db [_ active-panel params]]
   (js/console.log active-panel)
   (assoc-in db [:router :current] active-panel)))

;;---------------------------
;; FORM

(reg-event-db
 :update-form
 (fn [db [_ cursor val]]
   (assoc-in db [:form cursor] val)))

;;---------------------------
;; AUTH

(reg-event-db
 :auth/login
 (fn [db _]
   (let [endpoint "https://dev.nadya.tech/api/v2/auth/get_token"
         creds (subscribe [:form])
         {:keys [email password]} @creds]
     (ajax/POST endpoint
                {:params {:email email :password password}
                 :handler #(re-frame/dispatch [:auth/login-success %1])
                 :error-handler #(re-frame/dispatch [:auth/login-err %1])
                 :format (ajax/json-request-format)
                 :keywords? true})
     db)))

(reg-event-db
 :auth/login-success
 (fn [db [_ res]]
   (let [token (get res "token")
         page-redirect (-> db :router :redirect)
         redirect (if-not page-redirect :editor page-redirect)]
      (do
        (re-frame/dispatch [:set-active-panel redirect])
        (re-frame/dispatch [:auth/save-token-ls! token])
        (re-frame/dispatch [:auth/save-token-db token]))
      db)))

(reg-event-db
 :auth/login-err
 (fn [db [_ res]]
   (js/console.log res)
   db))

(reg-event-db
 :auth/save-token-ls!
 (fn [db [_ token]]
   (ls/save! (ls/new-localstorage-imp) "nadya-token" token)
   db))

(reg-event-db
 :auth/save-token-db
 (fn [db [_ token]]
   (js/console.log "SAVING TOKEN TO DB")
   (assoc-in db [:auth :token] token)))

;;---------------------------
;; CURRENTS

(reg-event-db
  :set-curr-day
  (fn [db [_  id]]
    (assoc-in db [:curr-day] id)))

(reg-event-db
  :set-curr-course
  (fn [db [_  id]]
    (assoc-in db [:curr-course] id)))


;;---------------------------
;; LOGGING

(reg-event-db
 :show_state
 (fn [db [_]]
  ;  (js/console.log (-> db :ui :msgs))
   (js/console.log db)
   db))


;;---------------------------
;; MESSAGES

;; TODO: this is hella ugly
(defn shallow-deps
  "Returns a set of shallow dependecies, useful for init render"
  [v]
  (set
    (->> v
         (filter #(contains? % :payload))
         (map :payload)
         (map flatten)
         flatten
         (map :next)
         (remove nil?))))

(defn recursively-create-deps
 "Recursively walks tree and returns dependecy vector"
 [item msgs]
 (let [id (:uid item)]
   (if-let [payload (:payload item)]
     [id (mapv
          (fn [p]
            (if-let [n (:next p)]
              (let [next (first (filter #(= (:uid %) n) msgs))]
                (recursively-create-deps next msgs))))
          payload)]
     id)))

(defn create-deps
 "Given map returns vector of dependencies"
 [msgs]
 (mapv
  #(recursively-create-deps % msgs)
  msgs))

(defn make-deps-map [acc item]
  (if (vector? item)
      (assoc acc (first item) (set
                                (->> (peek item)
                                     flatten
                                     (remove nil?))))
      (assoc acc item #{})))

(reg-event-db
 :ui/create-msgs-state
 (fn [db [_]]
  (let [courses (subscribe [:courses])
        course (subscribe [:curr-course])
        day (subscribe [:curr-day])
        msgs (get-in @courses [@course :days (keyword @day) :messages])
        tree (create-deps msgs)
        state {:hidden (shallow-deps msgs)
               :deps (reduce make-deps-map {} tree)}]
   (prn tree)
   (update-in db [:ui :msgs] merge state))))


(reg-event-db
 :swap-msgs
 (fn [db [_ index1 index2]]
  (js/console.log index1 index2)
  (let [course (:curr-course db)
        day (:curr-day db)
        msg-cursor [:courses course :days day :messages]
        msgs (get-in db msg-cursor)
        msg1 (get msgs index1)
        msg2 (get msgs index2)]
    (if (nil? (or msg1 msg2))
      (throw (js/Error. "Can't insert at this index (OUT OF BOUNDS) " index1 index2))
      (let [edited (-> msgs
                       (assoc-in [index1] msg2)
                       (assoc-in [index2] msg1))]
        (assoc-in db msg-cursor edited))))))


;; ADD MSG

(defn- default-message [type]
  (let [uid (gen-uid "msg")]
    (condp = type
      "text-message" {:uid uid  :type "text-message" :text "New!New!New!" :id 123}
      "button-template" {:uid uid  :text "New btn tmpl" :type "button-template" :payload [{:text "Forward"} {:text "Back"}]}
      "quick-reply" {:uid uid  :text "New QR!" :type "quick-reply" :payload [{:text "Quick" :payload nil} {:text "Reply" :payload nil}]}
      "generic-template" {:uid uid  :type "text-message" :text "New!New!New!" :id 123}
      "media" {:uid uid :type "text-message" :text "New!New!New!" :id 123}
      (js/console.log "Cant create msg with type " type))))

(defn- insert-at [v item index]
  (js/console.log index)
  (if (> index (count v))
    (throw (js/Error. "Can't insert at this index (OUT OF BOUNDS) " index))
    (if (= index 0)
      (into [] (concat [item] v))
      (let [left (subvec v 0 index)
            right (subvec v index)]
        (into [] (concat left [item] right))))))

(reg-event-db
 :add-msg
 (fn [db [_ type index]]
   (js/console.log "adding msg to index " index)
   (let [course (:curr-course db)
         day (:curr-day db)
         msg-cursor [:courses course :days day :messages]
         msgs (get-in db msg-cursor)
         item (default-message type)
         edited (insert-at msgs item index)]
      (assoc-in db msg-cursor edited))))

;; TODO
; (reg-event-db
;  :clone-msg
;  (fn [db [_ type index]]))

(defn remove-at [v i]
  "Removes item with index `i` from vector `v`"
  (if (or (>= i (count v)) (< i 0))
    "Index out of bounds"
    (if (= 0 i)
      (subvec v 1)
      (let [left (subvec v 0 i)
            right (subvec v (+ i 1))]
        (into [] (concat left right))))))

(reg-event-db
 :remove-msg
 (fn [db [_ i]]
   (let [course (:curr-course db)
         day (:curr-day db)
         msgs (get-in db [:courses course :days day :messages])
         updated (remove-at msgs i)]
     (prn updated)
     (assoc-in db [:courses course :days day :messages] updated))))


;;---------------------------
;; DAYS

(reg-event-db
 :set_current_day
 (fn [db [_ n]]
   (assoc db :curr-day n)))

(defn new-day []
  (let [uid (gen-uid "day")
        days (subscribe [:curr-days])
        order (inc (count (vals @days)))]
    {:uid uid
     :order order
     :messages []}))

(reg-event-db
 :add-day
 (fn [db [_]]
   (let [course (:curr-course db)
         days (get-in db [:courses course :days])
         d (new-day)]
      (if (< (count days) 28)
          (assoc-in db [:courses course :days (:uid d)] d)
          db))))

(reg-event-db
 :remove-day
 (fn [db [_ id order]]
   (let [course (:curr-course db)
         days (get-in db [:courses course :days])
         edited (reduce
                 (fn [acc item]
                   (let [uid (:uid item)
                         new-order (:order item)]
                      (if-not (= uid id)
                              (assoc-in acc uid (if (> new-order order)
                                                    (merge item {:order (dec new-order)})
                                                    item))
                              acc)))
                 {}
                 days)])))


;;---------------------------
;; BOTS

(defn default-bot [id]
  {:title "New bot"
   :description ""
   :status "development"
   :uid id})

(reg-event-db
 :add-bot
 (fn [db [_]]
   (let [id (gen-uid "bot")]
     (assoc-in db [:bots id] (default-bot id)))))

;;---------------------------
;; COURSES


(reg-event-db
 :add-course
 (fn [db [_]]))

;;---------------------------
;; POST DATA
;;---------------------------

(defn default-course [id]
  {:title "New course"
   :subtitle "Course subtitle"
   :uid id
   :days []})

(reg-event-db
 :course/create
 (fn [db _]
   (let [endpoint "https://dev.nadya.tech/api/v2/publisher/courses/create"
         token (get-in db [:auth :token])
         id (gen-uid "course")
         course (default-course id)]
     (ajax/POST endpoint
                {:params {:data course}
                 :handler #(re-frame/dispatch [:course/create-success %1 course])
                 :error-handler #(re-frame/dispatch [:course/create-err %1])
                 :format (ajax/json-request-format)
                 :headers {:authorization token}
                 :keywords? true})
    db)))

(reg-event-db
 :course/create-success
 (fn [db [_ res course]]
   (js/console.log course)
   (let [token (get res "token")
         page-redirect (-> db :router :redirect)
         redirect (if-not page-redirect :editor page-redirect)]
     (assoc-in db [:courses (:uid course)] course))))

(reg-event-db
 :course/create-err
 (fn [db [_ res]]
   (js/console.log res)
   db))

;;---------------------------
;; FETCH DATA
;;---------------------------

;; FETCH COURESES

(reg-event-db
 :courses-fetch
 (fn [db _]
   (let [endpoint "https://dev.nadya.tech/api/v2/publisher/courses"
         token (get-in db [:auth :token])]
     (ajax/GET endpoint
               {:handler #(re-frame/dispatch [:courses-fetch-success %1])
                :error-handler #(re-frame/dispatch [:courses-fetch-err %1])
                :response-format :json
                :headers {:authorization token}
                :keywords? true})
     db)))

(reg-event-db
 :courses-fetch-success
 (fn [db [_ res]]
   (let [data (:courses res)
         processed (reduce
                    (fn [acc item]
                      (assoc-in acc [(:uid item)] item))
                    {}
                    data)]
     (js/console.log res)
     (assoc-in db [:courses] processed))))

(reg-event-db
 :courses-fetch-err
 (fn [db [_ res]]
   (js/console.log res)
   db))

;; FETCH BOTS

(reg-event-db
 :bots-fetch
 (fn [db _]
   (let [endpoint "https://dev.nadya.tech/api/v2/publisher/bots"]
     (ajax/GET endpoint
               {:handler #(re-frame/dispatch [:bots-fetch-success %1])
                :error-handler #(re-frame/dispatch [:bots-fetch-err %1])
                :response-format :json
                :keywords? true})
     db)))

(reg-event-db
 :bots-fetch-success
 (fn [db [_ res]]
   (let [processed (reduce
                    (fn [obj item]
                      (conj obj {(:id item) item}))
                    {}
                    res)]
     (assoc-in db [:bots] processed))))

(reg-event-db
 :bots-fetch-err
 (fn [db [_ response]]
   (js/console.log response)
   db))

;;---------------------------
;; EDITOR UI ACTIONS

(reg-event-db
 :set-is-editing-id
 (fn [db [_ id]]
   (assoc-in db [:ui :is-editing-id] id)))

(reg-event-db
 :unset-is-editing-id
 (fn [db [_]]
   (assoc-in db [:ui :is-editing-id] nil)))

;;---------------------------
;; EDITOR CONTENT ACTIONS

(reg-event-db
 :edit-msg-text
 (fn [db [_ id text]]
   (let [course (:curr-course db)
         day (:curr-day db)
         msg-cursor [:courses course :days day :messages]
         msgs (get-in db msg-cursor)
         edited (->> msgs
                     (mapv #(if (= (% :uid) id)
                                (merge % {:text text})
                                %)))]
      (assoc-in db msg-cursor edited))))


(reg-event-db
 :ui/toggle-expanded-id
 (fn [db [_ [parent child]]]
   (let [parent-deps (get-in db [:ui :msgs :deps parent])
         hidden (-> db :ui :msgs :hidden)
         diff-hidden (clojure.set/difference hidden parent-deps)
         new-hidden (conj diff-hidden child)
         active (-> db :ui :msgs :active)
         diff-active (clojure.set/difference hidden parent-deps)
         new-active (conj diff-active child)]
     (if (nil? parent)
       (throw (js/Error. "ERROR! parent CANT BE NIL" parent))
       (if (nil? child)
         db
         (-> db
             (assoc-in [:ui :msgs :hidden] new-hidden)
             (assoc-in [:ui :msgs :active] new-active)))))))
