# Nexus

### State

```clj

{:user {:auth    {:token "12ER2319HKJ231SDFMB3"}
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

 :courses {:UUID {:title "Life hacks kek"
                  :subtitle "ke-ke-ke"
                  :days [{:order 1 ;; order in course
                          :UUID ;; we need this?
                          :type "button-template"
                          :text "Some txt kk"
                          :buttons {:title "User see this" ;; TODO. ordered map?
                                    :payload "UUID"}}

                         {:order 2
                          :UUID
                          :type "quick-reply"
                          :text "Text for QR"
                          :quick_replies {:title "User see this" ;; TODO. ordered map?
                                          :payload "UUID"}}]}}}
```
