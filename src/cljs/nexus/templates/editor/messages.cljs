
(ns nexus.templates.editor.messages
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [reagent.core :as r]
    [cljs.core.async :refer [<! put! chan timeout]]
    [nexus.helpers.core :refer [log]]
    [goog.dom :as g-dom]
    [goog.array :as g-array]
    [goog.events :as events])
  (:import [goog.events EventType]))

;; helpers


(defn get-element [id] (. js/document (getElementById (name id))))

(defn set-cursor! [cursor-type]
  (aset js/document "body" "style" "cursor" (name cursor-type)))

(defn children
  [el]
  (g-array/toArray (g-dom/getChildren el)))

(defn set-visible! [element-id visible?]
  (aset (get-element element-id) "style" "display" (if visible? "block" "none")))

(defn constrain [v min-v max-v]
  (cond (< v min-v) min-v
        (> v max-v) max-v
        :else v))

; (defn- get-scroll []
;   (-> (dom/getDocumentScroll) (.-y)))

;;----------------------------------
;; STATE


;; should come from re-frame atom
(def order (r/atom  [{:id "111" :order 1 :title "Topkek"}
                     {:id "222" :order 2 :title "Normkek"}
                     {:id "333" :order 3 :title "Mehkek"}
                     {:id "444" :order 4 :title "Pfff"}
                     {:id "555" :order 5 :title "zero."}
                     {:id "666" :order 6 :title "No-no-no"}
                     {:id "777" :order 7 :title "XXX"}]))

(def drag-id (r/atom nil))
(def draggable (r/atom {:y nil}))

;; send all events here
(def dnd-chan (chan))

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
    (set-cursor! "default")
    (events/unlisten js/window EventType.MOUSEMOVE
                     mouse-move)))


;;----------------------------------
;; DRAG START


(defn on-drag-start [e]
  (.preventDefault e)
  ;; TODO set img as draggable here
  (let [mouse-move (mouse-move-handler)]
    (events/listen js/window EventType.MOUSEMOVE
                   mouse-move)
    (events/listen js/window EventType.MOUSEUP
                   (mouse-up-handler mouse-move))
    (set-cursor! "move")
    (let [id (.-id (.-dataset (.-currentTarget e)))]
      (reset! drag-id id))))

(defn listen! []
  (go-loop []
     (let [y (<! dnd-chan)]
       (log  y)
       (recur))))

(listen!)

(defn lister [items]
  [:div#msg_wrapper.list_messages
    (doall
      (for [o @order]
        (let [{:keys [id title]} o]
           ^{:key id}
           [:div.list_message
            {:draggable true
             :class (if (= id @drag-id) "msg_dragged" "")
             :data-id id
             :on-drag-start on-drag-start}
            ;  :on-drag-end on-drag-end}
            ;  :on-mouse-down mouse-down-handler}
            title])))])
