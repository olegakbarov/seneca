
(ns nexus.handlers
  (:require [re-frame.core :refer [reg-event-db
                                   subscribe]]
            [re-frame.core :as re-frame]
            [nexus.db :as db]
            [nexus.helpers.core :refer [log]]
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
 (fn [db [_ active-panel]]
  ;  (js/console.log ":set-active panel handler " active-panel)
  ;; нехилый костыль
   (if (keyword? active-panel)
     (assoc-in db [:router :current] active-panel)
     (assoc-in db [:router :current] (active-panel)))))


;;---------------------------
;; AUTH

(reg-event-db
 :auth/login
 (fn [db _]
   (let [endpoint "http://localhost:7777/api/v1/auth/token"
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
;; LOGGING

(reg-event-db
 :show_state
 (fn [db [_]]
   (js/console.log (-> db :ui :msgs))
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
  (let [msgs (subscribe [:curr-msgs])
        tree (create-deps @msgs)
        state {:hidden (shallow-deps @msgs)
               :deps (reduce make-deps-map {} tree)}]
   (prn tree)
   (update-in db [:ui :msgs] merge state))))


(reg-event-db
 :swap-msgs
 (fn [db [_ index1 index2]]
  (let [course (:curr-course db)
        day (:curr-day db)
        msg-cursor [:courses course :days day :messages]
        msgs (get-in db msg-cursor)
        msg1 (get msgs index1)
        msg2 (get msgs index2)]
    (if-not (nil? (or msg1 msg2))
      (let [edited (-> msgs
                       (assoc-in [index1] msg2)
                       (assoc-in [index2] msg1))]
        (assoc-in db msg-cursor edited))
      (throw (js/Error. "Can't insert at this index (OUT OF BOUNDS) " index1 index2))))))


;; ADD MSG

(defn- default-message [type]
  (let [uid (gen-uid "msg")]
    (condp = type
      "text-message" {:uid uid  :type "text-message" :text "New!New!New!" :id 123}
      "button-template" {:uid uid  :text "New btn tmpl" :type "button-template" :buttons [{:text "Forward"} {:text "Back"}]}
      "quick-reply" {:uid uid  :text "New QR!" :type "quick-reply" :buttons [{:text "Quick"} {:text "Reply"}]}
      "generic-template" {:uid uid  :type "text-message" :text "New!New!New!" :id 123}
      "media" {:uid uid :type "text-message" :text "New!New!New!" :id 123})))

(defn- insert-at [v item index]
  (if (> index (count v))
    (throw (js/Error. "Can't insert at this index (OUT OF BOUNDS) " index))
    (if (= index (+ 1 (count v)))
      (concat v [item])
      (let [left (subvec v 0 index)
            right (subvec v index)]
        (into [] (concat left [item] right))))))

(reg-event-db
 :add-msg
 (fn [db [_ type index]]
   (let [course (:curr-course db)
         day (:curr-day db)
         msgs (get-in db [:courses course :days day :messages])
         msg-cursor [:courses course :days day :messages]
         msgs (get-in db msg-cursor)
         item (default-message type)]
    (assoc-in db msg-cursor (insert-at msgs item index)))))

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
     (assoc-in db [:courses course :days day :messages] updated))))


;;---------------------------
;; DAYS

(reg-event-db
 :set_current_day
 (fn [db [_ n]]
   (assoc db :curr-day n)))

;;---------------------------
;; BOTS

(def default-bot
  {:title "New bot"
   :description ""
   :status "development"})

(reg-event-db
 :add-bot
 (fn [db [_]]
   (assoc-in db [:bots 333] default-bot)))

;;---------------------------
;; FORM

(reg-event-db
 :update-form
 (fn [db [_ cursor val]]
   (assoc-in db [:form cursor] val)))

;;---------------------------
;; FETCH DATA

;; FETCH COURESES

(reg-event-db
 :courses-fetch
 (fn [db _]
   (let [endpoint "http://localhost:7777/api/v2/publisher/courses"]
     (ajax/GET endpoint
               {:handler #(re-frame/dispatch [:courses-fetch-success %1])
                :error-handler #(re-frame/dispatch [:courses-fetch-err %1])
                :response-format :json
                :keywords? true})
     db)))

(reg-event-db
 :courses-fetch-success
 (fn [db [_ res]]
   (let [processed (reduce
                    (fn [obj item]
                      (conj obj {(:id item) item}))
                    {}
                    res)]
     (assoc-in db [:bots] processed))))

(reg-event-db
 :courses-fetch-err
 (fn [db [_ response]]
   (js/console.log response)
   db))

;; FETCH BOTS

(reg-event-db
 :bots-fetch
 (fn [db _]
   (let [endpoint "http://localhost:7777/bots"]
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
         msgs (get-in db [:courses course :days day :messages])
         updated (assoc-in msgs [id :text] text)]
     (assoc-in db [:courses course :days day :messages] updated))))


;  (if (nil? parent)
;    (throw (js/Error. "ERROR! parent CANT BE NIL" parent))
;  (if (nil? child)
;    db

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
      ; (js/console.log parent-deps)
      ; (js/console.log hidden)
      ; (js/console.log diff-hidden)
      ; (js/console.log new-hidden)
      ; (js/console.log active)
      (-> db
          (assoc-in [:ui :msgs :hidden] new-hidden)
          (assoc-in [:ui :msgs :active] new-active)))))
