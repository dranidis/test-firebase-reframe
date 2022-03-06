(ns test-firebase.events
  (:require
   [re-frame.core :as re-frame]
   [test-firebase.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [test-firebase.firebase :refer [set-value! default-set-success default-set-error]]
   [test-firebase.firebase-auth :refer [get-current-user-uid]]))

(re-frame/reg-fx
 :set
 (fn [{:keys [path data success error]}]
   (let [success-callback (if success success default-set-success)
         error-callback (if error error default-set-error)]
     (set-value! (concat ["users" (get-current-user-uid)] path)
                 data
                 success-callback
                 error-callback))))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
            db/default-db))

(re-frame/reg-event-fx
 ::save
 (fn [cofx [_ data]]
   {:set {:path [:user]
          :data data}}))

(re-frame/reg-event-fx
 ::make-game-available
 (fn [cofx [_ game-id]]
   {:set {:path ["games" game-id]
          :data {:available true}
          :success #(println "Success")}}))

(re-frame/reg-event-db
 ::received
 (fn [db [_ data]]
   (assoc db :data data)))


(comment
  (re-frame/dispatch [::make-game-available "123125"])
  ;
  )