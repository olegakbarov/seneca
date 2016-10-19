
(ns nexus.templates.auth.signup
  (:require
    [nexus.common :refer [text-input
                          button]]
    [re-frame.core :refer [subscribe
                           dispatch]]))

(defn- input [value cursor type placeholder]
  [:input {:type type
           :value @value
           :class "text_input"
           :placeholder placeholder
           :on-change #(dispatch [:update-form cursor (-> % .-target .-value)])}])

(defn signup []
  (let [pwd (subscribe [:form/password])
        pwd2 (subscribe [:form/password-again])
        valid (subscribe [:form/passwords-match])]
    [:div
      [:div.h1_title "Create an account"]
      [:div.form_wrapper
        [input pwd :password "password" "Password"]
        [input pwd2 :password-again "password" "Confirm password"]
        [:div.form_submit
          [button "Sign in" "green" "block" #(do
                                               (.preventDefault %)
                                               (dispatch [:show_state]))]]]]))
