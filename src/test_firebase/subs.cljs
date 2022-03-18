(ns test-firebase.subs
  (:require
   [re-frame.core :as re-frame]
   [re-frame-firebase-nine.firebase-auth :refer [get-current-user-uid]]
   [re-frame-firebase-nine.fb-reframe :as fb-reframe]
   [test-firebase.form-events :as form-events]
   [test-firebase.utils :refer [is-substring?]]))

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
 (fn []
   (fb-sub-user-id ["collections"]))
 (fn [collections]
  ;;  (println "collections" collections)
   (reduce-kv (fn [m k v] (assoc m k (assoc v :id (name k)))) {} collections)))

(comment
  (def collections @(re-frame/subscribe [::collections]))
  collections
  (vals (reduce-kv (fn [m k v] (assoc m k (assoc v :id (str (name k))))) {} collections))
;
  )

(re-frame/reg-sub
 ::collection-ids
 :<- [::collections]
 (fn [collections]
   (keys collections)))

;; get the collection with id
;; game-ids are in the keys (e.g. {132: true, 124: true})
(re-frame/reg-sub
 ::collection
 :<- [::collections]
 (fn [collections [_ id]]
   (let [collection (id collections)
         games (keys (:games collection))]
     (assoc collection :games games))))


(re-frame/reg-sub
 ::public-data
 (fn []
   (fb-sub-root ["public"]))
 (fn [value]
   value))


;;
;; available games
;;
(re-frame/reg-sub
 ::available-games
 (fn []
   (fb-sub-user-id ["available"]))
 (fn [value]
   value))

(re-frame/reg-sub
 ::available
 :<- [::available-games]
 (fn [available-games [_ id]]
  ;;  (println "::available " id available-games)
   (let [value ((keyword id) available-games)]
     (re-frame/dispatch [::form-events/set-value! [:form :available id] value])
     value)))

;;
;; grouping (for boxes with games)
;;
(re-frame/reg-sub
 ::group-with-all
 (fn []
   (fb-sub-user-id ["group-with"]))
 (fn [value]
   value))

(re-frame/reg-sub
 ::group-with
 :<- [::group-with-all]
 (fn [value [_ id]]
   (let [val ((keyword id) value)]
     (re-frame/dispatch [::form-events/set-value! [:form :group-with id] val])
     val)))


(re-frame/reg-sub
 ::form
 (fn [[_ id]]
   [(re-frame/subscribe [::available  id])
    (re-frame/subscribe [::group-with id])])
 (fn [[group-with available] [_ _]]
   {:available available :group-with group-with}))



(re-frame/reg-sub
 ::dropdown-select-options
 (fn [db [_ path all-options]]
   (->> all-options
        (filter
         (fn [{:keys [_ name]}]
           (or (nil? (get-in db path)) (is-substring? (get-in db path) name)))))))



(re-frame/reg-sub
 ::dropdown-select-size
 (fn [[_ path all-options]]
   (re-frame/subscribe [::dropdown-select-options path all-options]))
 (fn [options]
   ;; an extra option is (Nothing)
   (min 10 (inc (count options)))))


(re-frame/reg-sub
 ::get-value
 (fn [db [_ path]]
   (get-in db path)))


