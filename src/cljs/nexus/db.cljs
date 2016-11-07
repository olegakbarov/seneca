
(ns nexus.db)

(declare initial-state)
(declare state)

(def initial-state {})

(def state
    {:router {:current ""}

     :ui {:scroll-top 0
          :is-editing-id nil
          :curr-thread nil
          :msgs {:hidden #{}
                 :deps-tree []}}

     :form {:email "default@mail.co"
            :password ""
            :password-again ""}

     :user {:auth    {:token "12ER2319HKJ231SDFMB3"}
            :profile {:user-id "123"
                      :email "yourmum@kek.ru"
                      :userpic "http://s3.azazozon.com"}
            :plan    {:current "platinum"}}

     :team {:name "The Village"
            :users ["uuid-1" "uuid-2"]}

     :bots {}

     :editor {:ui {:days-folded     false
                   :topbar-folded   false
                   :saved           false}

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
                      :order 0
                      :messages
                        {:1 {:uid "msg@1"
                                  :order 0
                                  :text "It is very unlikely that a <Header> element is going to generate a DOM that is going to look like what a <Content> would generate. Instead of spending time trying to match those two structures, React just re-builds the tree from scratch."
                                  :type "text-message"}
                         :2 {:uid "msg@2"
                                  :order 1
                                  :text "Normkek"
                                  :type "quick-reply"
                                  :payload [{:text "Left"
                                             :next :4}
                                            {:text "Right"
                                             :next :3}]}
                         :3 {:uid "msg@3"
                                  :order 2
                                  :text "This is hidden in thread"
                                  :type "text-message"}
                         :4 {:uid "msg@4"
                                  :order 1
                                  :text "Normkek"
                                  :type "quick-reply"
                                  :payload [{:text "Left"}
                                            {:text "Right"}]}}}

                    ;; -----------------------------------------------------------------------------------------------------------------------------------------------------

                    "day@ciu6ymswc03453503n3wsp"
                      {:uid "day@ciu6ymswc03453503n3wsp"
                       :order 1
                       :messages
                         {:21 {:uid :21 :order 0 :text "Normkek" :type "quick-reply" :payload [{:text "Quick"} {:text "Reply"}]}
                          :22 {:uid :22 :order 1 :text "Topkek" :type "quick-reply" :payload [{:text "Forward"} {:text "Back"}]}
                          :23 {:uid :23 :order 2 :text "Alien" :type "text-message"}}}

                    "day@234sdf123sdf23"
                      {:uid "day@234sdf123sdf23"
                       :order 2
                       :messages {}}}}}})
