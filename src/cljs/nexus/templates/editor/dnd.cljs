
(ns nexus.templates.editor.dnd
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [reagent.core :as r]
    [cljs.core.async :refer [<! put! chan timeout]]
    [nexus.helpers.core :refer [log]]
    [goog.dom :as g-dom]
    [goog.array :as g-array]
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
(def dnd-store (atom {:current nil
                      :drag nil}))

(defn on-drag-start [e]
  (let [index (int (.-index (.-dataset (.-target e))))
        brect-bottom (.-bottom (.getBoundingClientRect (.-target e)))
        brect-top (.-top (.getBoundingClientRect (.-target e)))
        middle-y (/ (- brect-bottom brect-top) 2)]
      (swap! dnd-store assoc :drag {:index index
                                    :brect-bottom brect-bottom
                                    :brect-top brect-top
                                    :middle-y middle-y})))

; (defn on-drag [e]
;   (let [index (int (.-index (.-dataset (.-target e))))
;         brect-bottom (.-bottom (.getBoundingClientRect (.-target e)))
;         brect-top (.-top (.getBoundingClientRect (.-target e)))
;         middle-y (+ brect-top (/ (- brect-bottom brect-top) 2))]
;       (put! dnd-chan {:drag @dnd-store
;                       :hover {:index index
;                               :brect-bottom brect-bottom
;                               :brect-top brect-top
;                               :middle-y middle-y}})))

(defn on-drag-over [e]
  (let [index (int (.-index (.-dataset (.-target e))))
        brect-bottom (.-bottom (.getBoundingClientRect (.-target e)))
        brect-top (.-top (.getBoundingClientRect (.-target e)))
        middle-y (/ (- brect-bottom brect-top 2))]
      (log @dnd-store)
      (swap! dnd-store assoc :current index)
      (put! dnd-chan {:drag (:drag @dnd-store)
                      :hover {:index index
                              :brect-bottom brect-bottom
                              :brect-top brect-top
                              :middle-y middle-y}})))

; (defn on-drag-end [e]
;   (let [id (.-id (.-dataset (.-target e)))
;         brect-bottom (.-bottom (.getBoundingClientRect (.-target e)))
;         brect-top (.-top (.getBoundingClientRect (.-target e)))]
;       (reset! drag-id nil)
;     (put! dnd-chan id)))

(defn listen! []
  (events/listen js/window EventType.DRAGSTART on-drag-start)
  (events/listen js/window EventType.DRAG on-drag-over)
  ; (events/listen js/window EventType.DRAGEND on-drag-end)
  (go-loop []
     (let [e (<! dnd-chan)
           {:keys [drag hover]} e]
        ; (log (:index drag))
        ; (log (:index hover))
        (when-not (= (-> drag :drag :index) (:index hover))
          (dispatch [:reorder_msg (:drag drag) hover]))
       (recur))))

(listen!)
