
(ns nexus.templates.home)

(defn home []
  [:div [:h2 "HOME PAGE!"]
   [:div [:a {:href "/editor"} "go to editor"]]])
