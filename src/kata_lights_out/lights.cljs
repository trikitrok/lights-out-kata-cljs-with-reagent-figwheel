(ns kata-lights-out.lights
  (:require
    [reagent.core :as r]
    [cljs.core.async :as async]
    [com.stuartsierra.component :as component]
    [kata-lights-out.lights-gateway :as lights-gateway])
  (:require-macros
    [cljs.core.async.macros :refer [go go-loop]]))

(def ^:private light-off 0)

(defn light-off? [light]
  (= light light-off))

(defn- listen-to-lights-updates! [{:keys [lights-channel lights]}]
  (go-loop []
    (when-let [new-lights (async/<! lights-channel)]
      (reset! lights new-lights)
      (recur))))

(defprotocol LightsOperations
  (reset-lights! [this m n])
  (flip-light! [this pos]))

(defrecord Lights [lights-gateway]
  component/Lifecycle
  (start [this]
    (println ";; Starting lights component")
    (let [this (assoc this
                      :lights-channel (async/chan)
                      :lights (r/atom []))
          lights-channel (:lights-channel this)
          lights-gateway (assoc lights-gateway
                                :lights-channel lights-channel)
          this (assoc this :lights-gateway lights-gateway)]
      (listen-to-lights-updates! this)
      this))

  (stop [this]
    (println ";; Stopping lights component")
    (async/close! (:lights-channel this))
    this)

  LightsOperations
  (reset-lights! [this m n]
    (lights-gateway/reset-lights! (:lights-gateway this) m n))

  (flip-light! [this pos]
    (lights-gateway/flip-light! (:lights-gateway this) pos)))

(defn all-lights-off? [lights]
  (every? light-off? (flatten lights)))

(defn make-lights []
  (map->Lights {}))