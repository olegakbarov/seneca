
(ns nexus.templates.editor.days_row
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [reagent.core :as r]
    [nexus.chans :refer [scroll-chan]]
    [re-frame.core :refer [reg-event-db
                           dispatch
                           subscribe]]))

(def style (r/atom {}))

(defn get-item-classes [day]
  (prn day)
  (let [curr-day (subscribe [:curr-day])
        {:keys [errors empty uuid]} day
        cur (if (= uuid @curr-day) "current " "")
        em (if (:empty? day) "empty " "")
        er (if (> (:errors day) 0) "error " "")]
    (str cur er em)))

(defn days-group [group group-n n]
  (let [is-first (= group-n 0)
        is-last  (= group-n (count (partition-all 7 (range n))))]
    [:div.days_group_container {:class (cond is-first "first"
                                             is-last  "last")}
      [:div.days_group_wrapper {:style {:border-left (if is-first "0px")}}
        [:div.days_group_number (+ 1 (* 7 group-n))]
        [:div.days_group_inner
          (doall
            (map-indexed
             (fn [index day]
               (let [uuid (:uuid day)]
                  ^{:key index}
                  [:div.days_row_item
                     {:class (get-item-classes day)
                      :on-click #(dispatch [:set_current_day uuid])}
                    (:errors day)]))
             group))]]]))

(defn days [items]
  (let [groups (partition-all 7 items) ;; 7 = week
        n (count items)]
      [:div.days_row_wrapper {:style @style}
        (doall
          (map-indexed
            (fn [index group]
              ^{:key index}
              [days-group group index n])
           groups))]))

(defn days-row []
  (let [dayz (subscribe [:days 123])
        days-processed (map
                         (fn [day]
                           (let [d day
                                 is-empty (empty? (:messages d))
                                 errors (:errors d)
                                 uuid (:uuid d)]
                            {:empty? is-empty
                             :errors errors
                             :uuid uuid}))
                         (vals @dayz))]
    [days days-processed]))

(defn listen! []
  (let [chan (scroll-chan)]
    (go-loop []
       (let [y (<! chan)]
        ;; TODO! only fire once first scroll
         (if (> y 0)
             (reset! style {:position "fixed"
                            :top "65px"
                            :z-index 9999})))
      (recur))))

(listen!)
