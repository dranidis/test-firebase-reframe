(ns test-firebase.firebase.fb-reframe
  (:require [re-frame.core :as re-frame]
            [day8.re-frame.tracing :refer-macros [fn-traced]]
            [test-firebase.firebase.firebase-database :refer [set-value! default-set-success-callback default-set-error-callback]]
            [test-firebase.firebase.firebase-auth :refer [get-current-user-uid]]))

;;  Effect for setting a value in firebase. Optional :success and :error keys for handlers
(re-frame/reg-fx
 ::firebase-set
 (fn-traced [{:keys [path data success error]}]
            (let [success-callback (if success success default-set-success-callback)
                  error-callback (if error error default-set-error-callback)]
              (set-value! (concat ["users" (get-current-user-uid)] path)
                          data
                          success-callback
                          error-callback))))
