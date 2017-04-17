
(ns nexus.views
    (:require [re-frame.core :refer [subscribe]]
              [reagent.core :as r]
              [nexus.routes :refer [context-url href]]
              [nexus.templates.editor.core :refer [editor]]
              [nexus.templates.profile :refer [profile]]
              [nexus.templates.auth.signup :refer [signup]]
              [nexus.templates.auth.login :refer [login]]
              [nexus.templates.notfound :refer [notfound]]))

(defmulti panels identity)
(defmethod panels :editor     [] [editor])
(defmethod panels :profile    [] [profile])
(defmethod panels :signup     [] [signup])
(defmethod panels :login      [] [login])
(defmethod panels :default    [] [notfound])

(defn main-panel []
  (let [active-panel (subscribe [:active-panel])]
    (js/console.log "[VIEWS] changing panel: " @active-panel)
    (fn []
      [:div
        (panels @active-panel)])))
