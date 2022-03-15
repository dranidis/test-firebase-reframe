(ns test-firebase.views
  (:require
   [re-frame.core :as re-frame]
   [test-firebase.subs :as subs]
   [test-firebase.events :as events]
   [test-firebase.forms :refer [input dropdown-search]]
   [test-firebase.utils :refer [random-names-new find-key-value-in-map-list]]))


(defn game-div
  [id]
  (let [_ @(re-frame/subscribe [::subs/form id])]
    ^{:key id}
    [:div
     [:h4 "Game " (str id)]
     [input "Available" :checkbox [:form :available (str id)]]
     [:label "Item"]
     (dropdown-search [:form :group-with (str id)] random-names-new :id :name "Click to select" "Type to find a game" "(no selection)")
     [:button {:on-click #(re-frame/dispatch [::events/save-game id])} "Save"]]))

(defn item-div
  [game-id collection-id]
  ^{:key game-id}
  [:li
   [:div 
    (:name (find-key-value-in-map-list random-names-new :id (str (name game-id)))) " "
    [:button {:on-click #(re-frame/dispatch [::events/remove-game-from-collection (name game-id) (name collection-id)])} "X"]]])

(defn collection-div
  [collection-id]
  (let [collection @(re-frame/subscribe [::subs/collection collection-id])]
    ^{:key collection-id}
    [:div
     [:h4 "Id: " collection-id " Name: " (:name collection)]
     ;; not sure why collection-id is a keyword
     [:button {:on-click #(re-frame/dispatch [::events/delete-collection (name collection-id)])} "Delete collection"]

     [:div
    ;;  [input "Item" :text [:form collection-id :item-id]]
      [:label "Add an Item"]
      (dropdown-search [:form  collection-id :item-id] random-names-new :id :name "Click to select" "Type to find a game" "(no selection)")

      [:button {:on-click #(re-frame/dispatch
                            [::events/add-game-to-collection
                             @(re-frame/subscribe [::subs/get-value [:form collection-id :item-id]]) (name collection-id)])} "Add"]]
     [:ul (map #(item-div % collection-id) (:games collection))]]))

(defn games-div
  []
  [:div
   [:h2 "Games info"]
   [:div (doall (map #(game-div (str %)) (range 3)))]])

(defn collections-div
  []
  (let [collection-ids (re-frame/subscribe [::subs/collection-ids])]
    [:div
     [:h2 "Collections"]
     [:div [input "Collection name" :text [:form :new-collection-name]]
      [:button {:on-click #(re-frame/dispatch
                            [::events/new-collection
                             @(re-frame/subscribe [::subs/get-value [:form :new-collection-name]])])} "Create"]]    ;;  [:button {:on-click #(re-frame/dispatch [::events/new-collection (str "Collection-" (rand-int 1000))])} "New collection"]
     [:div (doall (map collection-div @collection-ids))]]))

(defn sign-in-div
  []
  (let [form @(re-frame/subscribe [::subs/get-value [:form]])]
    [:div
     [input "email" :text [:form :email]]
     [input "password" :password [:form :password]]
     [:button {:on-click #(re-frame/dispatch [::events/sign-in (:email form) (:password form)])} "Sign in"]
     [:button {:on-click #(re-frame/dispatch [::events/sign-up (:email form) (:password form)])} "Sign up"]
     [:button {:on-click #(re-frame/dispatch [::events/sign-out])} "Sign out"]]))

(defn main-panel []
  (let [email (re-frame/subscribe [::subs/email])
        public-data (re-frame/subscribe [::subs/public-data])]
    [:div
     [:h4 "Public data: "]
     [:div @public-data]
     [:h4 "User email: " @email]
     [sign-in-div]
     [:button {:on-click #(re-frame/dispatch [::events/sign-in "dranidis@gmail.com" "password"])} "Sign-in as dranidis"]
     [:button {:on-click #(re-frame/dispatch [::events/sign-in "adranidisb@gmail.com" "password"])} "Sign-in as adranidisb"]
     [:button {:on-click #(re-frame/dispatch [::events/sign-up "dranidis@gmail.com" "password"])} "Sign up dranidis"]
     [:button {:on-click #(re-frame/dispatch [::events/sign-up "adranidisb@gmail.com" "password"])} "Sign up adranidisb"]

     (when @email (games-div))
     (when @email (collections-div))]
     ;
    ))



