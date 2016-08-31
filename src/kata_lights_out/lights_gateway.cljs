(ns kata-lights-out.lights-gateway
  (:require
    [cljs-http.client :as http]
    [com.stuartsierra.component :as component]
    [reagi.core :as reagi]
    [cljs.core.async :as async])
  (:require-macros
    [cljs.core.async.macros :refer [go]]))

(defn- extract-lights [response]
  (->> response
       :body
       (.parse js/JSON)
       .-lights
       js->clj))

(defn- post [lights-stream uri params]
  (go
    (when-let [response (async/<! (http/post uri {:with-credentials? false
                                                  :form-params params}))]
      (reagi/deliver lights-stream
                     (extract-lights response)))))

(defprotocol LightsGateway
  (reset-lights! [this m n])
  (flip-light! [this pos]))

(defrecord ApiLightsGateway [config]
  component/Lifecycle
  (start [this]
    (println ";; Starting ApiLightsGateway component")
    (assoc this :lights-stream (reagi/events)))

  (stop [this]
    (println ";; Stopping ApiLightsGateway component")
    (reagi/dispose (:lights-stream this))
    this)

  LightsGateway
  (reset-lights! [this m n]
    (post (:lights-stream this)
          (:reset-lights-url config)
          {:m m :n n}))

  (flip-light! [this [x y]]
    (post (:lights-stream this)
          (:flip-light-url config)
          {:x x :y y})))

(defn make-api-gateway [config]
  (->ApiLightsGateway config))
