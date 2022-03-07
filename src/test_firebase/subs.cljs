(ns test-firebase.subs
  (:require
   [re-frame.core :as re-frame]
   [test-firebase.events :as events]
   [test-firebase.firebase.firebase-database :refer [on-value]]
   [test-firebase.firebase.firebase-auth :refer [get-current-user-uid]]
   [reagent.ratom :as ratom]))

(re-frame/reg-sub-raw
 ::on-value
 (fn [app-db [_ path]]
   (on-value (concat ["users" (get-current-user-uid)] path)
             #(re-frame/dispatch [::events/write-to-temp (concat [:temp] path) %]))
   (ratom/make-reaction
    (fn [] (get-in @app-db (concat [:temp] path)))
    :on-dispose #(re-frame/dispatch [::events/cleanup-temp (concat [:temp] path)]))))

(re-frame/reg-sub
 ::available
 (fn [[_ id]]
   (re-frame/subscribe [::on-value ["games" id "available"]]))
 (fn [value]
   value))

(re-frame/reg-sub
 ::group-with
 (fn [[_ id]]
   (re-frame/subscribe [::on-value ["games" id "group-with"]]))
 (fn [value]
   value))

