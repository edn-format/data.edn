(ns edn-data-gen.generators
  (require [clojure.test.generative.generators :as gen]
           [clojure.string :as string]))

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
     (let [parts (inc (if (fn? partitions-sizer)
                        (partitions-sizer)
                        partitions-sizer))]
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

(comment

(ns-symbol 5) ;; give me (sizer 5) ns-partions
;;=> my.name.space.is.super/awesome

(ns-symbol 5 8) ;; give me (sizer 5) ns-partions of (sizer 8) length
;;=> my______.name____.space___.is______.super___/awesome_


;; X (ns-symbol 5 4 16) ;; give me (sizer 5) ns-partions of (sizer 8) length
;;=> my__.name.spac.is__.supr/awesome__________________________


(ns-symbol-gen [5])
(ns-symbol-gen [5 8])
(ns-symbol-gen [5 4 16])



  (defn ns-symbol [parition-count-sizer parition-length-sizer sym-sizer]
    "
ns-parts-sizer: number of
")

  )
