;; from here
;; https://gist.github.com/owenrh/8baeb98c5081ea648381

;; this is not a windowed debounce, it is a paused debounce
;; (the timer is reset on each non-timer value)
(defn debounce [ms somefunc]
  (let [in (chan)
        out (chan)]
    ; debounce in channel - based on https://gist.github.com/scttnlsn/9744501
    (go-loop [last-val nil]
      (let [val (if (nil? last-val) (<! in) last-val)
            timer (timeout ms)
            [new-val ch] (alts! [in timer])]
        (condp = ch
          timer (do (>! out val) (recur nil))
          in (if new-val (recur new-val) (close! out)))))

    ; call debounced function on the given function/handler
    (go-loop []
       (let [val (<! out)]
         (somefunc val)
         (recur)))

    ;return in event channel
    in))

(def in-channel (debounce 300 #(println %)))
