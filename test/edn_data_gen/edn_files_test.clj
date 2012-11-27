(ns edn-data-gen.edn-files-test
  "Short package description."
  (:require [edn-data-gen.edn-files :as files]
            [edn-data-gen.generators :as edn-gen]
            [clojure.test.generative.generators :as gen]
            [clojure.java.io :as io])
  (:use clojure.test))

(defn test-file-of
  ([generator n]
     (doseq [out (files/file-gen (partial files/file-of generator) files/file-path n {})]
       (let [read-data (with-open [r (io/reader (out :path))]
                         (read (java.io.PushbackReader. r)))]
         (if (= read-data
                (out :data))
           (io/delete-file (io/file (out :path)))
           (print "test fail on file: " (out :path) ", expected data: " (out :data)))))))

(defn test-files
  []
  (test-file-of gen/int 10)
  (test-file-of edn-gen/float 10)
  (test-file-of gen/string 10)
  (test-file-of gen/keyword 10)
  (test-file-of (partial gen/vec gen/int) 10)
  (test-file-of (partial gen/vec edn-gen/float) 10)
  (test-file-of (partial gen/vec gen/string) 10)
  (test-file-of (partial gen/vec gen/keyword) 10)
  (test-file-of edn-gen/hierarchical-anything 10))
