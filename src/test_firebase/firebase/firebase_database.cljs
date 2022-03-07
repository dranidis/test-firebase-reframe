(ns test-firebase.firebase.firebase-database
  (:require ["firebase/database" :as fdb]
            [clojure.string :as string]
            [re-frame.loggers :refer [console]]
            [test-firebase.firebase.firebase-app :refer [init-app]]))

(defn get-db
  []
  (fdb/getDatabase (init-app)))

(defn db-ref
  [db path]
  (fdb/ref db (string/join "/" path)))

(defn set-value!
  "Sets the value. Optionally then and catch callback functions can
   be provided"
  ([path data] (set-value! path data (fn []) (fn [_])))
  ([path data then-callback catch-callback]
   (-> (fdb/set
        (db-ref (get-db) path) (clj->js data))
       (.then then-callback)
       (.catch #(catch-callback %)))))

(defn on-value
  "Default only-once is false"
  ([path callback] (on-value path callback false))
  ([path callback only-once?]
   (println "Calling on-value")
   (fdb/onValue (db-ref (get-db) path)
                (fn [snap-shot]
               ;; ^js https://shadow-cljs.github.io/docs/UsersGuide.html#infer-externs
                  (callback (js->clj (.val ^js snap-shot) :keywordize-keys true)))
                #js {:onlyOnce only-once?})))

(defn default-set-success-callback
  [])

(defn default-set-error-callback
  [error]
  (console :error (js->clj error)))

(comment
  (set-value! ["users" "VhzqAOJbN3UTWSpRC3t5qz664Y02" "games"] "game"
              #(println "SUCCESS") #(println "ERRROR" (js->clj %)))

  ;; make game available
  ;; (set-value! ["users" (get-current-user-uid) "games" "0"] {:available true}
  ;;             #(println "SUCCESS") #(println "ERRROR" (js->clj %)))


  ;; (re-frame/dispatch [::events/save {:a 1 :b 2 :name "Dimitris" :m [1 2 3]}])


      ;
  )





