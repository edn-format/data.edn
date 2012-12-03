;;;; The use and distribution terms for this software are covered by the
;;;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;;; which can be found in the file epl-v10.html at the root of this distribution.
;;;; By using this software in any fashion, you are agreeing to be bound by
;;;; the terms of this license.
;;;; You must not remove this notice, or any other, from this software.

(ns ^{:author "Tom Hickey, Jim Altieri"}
  com.thortech.data.edn.suite
  "Generation of a 'suite' of data for testing edn generation"
  (:require [clojure.data.generators :as gen]
            [com.thortech.data.edn.generators :as edn-gen]))

(defn lots-of-data
  "Generate a map of lots of data"
  []
  {:scalars {:floats (gen/vec edn-gen/float)
             :ints (gen/vec gen/int)
             :numbers (gen/vec edn-gen/number)
             :insts (gen/vec edn-gen/date)
             :uuid (gen/vec edn-gen/uuid)
             :any (gen/vec edn-gen/scalar)}
   :maps {:keyword-to-int (gen/hash-map edn-gen/any-keyword gen/int)
          :scalar-to-int (gen/hash-map edn-gen/scalar gen/int)
          :symbol-to-int (gen/hash-map edn-gen/any-symbol gen/int)}
   :hierarchies {:vec-of-vecs (edn-gen/hierarchy 2
                                                 (gen/uniform 1 4)
                                                 gen/vec
                                                 gen/int)
                 :keyword-to-map-or-int (edn-gen/hierarchy 2
                                                           2
                                                           (partial gen/hash-map gen/keyword)
                                                           gen/int)
                 :vecs-of-maps (edn-gen/hierarchy 2
                                                  2
                                                  gen/vec
                                                  #(gen/hash-map gen/keyword gen/int))
                 :mixed (edn-gen/hierarchy 3 5 edn-gen/mixed-collection edn-gen/scalar)
                 :variable-depth (edn-gen/any-hierarchy 4 4 edn-gen/mixed-collection edn-gen/scalar)}})
