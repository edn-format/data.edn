;;;; The use and distribution terms for this software are covered by the
;;;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;;; which can be found in the file epl-v10.html at the root of this distribution.
;;;; By using this software in any fashion, you are agreeing to be bound by
;;;; the terms of this license.
;;;; You must not remove this notice, or any other, from this software.

(ns ^{:author "Tom Hickey, Jim Altieri"}
  org.edn-format.data.edn.examples.create-files
  "Example of creating files of edn data using the printable.interposed impl of IPrintable."
  (:require [clojure.data.generators :as gen]
            [org.edn-format.data.edn.generators :as edn-gen]
            [org.edn-format.data.edn :as edn]
            [org.edn-format.data.edn.file-util :as file-util]
            [org.edn-format.impls.printable.default :as printable-default]
            [org.edn-format.impls.writer.io-writer :as io-writer]))

(defn do-file
  [generator n path-suffix opts]
  (edn/file (file-util/out-path path-suffix) (generator n) opts)
  nil)

(defn do-file-of-many
  [generator n path-suffix opts]
  (dorun (edn/file-of-many generator n (file-util/out-path path-suffix) opts)))

(defn do-file-gen
  [& params]
  (dorun (apply edn/file-gen params)))

(comment
  (do-file-of-many gen/int 100 "ints.edn" {})
  (do-file-of-many gen/float 100 "floats.edn" {})
  (do-file-of-many edn-gen/number 100 "numbers.edn" {})
  (do-file-of-many edn-gen/any-keyword 100 "keywords.edn" {})
  (do-file-of-many #(edn-gen/hierarchy 2 3 edn-gen/mixed-collection edn-gen/scalar)
                   10 "hierarchical.edn" {:form-separator "\n"})
  (do-file-of-many edn-gen/date 10 "insants.edn" {})
  (do-file-of-many edn-gen/uuid 10 "uuids.edn" {})

  (do-file-of-many gen/int 100 "ints-with-comments.edn" {:form-separator edn-gen/comment-block})
  (do-file-of-many gen/int 100 "ints-with-newline.edn" {:form-separator "\n"})

  (edn/file (file-util/out-path "list-of-ints.edn")
            (gen/list gen/int 100)
            {:generator/tag (edn-gen/occasional edn-gen/tag-keyword 2 10)})

  (edn/file (file-util/out-path "ints-with-discard.edn")
            (gen/list gen/int 100)
            {:generator/discard (edn-gen/occasional gen/scalar 2 10)})

  (edn/file (file-util/out-path "ints-with-noise.edn")
            (gen/list gen/int 100)
            {:generator/comment (edn-gen/occasional edn-gen/comment-block 3 100)
             :generator/whitespace (edn-gen/occasional edn-gen/whitespace-str 3 10)
             :generator/tag (edn-gen/occasional edn-gen/tag-keyword 1 20)
             :generator/discard (edn-gen/occasional gen/scalar 1 20)})


  (edn/file-of-many gen/int 100 (file-util/out-path "ints.edn") {})
  (edn/file-of-many gen/float 100 (file-util/out-path "floats.edn") {})
  (edn/file-of-many edn-gen/number 100 (file-util/out-path "numbers.edn") {})
  (edn/file-of-many edn-gen/any-keyword 100 (file-util/out-path "keywords.edn") {})
  (edn/file-of-many edn-gen/hierarchical-anything 100 (file-util/out-path "hierarchical.edn") {})

  (edn/file-of-many gen/int 100 (file-util/out-path "ints.edn") {:form-separator edn-gen/comment-block})
  (edn/file-of-many gen/int 100 (file-util/out-path "ints.edn") {:form-separator "\n"})

  (dorun (edn/many-forms-files
          #(file-util/typed-file-path (file-util/out-dir "ints_50b") :int)
          #(gen/list gen/int 50)
          5
          {}))
  (dorun (edn/gen-files-of-many gen/int 50
                                #(file-util/typed-file-path (file-util/out-dir "many_ints_50") :int)
                                5
                                {}))

  (dorun (edn/gen-files-of-many gen/int 50
                                #(file-util/typed-file-path (file-util/out-dir "many_ints_newlines_50") :int)
                                5 {:form-separator "\n"}))


  (dorun (edn/many-files #(file-util/file-run-path "hierarchy_10")
                         #(edn-gen/any-hierarchy 4 3 edn-gen/mixed-collection edn-gen/scalar)
                         10
                         {}))

  (dorun (edn/many-files #(file-util/file-run-path "hierarchy_comments_10")
                         #(edn-gen/any-hierarchy 4 3 edn-gen/mixed-collection edn-gen/scalar)
                         10
                         {:generator/comment edn-gen/comment-block}))


  (dorun (edn/many-files #(file-util/file-run-path "hierarchy_noisy_10")
                         #(edn-gen/any-hierarchy 4 3 edn-gen/mixed-collection edn-gen/scalar)
                         10
                         {:generator/comment (edn-gen/occasional edn-gen/comment-block 1 33)
                          :generator/whitespace (edn-gen/occasional edn-gen/whitespace-str 3 10)
                          :generator/tag (edn-gen/occasional edn-gen/tag-keyword 1 20)
                          :generator/discard (edn-gen/occasional gen/scalar 1 20)}))

  ;; {:file-generator :file-of-many ;; file-of-forms, file-of
  ;;  :data-generator :gen/int
  ;;  :form-sizer 50
  ;;  :file-path-gen  {:run/path "ints_50"}
  ;;  :file-count 5}
  )
