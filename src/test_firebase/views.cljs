(ns test-firebase.views
  (:require
   [re-frame.core :as re-frame]
   [test-firebase.subs :as subs]
   [test-firebase.events :as events]))

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

(defn collection-div
  [collection-id]
  (let [collection @(re-frame/subscribe [::subs/collection collection-id])]
    ^{:key collection-id}
    [:div
     [:h4 "Id: " collection-id " Name: " (:name collection)]
     [:ul (map (fn [game]
                 ^{:key game}
                 [:li game]) (:games collection))]]))

(defn games-div
  []
  [:div
   [:h2 "Games info"]
   [:div (doall (map game-div (range 3)))]])

(defn collections-div
  []
  (let [collection-ids (re-frame/subscribe [::subs/collection-ids])]
    [:div
     [:h2 "Collections"]
     [:div (doall (map collection-div @collection-ids))]]))

(defn main-panel []
  (let [email (re-frame/subscribe [::subs/email])
        public-data (re-frame/subscribe [::subs/public-data])]
    [:div
     [:h1 "Public data: "] 
     [:div @public-data]
     [:h1 "User email: " @email]
     [:button {:on-click #(re-frame/dispatch [::events/sign-in "dranidis@gmail.com" "password"])} "Sign-in as dranidis"]
     [:button {:on-click #(re-frame/dispatch [::events/sign-in "adranidisb@gmail.com" "password"])} "Sign-in as adranidisb"]
     [:button {:on-click #(re-frame/dispatch [::events/sign-up "dranidis@gmail.com" "password"])} "Sign up dranidis"]
     [:button {:on-click #(re-frame/dispatch [::events/sign-out])} "Sign out"]
     (when @email (games-div))
     (when @email (collections-div))
     ]
     ;
    ))



