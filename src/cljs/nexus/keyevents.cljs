(ns nexus.keyevents)

(defn on-enter [event action]
  (when (= (.-keyCode event) 13)
    (action)))
