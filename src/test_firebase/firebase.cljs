(ns test-firebase.firebase
  (:require ["firebase/app" :as fb]
            ["firebase/database" :as fdb]
            [clojure.string :as string]
            [re-frame.loggers :refer [console]]))

(defn init-app []
  (fb/initializeApp
   #js {:apiKey "AIzaSyCLH4BlNSOfTrMlB_90Hsxg5cr3bn3p-7E",
        :authDomain "help-me-pick-what-to-play.firebaseapp.com",
        :databaseURL "https://help-me-pick-what-to-play-default-rtdb.europe-west1.firebasedatabase.app",
        :projectId "help-me-pick-what-to-play",
        :storageBucket "help-me-pick-what-to-play.appspot.com",
        :messagingSenderId "780911312465",
        :appId "1:780911312465:web:bbd9007195b3c630910270"}))

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





