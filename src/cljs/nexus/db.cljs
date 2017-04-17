
(ns nexus.db)

(defonce state
    {:router {:current :login
              :redirect nil} ;; store next location

     :auth {:token nil}

     :ui {:scroll-top 0}

     :form {:email ""
            :password ""
            :password-again ""}


     :user {:profile {:user-id ""
                      :email ""
                      :userpic ""}}})

