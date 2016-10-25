
(ns nexus.core
  (:require [reagent.core :as r]
            [re-frame.core :as re-frame]
            [devtools.core :as devtools]
            ;; must require both in root file
            [nexus.handlers]
            [nexus.subs]

            [nexus.routes :as routes]
            [nexus.views :as views]
            [nexus.config :as config]))
            ; [externs.textarea]))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")
    (devtools/install!)))

(defn mount-root []
  (r/render [views/main-panel]
    (.getElementById js/document "container")))

(defn ^:export init []
  ; (mount/start)
  (routes/app-routes)
  (re-frame/dispatch-sync [:initialize-db])
  (dev-setup)
  (mount-root))

; (js/console.log js/Textarea)
