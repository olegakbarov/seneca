
(ns nexus.templates.bots.core
  (:require
    [reagent.core :as r]
    [nexus.routes :as routes]
    [nexus.common :as common]
    [re-frame.core :refer [reg-event-db
                           dispatch
                           subscribe]]))

(defn bot-status [status]
    [:div.bot_page_item_status
      {:class status}])

(defn bot-img [bot]
  (let [{:keys [status path]} bot]
    [:div.bot_page_placeholder
      {:style (if path {:background-image path})}
      [bot-status status]]))

(defn bot-widget [item]
  (let [[id bot] item
        {:keys [title description status path]} bot]
    [:div.bots_page_item
      [bot-img bot]
      [:div.bot_page_meta
        [:div.bot_page_meta_title title]
        (common/button "Test" "white" nil)]]))

(defn add-bot []
  [:div.bot_page_add
    {:on-click #(dispatch [:add-bot])}
    "+"])

(defn bots []
  (fn []
    (let [bots (subscribe [:my-bots])]
      [:div.bots_page_container
        [:div.bots_page_title "Bots"]
        [:div.bots_page_wrapper
          (map-indexed
            (fn [ix bot]
               ^{:key ix}
               [bot-widget bot])
           @bots)
          [add-bot]]])))
