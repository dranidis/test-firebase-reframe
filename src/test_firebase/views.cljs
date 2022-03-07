(ns test-firebase.views
  (:require
   [re-frame.core :as re-frame]
   [test-firebase.subs :as subs]))

(defn main-panel []
  [:div
   (doall (map
           (fn [id]
             (let [available (re-frame/subscribe [::subs/available (str id)])
                   in-box (re-frame/subscribe [::subs/group-with (str id)])]
               ^{:key id}
               [:div
                "Available " id ": "
                (if-not (nil? @available) (str @available) "null")
                " in box: "
                (if-not (nil? @in-box) (str @in-box) "null")
          ;
                ]))
           (range 3)))]
     ;
  )



