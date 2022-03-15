(ns test-firebase.utils
  (:require [clojure.string :as s]))

(defn is-substring?
  [substring str]
  (s/includes? (s/upper-case str) (s/upper-case substring)))


(defn random-char
  []
  (nth (->> (range 10)
            (map #(+ 65 %))
            (map char)
            (into [])) (rand-int 10)))

(defn random-word
  [len]
  (->> (range len)
       (map (fn [_] (random-char)))
       (s/join)))

(defn random-name-map
  [len] (->> (range len)
             (map (fn [n] {:id n :name (random-word 10)}))
             (into [])))



(def random-names-new (random-name-map 300))


(comment
  (->> random-names-new
       (filter
        (fn [{:keys [_ name]}]
          (or (nil? "AA") (is-substring? "AA" name))))))

