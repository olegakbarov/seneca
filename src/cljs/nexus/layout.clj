
(ns nexus.layout
  (:require [hiccup.core :refer [html]]
            [hiccup.page :refer [include-js include-css]]))

(def main
  (html
    [:html
     [:head
      [:meta {:charset "utf-8"}]
      [:meta {:http-equiv "X-UA-Compatible" :content "IE=edge"}]
      [:meta {:name "viewport" :content "width=device-width, initial-scale=1.0"}]      [:title "Editor 🤔"]
      (include-css "css/less.css")]
     [:body
      [:div#root]
      (include-js "js/main.js")]]))
