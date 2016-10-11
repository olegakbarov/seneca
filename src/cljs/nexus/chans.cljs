
(ns nexus.chans
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [reagent.core :as r]
    [cljs.core.async :refer [<! put! chan timeout]]
    [goog.events :as events]
    [goog.events.EventType :as EventType]
    [goog.dom :as dom]))

;; write to cursor maybe?

(def cur-scroll-y (r/atom 0))
(def prev-scroll-y (r/atom 0))

(defn- get-scroll []
  (-> (dom/getDocumentScroll) (.-y)))

(defn- events->chan [el event-type c]
  (events/listen el event-type #(put! c %))
  c)

(defn scroll-chan []
  (events->chan js/window EventType/SCROLL (chan 1 (map get-scroll))))

; (defn resize-chan []
;   (events->chan js/window EventType/RESIZE(chan 1)))
