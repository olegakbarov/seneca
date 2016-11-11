
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
