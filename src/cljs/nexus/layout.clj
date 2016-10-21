
(ns nexus.layout
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]))

(def main
  (html
    [:html
     [:head
      [:meta {:charset "utf-8"}]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1"}]
      [:title "Editor 🤔"]
      (include-css "less.css")]
     [:body
      [:div#container]
      (include-js "app.js")]]))
