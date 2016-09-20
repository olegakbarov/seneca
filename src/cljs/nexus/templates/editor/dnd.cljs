
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
                    :y nil             ;; user;'s clientY
                    :msg-added false   ;; msg already added?
                    :item-type nil}))  ;; type of item we drag

(defn update-state! [key val]
  (swap! state assoc key val))

;; --------------------------
;; WTF:

;; reorder
;; add on dragendeter (wrapper)
;; remove on dragleave
;; sort on dragenter (item)

;; --------------------------
;; HELPERS:

(defn parse-event [e]
  ; (log e)
  ; (prn (-> e .-type))
  ; (cond = (-> e .-type)
  ;   "dragenter" (.preventDefault e)
  ;   "dragleave" (.preventDefault e))
  (let [ix (-> e .-target .-dataset .-index int)
        dix (if (nil? (:dix @state)) ix (:dix @state))
        bottom (-> e .-target .getBoundingClientRect .-bottom)
        top (-> e .-target .getBoundingClientRect .-top)
        event-type (-> e .-type)]

    (prn "index from e " ix)

    {:dix dix
     :hix ix
     :top top
     :bottom bottom
     :mid (/ (- bottom top) 2)
     :item-type  (-> e .-target .-dataset .-type)
     :event-type event-type}))

(defn should-reorder? [hover]
  "`bottom`, `top`, `mid` of hovered el"
  (let [{:keys [dix hix y]} @state
        {:keys [bottom top]} hover
        mid (/ (- bottom top) 2)
        hover-y (- y top)]
    (cond
      (and (< dix hix) (< hover-y mid)) true
      (and (> dix hix) (> hover-y mid)) true
      ; (nil? hover) false  ;; ?????
      :else false)))

;; --------------------------
;; EVENTS:

(defn on-event [e]
  (put! dnd-chan (parse-event e)))

;; -----------------------------------
;; HANDLERS

(defn handle-drag-start [e]
  (let [{:keys [dix item-type]} e]
    (if (some #(= item-type %) dnd-types)
      (update-state! :item-type item-type)
      (update-state! :dix dix))))

;; set dix
(defn handle-drag-enter [e]
  (let [{:keys [dix hix]} @state]
    (if (:msg-added @state)
      (do ;; remove message
        (update-state! :msg-added false)
        (dispatch [:remove_msg dix]))
      (do ;; add message
        (update-state! :msg-added true)
        (dispatch [:add_msg dix hix])))))

;; set dix
(defn handle-drag-leave [e]
  (let [{:keys [dix hix]} @state]
    (do
      (update-state! :msg-added false)
      (dispatch [:remove_msg dix]))))

;; delete from list
(defn handle-drag-end [e]
  (do
    (update-state! :msg-added false)
    (update-state! :dix nil)))

(defn handle-drag [e]
  ; (log @state)
  (update-state! :y (.-clientY e)))

;; reorder
(defn handle-drag-over [e]
  ; (log e)
  (let [{:keys [dix hix]} @state]
    (if (:msg-added @state)
      (let [dix (if (nil? dix) hix)
            hix (if (nil? dix) (inc hix))]
          (log @state)
          ; (log hix)
          (when-not (= dix hix)
            (dispatch [:reorder_msg dix hix])))
      (do
        (update-state! :msg-added true)
        (dispatch [:add_msg type dix hix])))))

(defn handle-dragend [e]
  "nimp")

(defn handle-drop [e]
  "nimp")

(defn listen! []
  (events/listen js/window EventType.DRAGSTART on-event)
  (events/listen js/window EventType.DRAGOVER  on-event)
  (events/listen js/window EventType.DRAGEND   on-event)
  (events/listen js/window EventType.DRAG      on-event)
  (events/listen js/window EventType.DRAGENTER on-event)
  (events/listen js/window EventType.DRAGLEAVE on-event)

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
