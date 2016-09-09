
(ns nexus.templates.editor.days_row
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [reagent.core :as r]
    [cljs.core.async :refer [<! put! chan timeout]]
    [nexus.chans :refer [scroll-chan cur-scroll-y prev-scroll-y]]))

(def days-basic {:height "100px"
                 :width "100px"
                 :text-align "center"
                 :line-height "100px"
                 :font-size "33px"})

(def days-folded {:width "40px"
                  :height "40px"
                  :text-align "center"
                  :line-height "40px"
                  :font-size "19px"})

(def days-style (r/atom days-basic))

(def row-basic {:position "absolute"})
(def row-folded {:position "fixed" :top "65px" :z-index 9999})
(def row-style (r/atom row-basic))

(defn days [items]
  [:div.days_wrapper {:style @row-style}
    (doall
      (for [id items]
          ^{:key id} [:div.day_item {:style @days-style} id]))])

(defn days-row []
  [days (range 7)])

(defn listen! []
  (let [chan (scroll-chan)]
    (go-loop []
         (let [y (<! chan)]
           (reset! prev-scroll-y @cur-scroll-y)
           (if (> y 0)
             (do
               (reset! row-style row-folded)        ;; This sucks.
               (reset! days-style days-folded))
             (do
               (reset! row-style row-basic)
               (reset! days-style days-basic))))
         (recur))))

(listen!)
