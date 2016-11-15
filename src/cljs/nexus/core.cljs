
(ns nexus.core
  (:require-macros [nexus.conf :refer [getenv]])
  (:require [reagent.core :as r]
            [re-frame.core :as re-frame]
            [re-frame.core :refer [dispatch-sync
                                   subscribe]]
            [devtools.core :as devtools]

            ;; must require both in root file
            [nexus.handlers]
            [nexus.subs]
            ;;

            [nexus.config :as config]
            [nexus.routes :as routes]
            [nexus.views :as views]
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
    (.getElementById js/document "root")))

(defn ^:export init []
  ; (mount/start)
  (dispatch-sync [:initialize-db])
  (check-for-token!)
  (routes/hook-browser-navigation!)
  (dev-setup)
  (mount-root))
