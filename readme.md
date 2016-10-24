# Nexus


### Why uids everywhere?

React requires unique keys for all list-style items. We can't rely on seq order cause it leads to bugs. So ... uids everywhere.

### State

```clojure

{:router {:current ""}

 :ui {:scroll-top 0}

 :user {:auth    {:token "12ER2319HKJ231SDFMB3"}
        :profile {:user-id "123"
                  :email "yourmum@kek.ru"
                  :userpic "http://s3.azazozon.com"}
        :plan    {:current "platinum"}}

 :team {:name "The Village"
        :users ["uuid-1" "uuid-2"]}

 :bots {123 {:title "Village bot"
             :description "woop-woop"
             :status "active"}} ;; development, pending, active

 :editor {:ui {:days-folded false
               :topbar-folded false
               :saved true}
          :errors {:editor {:type "error" ;; warning etc
                            :text "Can't be blank"}}}

 :curr-day "day@ciu6ymswc00018012503n3wsf"
 :curr-course "crs@tiu6ywsic00018012503n3wso"

 :courses [{:title "A Course about Stuff"
            :uid "crs@tiu6ywsic00018012503n3wso"
            :subtitle "Subtitle of course about stuff"
            :days [{:uid "day@ciu6ymswc00018012503n3wsf"
                    :errors 0
                    :messages [
                                {:uid "msg@ciu6ymswc00018012503n3wsf"
                                 :text "It is very unlikely that a <Header> element is going to generate a DOM that is going to look like what a <Content> would generate. Instead of spending time trying to match those two structures, React just re-builds the tree from scratch." :type "text-message"}
                                {:uid "msg@ciu6ynmzg00078012gdqy9jjt" :text "Normkek" :type "quick-reply" :buttons [{:text "Quick"} {:text "Reply"}]}
                                {:uid "msg@ciu6yo71d00098012dhe2hmpi" :text "Topkek" :type "button-template" :buttons [{:text "Forward"} {:text "Back"}]}
                                {:uid "msg@ciu6yo76300108012lmv63h8q" :text "Alien" :type "text-message"}]}]}]}
```


### Container component pattern

```clojure
;; presentation component
(defn bots-templ []
  (fn []
    (let [bots (subscribe [:my-bots])]
     (if @bots
         ;; iterate over bots
         ;; or show spinner

;; container component
(defn bots
  []
  (r/create-class
   {:component-did-mount #(dispatch [:bots-fetch])
    :display-name  "bots-container"
    :reagent-render
     (fn []
      [bots-templ])}))
```

### Components with Reagent

```
(defn drag-wrapper [opts component]
  (let [{:keys [drag-type]} opts
        decorated-props (merge opts @state)]
    (r/create-class
       {:component-did-mount
         (fn [this]
           (let [node (reagent.dom/dom-node this)]
             (do
               (dispatch [:register-source node]))))
        :render
          (fn []
            ;  (.log js/console (clj->js decorated-props))
             (this-as this
              (r/create-element
                ;; TODO
                "ReactClass"
                ;; we drop-in all the dnd-state into the comp
                (clj->js decorated-props)
                (r/as-element component))))})))
```
