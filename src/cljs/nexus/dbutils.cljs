
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

; (defn add-thread-info [m]
;   "Adds information about thread by detecting `:next nil`"
;   (let [n (atom 0)]
;     (mapv
;      (fn [[key val]]
;        (if (has-child? val)
;          (do
;            (let [v (assoc val :thread @n)]
;              (reset! n (inc @n))
;              v))
;          (assoc val :thread @n)))
;      m)))

;;-----------------------------------
;;-----------------------------------

;; might not necessary
(defn kwrdze [v]
  (mapv
   (fn [item]
    (if (vector? item)
        (kwrdze item)
        (keyword item)))
   v))


; (defn walk-kv-pair
;   "Recursively walks tree and returns dependecy vector"
;   [[key val] m]
;   (if-let [payload (:payload val)]
;     [key (mapv
;           (fn [p]
;             (if-let [n (:next p)]
;               (walk-kv-pair [n (get m n)] m)
;               key))
;           payload)]
;     key))
;
; (defn build-tree
;   "Builds nested vector tree from hashmap"
;   [m]
;   (->> m
;        (mapv (fn [item] (walk-kv-pair item m)))))
;       ;  kwrdze))


(defn vec->set
  "Takes nested vector and returns key-to-set map"
  [tr res]
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
  ; (js/console.log v)
  (let [res {}]
    (first (->> v
                (map #(vec->set % res))
                (remove nil?)))))


;; TODO: this is hella ugly
(defn shallow-deps
  "Returns a set of shallow dependecies, useful for init render"
  [m]
  (set
    (->> (vals m)
         (filter #(contains? % :payload))
         (map :payload)
         (map flatten)
         flatten
         (map :next)
         (remove nil?)
         (map keyword))))
