
(ns nexus.templates.editor.dnd
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [reagent.core :as r]
    [cljs.core.async :refer [<! put! chan timeout]]
    [goog.events :as events]
    [re-frame.core :refer [dispatch]])
  (:import [goog.events EventType]))

; dragindex:"1"
; dragtype: "MSG_TYPE"
; uid:    "msg@5555555TTTT"
; type:     "quick-reply"
;--
; {:target (-> e .-currentTarget)
;  :uid (-> e .-currentTarget .-dataset .-uid)
;  :drag-index (-> e .-currentTarget .-dataset .-dragindex int)
;  :rect   (-> e .-currentTarget .getBoundingClientRect)})

(def state (r/atom {}))

(def init-state {:drag-index nil
                ;  :drag-mid nil
                 :hover-y nil
                 :hover-index nil
                 :hover-mid nil

                 :adding-type nil
                 :msg-added false})

(defn init! []
  (reset! state init-state))

(init!)

(defn update-state! [key val]
  (do
    (swap! state assoc key val)
    (.log js/console @state)))

(defn on-drag-start [e]
  "Update drag-index"
  (let [drag-index (-> e .-currentTarget .-dataset .-dragindex int)
        top (-> e .-currentTarget .getBoundingClientRect .-top)
        bottom (-> e .-currentTarget .getBoundingClientRect .-bottom)
        mid (/ (- bottom top) 2)]
    (reset! state (merge @state {:drag-index drag-index
                                 :drag-mid mid}))))

(defn on-drag-enter [e]
  "Handles the adding message to thread"
  (let [hover-index (-> e .-currentTarget .-dataset .-dragindex int)
        type (-> e .-currentTarget .-dataset .-dragtype)
        top (-> e .-currentTarget .getBoundingClientRect .-top)
        bottom (-> e .-currentTarget .getBoundingClientRect .-bottom)
        mid (/ (- bottom top) 2)]
      (reset! state (merge @state {:drag-index hover-index}))
      (if-not (:msg-added @state)
        (if-not (= (@state :drag-type) "MSG_TYPE")
          (do
            (dispatch [:add-msg (@state :adding-type) hover-index])
            (reset! state (merge @state {:msg-added true})))))))

(defn should-reorder? []
  (let [{:keys [drag-index hover-index hover-y hover-mid]} @state]
    (if (= drag-index hover-index)
      false
      (cond
        (and (< drag-index hover-index) (< hover-y hover-mid)) false
        (and (> drag-index hover-index) (> hover-y hover-mid)) false
        :else true))))

(defn on-drag-over [e]
  (let [client-y (-> e .-clientY)
        top (-> e .-currentTarget .getBoundingClientRect .-top)
        bottom (-> e .-currentTarget .getBoundingClientRect .-bottom)
        hover-index (-> e .-currentTarget .-dataset .-dragindex int)
        hover-mid (/ (- bottom top) 2)
        hover-y (- client-y top)
        {:keys [drag-index drag-mid]} @state
        mid (min hover-mid drag-mid)]
      (do
        (reset! state (merge @state {:hover-index hover-index
                                     :hover-y hover-y
                                     :hover-mid mid}))
        (if (should-reorder?)
            (do
             (dispatch [:swap-msgs (@state :drag-index) hover-index])
             (reset! state (merge @state {:drag-index hover-index
                                          :hover-index hover-index
                                          :hover-mid mid})))))))

;; TODO wrong behavior
(defn on-drag-end [e]
  (do
    (js/console.log "ON DRAG END")
    (js/console.log @state)
    (init!)
    (js/console.log @state)))
