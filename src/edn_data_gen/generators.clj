(ns edn-data-gen.generators
  (require [clojure.test.generative.generators :as gen]
           [clojure.string :as string]))

(defn ns-str
  ([]
     (str (gen/symbol)))
  ([& sizers]
     (string/join "." (for [sizer sizers] (gen/symbol sizer)))))


(defn ns-symbol
  ([]
     (symbol (ns-str) (str (gen/symbol))))
  ([& sizers]
     (let [ns-sizers (butlast sizers)
           sym-sizer (last sizers)]
       (symbol (apply ns-str ns-sizers) (str (gen/symbol sym-sizer))))))
