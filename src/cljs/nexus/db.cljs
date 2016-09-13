
(ns nexus.db
  (:require
    [reagent.core :as r]))

(def initial-state {})

(def state
    {:router {:current ""}

     :ui {:scroll-top 0}

     :user {:auth    {:token "12ER2319HKJ231SDFMB3"}
            :profile {:user-id "123"
                      :email "yourmum@kek.ru"
                      :userpic "http://s3.azazozon.com"}
            :plan    {:current "platinum"}}

     :team {:name "The Village"
            :users ["UUID-1" "UUID-2"]}

     :bots {:UUID {:title "Village bot"
                   :description "woop-woop"
                   :status "pending"}} ;; in development, pending, active

     :editor {:ui {:days-folded false
                   :topbar-folded false
                   :saved true}
              :errors {:editor {:type "error" ;; warning etc
                                :text "Can't be blank"}}}

     :curr-course 123

     :curr-day 1

     :courses {123 {:title "Life hacks kek"
                    :subtitle "ke-ke-ke"
                    :days {1 {:UUID 123123
                              :messages [{:id 11 :order 0 :title "Topkek"}
                                         {:id 22 :order 1 :title "Normkek"}
                                         {:id 33 :order 2 :title "Alien"}
                                         {:id 44 :order 3 :title "Mehkek"}]}}}}})
