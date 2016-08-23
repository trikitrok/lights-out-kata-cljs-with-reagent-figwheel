(ns kata-lights-out.lights
  (:require
    [reagent.core :as r]
    [cljs-http.client :as http]
    [cljs.core.async :as async])
  (:require-macros
    [cljs.core.async.macros :refer [go go-loop]]))

(def ^:private lights (r/atom []))

(def ^:private light-off 0)

(defn light-off? [light]
  (= light light-off))

(defn- extract-lights [response]
  (->> response
       :body
       (.parse js/JSON)
       .-lights
       js->clj))

(defn listen-lights-updates! [lights-channel]
  (go-loop []
    (when-let [response (async/<! lights-channel)]
      (reset! lights (extract-lights response))
      (recur))))

(defn flip-light! [result-channel [x y]]
  (async/pipe
    (http/post "http://localhost:3000/flip-light"
               {:with-credentials? false
                :form-params {:x x :y y}})
    result-channel
    false))

(defn reset-lights! [result-channel m n]
  (async/pipe
    (http/post "http://localhost:3000/reset-lights"
               {:with-credentials? false
                :form-params {:m m :n n}})
    result-channel
    false))

(defn all-lights-off? [lights]
  (every? light-off? (flatten lights)))
