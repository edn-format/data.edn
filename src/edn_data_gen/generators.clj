(ns edn-data-gen.generators
  (:require [clojure.test.generative.generators :as gen]
            [edn-data-gen.print.protocols.printable :as printable]
            [clojure.string :as string]
            [edn-data-gen.print.writers.io-writer :as io-writer]))

(defn call-through
  "Recursively call x until it doesn't return a function."
  [x]
  (if (fn? x)
    (recur (x))
    x))

(def num-gens
  [gen/int
   gen/long
   gen/float
   gen/double])

(defn numbers
  []
  (fn [] ((rand-nth num-gens))))

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

(defn any-symbol
  []
  (gen/one-of gen/symbol ns-symbol))

(defn any-keyword
  []
  (keyword (any-symbol)))

(defn ns-keyword
  []
  (keyword (ns-symbol)))

;; TODO: this fn should be moved to the edn-data-gen/print namespace when it exists
(defn keyword->tag-str
  [k]
  (if-let [ns (namespace k)]
    (str "#" ns "/" (name k))
    (str "#" (name k))))

(def whitespace-chars
  [\newline \tab \formfeed \return \space])

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
  []
  (gen/string comment-line default-comment-block-sizer))

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

(declare hierarchical-anything)

(def default-hierarchy-sizer
  ^{:doc "Default sizer used to determine the depth a generated hierarchy will have."}
  #(gen/uniform 1 4))

(defn hierarchical-collection
  ([] (hierarchical-collection (default-hierarchy-sizer)))
  ([depth]
     (if (pos? depth)
       (mixed-collection (partial hierarchical-anything (dec depth)))
       scalar-collection)))

(def default-anything-sizer
  ^{:doc "Default sizer used to determine the depth a generated hierarchy will have."}
  #(gen/uniform 0 4))

(defn hierarchical-anything
  "Returns a function which returns one of either:
a scalar-fn
a coll-fn of scalars e.g. (gen/ven scalar)
a hierarchical-coll-fn (of partial depth) hierarchcal-anything's"
  ([] (hierarchical-anything (default-anything-sizer)))
  ([depth]
     (if (pos? depth)
       (gen/one-of gen/scalar scalar-collection (partial hierarchical-collection depth))
       (gen/scalar))))

;; (defn hierarchical-anything
;;   "Returns a function which returns one of either:
;; a scalar-fn
;; a coll-fn of scalars e.g. (gen/ven scalar)
;; a hierarchical-coll-fn (of partial depth) hierarchcal-anything's"
;;   [depth]
;;   (gen/one-of gen/scalar scalar-collection (partial hierarchical-collection depth)))

(defn edn-str
  [generator opts]
  (let [sw (java.io.StringWriter.)]
    (printable/print (generator) sw opts)
    (str sw)))

(defn edn-file
  [generator filewriter opts]
  (printable/print (generator) filewriter opts))
