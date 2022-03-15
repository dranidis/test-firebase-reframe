(ns test-firebase.forms
  (:require [re-frame.core :as re-frame]
            [test-firebase.subs :as subs]
            [test-firebase.form-events :as form-events]
            [test-firebase.utils :refer [find-key-value-in-map-list if-nil?->value]]))

;;
;; functions for generic form inputs
;;
(defmulti dispatch (fn [type _ _] type))

(defmethod dispatch :default [type path _]
  (throw (js/Error. (str "No dispatch method for form input element of type: " type " for path: " path))))

(defmethod dispatch :text [_ path value]
  (re-frame/dispatch [::form-events/set-value! path value]))

(defmethod dispatch :password [_ path value]
  (re-frame/dispatch [::form-events/set-value! path value]))

(defmethod dispatch :checkbox [_ path _]
  (re-frame/dispatch [::form-events/update-value! path not]))

(defmulti input-element (fn [type _] type))

(defmethod input-element :default [type path]
  [:input {:type (name type) :value @(re-frame/subscribe [::subs/get-value path])
           :on-change #(;;  re-frame/dispatch [::form/set-value! path (-> % .-target .-value)]
                        dispatch type path (-> % .-target .-value))}])

(defmethod input-element :checkbox [type path]
  (let [checked @(re-frame/subscribe [::subs/get-value path])]
    [:input {:type (name type) :checked (if (nil? checked) false checked)
             :on-change #(;;  re-frame/dispatch [::form/set-value! path (-> % .-target .-value)]
                          dispatch type path (-> % .-target .-value))}]))
(defn input
  [label type path]
  [:div
   [:label label]
   [input-element type path]])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;



(defn dropdown-search
  "Creates a dropdown search component consisting of a button, a search text, and a select element.
   
   path is the path in the db for setting and reading the information

   all-options is a list of maps for the options in the select element

   id-keyword is a keyword within the maps; the value is saved in the path

   display-keyword is a keyword within the maps; the value is shown in the options."
  [path all-options id-keyword display-keyword]
  (let [saved-index @(re-frame/subscribe [::subs/get-value path])
        _ (re-frame/dispatch [::form-events/set-value! (into [:dropdown-search :value] path) 
                              (display-keyword (find-key-value-in-map-list all-options id-keyword saved-index))])
        selected-value @(re-frame/subscribe [::subs/get-value (into [:dropdown-search :value] path)])
        button-text (if (or (nil? selected-value) (empty? selected-value)) "Select a game" selected-value)
        visible (re-frame/subscribe [::subs/get-value (into [:dropdown-search :visible] path)])
        options @(re-frame/subscribe [::subs/options (into [:dropdown-search :search] path) all-options])
        style {:width "200px"}]

    [:div
     [:button {:style (merge style {:height "20px"})
               :on-click
               #(re-frame/dispatch [::form-events/set-value! (into [:dropdown-search :visible] path)
                                    (not (if-nil?->value @visible false))])} button-text]
     
     [:input {:style (merge {:display (if (if-nil?->value @visible false) "block" "none")} style)
              :type :text
              :placeholder "Type to find a game"
              :value @(re-frame/subscribe [::subs/get-value (into [:dropdown-search :search] path)])
              :on-change #(;;  re-frame/dispatch [::form/set-value! (into [:dropdown-search :search] path) (-> % .-target .-value)]
                           dispatch :text (into [:dropdown-search :search] path) (-> % .-target .-value))}]

     [:select {:id "games" :style (merge {:display (if (if-nil?->value @visible false) "block" "none")} style)
               :size @(re-frame/subscribe [::subs/select-size (into [:dropdown-search :search] path) all-options])
               :value (if-nil?->value selected-value "")
               :on-change (fn [e]
                            (let [selected-index (-> e .-target .-selectedIndex)
                                  selected-id (if (= 0 selected-index) nil (id-keyword (nth options (dec selected-index))))]
                            (re-frame/dispatch [::form-events/set-value! (into [:dropdown-search :visible] path) false])
                            (re-frame/dispatch [::form-events/set-value! (into [:dropdown-search :search] path) ""])
                            (re-frame/dispatch [::form-events/set-value! (into [:dropdown-search :value] path) (-> e .-target .-value)])
                            (re-frame/dispatch [::form-events/set-value! path selected-id])
                            ))}
      [:option {:value ""} "(No game)"]
      (map (fn [m]
             [:option {:key (id-keyword m) :id (id-keyword m) :value (display-keyword m)} (display-keyword m)])
           options)]
        ;
     ]))

