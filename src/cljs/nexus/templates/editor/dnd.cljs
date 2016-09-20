
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
  (cond = (-> e .-type)
    "dragend" (.preventDefault e)
    "drop" (.preventDefault e)
    "dragleave" (.preventDefault e))

  (let [
        ix (-> e .-target .-dataset .-index int)
        bottom (-> e .-target .getBoundingClientRect .-bottom)
        top (-> e .-target .getBoundingClientRect .-top)
        item-type (-> e .-target .-dataset .-type)
        event-type (-> e .-type)]
    {:ix ix                 ;; index of item where event occured
     :item-type item-type   ;; type of item where event occured
     :event-type event-type ;; type of event
     :top top
     :bottom bottom
     :mid (/ (- bottom top) 2)}))

(defn should-reorder? [e]
  "`bottom`, `top`, `mid` of hovered el"
  (let [{:keys [dix hix y]} @state
        {:keys [bottom top]} e
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

;; ---------------------------
;; HANDLERS

(defn handle-drag-start [e]
  (let [{:keys [ix item-type]} e]
    (if (= item-type "msg")
      (update-state! :dix ix))))
    ; (if (some #(= item-type %) dnd-types)
    ;   (update-state! :item-type item-type))))

(defn handle-drag [e]
  (update-state! :y (.-clientY e)))

;; set dix
(defn handle-drag-enter [e]
  (let [{:keys [ix item-type]} e]
    (log "ON DRAG ENTER DIX")
    (log ix)
    (log item-type)

    (update-state! :dix ix)

    ;; when dont add:
    ;; if we drag msg
    ;; if 'msg added' and type of draggable el != msg

    (if-not (and (= (not item-type "msg")) (:msg-added @state))
      (do
        (dispatch [:add_msg "kek" ix])
        (update-state! :msg-added true)))))

;; set dix
(defn handle-drag-leave [e]
  (let [{:keys [dix hix]} @state
        {:keys [ix]} e]
    (prn (:msg-added @state))))
    ; (if (:msg-added @state)
    ;   (do
    ;     (dispatch [:remove_msg ix])
    ;     (update-state! :dix nil)
    ;     (update-state! :msg-added false)))))

;; reorder
(defn handle-drag-over [e]
  ; (log e)
  (let [{:keys [dix]} @state   ;; drag index
        {:keys [ix]} e]         ;; hover index
    (if (should-reorder? e)
      (do
        ; (update-state! :hix hix)
        ; (update-state! :dix dix)
        (dispatch [:reorder_msg dix ix])))))

;; delete from list
(defn handle-dragend [e]
  (log @state)
  (do
    (update-state! :msg-added false)))
    ; (update-state! :dix nil)))

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
        ; (prn event-type)
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
