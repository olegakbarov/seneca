
(ns nexus.handlers
    (:require [re-frame.core :refer [reg-event-db
                                     subscribe]]
              [re-frame.core :as re-frame]
              [nexus.db :as db]
              [nexus.helpers.core :refer [log]]
              [nexus.helpers.uids :refer [gen-uid]]
              [ajax.core :as ajax]))

(reg-event-db
 :initialize-db
 (fn  [_ _]
   db/state))

;; naīve logging
(reg-event-db
  :show_state
  (fn [db [_]]
    (log db)
    db))

(reg-event-db
  :set-active-panel
  (fn [db [_ active-panel]]
    (assoc-in db [:router :current] active-panel)))

;; MESSAGES

(defn swap-vec [v a b]
  (map
    (fn [[id msg]]
      (let [{:keys [order]} msg]
        (condp = order
          a {id (assoc-in msg [:order] b)}
          b {id (assoc-in msg [:order] a)}
          {id msg})))
    v))

(reg-event-db
  :reorder_msg
  (fn [db [_ dix hix]]
    ; (prn "event reorder:" dix hix)
    (let [course-id (:curr-course db)
          day-id (:curr-day db)
          msgs (get-in db [:courses course-id :days day-id :messages])
          updated (into {} (swap-vec msgs dix hix))]
      (assoc-in db [:courses course-id :days day-id :messages] updated))))

(defn- insert-at [v item index]
  (if (= index (+ 1 (count v)))
    (concat v [item])
    (let [left (subvec v 0 index)
          right (subvec v index)]
        (into [] (concat left [item] right)))))

(defn- default-message [type]
  (let [uid (gen-uid "msg")]
    (condp = type
      "text-message" {:uid uid  :type "text-message" :text "New!New!New!" :id 123}
      "button-template" {:uid uid  :text "New btn tmpl" :type "button-template" :buttons [{:text "Forward"} {:text "Back"}]}
      "quick-reply" {:uid uid  :text "New QR!" :type "quick-reply" :buttons [{:text "Quick"} {:text "Reply"}]}
      "generic-template" {:uid uid  :type "text-message" :text "New!New!New!" :id 123}
      "media" {:uid uid :type "text-message" :text "New!New!New!" :id 123})))

(reg-event-db
  :add_msg
  (fn [db [_ type hix]]
    ; (prn (str "hix: " hix))
    (let [course (:curr-course db)
          day (:curr-day db)
          msgs (get-in db [:courses course :days day :messages])
          updated (insert-at msgs (default-message type) hix)]
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

(reg-event-db
  :remove_msg
  (fn [db [_ i]]
    (let [course (:curr-course db)
          day (:curr-day db)
          msgs (get-in db [:courses course :days day :messages])
          updated (remove-at msgs i)]
      (assoc-in db [:courses course :days day :messages] updated))))



;; DAYS

(reg-event-db
  :set_current_day
  (fn [db [_ n]]
    (assoc db :curr-day n)))



;; BOTS

(def new-bot
  {:title "New bot"
   :description ""
   :status "development"})

(reg-event-db
  :add-bot
  (fn [db [_]]
    (assoc-in db [:bots 333] new-bot)))



;; FORM

(reg-event-db
  :update-form
  (fn [db [_ cursor val]]
    (assoc-in db [:form cursor] val)))



;; FETCH DATA

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



;; EDITOR ACTIONS

(reg-event-db
  :set-is-editing-id
  (fn [db [_ id]]
    (assoc-in db [:ui :is-editing-id] id)))
