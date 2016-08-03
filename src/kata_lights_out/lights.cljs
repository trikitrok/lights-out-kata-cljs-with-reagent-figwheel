(ns kata-lights-out.lights)

(def ^:private light-on 1)
(def ^:private light-off 0)

(defn all-lights-on [m n]
  (mapv #(vec (repeat n light-on))
        (range m)))

(defn- neighbors? [[i0 j0] [i j]]
  (or (and (= j0 j) (= 1 (Math/abs (- i0 i))))
      (and (= i0 i) (= 1 (Math/abs (- j0 j))))))

(defn- neighbors [m n pos]
  (for [i (range m)
        j (range n)
        :when (neighbors? pos [i j])]
    [i j]))

(defn light-off? [light]
  (= light light-off))

(defn- flip-light [light]
  (if (light-off? light)
    light-on
    light-off))

(defn- flip [lights pos]
  (update-in lights pos flip-light))

(defn flip-neighbors [m n pos lights]
  (->> pos
       (neighbors m n)
       (cons pos)
       (reduce flip lights)))

(defn all-lights-off? [lights]
 (every? zero? (flatten lights)))
