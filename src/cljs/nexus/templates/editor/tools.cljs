
(ns nexus.templates.editor.tools
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [goog.dom :as dom]
    [reagent.core :as r]
    [nexus.chans :refer [scroll-chan cur-scroll-y prev-scroll-y]]))

(def tools (r/atom {}))
(def normal {:position "relative"})

(defn tools-offset []
  (let [w js/window.innerWidth]
    (cond
      (> w 1340) (/ (- w 1340) 2)
      (< w 1140) (- w 1140)
      (and (< w 1340) (> 1140)) 0)))

(defn wrapper-width []
  (let [w js/window.innerWidth]
    (cond
        (> w 1340) 880
        (< w 1140) 700
        (and (< w 1340) (> 1140)) (- w 440))))

(defn sticky []
 (let [wrapper (.getElementById js/document "editor_messenger_wrapper")]
   (do (set! (.-width (.-style wrapper)) (str (wrapper-width) "px")))
   {:position "fixed" :top "144px" :right (str (tools-offset) "px")}))

;; REUSEABLE
(defn img-placeholder []
 [:ul.item_txt_stripe_wrap
  [:div.item_img_placeholder]
  (doall
    (map-indexed
      (fn [ix, item]
         ^{:key ix}
        [:li.item_txt_stripe])
      (vec (repeat 2 nil))))]) ;; clever hack to obtain keys for each el

(defn media-placeholder []
  ;; TODO icon img
  [:div.item_media_placeholder])

(defn quick-replies [n]
 [:ul.item_qr_wrapper
  (doall
    (map-indexed
      (fn [ix, item]
         ^{:key ix}
        [:li.item_qr
          [:div.item_qr_txt]])
      (vec (repeat n nil))))]) ;; clever hack to obtain keys for each el

(defn text-placeholder [n last]
 [:ul.item_txt_stripe_wrap
  {:class (if last "last" "")}
  (doall
    (map-indexed
      (fn [ix, item]
         ^{:key ix}
        [:li.item_txt_stripe])
      (vec (repeat n nil))))]) ;; clever hack to obtain keys for each el

(defn buttons-placeholder [n]
  [:ul.buttons_wrap
    (doall
      (map-indexed
        (fn [ix, item]
           ^{:key ix}
          [:li.item_btn_placeholder
            [:div.item_btn_txt]])
        (vec (repeat n nil))))]) ;; clever hack to obtain keys for each el

;; TXT MSG
(defn text-message []
  [:div.msg_type_wrapper
    [:div.msg_type_title "Text message"]
    [:div.msg_type_item.msg_item_text
      {:draggable true
       :data-type "text-message"
       :data-action "add"}
     [text-placeholder 3 true]]])

;; BTN TEMPLATE
(defn button-template []
  [:div.msg_type_wrapper
    [:div.msg_type_title "Button template"]
    [:div.msg_type_item
      {:draggable true
       :data-type "button-template"
       :data-action "add"}
     [text-placeholder 2 false]
     [buttons-placeholder 2]]])

(defn quick-reply []
  [:div.msg_type_wrapper
    [:div.msg_type_title "Quick reply"]
    [:div.msg_type_item
      {:draggable true
       :data-type "quick-reply"
       :data-action "add"}
     [text-placeholder 2 true]
     [quick-replies 2]]])

(defn generic-template []
  [:div.msg_type_wrapper
    [:div.msg_type_title "Generic template"]
    [:div.msg_type_item
      {:draggable true
       :data-type "generic-template"
       :data-action "add"}
     [img-placeholder]
     [buttons-placeholder 2]]])

(defn media-attach []
  [:div.msg_type_wrapper
    [:div.msg_type_title "Media attach"]
    [:div.msg_type_item
      {:draggable true
       :data-type "media"
       :data-action "add"}
      [media-placeholder]]])

(defn tools-list []
  [:div#editor_tools_wrapper
    [:div.editor_tools {:style @tools}
      [:div.editor_tools_left
        [text-message]
        [button-template]
        [quick-reply]]
      [:div.editor_tools_right
        [generic-template]
        [media-attach]]]])

(defn listen! []
  (let [chan (scroll-chan)]
    (go-loop []
       (let [y (<! chan)]
         (reset! prev-scroll-y @cur-scroll-y)
         (if (> y 0)
           (do (reset! tools (sticky)))))
          ;  (do (reset! tools normal))))
      (recur))))

(listen!)
