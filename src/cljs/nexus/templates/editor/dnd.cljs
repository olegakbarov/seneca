
(ns nexus.templates.editor.dnd
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [reagent.core :as r]
    [cljs.core.async :refer [<! put! chan timeout]]
    [nexus.helpers.core :refer [log]]
    [goog.dom :as g-dom]
    [goog.events :as events]
    [re-frame.core :refer [dispatch]])
  (:import [goog.events EventType]))

;; HELPERS

(defn set-cursor! [cursor-type]
  (aset js/document "body" "style" "cursor" (name cursor-type)))

;; e
;; clientY = 301 // от верха window
;; offsetY = 7   // от верха кликнутого элемента до места клика
;; screenY = 408 // весь экран

;; boundingRect
;; .top = 294 // от верха элемента до window
;; .bottom = // от низа до window

(def dnd-chan (chan))
(def dnd-store (r/atom {:drag-index nil          ;; the item we drag
                        :hover nil         ;; the item we hover on
                        :client-y nil}))

(defn on-drag-start [e]
  (let [index (int (.-index (.-dataset (.-target e))))
        brect-bottom (.-bottom (.getBoundingClientRect (.-target e)))
        brect-top (.-top (.getBoundingClientRect (.-target e)))
        middle-y (/ (- brect-bottom brect-top) 2)]
      (swap! dnd-store assoc :drag-index index)))

(defn on-drag [e]
  "nimp")

(defn on-drag-over [e]
  (let [index (int (.-index (.-dataset (.-target e))))
        brect-bottom (.-bottom (.getBoundingClientRect (.-target e)))
        brect-top (.-top (.getBoundingClientRect (.-target e)))
        middle-y (/ (- brect-bottom brect-top) 2)
        client-y (.-clientY e)]
      ;; TODO
      (swap! dnd-store assoc :client-y client-y)
      (log (:drag-index @dnd-store))
      ;;
      (if (and index brect-top brect-bottom middle-y)
          (put! dnd-chan {:client-y client-y
                          :hover {:index index
                                  :brect-bottom brect-bottom
                                  :brect-top brect-top
                                  :middle-y middle-y}}))))

(defn on-drag-end [e]
  (swap! dnd-store assoc :drag-index nil))

(defn listen! []
  (events/listen js/window EventType.DRAGSTART on-drag-start)
  (events/listen js/window EventType.DRAGOVER on-drag-over)
  (events/listen js/window EventType.DRAGEND on-drag-end)
  (go-loop []
     (let [e (<! dnd-chan)
           {:keys [hover client-y]} e
           drag-index (:drag-index @dnd-store)]
        (when-not (= drag-index (:index hover))
          (dispatch [:reorder_msg drag-index hover client-y]))
       (recur))))

(listen!)
