
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
                 :status "active"}} ;; development, pending, active

     :editor {:ui {:days-folded false
                   :topbar-folded false
                   :saved true}
              :errors {:editor {:type "error" ;; warning etc
                                :text "Can't be blank"}}}

     :curr-course "crs@tiu6ywsic00018012503n3wso"
     :curr-day "day@ciu6ymswc00018012503n3wsf"

     :courses {"crs@tiu6ywsic00018012503n3wso"
                {:title "A Course about Stuff"
                 :uid "crs@tiu6ywsic00018012503n3wso"
                 :subtitle "Subtitle of course about stuff"
                 :days
                   {"day@ciu6ymswc00018012503n3wsf"
                     {:uid "day@ciu6ymswc00018012503n3wsf"
                      :errors 0
                      :messages
                        [{:uid "msg@66666666KKK" :text "It is very unlikely that a <Header> element is going to generate a DOM that is going to look like what a <Content> would generate. Instead of spending time trying to match those two structures, React just re-builds the tree from scratch." :type "text-message"}
                         {:uid "msg@55555555TTTT" :text "Normkek" :type "quick-reply" :buttons [{:text "Quick"} {:text "Reply"}]}
                         {:uid "msg@444444444DDDD" :text "Topkek" :type "button-template" :buttons [{:text "Forward"} {:text "Back"}]}
                         {:uid "msg@33333AAAAA" :text "Alien" :type "text-message"}]}

                    "day@ciu6ymswc03453503n3wsp"
                      {:uid "day@ciu6ymswc03453503n3wsp"
                       :errors 0
                       :messages
                         [{:uid "msg@00000000099999" :text "Normkek" :type "quick-reply" :buttons [{:text "Quick"} {:text "Reply"}]}
                          {:uid "msg@11111111199999" :text "Topkek" :type "button-template" :buttons [{:text "Forward"} {:text "Back"}]}
                          {:uid "msg@222222229999999" :text "Alien" :type "text-message"}]}}}}})
