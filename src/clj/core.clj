
(ns nexus.core
  (:require [nexus.handler :refer [handler]]
            [config.core :refer [env]]
            ; [mount.core :refer [start-with-args]]
            [compojure.core :refer [ANY defroutes]]
            [ring.util.response :refer [resource-response]]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.adapter.jetty :refer [run-jetty]])
  (:gen-class))

(defn -main [& args]
  (let [port (Integer/parseInt (or (env :port) "3000"))]
    (run-jetty handler {:port port :join? false})))

(defroutes routes
  (ANY "*" [] (resource-response "index.html")))

(def dev-handler (-> #'routes wrap-reload))
