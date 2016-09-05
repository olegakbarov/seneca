;
; (ns nexus.app
;   (:require
;       [nexus.helpers.core :refer [log]]
;       [reagent.core :as r :refer [atom]]
;       [reagent.session :as session]
;       [goog.events :as events]
;       [goog.history.EventType :as EventType]
;       [nexus.routes :refer [current-page]])
;   (:require-macros [cljs.core.async.macros :refer (go)])
;   (:import goog.history.Html5History
;            goog.Uri))

(ns nexus.core
    (:require [reagent.core :as r]
              [re-frame.core :as re-frame]
              [devtools.core :as devtools]

              ;; must require them in root file
              [nexus.handlers]
              [nexus.subs]

              [nexus.routes :as routes]
              [nexus.config :as config]))

(enable-console-print!)

;;----------------------------------
;; Comps

; (defn header []
;   [:div.header
;    [:a {:href "/"}
;      [:div.header_logo]]])
;
;
; (defn root []
;   [:div
;     [header]
;     [current-page]])


(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")
    (devtools/install!)))

(defn mount-root []
  (r/render [routes/main-route]
    (.getElementById js/document "container")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))
