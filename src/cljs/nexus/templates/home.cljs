
(ns nexus.templates.home
    (:require [nexus.routes :as routes]))

(defn home []
  (fn []
     [:div
       [:h1 "HOME PAGE"]
       [:div [:a {:href (routes/url-for :editor)} "go to About Page"]]]))
