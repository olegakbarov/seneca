
(ns nexus.routes
  (:require [bidi.bidi :as bidi]
            [pushy.core :as pushy]
            [re-frame.core :refer [subscribe dispatch]]))

(defn auth-handler [page]
  (let [token (subscribe [:auth/token])]
    (if @token
      (dispatch [:set-active-panel page])
      (dispatch [:set-active-panel :login]))))

(def routes ["/" {"signup"        :signup
                  "login"         :login
                  "editor"        #(auth-handler :editor)
                  "courses"       #(auth-handler :courses)
                  "profile"       #(auth-handler :profile)
                  "bots"          #(auth-handler :bots)
                  true            :notfound}])

(defn- parse-url [url]
  (bidi/match-route routes url))

(defn- dispatch-route [matched-route]
  (let [panel-name (:handler matched-route)]
    (dispatch [:set-active-panel panel-name])))

(defn app-routes []
  (let [host (aget js/window "location" "host")
        redirect (str host "/login")]
    (pushy/start! (pushy/pushy dispatch-route parse-url))))
    ;; TODO fix pushState
    ; (set! (.-location js/window) redirect)))

(def url-for (partial bidi/path-for routes))
