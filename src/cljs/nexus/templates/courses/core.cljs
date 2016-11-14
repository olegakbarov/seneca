
(ns nexus.templates.courses.core
  (:require
    [reagent.core :as r]
    [nexus.routes :as routes]
    [nexus.common :as common]
    [nexus.templates.header :refer [header]]
    [re-frame.core :refer [reg-event-db
                           dispatch
                           subscribe]]))


; (defn add-course []
;   [:div
;     {:on-click #(dispatch [:add-bot])}
;     "+"])

(defn course-widget [item]
  (fn []
    [:div (:title item)]))

(defn courses-templ []
  (fn []
    (let [courses (subscribe [:courses])]
     (if @courses
       [:div
         [header]
         [:div.content
          [:div.bots_page_container
            [:div.bots_page_title "Courses"]
            [:div.bots_page_wrapper
              (map-indexed
                (fn [ix bot]
                   ^{:key ix}
                   [course-widget item])
               @bots)
              [add-bot]]]]]
       [:div "No bots yet"]))))

(defn courses
  []
  (r/create-class
   {:component-did-mount #(dispatch [:courses-fetch])
    :display-name  "courses-container"
    :reagent-render
     (fn []
      [courses-templ])}))
