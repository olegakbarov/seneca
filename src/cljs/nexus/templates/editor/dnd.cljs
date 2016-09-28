
(ns nexus.templates.editor.dnd
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [reagent.core :as r]
    [cljs.core.async :refer [<! put! chan timeout]]
    [nexus.helpers.core :refer [log]]
    [goog.events :as events]
    [re-frame.core :refer [dispatch dispatch-sync]])
  (:import [goog.events EventType]))

(defn set-cursor! [cursor-type]
  (aset js/document "body" "style" "cursor" (name cursor-type)))

;; --------------------------
;; STATE:

(def dnd-chan (chan))

(def dnd-types ["text-message"
                "button-template"
                "quick-reply"
                "generic-template"
                "media"])


(def state (r/atom {:dix nil           ;; the item we drag
                    :hix nil           ;; the item we hover on
                    :y nil             ;; user's clientY
                    :mid nil           ;; middle of hovered item
                    :msg-added false   ;; msg already added?
                    :drag-type nil     ;; type of item we drag
                    :drag-mig nil}))   ;; middle of dragged el

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
        tool (-> e .-target .-dataset .-tool)
        event-type (-> e .-type)]
    {:ix ix                                  ;; index of item where event occured
     :item-type item-type                    ;; type of item where event occured
     :tool (if (= "tool" tool) true false)   ;; boolean; indicates if we drag from tools
     :event-type event-type                  ;; type of event
     :top top                                ;; top coord of bounding rect
     :bottom bottom                          ;; bottom coord of bounding rect
     :y y}))                                 ;; y coord of cursor

;; --------------------------
;; EVENTS:

(defn on-event [e]
  (put! dnd-chan (parse-event e)))

;; ---------------------------
;; HANDLERS

(defn handle-drag-start [e]
  (let [{:keys [ix item-type tool bottom top]} e
        mid (/ (- bottom top) 2)]
    (if-not (= "tool" tool)
      (do
        (update-state! :dix ix)
        (update-state! :drag-mid mid)
        (update-state! :drag-type item-type)))))

(defn handle-drag [e]
  (update-state! :y (:y e)))

(defn handle-drag-enter [e]
  "We should compare middle of bounding rect to current mouse position"
  (let [{:keys [ix type item-type bottom top tool]} e
        mid (/ (- bottom top) 2)
        {:keys [drag-type]} @state]
    (do
      (update-state! :mid mid)
      (update-state! :hix ix)
      ;; тут должен быть индекс drop target блядь
      (prn "DRAGENTER " ix)
      (if-not (:msg-added @state)
        (if tool
          (do
            (dispatch [:add_msg drag-type ix])
            (update-state! :msg-added true)))))))

(defn handle-drag-leave [e]
  (let [{:keys [dix hix]} @state
        {:keys [ix]} e]
    (prn "Dragleave NIMP yep")))

(defn should-reorder? [e]
  "`bottom`, `top`, `mid` of hovered el"
  (let [{:keys [mid dix hix y]} @state
        {:keys [bottom top]} e
        hover-y (- y top)]
    ; (prn y top)
    (prn dix hix hover-y mid)
    ;;     2 1 186 93.5
    (cond
      (and (< dix hix) (< hover-y mid)) false
      (and (> dix hix) (> hover-y mid)) false
      :else true)))

(defn handle-drag-over [e]
  (let [{:keys [dix hix]} @state    ;; index of dragged item
        {:keys [ix]} e]             ;; data of hovered item

    ; (prn "DRAGOVER " ix)
    (update-state! :hix ix)
    (if (should-reorder? e)
      (if-not (= dix ix)
        (do
          (dispatch-sync [:reorder_msg dix ix])
          (update-state! :dix ix))))))

(defn handle-dragend [e]
  (do
    (update-state! :msg-added false)
    (update-state! :dix nil)
    (update-state! :hix nil)))

(defn handle-drop [e]
  "nimp")

(defn listen! []
  (events/listen js/window EventType.DRAGSTART on-event)
  (events/listen js/window EventType.DRAG      on-event)
  ; (events/listen js/window EventType.DRAGOVER  on-event)
  ; (events/listen js/window EventType.DRAGENTER on-event)
  ; (events/listen js/window EventType.DRAGLEAVE on-event)
  (events/listen js/window EventType.DRAGEND   on-event)
  (events/listen js/window EventType.DROP      on-event)

  (go-loop []
     (let [e (<! dnd-chan)
           {:keys [event-type]} e]
        ; (log e)
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
