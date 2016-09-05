
(ns nexus.templates.home)

(defn home []
  (fn []
     [:div
       [:h1 "HOME PAGE"]
       [:div [:a {:href (routes/url-for :editor)} "go to About Page"]]]))
