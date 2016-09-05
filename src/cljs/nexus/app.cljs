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

(ns nexus.app
    (:require [reagent.core :as r]
              [re-frame.core :as re-frame]
              [devtools.core :as devtools]
              ; [nexus-r.handlers]
              ; [nexus-r.subs]
              [nexus.routes :as routes]))
              ; [nexus-r.views :as views]))
              ; [nexus-r.config :as config]))

(enable-console-print!)

;;----------------------------------
;; Comps

(defn header []
  [:div.header
   [:a {:href "/"}
     [:div.header_logo]]])


(defn root []
  [:div
    [header]])
    ; [current-page]])

;;----------------------------------
;; History

; (defn up []
;   (when config/debug?
;     (enable-console-print!)
;     (println "dev mode")
;     (devtools/install!)))

;;----------------------------------
;; Root mount

(defn mount-root []
  (r/render-component [root]
    (.getElementById js/document "container")))

; (defn mount-root []
;   (reagent/render [main-panel]
;                   (.getElementById js/document "app")))

(defn ^:export init []
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  ; (dev-setup)
  (mount-root))
