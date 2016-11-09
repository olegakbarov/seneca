
(ns nexus.core
  (:require [reagent.core :as r]
            [re-frame.core :as re-frame]
            [re-frame.core :refer [dispatch-sync
                                   subscribe]]
            [devtools.core :as devtools]
            ;; must require both in root file
            [nexus.handlers]
            [nexus.subs]

            [nexus.routes :as routes]
            [nexus.views :as views]
            [nexus.config :as config]
            [npm-packages]))

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
  (dispatch-sync [:initialize-db])
  (routes/app-routes)
  (let [token (subscribe [:auth/token])]
    (when @token
      (js/console.log "GOT TOKEN " @token)))
      ; (re-frame/dispatch-sync [:set-authentication-token token])))
  (dev-setup)
  (mount-root))
