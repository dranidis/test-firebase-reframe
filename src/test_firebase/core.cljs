(ns test-firebase.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [test-firebase.events :as events]
   [test-firebase.views :as views]
   [test-firebase.config :as config]
   [test-firebase.firebase.firebase-app :refer [init-app]]
   [test-firebase.firebase.firebase-auth :refer [get-auth]]
   [test-firebase.firebase.fb-reframe :refer [set-temp-path! set-browser-session-persistence]]))


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
  (init-app)
  (get-auth)
  (set-browser-session-persistence)
  ;; set the path in the db for the fb temp storage
  (set-temp-path! [:fire-base-temp-storage])

  (re-frame/dispatch-sync [::events/initialize-db])

  ;; poll for a signed-in user for 2 seconds
  ;; auth is not ready
  (re-frame/dispatch [::events/poll-user 10000])

  (dev-setup)
  (mount-root))

(comment
  init
  ;
  )
