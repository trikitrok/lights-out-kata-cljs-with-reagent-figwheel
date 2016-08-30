(ns kata-lights-out.lights-gateway
  (:require
    [cljs-http.client :as http]
    [cljs.core.async :as async]
    [com.stuartsierra.component :as component])
  (:require-macros
    [cljs.core.async.macros :refer [go go-loop]]))

(defn extract-lights [response]
  (->> response
       :body
       (.parse js/JSON)
       .-lights
       js->clj))

(defn- post [lights-channel uri params]
  (async/pipe
    (async/pipe
      (http/post uri
                 {:with-credentials? false
                  :form-params params})
      (async/chan 1 (map extract-lights))
      false)
    lights-channel))

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
