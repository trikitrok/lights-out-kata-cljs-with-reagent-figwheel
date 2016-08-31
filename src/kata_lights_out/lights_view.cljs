(ns kata-lights-out.lights-view
  (:require
    [reagent.core :as r]
    [kata-lights-out.lights :as lights]
    [reagi.core :as reagi]))

(def ^:private light-on "1")
(def ^:private light-off "0")

(defn- all-lights-off-message-content [lights]
  (if (lights/all-lights-off? lights)
    "Lights out, Yay!"
    {:style {:display :none}}))

(defn- all-lights-off-message-component [lights]
  [:div#all-off-msg
   (all-lights-off-message-content lights)])

(defn- render-light [light]
  (if (lights/light-off? light)
    light-off
    light-on))

(defn- light-component [clicked-light-positions i j light]
  ^{:key (+ i j)}
  [:button
   {:on-click #(reagi/deliver clicked-light-positions [i j])}
   (render-light light)])

(defn- row-lights-component [clicked-light-positions i row-lights]
  ^{:key i}
  [:div (map-indexed (partial light-component clicked-light-positions i) row-lights)])

(defn- home-page [clicked-light-positions lights-component]
  (fn []
    (let [lights (:lights lights-component)]
      [:div [:h2 "Kata Lights Out"]
       (map-indexed (partial row-lights-component clicked-light-positions) @lights)
       [all-lights-off-message-component @lights]])))

(defn- flip-light-when-clicked [lights-component clicked-light-positions]
  (->> clicked-light-positions
       (reagi/map #(lights/flip-light! lights-component %))))

(defn mount [lights-component]
  (let [clicked-light-positions (reagi/events)]

    (flip-light-when-clicked
      lights-component clicked-light-positions)

    (r/render
      [home-page clicked-light-positions lights-component]
      (.getElementById js/document "app"))))
