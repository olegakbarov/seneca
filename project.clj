(defproject nexus "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [reagent "0.6.0"]
                 [re-frame "0.8.0"]
                 [compojure "1.5.0"]
                 [yogthos/config "0.8"]
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

                 [binaryage/devtools "0.6.1"]

                 [re-frame "0.8.0"]
                 [reagent "0.6.0-rc"]
                 [reagent-utils "0.2.0"]
                 [deraen/boot-less "0.2.1" :scope "test"]

                 [secretary "1.2.3"]
                 [venantius/accountant "0.1.7"]]

  :plugins [[lein-cljsbuild "1.1.4"]
            [lein-less "1.7.5"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :clean-targets ^{:protect false} ["resources/public/js/compiled" "target"]

  :figwheel {:css-dirs ["resources/public/css"]
             :ring-handler nexus.handler/dev-handler}

  :less {:source-paths ["less"]
         :target-path  "resources/static/css"
         :source-map    true}

  :profiles
  {:dev
   {:dependencies [[binaryage/devtools "0.8.2"]]

    :plugins      [[lein-figwheel "0.5.7"]
                   [deraen/lein-less4j "0.2.1"]]

    :less  {:compression true
            :target-path "resources/static/css"}}}

  :cljsbuild
  {:builds
   [{:id           "dev"
     :source-paths ["src/cljs"]
     :figwheel     {:on-jsload "nexus.core/mount-root"}
     :compiler     {:main                 nexus.core
                    :output-to            "resources/public/js/compiled/app.js"
                    :output-dir           "resources/public/js/compiled/out"
                    :asset-path           "js/compiled/out"
                    :source-map-timestamp true
                    :preloads             [devtools.preload]
                    :external-config      {:devtools/config {:features-to-install :all}}}}


    {:id           "min"
     :source-paths ["src/cljs"]
     :jar true
     :compiler     {:main            nexus.core
                    :output-to       "resources/static/js/main.js"
                    :optimizations   :advanced
                    :closure-defines {goog.DEBUG false}
                    :pretty-print    false}}]}




  :main nexus.server

  :aot [nexus.server]

  :uberjar-name "nexus.jar"

  :prep-tasks [["cljsbuild" "once" "min"]["less4j" "once"] "compile"])
