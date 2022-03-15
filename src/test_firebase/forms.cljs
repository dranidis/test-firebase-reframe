(ns test-firebase.forms
  (:require [re-frame.core :as re-frame]
            [test-firebase.subs :as subs]
            [test-firebase.form-events :as form-events]))

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
(defn if-nil?->value
  [v default]
  (if (nil? v) default v))
(if-nil?->value true false)

(defn dropdown-search
  [path all-options]
  (let [selected-value @(re-frame/subscribe [::subs/get-value path])
        button-text (if (or (nil? selected-value) (empty? selected-value)) "Select a game" selected-value)
        visible (re-frame/subscribe [::subs/get-value (into [:dropdown-search :visible] path)])
        style {:width "200px"}]

    [:div
     [:div "In box"]
     [:button {:style (merge style {:height "20px"})
               :on-click
               #(re-frame/dispatch [::form-events/set-value! (into [:dropdown-search :visible] path)
                                    (not (if-nil?->value @visible false))])} button-text]
        ;; [input "In box" :text path]
     [:input {:style (merge {:display (if (if-nil?->value @visible false) "block" "none")} style)
              :type :text
              :placeholder "Type to find a game"
              :value @(re-frame/subscribe [::subs/get-value (into [:dropdown-search :search] path)])
              :on-change #(;;  re-frame/dispatch [::form/set-value! (into [:dropdown-search :search] path) (-> % .-target .-value)]
                           dispatch :text (into [:dropdown-search :search] path) (-> % .-target .-value))}]

     [:select {:style (merge {:display (if (if-nil?->value @visible false) "block" "none")} style)
               :size @(re-frame/subscribe [::subs/select-size (into [:dropdown-search :search] path) all-options])
              ;;  :value @(re-frame/subscribe [::subs/get-value [:form (str id) :select-value]])
               :on-change (fn [e]
                            ;; (re-frame/dispatch [::form-events/set-value! [:form (str id) :select-value] (-> e .-target .-value)])
                            (re-frame/dispatch [::form-events/set-value! path (-> e .-target .-value)])
                            (re-frame/dispatch [::form-events/set-value! (into [:dropdown-search :visible] path) false])
                            (re-frame/dispatch [::form-events/set-value! (into [:dropdown-search :search] path) ""]))}
      [:option {:value ""} "(No game)"]
      (map (fn [m]
             [:option {:key (:id m) :value (:name m)} (:name m)])
           @(re-frame/subscribe [::subs/options (into [:dropdown-search :search] path) all-options]))]
        ;
     ]))

