
(ns nexus.helpers.uids
  (:require [cuid.core :as c]))

(defn gen-uid [type]
  (condp = type
    "msg"     (str "msg@" (c/cuid))
    "day"         (str "day@" (c/cuid))
    "course"      (str "crs@" (c/cuid))
    (prn "WRONG TYPE PASSED TO gen-uid: " type)))
