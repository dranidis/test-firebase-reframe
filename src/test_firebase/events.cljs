(ns test-firebase.events
  (:require [re-frame.core :as re-frame]
            [test-firebase.db :as db]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [test-firebase.firebase.fb-reframe :as fb-reframe]
            [test-firebase.firebase.firebase-auth :refer [get-current-user-uid]]))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
            db/default-db))


(re-frame/reg-event-fx
 ::update-value
 (fn-traced [_ [_ path value]]
            {::fb-reframe/firebase-set {:path path
                                        :data value
                                        :success #(println "Success")}}))

(comment
  ;;
  ;; examples of dispatch events
  ;;
  (re-frame/dispatch [::update-value ["users" (get-current-user-uid) "games" "1" "available"] true])
  (re-frame/dispatch [::update-value ["users" (get-current-user-uid) "games" "1" "group-with"] "0"])

  1

  ;
  )