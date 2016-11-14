(ns nexus.conf)

(defmacro getenv [k]
  (System/getenv k))
