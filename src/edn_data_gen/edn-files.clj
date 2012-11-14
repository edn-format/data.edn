(ns edn-data-gen.edn-files
  (require [clojure.test.generative.generators :as gen]
           [clojure.java.io :as io]
           [edn-data-gen.generators :as edngen]))

(defn write-edn
  ([data]
     (write-edn data "output/test.txt"))
  ([data filename]
     (with-open [wrtr (io/writer filename)]
       (.write wrtr (str data)))))

