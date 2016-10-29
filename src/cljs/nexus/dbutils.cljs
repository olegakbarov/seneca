(ns nexus.dbutils)

(defn threads->frames [coll]
  "Packages up flat vector of maps into groups
   each ended with item with :button key"
  (let [tmp (atom {:frames []})
        index (atom 0)]
    (reduce
     (fn [acc item]
       (if (contains? item :buttons)
         (do
           (swap! index inc)
           (swap! tmp update-in [:frames] conj item)
           (let [new-acc (conj acc @tmp)]
             (reset! tmp {:frames []})
             new-acc))
         (do
           (swap! index inc)
           (swap! tmp update-in [:frames] conj item)
           (if (= @index (count coll))
             (conj acc @tmp)
             acc))))
     []
     coll)))
