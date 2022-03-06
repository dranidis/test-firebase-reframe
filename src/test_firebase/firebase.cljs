(ns test-firebase.firebase
  (:require [re-frame.core :as rf]
            ["firebase/app" :as fb]
            ["firebase/database" :as fdb]
            [clojure.string :as string]))

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

(def DB (getDB (initApp)))

(defn db-ref
  [db path]
  (fdb/ref db (string/join "/" path)))

(db-ref (getDB (initApp)) "hello")

(defn save!
  [path data]
  (fdb/set
   (db-ref DB path) (clj->js data)))

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
  (on-value (db-ref DB path) #(rf/dispatch [event %])))

(comment
  (save! ["user"] "dimitris")
  ;
  )





