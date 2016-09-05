
(ns nexus.views
    (:require [re-frame.core :as re-frame]
              [nexus.routes :as routes]
              [nexus.templates.header :as header]))
              ; [re-com.core :as re-com]))


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
   [:div [:a {:href (routes/url-for :editor)} "go to About Page"]]))

(defn editor []
  (fn []
    [:div "This is the About Page."
     [:div [:a {:href (routes/url-for :home)} "go to Home Page"]]]))

(defmulti panels identity)
(defmethod panels :home [] [home])
(defmethod panels :editor [] [editor])
(defmethod panels :default [] [:div])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      (panels @active-panel))))
