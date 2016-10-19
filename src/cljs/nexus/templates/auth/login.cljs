
(ns nexus.templates.auth.login
  (:require
    [nexus.common :refer [text-input
                          button]]
    [re-frame.core :refer [subscribe]]))

(defn login []
  [:div
    [:div.h1_title "Sign in"]
    [:form.form_wrapper
      [text-input "email" "email"]
      [text-input "password" "Password"]
      [:div.form_submit
        [button "Sign in" "green" "mid" #(js/console.log "Sign in")]]]])
