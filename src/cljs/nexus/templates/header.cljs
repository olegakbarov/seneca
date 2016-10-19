
(ns nexus.templates.header
  (:require
    [nexus.routes :as routes]
    [re-frame.core :refer [dispatch
                           dispatch-sync
                           subscribe]]))

(defn header []
  (fn []
    [:div.header
      [:div.header_wrapper
        [:div.header_left
          [:a.header_logo {:href (routes/url-for :home)}]
          [:ul.header_crumbs
            [:li
              [:a {:href (routes/url-for :bots)} "My Bots"]]
            [:li
              [:a {:href (routes/url-for :editor)} "Weather bot"]]
            [:li
              [:a {:href (routes/url-for :signup)} "Signup"]]]]
        [:div.header_right
          [:div.btn.header_save_button
            "Save"]
          [:div.btn.header_test_button
            {:on-click #(dispatch [:show_state])}
            "Test"]
          [:div.header_userpic]
          [:a {:href (routes/url-for :profile)}
             "Oleg Akbarov"]]]]))
