(ns kata-lights-out.lights-gateway
  (:require
    [cljs-http.client :as http]
    [cljs.core.async :as async]
    [com.stuartsierra.component :as component]))

(defn- extract-lights [response]
  (->> response
       :body
       (.parse js/JSON)
       .-lights
       js->clj))

(defn- post [lights-channel uri params]
  (async/pipeline
    1
    lights-channel
    (map extract-lights)
    (http/post uri {:with-credentials? false :form-params params})
    false))

(defprotocol LightsGateway
  (reset-lights! [this m n])
  (flip-light! [this pos]))

(defrecord ApiLightsGateway [config]
  component/Lifecycle
  (start [this]
    (println ";; Starting ApiLightsGateway component")
    this)

  (stop [this]
    (println ";; Stopping ApiLightsGateway component")
    this)

  LightsGateway
  (reset-lights! [this m n]
    (post (:lights-channel this)
          (:reset-lights-url config)
          {:m m :n n}))

  (flip-light! [this [x y]]
    (post (:lights-channel this)
          (:flip-light-url config)
          {:x x :y y})))

(defn make-api-gateway [config]
  (->ApiLightsGateway config))
