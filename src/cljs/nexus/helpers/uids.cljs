
(ns nexus.helpers.uids
  (:require [cuid.core :as c]))

(defn gen-uid [type]
  (condp = type
    "msg"     (str "msg_" (c/cuid))
    "day"         (str "day_" (c/cuid))
    "course"      (str "crs_" (c/cuid))
    (prn "WRONG TYPE PASSED TO gen-uid: " type)))
