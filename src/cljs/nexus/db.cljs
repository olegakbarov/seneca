
(ns nexus.db)

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

     :courses {123 {:title "A Course about Stuff"
                    :subtitle "Subtitle of course about stuff"
                    :days {0 {:UUID 123123
                              :messages [{:id 44 :text "It is very unlikely that a <Header> element is going to generate a DOM that is going to look like what a <Content> would generate. Instead of spending time trying to match those two structures, React just re-builds the tree from scratch." :type "text-message"}
                                         {:id 11 :text "Topkek" :type "button-template" :buttons [{:text "Forward"} {:text "Back"}]}
                                         {:id 22 :text "Normkek" :type "quick-reply" :buttons [{:text "Quick"} {:text "Reply"}]}
                                         {:id 33 :text "Alien" :type "text-message"}]}
                            1 {:UUID 2938457
                               :messages [{:id 44 :text "f should be a function of 2 arguments. If val is not supplied, returns the result of applying f to the first 2 items in coll, then applying f to that result and the 3rd item, etc. If coll contains no items, f must accept no arguments as well, and reduce returns theresult of calling f with no arguments. " :type "text-message"}
                                          {:id 11 :text "Beer"    :type "button-template" :buttons [{:text "Forward"} {:text "Back"}]}
                                          {:id 22 :text "Chips" :type "quick-reply" :buttons [{:text "Quick"} {:text "Reply"}]}
                                          {:id 33 :text "Football" :type "text-message"}]}
                            2 {:UUID 2938457
                               :messages [{:id 44 :text "Reduces a collection using a (potentially parallel) reduce-combine strategy. The collection is partitioned into groups of approximately n (default 512), each of which is reduced with reducef (with a seed value obtained by calling (combinef) with no arguments)." :type "text-message"}
                                          {:id 11 :text "Topkek" :type "button-template" :buttons [{:text "Forward"} {:text "Back"}]}
                                          {:id 22 :text "Normkek" :type "quick-reply" :buttons [{:text "Quick"} {:text "Reply"}]}
                                          {:id 33 :text "Alien" :type "text-message"}]}}}}})
