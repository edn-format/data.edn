(ns edn-data-gen.generators
  (require [clojure.test.generative.generators :as gen]
           [clojure.string :as string]))

;; (defn get-size
;;   [sizer]
;;   (if (fn? sizer)
;;     (sizer)
;;     sizer))

(defn call-through
  "Recursively call x until it doesn't return a function."
  [x]
  (if (fn? x)
    (recur (x))
    x))

(def default-ns-partitions-sizer
  ^{:doc "Default sizer used to determine the number of partitions a generated namespace will have."}
  #(gen/uniform 1 6))

(defn rescaled-geometric
  "Returns a sizer with geometric distribution around n"
  [n]
  #(gen/geometric (/ 1 n)))

(def default-ns-part-length-sizer
  ^{:doc "Default sizer used to determine the length of symbol."}
  (rescaled-geometric 10))

(defn ns-str
  "Returns a string to be passed into core/symbol as the namespace. Number of partitions of namespace is determined by sizer supplied in first argument, length of the parts are determined by the sizer supplied in second argument"
  ([]
     (ns-str default-ns-partitions-sizer default-ns-part-length-sizer))
  ([partitions-sizer part-length-sizer]
     (let [parts (inc (call-through partitions-sizer))]
       (string/join "." (for [_ (range parts)]
                          (gen/symbol part-length-sizer))))))


(defn ns-symbol
  "Generates a fully qualified symbol. Number of partitions determined by sizer in first argument, length of parts determined by sizer in second argument."
  ([]
     (ns-symbol default-ns-partitions-sizer default-ns-part-length-sizer))
  ([ns-partitions-sizer]
     (ns-symbol ns-partitions-sizer default-ns-part-length-sizer))
  ([ns-partitions-sizer part-length-sizer]
     (symbol (ns-str ns-partitions-sizer part-length-sizer) (str (gen/symbol part-length-sizer)))))



(def collection-specs
  [[gen/vec 1]
   [gen/set 1]
   [gen/hash-map 2]])

(defn mixed-collection
  [child-fn]
  (let [[coll-fn arg-count] (rand-nth collection-specs)]
    (apply coll-fn  (for [_ (range arg-count)] #(child-fn)))))

(def scalar-collection
  "Returns a random collection of scalar elements."
  (partial mixed-collection gen/scalar))

(declare hierarchical-collection)

(defn hierarchical-anything
  "Returns a function which returns one of either:
a scalar-fn
a coll-fn of scalars e.g. (gen/ven scalar)
a hierarchical-coll-fn (of partial depth) hierarchcal-anything's
"
  [depth]
  (gen/one-of gen/scalar scalar-collection (partial hierarchical-collection depth)))

(defn hierarchical-collection
  [depth]
  (if (pos? depth)
    (mixed-collection (partial hierarchical-anything (dec depth)))
    scalar-collection))
