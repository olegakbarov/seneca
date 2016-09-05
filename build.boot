(set-env!
 :source-paths    #{"src/cljs" "less"}
 :resource-paths  #{"resources"}
 ; :libs            #{"externs.js"} ???
 :dependencies '[[adzerk/boot-cljs          "1.7.228-1"  :scope "test"]
                 [adzerk/boot-cljs-repl     "0.3.0"      :scope "test"]
                 [adzerk/boot-reload        "0.4.8"      :scope "test"]
                 [pandeiro/boot-http        "0.7.2"      :scope "test"]
                 [com.cemerick/piggieback   "0.2.1"      :scope "test"]
                 [org.clojure/tools.nrepl   "0.2.12"     :scope "test"]
                 [weasel                    "0.7.0"      :scope "test"]
                 [org.clojure/clojurescript "1.7.228"]

                 [org.clojure/core.async "0.1.346.0-17112a-alpha"]
                 [cljs-http "0.1.39"]

                ;  [ring/ring-core "1.4.0"]
                ;  [ring/ring-jetty-adapter "1.4.0"]

                 [bidi "2.0.10"]
                 [kibu/pushy "0.3.6"]

                 [binaryage/devtools "0.6.1"]

                 [re-frame "0.8.0"]
                 [re-com "0.8.3"]

                 [reagent "0.6.0-rc"]
                 [reagent-utils "0.2.0"]
                 [deraen/boot-less "0.2.1" :scope "test"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[pandeiro.boot-http    :refer [serve]]
 '[deraen.boot-less      :refer [less]])

(deftask build []
  (comp
    (cljs)
    (less)
    (sift   :move {#"less.css"          "css/less.css"
                   #"less.main.css.map" "css/less.main.css.map"})))

(deftask run []
  (comp (serve)
        (watch)
        (cljs-repl)
        (reload)
        (build)))

(deftask production []
  (task-options! cljs {:optimizations :advanced}
                      less   {:compression true})
  identity)

(deftask development []
  (task-options! cljs   {:optimizations :none :source-map true}
                 reload {:on-jsload 'nexus.core/init}
                 less   {:source-map  true})
  identity)

(deftask dev
  "Simple alias to run application in development mode"
  []
  (comp (development)
        (run)))
