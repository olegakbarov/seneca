
(ns nexus.templates.profile
    (:require [nexus.routes :as routes]))

(defn profile []
  (fn []
     [:div
       [:h1 "profile"]
       [:div [:a {:href (routes/url-for :editor)} "go to About Page"]]]))
