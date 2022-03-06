(ns test-firebase.core
  (:require
   [reagent.dom :as rdom]
   [re-frame.core :as re-frame]
   [test-firebase.events :as events]
   [test-firebase.views :as views]
   [test-firebase.config :as config]
   [test-firebase.firebase :refer [init-app]]
   [test-firebase.firebase-auth :refer [get-auth]]))


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

  (re-frame/dispatch-sync [::events/initialize-db])
  (dev-setup)
  (mount-root))

(comment
  init
  ;
  )
