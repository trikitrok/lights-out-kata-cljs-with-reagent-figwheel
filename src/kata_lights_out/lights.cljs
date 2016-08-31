(ns kata-lights-out.lights
  (:require
    [reagent.core :as r]
    [com.stuartsierra.component :as component]
    [kata-lights-out.lights-gateway :as lights-gateway]
    [reagi.core :as reagi]))

(def ^:private light-off 0)

(defn light-off? [light]
  (= light light-off))

(defn- listen-to-lights-updates! [{:keys [lights-stream lights]}]
  (->> lights-stream
       (reagi/map #(reset! lights %))))

(defprotocol LightsOperations
  (reset-lights! [this m n])
  (flip-light! [this pos]))

(defrecord Lights [lights-gateway]
  component/Lifecycle
  (start [this]
    (println ";; Starting lights component")
    (let [this (assoc this
                      :lights-stream (:lights-stream lights-gateway)
                      :lights (r/atom []))
          this (assoc this :lights-gateway lights-gateway)]
      (listen-to-lights-updates! this)
      this))

  (stop [this]
    (println ";; Stopping lights component")
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
