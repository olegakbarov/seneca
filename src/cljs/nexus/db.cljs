
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

     :courses {}})
