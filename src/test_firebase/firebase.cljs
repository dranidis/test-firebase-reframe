(ns test-firebase.firebase
  (:require [re-frame.core :as rf]
            ["firebase/app" :as fb]
            ["firebase/database" :as fdb]
            [clojure.string :as string]
            [test-firebase.firebase-auth :refer [get-current-user-uid]]
            [re-frame.loggers :refer [console]]))

(defn initApp []
  (fb/initializeApp
   #js {:apiKey "AIzaSyCLH4BlNSOfTrMlB_90Hsxg5cr3bn3p-7E",
        :authDomain "help-me-pick-what-to-play.firebaseapp.com",
        :databaseURL "https://help-me-pick-what-to-play-default-rtdb.europe-west1.firebasedatabase.app",
        :projectId "help-me-pick-what-to-play",
        :storageBucket "help-me-pick-what-to-play.appspot.com",
        :messagingSenderId "780911312465",
        :appId "1:780911312465:web:bbd9007195b3c630910270"}))

(defn getDB
  [app]
  (fdb/getDatabase app))

(defn DB
  []
  (getDB (initApp)))

(defn db-ref
  [db path]
  (fdb/ref db (string/join "/" path)))

;; (db-ref (getDB (initApp)) "hello")

(defn set-value!
  "Sets the value. Optionally then and catch callback functions can
   be provided"
  ([path data] (set-value! path data (fn []) (fn [_])))
  ([path data then-callback catch-callback]
   (-> (fdb/set
        (db-ref (DB) path) (clj->js data))
       (.then then-callback)
       (.catch #(catch-callback %)))))

(defn on-value
  "Default only-once is false"
  ([ref callback] (on-value ref callback false))
  ([ref callback only-once?]
   (fdb/onValue ref
                (fn [snap-shot]
               ;; ^js https://shadow-cljs.github.io/docs/UsersGuide.html#infer-externs
                  (callback (js->clj (.val ^js snap-shot) :keywordize-keys true)))
                #js {:onlyOnce only-once?})))

(defn on-value-sub
  [path event]
  (on-value (db-ref (DB) path) #(rf/dispatch [event %])))

(defn default-set-success
  [])

(defn default-set-error
  [error]
  (console :error (js->clj error)))

(comment
  (set-value! ["users" "VhzqAOJbN3UTWSpRC3t5qz664Y02" "games"] "game"
              #(println "SUCCESS") #(println "ERRROR" (js->clj %)))

  ;; make game available
  (set-value! ["users" (get-current-user-uid) "games" "12121"] {:available true}
              #(println "SUCCESS") #(println "ERRROR" (js->clj %)))

  ;; is game available?
  (defn game-available?
    [game-id])

  ;; (re-frame/dispatch [::events/save {:a 1 :b 2 :name "Dimitris" :m [1 2 3]}])


      ;
  )





