(ns test-firebase.events
  (:require [re-frame.core :as re-frame]
            [test-firebase.db :as db]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [test-firebase.firebase.fb-reframe :as fb-reframe]))

;; https://stackoverflow.com/questions/14488150/how-to-write-a-dissoc-in-command-for-clojure
(defn dissoc-in
  "Dissociates an entry from a nested associative structure returning a new
  nested structure. keys is a sequence of keys. Any empty maps that result
  will not be present in the new structure."
  [m [k & ks]]
  (if ks
    (if-let [nextmap (get m k)]
      (let [newmap (dissoc-in nextmap ks)]
        (if (seq newmap)
          (assoc m k newmap)
          (dissoc m k)))
      m)
    (dissoc m k)))

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

;; temp storage for fire-base reads
(re-frame/reg-event-db
 ::write-to-temp
 (fn-traced [db [_ path data]]
            (assoc-in db path data)))

(re-frame/reg-event-db
 ::cleanup-temp
 (fn-traced [db [_ path]]
            (dissoc-in db path)))


(comment
  (re-frame/dispatch [::update-value ["games" "3" "available"] true])

  (re-frame/dispatch [::update-value ["games" "3" "group-with"] "1"])

  1

  ;
  )