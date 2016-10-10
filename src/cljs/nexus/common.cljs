
(ns nexus.common
  (:require
    [reagent.core :as r]))

(defn button [text color action]
  [:button
    {:class (str "btn-" color)
     :on-click #(action)}
    text])
