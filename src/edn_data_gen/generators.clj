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

(comment

(ns-symbol 5) ;; give me (sizer 5) ns-partions
;;=> my.name.space.is.super/awesome

(ns-symbol 5 8) ;; give me (sizer 5) ns-partions of (sizer 8) length
;;=> my______.name____.space___.is______.super___/awesome_


(ns-symbol 5 4 16) ;; give me (sizer 5) ns-partions of (sizer 8) length
;;=> my__.name.spac.is__.supr/awesome__________________________


(ns-symbol-gen [5])
(ns-symbol-gen [5 8])
(ns-symbol-gen [5 4 16])



  (defn ns-symbol [parition-count-sizer parition-length-sizer sym-sizer]
    "
ns-parts-sizer: number of
")

  )
