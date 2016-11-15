# Nexus

### TODO

- [ ] add message with quick-reply

- [ ] editable message with quick-reply

- [ ] divide into threads and add overlaying

- [ ] validate saving course

- [ ] map router in db to window.location

- [ ] ? add new course (to db)

- [ ] add profile page

- [ ] fix layout on repaint (editor)

- [ ] don't show `+` when 28 days


### Setup

For JS shinanigans you need `npm >3.0`

`npm i` from `resourses/npm`

### config

```
(js/console.log (getenv "ENV"))
```

Check it for compatibility with JS.

### Messages compose rules

1. First message of the day should be with button (quick-reply or button template)

2. Quick replies or buttons are necessary to have `payload` otherwise they are invalid

3. Messages linked with payload are grouped

### Why uids everywhere?

React requires unique keys for all list-style items. We can't rely on seq order cause it leads to bugs. So ... uids everywhere.

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

```clojure
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
