(ns test-firebase.firebase.fb-reframe
  (:require [re-frame.core :as re-frame]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [test-firebase.firebase.firebase-database :refer [set-value! default-set-success-callback default-set-error-callback on-value off]]
            [reagent.ratom :as ratom]
            [re-frame.utils :refer [dissoc-in]]
            [test-firebase.firebase.firebase-auth :refer [error-callback sign-in sign-out]]))

;;  Effect for setting a value in firebase. Optional :success and :error keys for handlers
(re-frame/reg-fx
 ::firebase-set
 (fn-traced [{:keys [path data success error]}]
            (set-value! path
                        data
                        (if success success default-set-success-callback)
                        (if error error default-set-error-callback))))

(re-frame/reg-fx
 ::firebase-sign-in
 (fn-traced [{:keys [email password success]}]
            (sign-in email password
                     #(re-frame/dispatch [success %])
                     error-callback)))

(re-frame/reg-fx
 ::firebase-sign-out
 (fn-traced [{:keys [success]}]
            (sign-out #(re-frame/dispatch [success %])
                      error-callback)))

(def temp-path-atom (atom [:temp]))

(defn set-temp-path!
  [new-path]
  (swap! temp-path-atom (fn [] new-path)))

(re-frame/reg-sub-raw
 ::on-value
 (fn [app-db [_ path]]
   (let [query-token (on-value path
                               #(re-frame/dispatch [::write-to-temp path %]))]
     (ratom/make-reaction
      (fn [] (get-in @app-db (concat @temp-path-atom path)))
      :on-dispose #(do (off path query-token)
                       (re-frame/dispatch [::cleanup-temp path]))))))

;; temp storage for fire-base reads
(re-frame/reg-event-db
 ::write-to-temp
 (fn-traced [db [_ path data]]
            (assoc-in db (concat @temp-path-atom path) data)))


;; clean temp storage
(re-frame/reg-event-db
 ::cleanup-temp
 (fn-traced [db [_ path]]
            (dissoc-in db (concat @temp-path-atom path))))

(comment
  (dissoc-in {:a {:b {:c "val"}}} [:a :b :c])
  ;=> {}
  (dissoc-in {:a {:b {:c "val"}} :a1 "a1"} [:a :b :c])
  ;=> {:a1 "a1"}

  ;
  )
