(ns test-firebase.forms
  (:require [re-frame.core :as re-frame]
            [test-firebase.subs :as subs]
            [test-firebase.form-events :as form-events]
            [test-firebase.utils :refer [find-key-value-in-map-list if-nil?->value]]))


(defn db-get-ref
  [db-path]
  (re-frame/subscribe [::subs/get-value db-path]))

(defn db-set-value!
  [db-path value]
  (re-frame/dispatch [::form-events/set-value! db-path value]))

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
  [:input {:type (name type) :value @(db-get-ref path)
           :on-change #(;;  re-frame/dispatch [::form/set-value! path (-> % .-target .-value)]
                        dispatch type path (-> % .-target .-value))}])

(defmethod input-element :checkbox [type path]
  (let [checked @(db-get-ref path)]
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
   
   db-path is the path in the db for setting and reading the information,

   options is a list of maps for the options in the select element,

   id-keyword is a keyword within the maps; the value is saved in the path,

   display-keyword is a keyword within the maps; the value is shown in the options,
   
   button-text-empty is the button text when nothing is selected,
   
   input-placeholder is the placeholder for the search text box."
  [db-path options id-keyword display-keyword button-text-empty input-placeholder]
  (let [initial-value @(db-get-ref db-path)
        _ (db-set-value! (into [:dropdown-search :value] db-path)
                    (display-keyword (find-key-value-in-map-list options id-keyword initial-value)))
        value @(db-get-ref (into [:dropdown-search :value] db-path))
        button-text (if-nil?->value value button-text-empty)
        visible? (db-get-ref (into [:dropdown-search :visible] db-path))
        display-style {:display (if (if-nil?->value @visible? false) "block" "none")}
        filtered-options @(re-frame/subscribe [::subs/options (into [:dropdown-search :search] db-path) options])
        style {:width "200px"}]

    [:div
     [:button {:style style
               :on-click #(db-set-value! (into [:dropdown-search :visible] db-path)
                                    (not (if-nil?->value @visible? false)))} button-text]

     [:input {:style (merge display-style style)
              :type :text
              :placeholder input-placeholder
              :value @(db-get-ref (into [:dropdown-search :search] db-path))
              :on-change #(db-set-value! (into [:dropdown-search :search] db-path) (-> % .-target .-value))}]

     [:select {:style (merge display-style style)
               :size @(re-frame/subscribe [::subs/select-size (into [:dropdown-search :search] db-path) options])
               :value (if-nil?->value value "")
               :on-change (fn [e]
                            (let [selected-index (-> e .-target .-selectedIndex)
                                  selected-id (if (= 0 selected-index) nil (id-keyword (nth filtered-options (dec selected-index))))]
                              (db-set-value! (into [:dropdown-search :visible] db-path) false)
                              (db-set-value! (into [:dropdown-search :search] db-path) "")
                              (db-set-value! (into [:dropdown-search :value] db-path) (-> e .-target .-value))
                              (db-set-value! db-path selected-id)))}
      [:option {:value ""} "(No game)"]
      (map (fn [m] [:option {:key (id-keyword m) :id (id-keyword m) :value (display-keyword m)} (display-keyword m)])
           filtered-options)]]))

