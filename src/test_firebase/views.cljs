(ns test-firebase.views
  (:require
   [re-frame.core :as re-frame]
   [test-firebase.subs :as subs]))

(defn main-panel []
  [:div
   (doall (map
           (fn [id]
             ^{:key id}
             (let [available (re-frame/subscribe [::subs/on-value ["games" (str id) "available"]])
                   in-box (re-frame/subscribe [::subs/on-value ["games" (str id) "group-with"]])]
               [:div
                "Available " id ": "
                (if-not (nil? @available) (str @available) false)
                " in box: "
                (if-not (nil? @in-box) (str @in-box) "no")
          ;
                ]))
           (range 5)))]
     ;
  )



