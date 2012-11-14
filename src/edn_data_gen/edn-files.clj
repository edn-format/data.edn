(ns edn-data-gen.edn-files
  (require [clojure.test.generative.generators :as gen]
           [clojure.string :as string]
           [clojure.java.io :as io]
           [edn-data-gen.generators :as edngen]))

(defn write-edn
  ([data] ;;data is a sequence of edn elements to be written
     (write-edn data "output/out.edn"))
  ([data filename]
     (with-open [wrtr (io/writer (str "output/" filename))]
       (.write wrtr (string/join " " data)))))

(def num-gens
  [gen/int
   gen/long
   gen/float
   gen/double])

(defn file-of-numbers
  ([n filename]
     (write-edn (repeatedly n (fn [] ((rand-nth num-gens)))) filename)))