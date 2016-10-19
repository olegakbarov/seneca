
(ns nexus.common
  (:require
    [reagent.core :as r]))

(defn button [text color size action]
  [:button
    {:class (str "btn-" color " " size)
     :on-click #(action)}
    text])

(defn- input [value type placeholder]
  [:input {:type type
           :value @value
           :class "text_input"
           :placeholder placeholder
           :on-change #(reset! value (-> % .-target .-value))}])

(defn text-input [type placeholder]
  (let [val (r/atom "")]
    (fn []
       [input val type placeholder])))
