(ns test-firebase.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [test-firebase.events :as events]
   [test-firebase.views :as views]
   [test-firebase.config :as config]
   [re-frame-firebase-nine.fb-reframe :refer [set-browser-session-persistence fb-reframe-config]]
   [re-frame-firebase-nine.firebase-auth :refer [get-auth]]))


(defn dev-setup []
  (when config/debug?
    (println "dev mode")))

(defn ^:dev/after-load mount-root []
  (re-frame/clear-subscription-cache!)
  (let [root-el (.getElementById js/document "app")]
    (rdom/unmount-component-at-node root-el)
    (rdom/render [views/main-panel] root-el)))

(defn init []
  ;; at the beginning so that they are loaded first
  (fb-reframe-config {:temp-path [:firebase-temp-storage]
                      :firebase-config {:apiKey "AIzaSyCpMjjOO9t-TVrzJ-seEy-4MUnH9PWc-uc",
                                        :authDomain "test-firebase-refr.firebaseapp.com",
                                        :databaseURL "https://test-firebase-refr-default-rtdb.europe-west1.firebasedatabase.app",
                                        :projectId "test-firebase-refr",
                                        :appId "1:1052651152055:web:4971b846529e25aa0ba332"}})

  (get-auth)
  (set-browser-session-persistence)
  ;; set the path in the db for the fb temp storage
  ;; and returning maps instead of lists


  (re-frame/dispatch-sync [::events/initialize-db])

  ;; poll for a signed-in user for 2 seconds
  ;; auth is not ready
  (re-frame/dispatch [::events/poll-user 10000])

  (dev-setup)
  (mount-root))

(comment
  init
  (require '(clojure.spec.alpha :as s))
  (s/valid? (s/keys :req-un [::sort-id])  {:sort-id 1})
  ;
  )
