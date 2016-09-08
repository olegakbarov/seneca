
(ns nexus.templates.editor.tools)

(defn button-template []
  [:div.msg_type_wrapper
    [:div.msg_type_title "Button template"]
    [:div.msg_type_item
      "item contents"]])

(defn quick-reply []
  [:div.msg_type_wrapper
    [:div.msg_type_title "Quick reply"]
    [:div.msg_type_item
      "Quick reply"]])

(defn image-attach []
  [:div.msg_type_wrapper
    [:div.msg_type_title "Image reply"]
    [:div.msg_type_item
      "Image attach"]])

(defn video-attach []
  [:div.msg_type_wrapper
    [:div.msg_type_title "Video reply"]
    [:div.msg_type_item
      "Video attach"]])

(defn tools-list []
  [:div.editor_tools_wrapper
    [:div.editor_tools
      [button-template]
      [quick-reply]
      [image-attach]
      [video-attach]]])
