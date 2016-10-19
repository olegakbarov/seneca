
(ns nexus.templates.auth.signup
  (:require
    [nexus.common :refer [text-input
                          button]]))

(defn signup []
  [:div
    [:div.h1_title "Create an account"]
    [:form.form_wrapper
      [text-input "password" "Password"]
      [text-input "password" "Confirm password"]
      [:div.form_submit
        [button "Sign in" "green" "mid" #(js/console.log "Sign in")]]]])
