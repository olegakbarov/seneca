
(ns nexus.templates.editor.tools
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [reagent.core :as r]
    [nexus.chans :refer [scroll-chan cur-scroll-y prev-scroll-y]]))

(def tools (r/atom {}))
(def normal {:position "relative"})
(def sticky {:position "fixed" :top "150px" :right "60px"})

(defn button-template []
  [:div.msg_type_wrapper
    [:div.msg_type_title "Button template"]
    [:div.msg_type_item
      "button"]])

(defn quick-reply []
  [:div.msg_type_wrapper
    [:div.msg_type_title "Quick reply"]
    [:div.msg_type_item
      "QR"]])

(defn image-attach []
  [:div.msg_type_wrapper
    [:div.msg_type_title "Image reply"]
    [:div.msg_type_item
      "image"]])

(defn video-attach []
  [:div.msg_type_wrapper
    [:div.msg_type_title "Video reply"]
    [:div.msg_type_item
      "vidto"]])

(defn tools-list []
  [:div.editor_tools_wrapper
    [:div.editor_tools {:style @tools}
      [button-template]
      [quick-reply]
      [image-attach]
      [video-attach]]])

(defn listen! []
  (let [chan (scroll-chan)]
    (go-loop []
       (let [y (<! chan)]
         (reset! prev-scroll-y @cur-scroll-y))
        ; TODO stick on scroll
        ;  (if (> y 50)
        ;    (do (reset! tools sticky))
        ;    (do (reset! tools normal))))
      (recur))))

(listen!)
