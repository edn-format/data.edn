;;;; The use and distribution terms for this software are covered by the
;;;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;;; which can be found in the file epl-v10.html at the root of this distribution.
;;;; By using this software in any fashion, you are agreeing to be bound by
;;;; the terms of this license.
;;;; You must not remove this notice, or any other, from this software.

(ns ^{:author "Tom Hickey, Jim Altieri"}
  org.enclojure.edn.data.generators
  "An extension of clojure.data.generators... TODO"
  (:require [clojure.data.generators :as gen]
            [clojure.string :as string]
            [org.enclojure.impls.writer.io-writer :as io-writer]
            [org.enclojure.print.protocols.printable :as printable])
  (:refer-clojure :exclude [float]))

(defn call-through
  "Recursively call x until it doesn't return a function."
  [x]
  (if (fn? x)
    (recur (x))
    x))

(defn normal
  "Quick and dirty normal distribution around a mean based on java.util.Randon nextGaussian"
  [mean]
  (let [rndm (.nextGaussian (java.util.Random.))
        result (Math/round (+ mean
                              (* rndm (/ mean 5))))]
    (if (and (<= result (* 2 mean))
             (>= result 0))
      result
      (recur mean))))

(defn rescaled-geometric
  "Returns a sizer with geometric distribution around n"
  [n]
  #(gen/geometric (/ 1 n)))

(defn occasional
  ([generator gen-prob nil-prob]
     #(gen/weighted {nil nil-prob
                     generator gen-prob})))


(def not-yet-valid-edn-doubles
  #{Double/NaN Double/POSITIVE_INFINITY Double/NEGATIVE_INFINITY 1})

(defn is-valid-edn-double?
  [n]
  (not (some #(.equals % n) not-yet-valid-edn-doubles)))

(defn float
  []
  (let [f (gen/float)]
    (if (is-valid-edn-double? f)
      f
      (recur))))

(def num-gens
  [gen/int
   gen/long
   float
   gen/double])

(defn number
  []
  ((rand-nth num-gens)))

(defn date
  "Generates a random clojure date"
  []
  (java.util.Date. ((rescaled-geometric 1190433600000))))

(defn uuid
  []
  (java.util.UUID/randomUUID))


(def default-ns-partitions-sizer
  ^{:doc "Default sizer used to determine the number of partitions a generated namespace will have."}
  #(gen/uniform 1 6))

(def default-ns-part-length-sizer
  ^{:doc "Default sizer used to determine the length of symbol."}
  (normal 10))

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

(defn small-symbol
  []
  (gen/symbol #(gen/uniform 1 32)))

(defn any-symbol
  []
  (gen/one-of small-symbol ns-symbol))

(defn any-keyword
  []
  (keyword (any-symbol)))

(defn ns-keyword
  []
  (keyword (ns-symbol)))

(def ascii-alpha
  (concat (range 65 (+ 65 26))
          (range 97 (+ 97 26))))

(defn tag-prefix
  []
  (str (char (gen/rand-nth ascii-alpha))))

(defn tag-keyword
  []
  (keyword (str (tag-prefix) (ns-symbol))))

(def scalars
  [(constantly nil)
   gen/byte
   gen/long
   date
   uuid
   number
   gen/boolean
   gen/printable-ascii-char
   gen/string
   small-symbol
   any-keyword])

(defn scalar
  "Returns a random scalar."
  []
  (call-through (gen/rand-nth scalars)))

(def whitespace-chars
  [\newline \tab \return \space])

(def edn-whitespace-chars
  (conj whitespace-chars \,))

(defn whitespace-str
  ([]
     (whitespace-str gen/default-sizer))
  ([sizer]
     (gen/string #(char (rand-nth whitespace-chars)) sizer)))

(def default-whitespace-sizer
  (rescaled-geometric 5))

(defn weighted-whitespace-str
  ([]
     (weighted-whitespace-str default-whitespace-sizer))
  ([sizer]
     (gen/string (fn [] (gen/weighted {\space 20
                                       \newline 5
                                       #(char (rand-nth whitespace-chars)) 1}))
                 sizer)))

(defn edn-whitespace-str
  ([]
     (edn-whitespace-str gen/default-sizer))
  ([sizer]
     (gen/string #(char (rand-nth edn-whitespace-chars)) sizer)))

(defn comment-str
  []
  "Generate a semicolon, single-line string, and a newline"
  (str ";" (gen/string) "\n"))

(defn comment-line
  []
  "A comment-line can start with any amount of whitespace.
Before any non-whitespace characters must contain a semicolon.
Must end in a newline."
  (str (weighted-whitespace-str) (comment-str)))

(def default-comment-block-sizer
  (rescaled-geometric 2))

(defn comment-block
  ([]
     (comment-block default-comment-block-sizer))
  ([sizer]
      (gen/string comment-line sizer)))

(def collection-specs
  [[gen/vec 1]
   [gen/set 1]
   [gen/hash-map 2]])

(def default-collection-sizer
  ^{:doc "Default sizer used to determine the number of elements a generated collection will have."}
  #(normal 64))

(defn mixed-collection
  ([child-fn]
     (mixed-collection child-fn default-collection-sizer))
  ([child-fn coll-sizer]
      (let [[coll-fn arg-count] (rand-nth collection-specs)]
        (apply coll-fn  (conj (vec (for [_ (range arg-count)] #(child-fn)))
                              coll-sizer)))))

(def scalar-collection
  "Returns a random collection of scalar elements."
  (partial mixed-collection scalar))

(declare hierarchical-anything)

(def default-hierarchy-depth-sizer
  ^{:doc "Default sizer used to determine the depth a generated hierarchy will have."}
  #(gen/uniform 1 6))

(def default-hierarchy-length-sizer
  ^{:doc "Default sizer used to determine the depth a generated hierarchy will have."}
  #(gen/uniform 1 12))

(defn- hierarchy-recur
  "Generate a nested collection of items, where each item
   is generated by gen-item. Variablity of depth is controlled
by stop? fn."
  [stop? depth max-depth length-sizer gen-coll gen-item]
  (if (stop? depth max-depth)
    (gen-item)
    (recur stop? (inc depth) max-depth length-sizer gen-coll
           (fn [] (gen-coll gen-item length-sizer)))))

(defn hierarchy
  "Generate a nested collection of items, where each item
is generated by gen-item."
  [max-depth length-sizer gen-coll gen-item]
  (hierarchy-recur = 0 (call-through max-depth) length-sizer gen-coll gen-item))

(defn any-hierarchy
  "Generate a nested collection of items, where each item
   is generated by gen-item. Shallower nestings are much
   more likely."
  [max-depth length-sizer gen-coll gen-item]
  (hierarchy-recur (fn [d m]
                     (< (.nextDouble gen/*rnd*) (/ d m)))
                   0 (call-through max-depth) length-sizer gen-coll gen-item))

(comment
  (def _ (hierarchy 3
                    (rescaled-geometric 4)
                    mixed-collection
                    scalar))

  (def _ (hierarchy 7
                    #(gen/uniform 1 4)
                    mixed-collection
                    scalar))

  (def _ (any-hierarchy 3
                        (rescaled-geometric 16)
                        mixed-collection
                        scalar))
  (def _ (any-hierarchy 4
                        (gen/uniform 8)
                        mixed-collection
                        scalar))
  (def _ (any-hierarchy default-hierarchy-depth-sizer
                        default-hierarchy-length-sizer
                        mixed-collection
                        scalar)))
