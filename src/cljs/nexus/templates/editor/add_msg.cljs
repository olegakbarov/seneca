; 
; (ns nexus.templates.editor.add_msg
;   (:require
;     [reagent.core :as r]
;     [re-frame.core :refer [dispatch]]))
;
; (def is_editable (r/atom false))
; (def title (r/atom ""))
;
; (defn toggle-edit []
;   (reset! is_editable (not @is_editable)))
;
; (defn save-msg []
;   (toggle-edit)
;   (dispatch [:add_msg @title])
;   (reset! title ""))
;
; (defn add-msg []
;   (if @is_editable
;     [:div.msgs_add_msg
;       [:input {:type "text"
;                :value @title
;                :on-change #(reset! title (-> % .-target .-value))}]
;       [:input {:type "button"
;                :value "submit"
;                :on-click save-msg}]]
;     [:div.msgs_add_msg
;       [:input {:type "button"
;                :value "+"
;                :on-click toggle-edit}]]))
