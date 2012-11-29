(ns org.enclojure.edn.file.generation-test
  (:require [clojure.data.generators :as gen]
            [clojure.java.io :as io]
            [org.enclojure.edn.data.generators :as edn-gen]
            [org.enclojure.edn.file.generation :as files]
            [org.enclojure.edn.file.util :as util])
  (:use clojure.test))

(defn test-file-of
  ([generator n]
     (test-file-of generator n {}))
  ([generator n opts]
     (doseq [out (files/file-gen (partial files/file-of generator) util/file-path n opts)]
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
  (test-file-of edn-gen/hierarchical-anything 10)
  (test-file-of edn-gen/hierarchical-anything 10 {:generator/comment edn-gen/comment-block})
  (test-file-of edn-gen/hierarchical-anything 100 {:generator/comment (edn-gen/occasional edn-gen/comment-block 3)
                                                   :generator/whitespace (edn-gen/occasional edn-gen/whitespace-str 30)
                                        ;:generator/tag (edn-gen/occasional edn-gen/ns-keyword 5)
                                                   :generator/discard (edn-gen/occasional gen/scalar 5)}))
