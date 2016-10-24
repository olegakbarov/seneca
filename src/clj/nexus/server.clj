
(ns nexus.server
  (:require [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware
             [file :refer [wrap-file]]
             [resource :refer [wrap-resource]]
             [content-type :refer [wrap-content-type]]
             [defaults :refer [site-defaults wrap-defaults]]
             [not-modified :refer [wrap-not-modified]]
             [reload :refer [wrap-reload]]]
            [nexus.layout :as layout]
            [mount.core :refer [defstate]]
            [compojure.core :refer [routes GET ANY defroutes]]
            [compojure.route :as route])
  (:gen-class)
  (:use [ring.middleware.content-type]))

(defroutes handler-routes
   (ANY "/" [] layout/main)
   (route/resources "/"))
  ;  (ANY "/*" [] layout/main))

(def wrapped-routes
  (-> handler-routes
      (wrap-content-type)))
