(ns test-firebase.events
  (:require
   [re-frame.core :as re-frame]
   [test-firebase.db :as db]
   [day8.re-frame.tracing :refer-macros [fn-traced]]
   [test-firebase.firebase :refer [set-value!]]))

(re-frame/reg-fx
 :set
 (fn [{:keys [path data]}]
   (set-value! path data)))

(re-frame/reg-event-db
 ::initialize-db
 (fn-traced [_ _]
            db/default-db))

(re-frame/reg-event-fx
 ::save
 (fn [cofx [_ data]]
   {:set {:path [:user]
          :data data}}))

(re-frame/reg-event-db
 ::received
 (fn [db [_ data]]
   (assoc db :data data)))