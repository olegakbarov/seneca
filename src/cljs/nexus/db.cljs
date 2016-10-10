
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
            :users ["uuid-1" "uuid-2"]}

     :bots {123 {:title "Village bot"
                 :description "woop-woop"
                 :status "active"}} ;; in development, pending, active

     :editor {:ui {:days-folded false
                   :topbar-folded false
                   :saved true}
              :errors {:editor {:type "error" ;; warning etc
                                :text "Can't be blank"}}}

     :curr-course 123

     :curr-day 223457

     :courses {123 {:title "A Course about Stuff"
                    :subtitle "Subtitle of course about stuff"
                    :days {0 {:uuid 123123
                              :errors 0
                              :messages [
                                         {:id 44 :text "It is very unlikely that a <Header> element is going to generate a DOM that is going to look like what a <Content> would generate. Instead of spending time trying to match those two structures, React just re-builds the tree from scratch." :type "text-message"}
                                         {:id 22 :text "Normkek" :type "quick-reply" :buttons [{:text "Quick"} {:text "Reply"}]}
                                         {:id 11 :text "Topkek" :type "button-template" :buttons [{:text "Forward"} {:text "Back"}]}
                                         {:id 33 :text "Alien" :type "text-message"}]}

                           1 {:uuid 2938457
                              :errors 0
                              :messages []}

                           2 {:uuid 223457
                              :errors 1
                              :messages [
                                         {:id 33 :text "Alien" :type "text-message"}
                                         {:id 44 :text "Reduces a collection using a (potentially parallel) reduce-combine strategy. The collection is partitioned into groups of approximately n (default 512), each of which is reduced with reducef (with a seed value obtained by calling (combinef) with no arguments)." :type "text-message"}
                                         {:id 11 :text "Topkek" :type "button-template" :buttons [{:text "Forward"} {:text "Back"}]}
                                         {:id 22 :text "Normkek" :type "quick-reply" :buttons [{:text "Quick"} {:text "Reply"}]}]}
                           3 {:uuid 2239994047
                              :errors 0
                              :messages [
                                         {:id 33 :text "Alien" :type "text-message"}
                                         {:id 11 :text "Topkek" :type "button-template" :buttons [{:text "Forward"} {:text "Back"}]}
                                         {:id 44 :text "Reduces a collection using a (potentially parallel) reduce-combine strategy. The collection is partitioned into groups of approximately n (default 512), each of which is reduced with reducef (with a seed value obtained by calling (combinef) with no arguments)." :type "text-message"}
                                         {:id 22 :text "Normkek" :type "quick-reply" :buttons [{:text "Quick"} {:text "Reply"}]}]}

                           4 {:uuid 733412231131
                              :errors 0
                              :messages []}

                           5 {:uuid 89234093457
                              :errors 0
                              :messages [
                                         {:id 11 :text "Topkek" :type "button-template" :buttons [{:text "Forward"} {:text "Back"}]}
                                         {:id 44 :text "Reduces a collection using a (potentially parallel) reduce-combine strategy. The collection is partitioned into groups of approximately n (default 512), each of which is reduced with reducef (with a seed value obtained by calling (combinef) with no arguments)." :type "text-message"}
                                         {:id 22 :text "Normkek" :type "quick-reply" :buttons [{:text "Quick"} {:text "Reply"}]}]}

                           6 {:uuid 3233494207
                              :errors 0
                              :messages [
                                         {:id 33 :text "Alien" :type "text-message"}
                                         {:id 44 :text "Reduces a collection using a (potentially parallel) reduce-combine strategy. The collection is partitioned into groups of approximately n (default 512), each of which is reduced with reducef (with a seed value obtained by calling (combinef) with no arguments)." :type "text-message"}
                                         {:id 22 :text "Normkek" :type "quick-reply" :buttons [{:text "Quick"} {:text "Reply"}]}]}

                           7 {:uuid 4918322123
                              :errors 0
                              :messages [
                                         {:id 33 :text "Alien" :type "text-message"}
                                         {:id 11 :text "Topkek" :type "button-template" :buttons [{:text "Forward"} {:text "Back"}]}
                                         {:id 22 :text "Normkek" :type "quick-reply" :buttons [{:text "Quick"} {:text "Reply"}]}]}

                           8 {:uuid 324242342251
                              :errors 0
                              :messages [
                                         {:id 33 :text "Alien" :type "text-message"}
                                         {:id 22 :text "Normkek" :type "quick-reply" :buttons [{:text "Quick"} {:text "Reply"}]}]}

                           9 {:uuid 23423493399
                              :errors 3
                              :messages [
                                         {:id 22 :text "Normkek" :type "quick-reply" :buttons [{:text "Quick"} {:text "Reply"}]}
                                         {:id 33 :text "Alien" :type "text-message"}
                                         {:id 11 :text "Topkek" :type "button-template" :buttons [{:text "Forward"} {:text "Back"}]}]}
                           10 {:uuid 4287294823741
                               :errors 0
                               :messages []}}}}})
