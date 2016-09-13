
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


(def dnd-chan (chan))
(def drag-id (r/atom nil))

;; e
;; clientY = 301 // от верха window
;; offsetY = 7   // от верха кликнутого элемента до места клика
;; screenY = 408 // весь экран

;; boundingRect
;; .top = 294 // от верха элемента до window
;; .bottom = // от низа до window
;;

(defn on-drag-start [e]
  (let [id (.-order (.-dataset (.-target e)))
        brect-bottom (.-bottom (.getBoundingClientRect (.-target e)))
        brect-top (.-top (.getBoundingClientRect (.-target e)))
        middle-y (/ (- brect-bottom brect-top) 2)]
      (log brect-top)
      (log brect-bottom)
      (if (> middle-y brect-bottom)
        (do
          (put! dnd-chan {:order (int order) :type :dec})))
      (if (< middle-y brect-bottom)
        (do
          (put! dnd-chan {:order (int order) :type :inc})))))

(defn on-drag-end [e]
  (let [id (.-id (.-dataset (.-target e)))
        brect-bottom (.-bottom (.getBoundingClientRect (.-target e)))
        brect-top (.-top (.getBoundingClientRect (.-target e)))]
      (reset! drag-id nil)))
    ; (put! dnd-chan id)))

(defn listen! []
  ; (events/listen js/window EventType.DRAGSTART on-drag-start)
  (events/listen js/window EventType.DRAG on-drag-start)
  (events/listen js/window EventType.DRAGEND on-drag-end)
  (go-loop []
     (let [e (<! dnd-chan)
           {:keys [order type]} e]
        (prn e)
        (dispatch [:reorder_msg order type])
       (recur))))

(listen!)


; (defn get-element [id] (. js/document (getElementById (name id))))
;
; (defn children
;   [el]
;   (g-array/toArray (g-dom/getChildren el)))
;
; (defn set-visible! [element-id visible?]
;   (aset (get-element element-id) "style" "display" (if visible? "block" "none")))
;
; (defn constrain [v min-v max-v]
;   (cond (< v min-v) min-v
;         (> v max-v) max-v
;         :else v))

; (defn- get-scroll []
;   (-> (dom/getDocumentScroll) (.-y)))

;; MOVE TO CHANNELS
; (def dnd-chan (chan))
;
; (def draggable (r/atom {:y nil}))
;
;
; (defn listen! []
;   (go-loop []
;      (let [y (<! dnd-chan)]
;        (log  y)
;        (recur))))
;
; (listen!)
;
; (defn mouse-move-handler []
;   (fn [e]
    ; (log (str "el: " (.-top (.getBoundingClientRect (.-target e)))))
;     (log (str "pr: " (.-top (.getBoundingClientRect (.-parentElement (.-target e))))))))
;     ; (log (.getBoundingClientRect (.-target e)))))
;     ; (let [y (.-clientY e)]
;       ; (put! dnd-chan y))))
;
; (defn mouse-up-handler [mouse-move]
;   (fn [e]
;     (log "mouse up fired")
;     ; (.preventDefault e)
    ; (reset! drag-id nil)
;     ; (set-cursor! "default")
;     (events/unlisten js/window EventType.MOUSEMOVE
;                      mouse-move)))
;
; ;; DECORATORS
;
; (defn decorated []
;   [:div "im test comp"])
;
; (defn decorate [comp]
;   (fn []
;     (log comp)))
;
;
; (defn on-drag-start [e]
;   (.preventDefault e)
;   ;; TODO set img as draggable here
;   (let [mouse-move (mouse-move-handler)]
;     (events/listen js/window EventType.MOUSEMOVE
;                    mouse-move)
;     (events/listen js/window EventType.MOUSEUP
;                    (mouse-up-handler mouse-move))
;     ; (set-cursor! "move")
;     (let [id (.-id (.-dataset (.-currentTarget e)))]
;       (reset! drag-id id))))


; (defn greeter
;   [name]
;   [:div "Hello: " name])
;
; (defn decorate
;   [& HoCs]
;   (into [:div] HoCs))
;
; ;; markup:
; [decorate [greeter "Spot"] [greeter "Fiddo"]]
