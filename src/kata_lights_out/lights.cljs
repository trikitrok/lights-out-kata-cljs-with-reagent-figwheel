(ns kata-lights-out.lights
  (:require
    [reagent.core :as r]
    [cljs-http.client :as http]
    [cljs.core.async :as async]
    [com.stuartsierra.component :as component])
  (:require-macros
    [cljs.core.async.macros :refer [go go-loop]]))

(def ^:private light-off 0)

(defn light-off? [light]
  (= light light-off))

(defn- extract-lights [response]
  (->> response
       :body
       (.parse js/JSON)
       .-lights
       js->clj))

(defn listen-to-lights-updates! [{:keys [lights-channel lights]}]
  (go-loop []
    (when-let [response (async/<! lights-channel)]
      (reset! lights (extract-lights response))
      (recur))))

(defprotocol LightsOperations
  (reset-lights! [this m n])
  (flip-light! [this pos]))

(defrecord Lights [lights-channel]
  component/Lifecycle
  (start [this]
    (println ";; Starting lights component")
    (let [this (merge this {:lights-channel lights-channel
                            :lights (r/atom [])})]
      (listen-to-lights-updates! this)
      this))

  (stop [this]
    (println ";; Stopping lights component")
    this)

  LightsOperations
  (reset-lights! [this m n]
    (async/pipe
      (http/post "http://localhost:3000/reset-lights"
                 {:with-credentials? false
                  :form-params {:m m :n n}})
      (:lights-channel this)
      false))

  (flip-light! [this [x y]]
    (async/pipe
      (http/post "http://localhost:3000/flip-light"
                 {:with-credentials? false
                  :form-params {:x x :y y}})
      (:lights-channel this)
      false)))

(defn all-lights-off? [lights]
  (every? light-off? (flatten lights)))