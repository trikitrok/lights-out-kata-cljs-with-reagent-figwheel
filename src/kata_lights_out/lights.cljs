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

(defn- post [lights-channel uri params]
  (async/pipe
    (http/post uri
               {:with-credentials? false
                :form-params params})
    lights-channel
    false))

(defprotocol LightsGateway
  (reset-lights! [this m n])
  (flip-light! [this pos]))

(defrecord ApiLightsGateway [config lights-channel]
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

  LightsGateway
  (reset-lights! [this m n]
    (post (:lights-channel this)
          (:reset-lights-url config)
          {:m m :n n}))

  (flip-light! [this [x y]]
    (post (:lights-channel this)
          (:flip-light-url config)
          {:x x :y y})))

(defn all-lights-off? [lights]
  (every? light-off? (flatten lights)))

(defn make-api-gateway [config channel]
  (map->ApiLightsGateway
    {:config config
     :lights-channel channel}))