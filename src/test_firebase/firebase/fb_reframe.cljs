(ns test-firebase.firebase.fb-reframe
  (:require [re-frame.core :as re-frame]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [test-firebase.firebase.firebase-database :refer [set-value! default-set-success-callback default-set-error-callback on-value]]
            [reagent.ratom :as ratom]))

;;  Effect for setting a value in firebase. Optional :success and :error keys for handlers
(re-frame/reg-fx
 ::firebase-set
 (fn-traced [{:keys [path data success error]}]
            (let [success-callback (if success success default-set-success-callback)
                  error-callback (if error error default-set-error-callback)]
              (set-value! path
                          data
                          success-callback
                          error-callback))))

(re-frame/reg-sub-raw
 ::on-value
 (fn [app-db [_ path]]
   (on-value path
             #(re-frame/dispatch [::write-to-temp (concat [:temp] path) %]))
   (ratom/make-reaction
    (fn [] (get-in @app-db (concat [:temp] path)))
    :on-dispose #(re-frame/dispatch [::cleanup-temp (concat [:temp] path)]))))

;; temp storage for fire-base reads
(re-frame/reg-event-db
 ::write-to-temp
 (fn-traced [db [_ path data]]
            (assoc-in db path data)))


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

;; clean temp storage
(re-frame/reg-event-db
 ::cleanup-temp
 (fn-traced [db [_ path]]
            (dissoc-in db path)))
