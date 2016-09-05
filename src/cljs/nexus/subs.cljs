(ns nexus.subs
  (:require [re-frame.core :as re-frame]
            [nexus.db :as db]))

(re-frame/reg-sub
 :active-panel
 (fn [db _]
   (:active-panel db)))
