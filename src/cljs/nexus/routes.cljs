
(ns nexus.routes
  (:require [bidi.bidi :as bidi]
            [nexus.templates.editor :refer [editor]]
            [nexus.templates.home :refer [home]]
            [pushy.core :as pushy]
            [re-frame.core :as re-frame]))

(def routes ["/" {""       home
                  "editor" editor}])

(defn- parse-url [url]
  (bidi/match-route routes url))

(defn- dispatch-route [route]
    (re-frame/dispatch [route]))

(defn app-routes []
  (pushy/start! (pushy/pushy dispatch-route parse-url)))

(def url-for (partial bidi/path-for routes))
