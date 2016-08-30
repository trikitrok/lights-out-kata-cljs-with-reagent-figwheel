(ns kata-lights-out.lights-view
  (:require
    [reagent.core :as r]
    [kata-lights-out.lights :as lights]))

(def ^:private light-on "1")
(def ^:private light-off "0")

(defn- all-lights-off-message-content [lights]
  (if (lights/all-lights-off? lights)
    "Lights out, Yay!"
    {:style {:display :none}}))

(defn- all-lights-off-message-component [lights]
  [:div#all-off-msg (all-lights-off-message-content lights)])

(defn- on-light-click [lights-component pos]
  (lights/flip-light! lights-component pos))

(defn- render-light [light]
  (if (lights/light-off? light)
    light-off
    light-on))

(defn- light-component [lights-component i j light]
  ^{:key (+ i j)}
  [:button
   {:on-click #(on-light-click lights-component [i j])} (render-light light)])

(defn- row-lights-component [lights-component i row-lights]
  ^{:key i}
  [:div (map-indexed (partial light-component lights-component i) row-lights)])

(defn- home-page [lights-component]
  (fn []
    [:div [:h2 "Kata Lights Out"]
     (map-indexed (partial row-lights-component lights-component) @(:lights lights-component))
     [all-lights-off-message-component @(:lights lights-component)]]))

(defn mount [lights-component m n]
  (lights/reset-lights! lights-component m n)

  (r/render
    [home-page lights-component]
    (.getElementById js/document "app")))