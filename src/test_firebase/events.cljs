(ns test-firebase.events
  (:require [re-frame.core :as re-frame]
            [test-firebase.db :as db]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [test-firebase.firebase.fb-reframe :as fb-reframe :refer [get-current-user]]))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
            db/default-db))
(def poll-time-interval-ms 200)

(re-frame/reg-event-fx
 ::poll-user
 (fn-traced [cofx [_ timeout]]
            (let [time (:time (:db cofx))]
              (if (> time timeout)
                (do
                  (println "Log in")
                  {})
                (if (get-current-user)
                  {:db (assoc (:db cofx) :email (fb-reframe/get-current-user-email))}
                  {:db (assoc (:db cofx) :time (+ time poll-time-interval-ms))
                   :dispatch-later {:ms poll-time-interval-ms
                                    :dispatch [::poll-user timeout]}})))))

(re-frame/reg-event-fx
 ::update-value
 (fn-traced [_ [_ path value]]
            {::fb-reframe/firebase-set {:path path
                                        :data value
                                        :success #(println "Success")}}))

(re-frame/reg-event-db
 ::sign-in-success
 (fn-traced [db [_ userCredential]]
            (println "User signed-in")
            (let [email (.-email (.-user userCredential))]
              (assoc db :email email))))

(re-frame/reg-event-db
 ::sign-out-success
 (fn-traced [db [_]]
            (println "User signed-out")
            (dissoc db :email)))


;; (re-frame/reg-event-fx
;;  ::sign-in
;;  (fn-traced [_ [_ email password]]
;;             {::fb-reframe/firebase-sign-in {:email email
;;                                             :password password
;;                                             :success ::sign-in-success}}))

(re-frame/reg-event-fx
 ::sign-in
 (fn-traced [_ [_ email password]]
            {:fx [[:dispatch [::sign-out]]
                  [::fb-reframe/firebase-sign-in {:email email
                                                  :password password
                                                  :success ::sign-in-success}]]}))


(re-frame/reg-event-fx
 ::sign-out
 (fn-traced [_ [_]]
            {::fb-reframe/firebase-sign-out {:success ::sign-out-success}}))



(comment
  ;;
  ;; examples of dispatch events
  ;;

  (re-frame/dispatch [::sign-in "adranidisb@gmail.com" "password"])

  (re-frame/dispatch [::sign-in "dranidis@gmail.com" "password"])

  (re-frame/dispatch [::sign-out])

  (re-frame/dispatch [::update-value ["users" (fb-reframe/get-current-user-uid) "games" "1" "available"] false])
  (re-frame/dispatch [::update-value ["users" (fb-reframe/get-current-user-uid) "games" "1" "group-with"] "0"])



  1

  ;
  )