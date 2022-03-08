(ns test-firebase.firebase.firebase-app
  (:require ["firebase/app" :as fb]))

(defn init-app []
  (fb/initializeApp
   #js {:apiKey "AIzaSyCpMjjOO9t-TVrzJ-seEy-4MUnH9PWc-uc",
        :authDomain "test-firebase-refr.firebaseapp.com",
        :databaseURL "https://test-firebase-refr-default-rtdb.europe-west1.firebasedatabase.app",
        :projectId "test-firebase-refr",
        :storageBucket "test-firebase-refr.appspot.com",
        :messagingSenderId "1052651152055",
        :appId "1:1052651152055:web:4971b846529e25aa0ba332"}))

