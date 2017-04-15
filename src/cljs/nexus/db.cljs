
(ns nexus.db)

(defonce state
    {:router {:current :login
              :redirect nil} ;; store next location

     :auth {:token nil}

     :ui {:scroll-top 0
          :is-editing-id nil
          :curr-thread nil
          :msgs {:hidden #{}
                 :deps {}
                 :active #{}}}

     :form {:email ""
            :password ""
            :password-again ""}


     :user {:profile {:user-id "123"
                      :email "email@adress.com"
                      :userpic "http://s3.azazozon.com"}}

     :bots {}

     :editor {:ui {:days-folded     false
                   :topbar-folded   false
                   :saved           false}}

     :curr-course ""
     :curr-day ""

     :courses {}})
