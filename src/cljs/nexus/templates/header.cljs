
(ns nexus.templates.header
  (:require
    [nexus.routes :as routes]))

(defn header []
  (fn []
    [:div.header
      [:div.header_wrapper
        [:div.header_left
          [:a.header_logo {:href (routes/url-for :home)}]
          [:ul.header_crumbs
            [:li
              [:a {:href (routes/url-for :home)} "My Bots"]]
            [:li
              [:a {:href (routes/url-for :home)} "Weather bot"]]]]
        [:div.header_right
          [:div.btn.header_save_button
            "Save"]
          [:div.btn.header_test_button
            "Test"]
          [:div.header_userpic]
          [:a {:href (routes/url-for :profile)}
             "Oleg Akbarov"]]]]))
