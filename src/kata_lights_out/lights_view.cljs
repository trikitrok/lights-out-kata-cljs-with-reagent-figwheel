(ns kata-lights-out.lights-view
  (:require
    [reagent.core :as r]
    [kata-lights-out.lights :as l]))

(def ^:private light-on "1")
(def ^:private light-off "0")

(defn- all-lights-off-message-content [lights]
  (if (l/all-lights-off? lights)
    "Lights out, Yay!"
    {:style {:display :none}}))

(defn- all-lights-off-message-component [lights]
  [:div#all-off-msg (all-lights-off-message-content lights)])

(defn- on-light-click [pos m n lights]
  (swap! lights (partial l/flip-neighbors m n pos)))

(defn- render-light [light]
  (if (l/light-off? light)
    light-off
    light-on))

(defn- light-component [m n lights i j light]
  ^{:key (+ i j)} [:button {:on-click #(on-light-click [i j] m n lights)} (render-light light)])

(defn- row-lights-component [m n lights i row-lights]
  ^{:key i} [:div (map-indexed (partial light-component m n lights i) row-lights)])

(defn- home-page [m n lights]
  [:div [:h2 "Kata Lights Out"]
   (map-indexed (partial row-lights-component m n lights) @lights)
   [all-lights-off-message-component @lights]])

(defn mount [m n lights]
  (r/render
    [home-page m n lights]
    (.getElementById js/document "app")))