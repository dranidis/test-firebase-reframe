(ns test-firebase.firebase-auth
  (:require ["firebase/auth" :as firebase-auth]))

(defn get-auth
  []
  (firebase-auth/getAuth))

(defn create-user
  [email password then-callback catch-callback]
  (-> (firebase-auth/createUserWithEmailAndPassword (get-auth) email password)
      (.then then-callback)
      (.catch catch-callback)))

(defn sign-in
  [email password then-callback catch-callback]
  (-> (firebase-auth/signInWithEmailAndPassword (get-auth) email password)
      (.then then-callback)
      (.catch catch-callback)))

(defn sign-out
  [then-callback catch-callback]
  (-> (firebase-auth/signOut (get-auth))
      (.then then-callback)
      (.catch catch-callback)))

(defn on-auth-state-changed
  "callbask is (fn [user] ... )"
  [callback]
  (firebase-auth/onAuthStateChanged (get-auth) callback))

(defn get-current-user-uid
  []
  (let [auth (get-auth)]
    (if (and auth (.-currentUser auth))
      (.-uid (.-currentUser auth))
      nil)))




;; some callback functions
(defn user-callback
  [userCredential]
  (println "User signed-in")
    ;; (println (js->clj (.-user userCredential)))
  (js/console.log ^js (.-user userCredential)))


(defn error-callback
  [error]
  (println "Error")
  (js/console.log error)
  (println (js->clj (.-code error)))
  (println (js->clj (.-message error))))

(defn on-auth-state-changed-callback
  [user]
  (if user
    (println "Singed in user uid: " (js->clj (.-uid user)) (js->clj (.-email user)))
    (println "logged-out")))

(comment

  (create-user "dranidis@gmail.com" "password" user-callback error-callback)
  (create-user "adranidisb@gmail.com" "password" user-callback error-callback)
  (create-user "some-random-user-1312321@gmail.com" "password" user-callback error-callback)

  (sign-in "dranidis@gmail.com" "password" user-callback error-callback)
  (sign-in "dranidis@gmail.com" "password" user-callback error-callback)

  (sign-in "dranidis@gmail.com" "wrond" user-callback error-callback)


  (on-auth-state-changed on-auth-state-changed-callback)

  (.stringify js/JSON (.-currentUser (get-auth)))

  (sign-out #(println "Sign-out successful") #(println "An error happened"))

  ;; does not work
  (js->clj (.-currentUser (get-auth)))

  ;; Works
  (js/console.log (.-currentUser (get-auth)))

  (.-uid (.-currentUser (get-auth)))
  (.-email (.-currentUser (get-auth)))

  (.-emailVerified (.-currentUser (get-auth)))


  ;; the following works
  (defn obj->clj
    [obj]
    (-> (fn [result key]
          (let [v (.get goog/object obj key)]
            (if (= "function" (goog/typeOf v))
              result
              (assoc result key v))))
        (reduce {} (.getKeys goog/object obj))))

  (println (obj->clj (.-currentUser (get-auth))))
  ;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

  ;; send an email for verification
  (-> (firebase-auth/sendEmailVerification (.-currentUser (get-auth)))
      (.then (println "Email sent")))



;;   {
;;   "rules": {
;;     ".read": true,
;;     ".write": true
;;   }
;; }
;
  )

