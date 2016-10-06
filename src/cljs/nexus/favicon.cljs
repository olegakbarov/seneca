
(ns nexus.favicon
  (:refer-clojure :exclude [reset!]))

(defn favicon-link []
 (.querySelector js/document "link[rel='icon']"))

(defn get-clr []
 (last (re-find #"favicon-([^\.]+)\.ico" (.getAttribute (favicon-link) "href"))))

(defn set-clr! [color]
  (.setAttribute (favicon-link) "href" (utils/cdn-path (str "/favicon-" color ".ico?v=29"))))

(defn reset! []
  (set-color! "undefined"))
