
(ns nexus.templates.profile
    (:require [nexus.routes :as routes]
              [nexus.templates.header :refer [header]]))

(defn profile []
  (fn []
   [:div
     [header]
     [:div.content]
     [:h1 "profile"]
     [:div [:a {:href (routes/url-for :editor)} "go to About Page"]]]))
