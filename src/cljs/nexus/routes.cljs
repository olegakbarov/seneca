
(ns nexus.routes
  ; (:require [bidi.bidi :as bidi]
  ;           [pushy.core :as pushy])
  (:require [secretary.core :as secretary :refer-macros [defroute]]
            [re-frame.core :refer [subscribe dispatch]]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [accountant.core :as accountant])
  (:import goog.History))

(defn logged-in? []
  @(subscribe [:auth/token]))

(defn redirect-to
  [resource]
  (secretary/dispatch! resource)
  (.setToken (History.) resource))

(defn run-events [events]
  (doseq [event events]
    (if (logged-in?)
      (dispatch event)
      (dispatch [:add-login-event event]))))

(defn context-url [url]
  (str js/context url))

(defn href [url]
  {:href (str js/context url)})

(defn navigate! [url]
  (accountant/navigate! (context-url url)))

(defn home-page-events [& events]
  (.scrollTo js/window 0 0)
  (run-events (into
                [
                ;  [:load-tags]
                 [:set-active-page :home]]
                events)))

; (secretary/set-config! :prefix "#")

(secretary/defroute "/bots" []
  (dispatch [:set-active-panel :bots]))

(secretary/defroute "/editor" []
  (dispatch [:set-active-panel :editor]))

(secretary/defroute "/editor/:course-id" {:as params}
  (dispatch [:set-active-panel :editor params]))

; (secretary/defroute "*" []
;   (redirect-to "/notfound"))

(defn hook-browser-navigation! []
  (doto (History.)
    (events/listen
      HistoryEventType/NAVIGATE
      (fn [event]
        (secretary/dispatch! (.-token event))))
    (.setEnabled true))
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!))
