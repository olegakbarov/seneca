
(ns nexus.templates.login
  (:require [nexus.routes :as routes]))

(defn profile []
  (fn []
     [:div
       [:h1 "LOGIN"]
       [:div [:a {:href (routes/url-for :home )} "Auth"]]]))
