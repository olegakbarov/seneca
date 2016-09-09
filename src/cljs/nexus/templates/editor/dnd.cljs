
(ns nexus.templates.editor.dnd
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [reagent.core :as r]
    [cljs.core.async :refer [<! put! chan timeout]]
    [nexus.helpers.core :refer [log]]
    [goog.dom :as g-dom]
    [goog.array :as g-array]
    [goog.events :as events])
  (:import [goog.events EventType]))

; (defn get-element [id] (. js/document (getElementById (name id))))
;
; (defn set-cursor! [cursor-type]
;   (aset js/document "body" "style" "cursor" (name cursor-type)))
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
(def dnd-chan (chan))

(def drag-id (r/atom nil))
(def draggable (r/atom {:y nil}))


(defn listen! []
  (go-loop []
     (let [y (<! dnd-chan)]
       (log  y)
       (recur))))

(listen!)

(defn mouse-move-handler []
  (fn [e]
    (log (str "el: " (.-top (.getBoundingClientRect (.-target e)))))
    (log (str "pr: " (.-top (.getBoundingClientRect (.-parentElement (.-target e))))))))
    ; (log (.getBoundingClientRect (.-target e)))))
    ; (let [y (.-clientY e)]
    ;   (put! dnd-chan y))))

(defn mouse-up-handler [mouse-move]
  (fn [e]
    (log "mouse up fired")
    ; (.preventDefault e)
    (reset! drag-id nil)
    ; (set-cursor! "default")
    (events/unlisten js/window EventType.MOUSEMOVE
                     mouse-move)))


(defn on-drag-start [e]
  (.preventDefault e)
  ;; TODO set img as draggable here
  (let [mouse-move (mouse-move-handler)]
    (events/listen js/window EventType.MOUSEMOVE
                   mouse-move)
    (events/listen js/window EventType.MOUSEUP
                   (mouse-up-handler mouse-move))
    ; (set-cursor! "move")
    (let [id (.-id (.-dataset (.-currentTarget e)))]
      (reset! drag-id id))))
