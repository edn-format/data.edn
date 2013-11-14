;;;; The use and distribution terms for this software are covered by the
;;;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;;; which can be found in the file epl-v10.html at the root of this distribution.
;;;; By using this software in any fashion, you are agreeing to be bound by
;;;; the terms of this license.
;;;; You must not remove this notice, or any other, from this software.

(ns ^{:author "Tom Hickey, Jim Altieri"}
  org.edn-format.data.edn-test
  (:require [clojure.data.generators :as gen]
            [clojure.java.io :as io]
            [org.edn-format.data.edn.generators :as edn-gen]
            [org.edn-format.data.edn :as edn]
            [org.edn-format.data.edn.file-util :as util])
  (:use clojure.test))

(defn test-file-of
  ([generator n]
     (test-file-of generator n {}))
  ([generator n opts]
     (doseq [out (edn/gen-files-of-many generator util/file-path n opts)]
       (let [path (:out out)
             data (with-open [r (io/reader path)]
                    (loop [acc []
                           rdr (java.io.PushbackReader. r)]
                      (if-let [r (read rdr false nil)]
                        (recur (conj acc r)
                               rdr)
                        acc)))]
         (if (= data (:data out))
           (io/delete-file (io/file path))
           (printf "failed \nexpected: %s\ngot:%s\n" (class (:data out)) (class data)))))))

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
  (test-file-of edn-gen/any-hierarchy 10)
  (test-file-of edn-gen/any-hierarchy 10 {:generator/comment edn-gen/comment-block})
  (test-file-of edn-gen/any-hierarchy 100 {:generator/comment (edn-gen/occasional edn-gen/comment-block 1 33)
                                           :generator/whitespace (edn-gen/occasional edn-gen/whitespace-str 3 10)
                                           ;;:generator/tag (edn-gen/occasional edn-gen/ns-keyword 1 20)
                                           :generator/discard (edn-gen/occasional gen/scalar 1 20)}))

(defn test-string
  ([generator n]
     (test-string generator n {}))
  ([generator n opts]
     (doseq [out (edn/many-strings generator n opts)]
       (let [read-data (read-string (out :out))]
         (when-not (= read-data
                      (out :data))
           (print "test fail on string: " (out :out) ", expected data: " (out :data)))))))


(defn test-strings
  []
  (test-string gen/int 10)
  (test-string edn-gen/float 10)
  (test-string gen/string 10)
  (test-string gen/keyword 10)
  (test-string (partial gen/vec gen/int) 10)
  (test-string (partial gen/vec edn-gen/float) 10)
  (test-string (partial gen/vec gen/string) 10)
  (test-string (partial gen/vec gen/keyword) 10)
  (test-string edn-gen/any-hierarchy 10)
  (test-string edn-gen/any-hierarchy 10 {:generator/comment edn-gen/comment-block})
  (test-string edn-gen/any-hierarchy 100 {:generator/comment (edn-gen/occasional edn-gen/comment-block 1 33)
                                          :generator/whitespace (edn-gen/occasional edn-gen/whitespace-str 3 10)
                                          ;;:generator/tag (edn-gen/occasional edn-gen/ns-keyword 1 20)
                                          :generator/discard (edn-gen/occasional gen/scalar 1 20)}))

(comment
  (require '[org.edn-format.impls.printable.default :as printable-default]
           '[org.edn-format.impls.writer.io-writer :as io-writer])
  (test-strings)
  (test-files))
