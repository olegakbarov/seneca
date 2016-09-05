(ns nexus.handlers
    (:require [re-frame.core :as re-frame]
              [nexus.db :as db]))

(re-frame/reg-event-db
 :initialize-db
 (fn  [_ _]
   db/default-db))
