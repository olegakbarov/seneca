
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
            [nexus.localstorage :as ls]
            [npm-packages]))

(defn dev-setup []
  (when config/debug?
    (enable-console-print!)
    (println "dev mode")
    (devtools/install!)))


(defn check-for-token! []
  (let [localstorage-imp (ls/new-localstorage-imp)
        token (ls/read localstorage-imp "nadya-token")]
    (if token
      (re-frame/dispatch-sync [:auth/save-token-db token]))))


(defn mount-root []
  (r/render [views/main-panel]
    (.getElementById js/document "container")))


(defn ^:export init []
  ; (mount/start)
  (dispatch-sync [:initialize-db])
  (routes/app-routes)
  (check-for-token!)
  (dev-setup)
  (mount-root))
