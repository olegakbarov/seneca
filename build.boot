(set-env!
 :source-paths    #{"src/clj" "src/cljs" "less"}
 :resource-paths  #{"src/clj"}
 :dependencies '[[adzerk/boot-cljs           "1.7.228-2"  :scope "test"]
                 [adzerk/boot-cljs-repl      "0.3.0"      :scope "test"]
                 [adzerk/boot-reload         "0.4.8"      :scope "test"]
                 [pandeiro/boot-http         "0.7.3"      :scope "test"]
                 [com.cemerick/piggieback    "0.2.1"      :scope "test"]
                 [org.clojure/tools.nrepl    "0.2.12"     :scope "test"]
                 [weasel                     "0.7.0"      :scope "test"]
                 [org.clojure/clojurescript  "1.9.293"]
                 [org.clojure/core.async     "0.1.346.0-17112a-alpha"]

                 [cljs-http "0.1.39"]
                 [hiccup "1.0.5"]
                 [mount "0.1.10"]
                 [cljs-ajax "0.3.10"]

                 [ring/ring-jetty-adapter "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [metosin/ring-http-response "0.8.0"]
                 [ring "1.4.0"]
                 [compojure "1.4.0"]

                 [cuid "0.1.1"]

                 [bidi "2.0.10"]
                 [kibu/pushy "0.3.6"]

                 [binaryage/devtools "0.6.1"]

                 [re-frame "0.8.0"]
                 [reagent "0.6.0-rc"]
                ;  [reagent "0.6.0-rc" :exclusions [cljsjs/react cljsjs/react-dom]]
                 [reagent-utils "0.2.0"]
                 [deraen/boot-less "0.2.1" :scope "test"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[pandeiro.boot-http    :refer [serve]]
 '[deraen.boot-less      :refer [less]]
 '[clojure.tools.namespace.repl :as repl]
 '[mount.core :as mount])
 ; '[nexus.server :refer [start!]])


(deftask bundle []
  (comp
    (less)
    (sift   :move {#"less.css"          "public/less.css"
                   #"less.main.css.map" "public/less.main.css.map"})))

(deftask development []
 (task-options! cljs   {:optimizations :none :source-map true}
                reload {:on-jsload 'nexus.core/init}
                less   {:source-map  true})
 identity)

;; TODO
; {:foreign-libs [{:file "resources/npm/bundle.js"
;                  :file-min "resources/npm/bundle.min.js"
;                  :provides ["npm-packages"]}]}

(deftask run []
 (comp
   (watch)
   (reload)
   (cljs-repl :ids #{"js/main"})
   (cljs :ids #{"js/main"})
   (less)
   (serve :handler 'nexus.server/wrapped-routes :reload true)
   (sift :move {#"less.css"          "css/less.css"
                #"less.main.css.map" "css/less.main.css.map"})))


(deftask dev
 "Simple alias to run application in development mode"
 []
 (comp (development)
       (run)))


;; ------------------------------------
;; BUILD

(deftask build []
  (comp
    (cljs :optimizations :advanced)
    (less :compression true)
    (sift :move {#"less.css"          "css/less.css"
                 #"less.main.css.map" "css/less.main.css.map"})
    (aot)
    (pom)
    (uber)
    (jar)
    (target)))
