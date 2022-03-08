(ns test-firebase.views
  (:require
   [re-frame.core :as re-frame]
   [test-firebase.subs :as subs]))

(defn game-div
  [id]
  (let [available (re-frame/subscribe [::subs/available (str id)])
        in-box (re-frame/subscribe [::subs/group-with (str id)])]
    ^{:key id}
    [:div
     "Game " id " available: "
     (if-not (nil? @available) (str @available) "null")
     " in box: "
     (if-not (nil? @in-box) (str @in-box) "null")
          ;
     ]))

(defn games-div
  []
  (let [email (re-frame/subscribe [::subs/email])]
    [:div (when @email (doall (map
                               game-div
                               (range 3))))]))

(defn main-panel []
  (let [email (re-frame/subscribe [::subs/email])
        public-data (re-frame/subscribe [::subs/public-data])]
    [:div
     [:h1 "Public data: " @public-data]
     [:h2 "User: " @email]
     [:h2 "Games:"]
     (games-div)]
     ;
    ))



