
(ns nexus.templates.header
    (:require [nexus.routes :as routes]))

(defn header []
  (fn []
    [:div.header
      [:a.header_logo {:href (routes/url-for :home)}]
      [:ul.header_crumbs
        [:li "My Bots"]
        [:li "Weather Bot"]]
      [:div.header_profile]]))
