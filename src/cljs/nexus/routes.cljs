
(ns nexus.routes
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [re-frame.core :as re-frame]
            [nexus.helpers.core :refer [log]]))

(def routes ["/" {""        :editor
                  "profile" :profile
                  "bots"    :bots
                  "signup"  :signup
                  "login"   :login}])

(defn- parse-url [url]
  (bidi/match-route routes url))

(defn- dispatch-route [matched-route]
  (let [panel-name (:handler matched-route)]
    (re-frame/dispatch [:set-active-panel panel-name])))

(defn app-routes []
  (pushy/start! (pushy/pushy dispatch-route parse-url)))

(def url-for (partial bidi/path-for routes))
