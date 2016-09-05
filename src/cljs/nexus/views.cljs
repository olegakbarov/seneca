
(ns nexus.views
    (:require [re-frame.core :as re-frame]
              [nexus.routes :as routes]
              [nexus.templates.header :refer [header]]
              [nexus.templates.editor :refer [editor]]
              [re-com.core :as re-com]))


; (defn title []
;   (let [name (re-frame/subscribe [:name])]
;     (fn []
;       [re-com/title
;        :label (str "Hello from " @name)
;        :level :level1])))
;
; (defn main-panel []
;   (fn []
;     [re-com/v-box
;      :height "100%"
;      :children [[title]]]))

(defn home []
  (fn []
   [:div
     [:h1 "HOME PAGE"]
     [:div [:a {:href (routes/url-for :editor)} "go to About Page"]]]))

(defmulti panels identity)
(defmethod panels :home [] [home])
(defmethod panels :editor [] [editor])
(defmethod panels :default [] [:div])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [:div
        [header]
        (panels @active-panel)])))
