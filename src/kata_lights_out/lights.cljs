(ns kata-lights-out.lights
  (:require
    [cljs-http.client :as http]
    [cljs.core.async :as async])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(def ^:private light-off 0)

(defn light-off? [light]
  (= light light-off))

(defn- extract-lights [response]
  (->> response
       :body
       (.parse js/JSON)
       .-lights
       js->clj))

(defn flip-light [[x y]]
  (go
    (let [response (async/<! (http/post "http://localhost:3000/flip-light"
                                        {:with-credentials? false
                                         :form-params {:x x :y y}}))]
      )))

(defn reset-lights! [result-channel m n]
  (go
    (let [response (async/<! (http/post "http://localhost:3000/reset-lights"
                                        {:channel (async/chan 1)
                                         :with-credentials? false
                                         :form-params {:m m :n n}}))]
      ;(async/>! result-channel (:body response))

      (println (extract-lights response))
      )))

(defn all-lights-off? [lights]
  (every? zero? (flatten lights)))
