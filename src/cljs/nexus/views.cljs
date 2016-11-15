
(ns nexus.views
    (:require [nexus.routes :refer [context-url href navigate!]]
              [nexus.templates.bots.core :refer [bots]]
              [nexus.templates.editor.core :refer [editor]]
              [nexus.templates.courses.core :refer [courses]]
              [nexus.templates.profile :refer [profile]]
              [nexus.templates.auth.signup :refer [signup]]
              [nexus.templates.auth.login :refer [login]]
              [nexus.templates.notfound :refer [notfound]]
              [re-frame.core :refer [subscribe]]))

(defmulti panels identity)
(defmethod panels :editor     [] [editor])
(defmethod panels :bots       [] [bots])
(defmethod panels :courses    [] [courses])
(defmethod panels :profile    [] [profile])
(defmethod panels :signup     [] [signup])
(defmethod panels :login      [] [login])
(defmethod panels :notfound   [] [notfound])
(defmethod panels :default    [] [:notfound])

(defn main-panel []
  (let [active-panel (subscribe [:active-panel])]
    (js/console.log "main panel called with " @active-panel)
    (fn []
      [:div
        (panels @active-panel)])))

(defn nav-link [url title page]
  (let [active-page (subscribe [:active-panel])]
    [:a {:href (context-url url) :active (= page @active-page)} title]))

; (defn title []
;   (let [name (re-frame/subscribe [:name])]
;     (fn []
;       [re-com/title
;        :label (str "Hello from " @name)
;        :level :level1])))
