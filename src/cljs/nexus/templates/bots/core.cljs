(ns nexus.templates.bots.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require
    [reagent.core :as r]
    [cljs.core.async :refer [<! put! chan timeout]]
    [nexus.helpers.core :refer [log]]
    [goog.dom :as g-dom]
    [goog.array :as g-array]
    [goog.events :as events])
  (:import [goog.events EventType]))
