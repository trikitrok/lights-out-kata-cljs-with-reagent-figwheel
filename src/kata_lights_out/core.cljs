(ns kata-lights-out.core
  (:require
    [kata-lights-out.lights-view :as lights-view]
    [com.stuartsierra.component :as component]
    [kata-lights-out.lights :as lights]
    [kata-lights-out.lights-gateway :as lights-gateway]))

(enable-console-print!)

(defn init! [m n]
  (component/start
    (component/system-map
      :lights-gateway (lights-gateway/make-api-gateway
                        {:reset-lights-url "http://localhost:3000/reset-lights"
                         :flip-light-url "http://localhost:3000/flip-light"})

      :lights-component (component/using
                          (lights/make-lights m n)
                          [:lights-gateway])

      :lights-view (component/using
                     (lights-view/make {:success-message "Lights out, Yay!"
                                        :light-on "1"
                                        :light-off "0"
                                        :title "Kata Lights Out"})
                     [:lights-component]))))

(init! 3 3)
