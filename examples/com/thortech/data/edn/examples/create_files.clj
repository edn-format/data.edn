;;;; The use and distribution terms for this software are covered by the
;;;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;;; which can be found in the file epl-v10.html at the root of this distribution.
;;;; By using this software in any fashion, you are agreeing to be bound by
;;;; the terms of this license.
;;;; You must not remove this notice, or any other, from this software.

(ns ^{:author "Tom Hickey, Jim Altieri"}
  com.thortech.data.edn.examples.create-files
  "Example of creating files of edn data using the printable.interposed impl of IPrintable."
  (:require [clojure.data.generators :as gen]
            [com.thortech.data.edn.generators :as edn-gen]
            [com.thortech.data.edn :as edn]
            [com.thortech.data.edn.file-util :as file-util]
            [com.thortech.impls.printable.default :as printable-default]
            [com.thortech.impls.writer.io-writer :as io-writer]))

(defn do-file
  [generator n path-suffix opts]
  (edn/file (generator) n (file-util/out-path path-suffix) opts)
  nil)

(defn do-file-of-many
  [generator n path-suffix opts]
  (dorun (edn/file-of-many generator n (file-util/out-path path-suffix) opts)))

(defn do-file-gen
  [& params]
  (dorun (apply edn/file-gen params)))

(comment
  (do-file-of-many gen/int 100 "ints.edn" {})
  (do-file-of-many edn-gen/date 10 "insants.edn" {})
  (do-file-of-many edn-gen/uuid 10 "uuids.edn" {})
  (do-file-of-many gen/float 100 "floats.edn" {})
  (do-file-of-many edn-gen/number 100 "numbers.edn" {})
  (do-file-of-many edn-gen/any-keyword 100 "keywords.edn" {})
  (do-file-of-many #(edn-gen/hierarchy 2 3 edn-gen/mixed-collection edn-gen/scalar)
                   10"hierarchical.edn" {:form-separator "\n"})

  (do-file-of-many gen/int 100 "ints-with-comments.edn" {:form-separator edn-gen/comment-block})
  (do-file-of-many gen/int 100 "ints-with-newline.edn" {:form-separator "\n"})

  (edn/file (gen/list gen/int 100)
            (file-util/out-path "ints.edn")
            {:generator/tag (edn-gen/occasional edn-gen/tag-keyword 20)})

  (def _ (edn/file (gen/list gen/int 100)
                        (file-util/out-path "ints.edn")
                        {:generator/discard (edn-gen/occasional gen/scalar 20)}))
  (def _ (edn/file (gen/list gen/int 100)
                        (file-util/out-path "ints.edn")
                        {:generator/comment (edn-gen/occasional edn-gen/comment-block 3)
                         :generator/whitespace (edn-gen/occasional edn-gen/whitespace-str 30)
                         :generator/tag (edn-gen/occasional edn-gen/tag-keyword 5)
                         :generator/discard (edn-gen/occasional gen/scalar 5)}))


  (edn/file-of-many gen/int 100 (file-util/out-path "ints.edn") {})
  (edn/file-of-many gen/float 100 (file-util/out-path "floats.edn") {})
  (edn/file-of-many edn-gen/number 100 (file-util/out-path "numbers.edn") {})
  (edn/file-of-many edn-gen/any-keyword 100 (file-util/out-path "keywords.edn") {})
  (edn/file-of-many edn-gen/hierarchical-anything 100 (file-util/out-path "hierarchical.edn") {})

  (edn/file-of-many gen/int 100 (file-util/out-path "ints.edn") {:form-separator edn-gen/comment-block})
  (edn/file-of-many gen/int 100 (file-util/out-path "ints.edn") {:form-separator "\n"})

  (edn/file (gen/list gen/int 100)
            (file-util/out-path "ints.edn")
            {:generator/tag (edn-gen/occasional edn-gen/tag-keyword 20)})

  (def _ (edn/file (gen/list gen/int 100)
                   (file-util/out-path "ints.edn")
                   {:generator/discard (edn-gen/occasional gen/scalar 20)}))
  (def _ (edn/file (gen/list gen/int 100)
                   (file-util/out-path "ints.edn")
                   {:generator/comment (edn-gen/occasional edn-gen/comment-block 3)
                    :generator/whitespace (edn-gen/occasional edn-gen/whitespace-str 30)
                    :generator/tag (edn-gen/occasional edn-gen/tag-keyword 5)
                    :generator/discard (edn-gen/occasional gen/scalar 5)}))



  #_(do-file-gen (partial edn/file-of-many gen/int 50)
               #(file-util/typed-file-path (file-util/out-dir "ints_50") :int)
               5 {})



  #_(do-file-gen (partial edn/file-of-many gen/int 50)
               #(file-util/typed-file-path (file-util/out-dir "ints_50") :int)
               5 {:form-separator "\n"})

  #_(do-file-gen (partial edn/file-of edn-gen/hierarchical-collection)
               #(file-util/typed-file-path (file-util/out-dir "hierarchy_comments_10") :hierarachy-comments)
               10 {:generator/comment edn-gen/comment-block})


  #_(do-file-gen (partial edn/file-of edn-gen/hierarchical-collection)
               #(file-util/typed-file-path (file-util/out-dir "hierarchy_noisy10") :hierarachy-comments)
               10
               {:generator/comment (edn-gen/occasional edn-gen/comment-block 3)
                :generator/whitespace (edn-gen/occasional edn-gen/whitespace-str 30)
                :generator/tag (edn-gen/occasional edn-gen/tag-keyword 5)
                :generator/discard (edn-gen/occasional gen/scalar 5)})


  {:file-generator :file-of-many ;; file-of-forms, file-of
   :data-generator gen/int
   :form-sizer 50
   :file-path-gen  (file-util/typed-file-path (out-dir "ints_50") :int)
   :file-count 5}
  )
