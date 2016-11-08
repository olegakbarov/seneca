
(ns nexus.server
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware
             [file :refer [wrap-file]]
             [resource :refer [wrap-resource]]
             [content-type :refer [wrap-content-type]]
             [defaults :refer [site-defaults wrap-defaults]]
             [not-modified :refer [wrap-not-modified]]
             [reload :refer [wrap-reload]]]
            [ring.util.response :refer [redirect]]
            [ring.util.http-response :refer :all]
            [nexus.layout :as layout]
            [mount.core :refer [defstate]]
            [compojure.core :refer [GET ANY defroutes]]
            [compojure.route :as route])
  (:use [ring.middleware.content-type])
  (:gen-class))

; (defroutes routes
;  (ANY "/" [] layout/main)
;  (route/resources "/")
 ; (ANY "/**" [] layout/main))

(defroutes routes
 (route/resources "/js" {:root "js"})
 (route/resources "/css" {:root "css"})

 (GET "/" []
   ; Use (resource-response "index.html") to serve index.html from classpath
   (-> (ok layout/main) (content-type "text/html")))
 (ANY "/**" [] layout/main))

(def wrapped-routes
  (-> routes
      (wrap-content-type)))

; (defn start! []
;   (run-jetty wrapped-routes {:join? false :port 3000}))
