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

(re-frame/reg-event-fx
 ::push-value
 (fn-traced [_ [_ path value]]
            {::fb-reframe/firebase-push {:path path
                                         :data value
                                         :success #(println "Success")
                                         :key-path [:key]}}))

(re-frame/reg-event-fx
 ::add-collection
 (fn-traced [_ [_ name]]
            {::fb-reframe/firebase-push {:path ["users" (fb-reframe/get-current-user-uid) "collections"]
                                         :data {:name name}
                                         :success #(println "Success")
                                         :key-path [:current-collection-key]}}))

(re-frame/reg-event-fx
 ::add-game-to-collection
 (fn-traced [cofx [_ game-id]]
            {::fb-reframe/firebase-set
             {:path ["users" (fb-reframe/get-current-user-uid)
                     "collections" (get-in cofx [:db :current-collection-key]) "games" game-id]
              :data true
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

(re-frame/reg-event-db
 ::sign-up-success
 (fn-traced [db [_ userCredential]]
            (println "User created")
            (let [email (.-email (.-user userCredential))]
              (assoc db :email email))))

(re-frame/reg-event-fx
 ::sign-up
 (fn-traced [_ [_ email password]]
            {::fb-reframe/firebase-create-user {:email email
                                                :password password
                                                :success ::sign-up-success}}))


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

  (re-frame/dispatch [::update-value ["users" (fb-reframe/get-current-user-uid) "collections" "collection-1"] (str #{"1" "3" "5"})])
  (re-frame/dispatch [::update-value ["users" (fb-reframe/get-current-user-uid) "collections" "collection-2"] (str #{"4" "2"})])
  (re-frame/dispatch [::update-value ["users" (fb-reframe/get-current-user-uid) "collections" "collection-3"] {:name "My collection" :games #{"1" "2"}}])
  (re-frame/dispatch [::update-value ["users" (fb-reframe/get-current-user-uid) "collections" "collection-1"] {:name "In the car" :games #{"1" "4" "5"}}])
  (re-frame/dispatch [::update-value ["users" (fb-reframe/get-current-user-uid) "collections" "collection-0"] {:name "Empty collection" :games #{}}])


  (re-frame/dispatch
   [::update-value ["users" (fb-reframe/get-current-user-uid) "collections"]
    {:collection-01 {:name "collection01" :games (str #{"0" "1"})}
     :collection-02 {:name "collection02" :games (str #{"2" "3"})}}])

  (fb-reframe/get-current-user-uid)

  ;; create a new collection
  (re-frame/dispatch
   [::push-value
    ["users" (fb-reframe/get-current-user-uid) "collections"]
    {:name "My favorite games No 2"}])


  (re-frame/dispatch [::add-collection "A_newcollector 13"])
  (re-frame/dispatch [::add-game-to-collection  "14"])



  1

  ;
  )