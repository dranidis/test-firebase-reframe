(ns test-firebase.views
  (:require
   [re-frame.core :as re-frame]
   [test-firebase.subs :as subs]))

(defn main-panel []
  (let [email (re-frame/subscribe [::subs/email])]
    [:div
     [:h2 "User: " @email]
     [:h2 "Games:"]
     [:div (when @email (doall (map
                                (fn [id]
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
                                (range 3))))]]
     ;
    ))



