
(ns nexus.routes
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [re-frame.core :as re-frame]

            [nexus.templates.home :refer [home]]
            [nexus.templates.editor :refer [editor]]))


(def routes ["/" {""       :home
                  "editor" :editor}])

(defn- parse-url [url]
  (bidi/match-route routes url))

(defn- dispatch-route [route]
    (re-frame/dispatch [route]))

(defn app-routes []
  (pushy/start! (pushy/pushy dispatch-route parse-url)))

(def url-for (partial bidi/path-for routes))

(defmulti panels identity)
(defmethod panels :kek [] [home])
(defmethod panels :home [] [editor])
(defmethod panels :default [] [:div])

(defn main-route []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      (panels @active-panel))))
