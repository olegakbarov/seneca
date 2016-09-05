;; TODO Render server

;
; (ns nexus.core
;   (:require [nexus.handler :refer [handler]]
;             [config.core :refer [env]]
;             ; [mount.core :refer [start-with-args]]
;             [ring.adapter.jetty :refer [run-jetty]])
;   (:gen-class))
;
; (defn -main [& args]
;   (let [port (Integer/parseInt (or (env :port) "3000"))]
;     (run-jetty handler {:port port :join? false})))
;
; (ns nexus.handler
;   (:require [compojure.core :refer [ANY defroutes]]
;             [ring.util.response :refer [resource-response]]
;             [ring.middleware.reload :refer [wrap-reload]]))
;
; (defroutes routes
;   (ANY "*" [] (resource-response "index.html")))
;
; (def dev-handler (-> #'routes wrap-reload))
;
; (def handler routes)
