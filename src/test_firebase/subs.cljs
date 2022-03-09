(ns test-firebase.subs
  (:require
   [re-frame.core :as re-frame]
   [test-firebase.firebase.firebase-auth :refer [get-current-user-uid]]
   [test-firebase.firebase.fb-reframe :as fb-reframe]))

(defn fb-sub-user-id
  [path]
  (re-frame/subscribe [::fb-reframe/on-value (concat ["users" (get-current-user-uid)] path)]))

(defn fb-sub-root
  [path]
  (re-frame/subscribe [::fb-reframe/on-value  path]))


(re-frame/reg-sub
 ::email
 (fn [db]
   (:email db)))

;;
;; collections
;;
(re-frame/reg-sub
 ::collections
 (fn [[_ _]]
   (fb-sub-user-id ["collections"]))
 (fn [collections]
   collections))

(re-frame/reg-sub
 ::collection-ids
 :<- [::collections]
 (fn [collections]
   (keys collections)))

;; get the collection with id
(re-frame/reg-sub
 ::collection
 :<- [::collections]
 (fn [collections [_ id]]
   (id collections)))

;;

(re-frame/reg-sub
 ::public-data
 (fn [[_ _]]
   (fb-sub-root ["public"]))
 (fn [value]
   value))

(re-frame/reg-sub
 ::available
 (fn [[_ id]]
   (fb-sub-user-id ["games" id "available"]))
 (fn [value]
   value))

(re-frame/reg-sub
 ::group-with
 (fn [[_ id]]
   (fb-sub-user-id ["games" id "group-with"]))
 (fn [value]
   value))

