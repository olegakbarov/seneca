
(ns nexus.db)

(declare initial-state)
(declare state)

(def initial-state {})

(def state
    {:router {:current ""
              :redirect nil} ;; store next location

     :ui {:scroll-top 0
          :is-editing-id nil
          :curr-thread nil
          :msgs {:hidden #{}
                 :deps {}
                 :active #{}}}

     :form {:email ""
            :password ""
            :password-again ""}

     :auth {:token nil}

     :user {:profile {:user-id "123"
                      :email "email@adress.com"
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
                        [{:uid "123"
                          :text "In this great future we cant forget the past ... In this great future we cant forget the past .. In this great future we cant forget the past "
                          :type "text-message"}

                         {:uid "456"
                          :text "Normkek"
                          :type "quick-reply"
                          :payload [{:text "Left"
                                     :next "780"}
                                    {:text "Right"
                                     :next "012"}]}

                         {:uid "780"
                          :text "This is hidden in thread"
                          :type "text-message"}

                         {:uid "012"
                          :text "Message with id :4"
                          :type "quick-reply"
                          :payload [{:text "Good"
                                     :payload nil}
                                    {:text "Evil"
                                     :payload nil}]}]}

                    ;; -----------------------------------------------------------------------------------------------------------------------------------------------------

                    "day@ciu6ymswc03453503n3wsp"
                      {:uid "day@ciu6ymswc03453503n3wsp"
                       :order 1
                       :messages
                         [{:uid "msgs@1" :order 0 :text "Normkek" :type "quick-reply" :payload [{:text "Quick" :next nil} {:text "Reply" :next nil}]}
                          {:uid "msgs@2" :order 1 :text "Topkek" :type "quick-reply" :payload [{:text "Forward" :next nil} {:text "Back" :next nil}]}
                          {:uid "msgs@3" :order 2 :text "Alien" :type "text-message"}]}

                    "day@234sdf123sdf23"
                      {:uid "day@234sdf123sdf23"
                       :order 2
                       :messages []}}}}})
