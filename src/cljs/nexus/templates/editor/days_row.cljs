
(ns nexus.templates.editor.days_row
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [reagent.core :as r]
    [re-frame.core :refer [reg-event-db
                           subscribe]]))

(def days-folded {:width "40px"
                  :height "40px"
                  :text-align "center"
                  :line-height "40px"
                  :font-size "19px"})

(def days-style (r/atom days-folded))

; (def row-basic {:position "absolute"})
(def row-folded {:position "fixed" :top "65px" :z-index 9999})
(def row-style (r/atom row-folded))

(defn days [items]
  [:div.days_wrapper {:style @row-style}
    (doall
      (for [id items]
          ^{:key id} [:div.day_item {:style @days-style} id]))])

(defn num->vec [n]
  (let [v (vec (repeat n nil))]
    (map-indexed
      (fn [ix _] ix)
     v)))

(defn days-row []
  (let [dayz (subscribe [:days 123])
        days-from-1 (map inc (num->vec (count @dayz)))]
    [days days-from-1]))
