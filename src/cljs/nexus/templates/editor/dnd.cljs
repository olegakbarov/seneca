
(ns nexus.templates.editor.dnd
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [reagent.core :as r]
    [cljs.core.async :refer [<! put! chan timeout]]
    [nexus.helpers.core :refer [log]]
    [goog.events :as events]
    [re-frame.core :refer [dispatch]])
  (:import [goog.events EventType]))

(defn set-cursor! [cursor-type]
  (aset js/document "body" "style" "cursor" (name cursor-type)))

;; --------------------------
;; STATE:

(def dnd-chan (chan))

(def dnd-types ["button-template"])

(def state (r/atom {:dix nil           ;; the item we drag
                    :hix nil           ;; the item we hover on
                    :y nil             ;; user's clientY
                    :msg-added false   ;; msg already added?
                    :drag-type nil}))  ;; type of item we drag

(defn update-state! [key val]
  (swap! state assoc key val))

;; --------------------------
;; HELPERS:

(defn parse-event [e]
  (cond = (-> e .-type)
    "dragend" (.preventDefault e)
    "drop" (.preventDefault e)
    "dragleave" (.preventDefault e))

  (let [ix (-> e .-target .-dataset .-index int)
        bottom (-> e .-target .getBoundingClientRect .-bottom)
        y (-> e .-clientY)
        top (-> e .-target .getBoundingClientRect .-top)
        item-type (-> e .-target .-dataset .-type)
        event-type (-> e .-type)]
    {:ix ix                         ;; index of item where event occured
     :item-type item-type           ;; type of item where event occured
     :event-type event-type         ;; type of event
     :top top                       ;; top coord of bounding rect
     :bottom bottom                 ;; bottom coord of bounding rect
     :y y                           ;; y coord of cursor
     :mid (/ (- bottom top) 2)}))   ;; middle of bounding rect

(defn should-reorder? [e]
  "`bottom`, `top`, `mid` of hovered el"
  (let [{:keys [dix hix y]} @state
        {:keys [bottom top]} e
        mid (/ (- bottom top) 2)
        hover-y (- y top)]
    (cond
      (and (< dix hix) (< hover-y mid)) true
      (and (> dix hix) (> hover-y mid)) true
      :else false)))

;; --------------------------
;; EVENTS:

(defn on-event [e]
  (put! dnd-chan (parse-event e)))

;; ---------------------------
;; HANDLERS

(defn handle-drag-start [e]
  (let [{:keys [ix item-type]} e]
    (do
      (update-state! :dix ix)
      (update-state! :drag-type item-type))))

(defn handle-drag [e]
  (update-state! :y (:y e)))

(defn handle-drag-enter [e]
  (let [{:keys [ix item-type]} e
        {:keys [drag-type]} @state]
    (update-state! :hix ix)
    (if-not (:msg-added @state)
      (if (some #(= drag-type %) dnd-types)
        (do
          (dispatch [:add_msg drag-type ix])
          (update-state! :msg-added true))))))

; (defn handle-drag-leave [e]
;   (let [{:keys [dix hix]} @state
;         {:keys [ix]} e]))

(defn handle-drag-over [e]
  (let [{:keys [dix]} @state    ;; index of dragged item
        {:keys [ix]} e]         ;; index of hovered item
    (if (should-reorder? e)
      (do
        (update-state! :dix ix)
        (update-state! :hix dix)
        (dispatch [:reorder_msg dix ix])))))

(defn handle-dragend [e]
  (do
    (update-state! :msg-added false)
    (update-state! :dix nil)))

(defn handle-drop [e]
  "nimp")

(defn listen! []
  (events/listen js/window EventType.DRAGSTART on-event)
  ; (events/listen js/window EventType.DRAGOVER  on-event)
  (events/listen js/window EventType.DRAGEND   on-event)
  (events/listen js/window EventType.DROP      on-event)
  (events/listen js/window EventType.DRAG      on-event)
  ; (events/listen js/window EventType.DRAGENTER on-event)
  ; (events/listen js/window EventType.DRAGLEAVE on-event)

  (go-loop []
     (let [e (<! dnd-chan)
           {:keys [event-type]} e]
        (condp = event-type
          "dragenter" (handle-drag-enter e)
          "dragleave" (handle-drag-leave e)
          "dragstart" (handle-drag-start e)
          "dragover"  (handle-drag-over e)
          "drag"      (handle-drag e)
          "drop"      (handle-drop e)
          "dragend"   (handle-dragend e))
       (recur))))

(listen!)
