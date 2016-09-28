
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
                    :days {1 {:UUID 123123
                              :messages [{:id 44 :text "It is very unlikely that a <Header> element is going to generate a DOM that is going to look like what a <Content> would generate. Instead of spending time trying to match those two structures, React just re-builds the tree from scratch." :type "text-message"}
                                         {:id 11 :text "Topkek" :type "button-template" :buttons [{:text "Forward"} {:text "Back"}]}
                                         {:id 22 :text "Normkek" :type "quick-reply" :buttons [{:text "Quick"} {:text "Reply"}]}
                                         {:id 33 :text "Alien" :type "text-message"}]}}}}})
