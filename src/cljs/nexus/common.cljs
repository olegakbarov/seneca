
(ns nexus.common
  (:require
    [reagent.core :as r]))

(defn button [text color size action]
  [:button
    {:class (str "btn-" color " " size)
     :on-click #(action)}
    text])

(defn- input [value type size placeholder]
  [:input {:type type
           :value @value
           :class (str "text_input " size)
           :placeholder placeholder
           :on-change #(reset! value (-> % .-target .-value))}])

(defn text-input [type placeholder]
  (let [val (r/atom "")]
    (fn []
       [input val type placeholder])))
