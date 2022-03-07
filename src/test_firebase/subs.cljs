(ns test-firebase.subs
  (:require
   [re-frame.core :as re-frame]
   [test-firebase.firebase.firebase-auth :refer [get-current-user-uid]]
   [test-firebase.firebase.fb-reframe :as fb-reframe]))

(re-frame/reg-sub
 ::email
 (fn [db _]
   (:email db)))

(re-frame/reg-sub
 ::available
 (fn [[_ id]]
   (re-frame/subscribe [::fb-reframe/on-value ["users" (get-current-user-uid) "games" id "available"]]))
 (fn [value]
   value))

(re-frame/reg-sub
 ::group-with
 (fn [[_ id]]
   (re-frame/subscribe [::fb-reframe/on-value ["users" (get-current-user-uid) "games" id "group-with"]]))
 (fn [value]
   value))

