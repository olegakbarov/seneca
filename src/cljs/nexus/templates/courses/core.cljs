
(ns nexus.templates.courses.core
  (:require
    [reagent.core :as r]
    [nexus.routes :as routes]
    [nexus.common :as common]
    [nexus.templates.header :refer [header]]
    [re-frame.core :refer [reg-event-db
                           dispatch
                           subscribe]]))


(defn add-course []
  [:div
    {:on-click #(dispatch [:course/create])}
    [:h1 "Add course"]])

(defn course-widget [item]
  (fn []
    [:div (:title item)]))

(defn courses-templ []
  (fn []
    (let [courses (subscribe [:courses])]
       [:div
         [header]
         [:div.content
          [:div.bots_page_container
            [:div.bots_page_title "Courses"]
            [add-course]
            [:div.bots_page_wrapper
              (if (> (count @courses) 0)
                  (map-indexed
                    (fn [ix item]
                       ^{:key ix}
                       [course-widget item])
                   (vals @courses)))]]]])))

(defn courses
  []
  (r/create-class
   {:component-did-mount #(dispatch [:courses-fetch])
    :display-name  "courses-container"
    :reagent-render
     (fn []
      [courses-templ])}))
