(ns nexus.dbutils)

(defn threads->frames [coll]
  "Converts ui-friendly threads to db-friendly frames."
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


(defmulti has-child?
  (fn [item]
    (:type item)))

(defmethod has-child? :default [item] nil)

(defmethod has-child? "text-message" [item]
  true)

(defmethod has-child? "media" [item]
  true)

(defmethod has-child? "generic-template" [item]
  true)

(defmethod has-child? "button-template" [item]
  (let [btns (:buttons item)]
    (reduce
     (fn [res item]
      (if-not res
              res
              (contains? item :payload)))
     true
     btns)))

(defmethod has-child? "quick-reply" [item]
  (let [btns (:buttons item)]
    (reduce
     (fn [res item]
      (if-not res
              res
              (contains? item :payload)))
     true
     btns)))


(defn add-thread-info [m]
  "Adds information about thread by detecting `:next nil`"
  (let [n (atom 0)]
    (mapv
     (fn [[key val]]
       (if (has-child? val)
         (do
           (let [v (assoc val :thread @n)]
             (reset! n (inc @n))
             v))
         (assoc val :thread @n)))
     m)))

;;-----------------------------------
;;-----------------------------------



(defn keywordize-ids [m]
  (reduce
    (fn [acc [key val]]
      (if (keyword? key)
        (assoc acc key val)
        (if (map? val)
            (assoc acc (keyword key) (keywordize-ids val))
            (assoc acc (keyword key) val))))
    {}
    m))


(defn build-tree
  "
  Builds nested vector tree from hashmap

  (map
   (fn [item]
     (build-tree item msgs))
   msgs)
  "
  [[key val] m]
  (if-let [payload (:payload val)]
    [key (mapv
          (fn [k]
            (build-tree [k (get m k)] m))
          payload)]
    key))


(defn vec->set
  "Takes nested vector and returns key-to-set map"
  [tr res]
  (js/console.log tr res)
  (when-not (keyword? tr)
    (let [k (first tr)
          v (peek tr)]
      (if (keyword? v)
        res
        (recur v (assoc res k (-> v
                                  flatten
                                  set)))))))


(defn make-deps-tree
  "Creates a map of 'dependecies' from tree represented as vector"
  [v]
  (let [res {}]
    (first (->> v
                (map #(vec->set % res))
                (remove nil?)))))


(defn shallow-deps
  "Returns a set of shallow dependecies, useful for init render"
  [m]
  (set
    (->> (vals m)
         (filter #(contains? % :payload))
         (map :payload)
         (apply concat))))
