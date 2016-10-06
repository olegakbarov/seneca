
(ns nexus.templates.editor.days_row
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [reagent.core :as r]
    [nexus.chans :refer [scroll-chan]]
    [re-frame.core :refer [reg-event-db
                           dispatch
                           subscribe]]))

(def style (r/atom {}))

(defn days [items]
  (let [curr-day (subscribe [:curr-day])]
    [:div.days_row_wrapper {:style @style}
      (doall
        (for [id items]
            ^{:key id}
            [:div.days_row_item
               {:class (if (= (dec id) @curr-day) "current" "")
                :on-click #(dispatch [:set_current_day (dec id)])}
               id]))]))

(defn num->vec [n]
  (let [v (vec (repeat n nil))]
    (map-indexed
      (fn [ix _] ix)
     v)))

(defn days-row []
  (let [dayz (subscribe [:days 123])
        days-from-1 (map inc (num->vec (count @dayz)))]
    [days days-from-1]))

(defn listen! []
  (let [chan (scroll-chan)]
    (go-loop []
       (let [y (<! chan)]
         (if (> y 0)
           (do (reset! style {:position "fixed"
                              :top "65px"
                              :z-index 9999}))))
      (recur))))

(listen!)
