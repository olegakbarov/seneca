
(ns nexus.templates.header
    (:require [nexus.routes :as routes]))

(defn header []
  (fn []
    [:div.header
      [:a {:href (routes/url-for :home)}
        [:div.header_logo]]]))
