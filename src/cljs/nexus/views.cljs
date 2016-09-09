
(ns nexus.views
    (:require [re-frame.core :as re-frame]
              [nexus.routes :as routes]
              [nexus.templates.header :refer [header]]
              [nexus.templates.editor.core :refer [editor]]
              [nexus.templates.home :refer [home]]
              [nexus.templates.profile :refer [profile]]
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

(defmulti panels identity)
(defmethod panels :home [] [home])
(defmethod panels :editor [] [editor])
(defmethod panels :profile [] [profile])
(defmethod panels :default [] [:div])

(defn main-panel []
  (let [active-panel (re-frame/subscribe [:active-panel])]
    (fn []
      [:div
        [header]
        [:div.content
          (panels @active-panel)]])))
