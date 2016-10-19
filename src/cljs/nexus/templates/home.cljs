
(ns nexus.templates.home
    (:require [nexus.routes :as routes]
              [nexus.templates.header :refer [header]]))

(defn home []
  (fn []
   [:div
     [header]
     [:div.content]
     [:h1 "HOME PAGE"]
     [:div [:a {:href (routes/url-for :editor)} "go to About Page"]]]))
