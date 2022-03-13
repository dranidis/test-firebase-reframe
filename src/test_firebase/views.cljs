(ns test-firebase.views
  (:require
   [re-frame.core :as re-frame]
   [test-firebase.subs :as subs]
   [test-firebase.events :as events]
   [test-firebase.form-events :as form]))

;;
;; functions for generic form inputs
;;
(defmulti dispatch (fn [type _ _] type))

(defmethod dispatch :default [type path _]
  (throw (js/Error. (str "No dispatch method for form input element of type: " type " for path: " path))))

(defmethod dispatch :text [_ path value]
  (re-frame/dispatch [::form/set-value! path value]))

(defmethod dispatch :password [_ path value]
  (re-frame/dispatch [::form/set-value! path value]))

(defmethod dispatch :checkbox [_ path _]
  (re-frame/dispatch [::form/update-value! path not]))

(defmulti input-element (fn [type _] type))

(defmethod input-element :default [type path]
  [:input {:type (name type) :value @(re-frame/subscribe [::subs/get-value path])
           :on-change #(dispatch type path (-> % .-target .-value))}])

(defmethod input-element :checkbox [type path]
  (let [checked @(re-frame/subscribe [::subs/get-value path])]
    [:input {:type (name type) :checked (if (nil? checked) false checked)
             :on-change #(dispatch type path (-> % .-target .-value))}]))
(defn input
  [label type path]
  [:div
   [:label label]
   [input-element type path]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn game-div
  [id]
  (let [_ @(re-frame/subscribe [::subs/form id])]
    ^{:key id}
    [:div
     [:h4 "Game " (str id)]
     [input "Available" :checkbox [:form :available (str id)]]
     [input "In box" :text [:form :group-with (str id)]]
     [:button {:on-click #(re-frame/dispatch [::events/save-game id])} "Save"]]))

(defn item-div
  [game-id collection-id]
  ^{:key game-id}
  [:li game-id " "
   [:button {:on-click #(re-frame/dispatch [::events/remove-game-from-collection (name game-id) (name collection-id)])} "X"]])

(defn collection-div
  [collection-id]
  (let [collection @(re-frame/subscribe [::subs/collection collection-id])]
    ^{:key collection-id}
    [:div
     [:h4 "Id: " collection-id " Name: " (:name collection)]
     ;; not sure why collection-id is a keyword
     [:button {:on-click #(re-frame/dispatch [::events/delete-collection (name collection-id)])} "Delete collection"]

     [:div [input "Item" :text [:form :item-id]]
      [:button {:on-click #(re-frame/dispatch
                            [::events/add-game-to-collection
                             @(re-frame/subscribe [::subs/get-value [:form :item-id]]) (name collection-id)])} "Add"]]
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



