(ns kata-lights-out.lights-view
  (:require
    [reagent.core :as r]
    [kata-lights-out.lights :as lights]
    [reagi.core :as reagi]
    [com.stuartsierra.component :as component]))

(defn- all-lights-off-message-content [config lights]
  (if (lights/all-lights-off? lights)
    (:success-message config)
    {:style {:display :none}}))

(defn- all-lights-off-message-component [config lights]
  [:div#all-off-msg
   (all-lights-off-message-content config lights)])

(defn- render-light [{:keys [light-on light-off]} light]
  (if (lights/light-off? light)
    light-off
    light-on))

(defn- light-component [config clicked-light-positions i j light]
  ^{:key (+ i j)}
  [:button
   {:on-click #(reagi/deliver clicked-light-positions [i j])}
   (render-light config light)])

(defn- row-lights-component [config clicked-light-positions i row-lights]
  ^{:key i}
  [:div (map-indexed (partial light-component config clicked-light-positions i) row-lights)])

(defn- home-page [config clicked-light-positions lights-component]
  (fn []
    (let [lights (:lights lights-component)]
      [:div [:h2 (:title config)]
       (map-indexed (partial row-lights-component config clicked-light-positions) @lights)
       [all-lights-off-message-component config @lights]])))

(defn- flip-light-when-clicked [lights-component clicked-light-positions]
  (->> clicked-light-positions
       (reagi/map #(lights/flip-light! lights-component %))))

(defprotocol View
  (mount [this]))

(defrecord LightsView [lights-component config]
  component/Lifecycle
  (start [this]
    (println ";; Starting LightsOutView component")
    (let [this (assoc this :clicked-light-positions (reagi/events))]
      (mount this)
      this))

  (stop [this]
    (println ";; Stopping LightsOutView component")
    (reagi/dispose (:clicked-light-positions this))
    this)

  View
  (mount [{:keys [clicked-light-positions]}]
    (flip-light-when-clicked
      lights-component clicked-light-positions)

    (r/render
      [home-page config clicked-light-positions lights-component]
      (.getElementById js/document "app"))))

(defn make [config]
  (map->LightsView {:config config}))