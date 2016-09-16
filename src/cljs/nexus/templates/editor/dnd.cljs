
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
(def dnd-store (r/atom {:drag-index nil    ;; the item we drag
                        :hover nil         ;; the item we hover on
                        :client-y nil
                        :msg-added false}))

(def dnd-types ["button-template"])

(defn on-drag-start [e]
  (let [index (int (.-index (.-dataset (.-target e))))
        brect-bottom (.-bottom (.getBoundingClientRect (.-target e)))
        brect-top (.-top (.getBoundingClientRect (.-target e)))
        middle-y (/ (- brect-bottom brect-top) 2)
        type (.-type (.-dataset (.-target e)))]
      ; (log type)
      ; (log (some #(= type %) dnd-types))
      (if-not (some #(= type %) dnd-types)
        (swap! dnd-store assoc :drag-index index))))

(defn on-drag [e]
  "nimp")

(defn on-drag-over [e]
  (.preventDefault e) ;; needed for drop event
  (let [index (int (.-index (.-dataset (.-target e))))
        brect-bottom (.-bottom (.getBoundingClientRect (.-target e)))
        brect-top (.-top (.getBoundingClientRect (.-target e)))
        middle-y (/ (- brect-bottom brect-top) 2)
        client-y (.-clientY e)]

      (swap! dnd-store assoc :client-y client-y)

      (if (and index brect-top brect-bottom middle-y)
          (put! dnd-chan {:client-y client-y
                          :hover {:index index
                                  :brect-bottom brect-bottom
                                  :brect-top brect-top
                                  :middle-y middle-y}}))))

(defn on-drag-end [e]
  (do
    (swap! dnd-store assoc :msg-added false)
    (swap! dnd-store assoc :drag-index nil)))

(defn on-drag-enter [e]
  (log "ON DRAG_ENTER")
  (.preventDefault e) ;; needed for drop event
  (let [index (int (.-index (.-dataset (.-target e))))
        drag-index (:drag-index @dnd-store)
        brect-bottom (.-bottom (.getBoundingClientRect (.-target e)))
        brect-top (.-top (.getBoundingClientRect (.-target e)))
        middle-y (/ (- brect-bottom brect-top) 2)]
    ; (if (some #(= type %) dnd-types) ;; drag from tools
      (do
        (swap! dnd-store assoc :drag-index index)
        (if-not (:msg-added @dnd-store)
          (dispatch [:add_msg "kek" drag-index {:index index
                                                :brect-bottom brect-bottom
                                                :brect-top brect-top
                                                :middle-y middle-y}]))
        (swap! dnd-store assoc :msg-added true))))

(defn on-drag-leave [e]
  (.preventDefault e) ;; needed for drop event
  (let [index (int (.-index (.-dataset (.-target e))))]))
    ; (log index)))
      ; (do
      ;   (swap! dnd-store assoc :drag-index index)
      ;   (if (:msg-added @dnd-store)
      ;     (dispatch [:add_msg "kek"]))
      ;   (swap! dnd-store assoc :msg-added true))))

(defn on-drop [e]
  (log e))
    ;; if current drag source match current drop target, handle the drop!

(defn listen! []
  (events/listen js/window EventType.DRAGSTART on-drag-start)
  (events/listen js/window EventType.DRAGOVER on-drag-over)
  (events/listen js/window EventType.DRAGEND on-drag-end)
  ; (events/listen js/window EventType.DRAGENTER on-drag-enter)
  (events/listen js/window EventType.DRAGOVER on-drag-over)
  ; (events/listen js/window EventType.DROP on-drop)

  (go-loop []
     (let [e (<! dnd-chan)
           {:keys [hover client-y]} e
           drag-index (:drag-index @dnd-store)]
        (when-not (= drag-index (:index hover))
          (dispatch [:reorder_msg drag-index hover client-y]))
       (recur))))

(listen!)
