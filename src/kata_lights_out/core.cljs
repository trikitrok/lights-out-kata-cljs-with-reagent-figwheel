(ns kata-lights-out.core
  (:require
    [reagent.core :as r]
    [kata-lights-out.lights :as l]))

(enable-console-print!)

;; -------------------------
;; Views

(declare init!)

(def m 3)
(def n 3)

(def light-on "1")
(def light-off "0")

(def lights (r/atom []))

(defn message-component [lights]
  [:div#all-offmsg
   (if (l/all-lights-off? lights)
     "Lights out, Yay!"
     {:style {:display :none}})])

(defn on-light-click [pos lights]
  (swap! lights (partial l/flip-neighbors m n pos)))

(defn render-light [light]
  (if (l/light-off? light)
    light-off
    light-on))

(defn light-component [i j light]
  ^{:key (+ i j)} [:button {:on-click #(on-light-click [i j] lights)} (render-light light)])

(defn row-lights-component [i row-lights]
  ^{:key i} [:div (map-indexed (partial light-component i) row-lights)])

(defn home-page []
  [:div [:h2 "Kata Lights Out"]
   (map-indexed row-lights-component @lights)
   [message-component @lights]])

;; -------------------------
;; Initialize app

(defn mount-root []
  (r/render
    [home-page]
    (.getElementById js/document "app")))

(defn init! []
  (reset! lights (l/all-lights-on m n))
  (mount-root))

(init!)