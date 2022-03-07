(ns test-firebase.subs
  (:require
   [re-frame.core :as re-frame]
   [test-firebase.firebase.firebase-auth :refer [get-current-user-uid]]
   [test-firebase.firebase.fb-reframe :as fb-reframe]))

(defn fb-sub
  [path]
  (re-frame/subscribe [::fb-reframe/on-value (concat ["users" (get-current-user-uid) "games"] path)]))

(re-frame/reg-sub
 ::email
 (fn [db]
   (:email db)))

(re-frame/reg-sub
 ::available
 (fn [[_ id]]
   (fb-sub [id "available"]))
 (fn [value]
   value))

(re-frame/reg-sub
 ::group-with
 (fn [[_ id]]
   (fb-sub [id "group-with"]))
 (fn [value]
   value))

