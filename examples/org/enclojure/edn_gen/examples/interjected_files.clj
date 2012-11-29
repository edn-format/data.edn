;;;; The use and distribution terms for this software are covered by the
;;;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;;; which can be found in the file epl-v10.html at the root of this distribution.
;;;; By using this software in any fashion, you are agreeing to be bound by
;;;; the terms of this license.
;;;; You must not remove this notice, or any other, from this software.

(ns org.enclojure.edn-gen.examples.interjected-files
  "Example of creating files of edn data using the printable.interposed impl of IPrintable."
  (:require [clojure.data.generators :as gen]
            [org.enclojure.edn.data.generators :as edn-gen]
            [org.enclojure.edn.file.generation :as files]
            [org.enclojure.edn.file.util :as util]
            [org.enclojure.impls.printable.interjection :as interjection]
            [org.enclojure.impls.writer.io-writer :as io-writer]))

(defn do-file-of
  [generator n path-suffix opts]
  (files/file-of generator n (util/out-path path-suffix) opts)
  nil)

(defn do-file-of-many
  [generator n path-suffix opts]
  (dorun (files/file-of-many generator n (util/out-path path-suffix) opts)))

(defn do-file-gen
  [& params]
  (dorun (apply files/file-gen params)))

(comment
  (do-file-of-many gen/int 100 "ints.edn" {})
  (do-file-of-many gen/float 100 "floats.edn" {})
  (do-file-of-many edn-gen/number 100 "numbers.edn" {})
  (do-file-of-many edn-gen/any-keyword 100 "keywords.edn" {})
  (do-file-of-many edn-gen/hierarchical-collection 10 "hierarchical.edn" {})

  (do-file-of-many gen/int 100 "ints-with-comments.edn" {:form-separator edn-gen/comment-block})
  (do-file-of-many gen/int 100 "ints-with-newline.edn" {:form-separator "\n"})


  (files/file-of #(gen/list gen/int 100)
                 (util/out-path "ints.edn")
                 {:generator/tag (edn-gen/occasional edn-gen/tag-keyword 20)})

  (def _ (files/file-of #(gen/list gen/int 100)
                        (util/out-path "ints.edn")
                        {:generator/discard (edn-gen/occasional gen/scalar 20)}))
  (def _ (files/file-of #(gen/list gen/int 100)
                        (util/out-path "ints.edn")
                        {:generator/comment (edn-gen/occasional edn-gen/comment-block 3)
                         :generator/whitespace (edn-gen/occasional edn-gen/whitespace-str 30)
                         :generator/tag (edn-gen/occasional edn-gen/tag-keyword 5)
                         :generator/discard (edn-gen/occasional gen/scalar 5)}))


  (files/file-of-many gen/int 100 (util/out-path "ints.edn") {})
  (files/file-of-many gen/float 100 (util/out-path "floats.edn") {})
  (files/file-of-many edn-gen/number 100 (util/out-path "numbers.edn") {})
  (files/file-of-many edn-gen/any-keyword 100 (util/out-path "keywords.edn") {})
  (files/file-of-many edn-gen/hierarchical-anything 100 (util/out-path "hierarchical.edn") {})

  (files/file-of-many gen/int 100 (util/out-path "ints.edn") {:form-separator edn-gen/comment-block})
  (files/file-of-many gen/int 100 (util/out-path "ints.edn") {:form-separator "\n"})

  (files/file-of #(gen/list gen/int 100)
                 (util/out-path "ints.edn")
                 {:generator/tag (edn-gen/occasional edn-gen/tag-keyword 20)})

  (def _ (files/file-of #(gen/list gen/int 100)
                        (util/out-path "ints.edn")
                        {:generator/discard (edn-gen/occasional gen/scalar 20)}))
  (def _ (files/file-of #(gen/list gen/int 100)
                        (util/out-path "ints.edn")
                        {:generator/comment (edn-gen/occasional edn-gen/comment-block 3)
                         :generator/whitespace (edn-gen/occasional edn-gen/whitespace-str 30)
                         :generator/tag (edn-gen/occasional edn-gen/tag-keyword 5)
                         :generator/discard (edn-gen/occasional gen/scalar 5)}))



  (do-file-gen (partial files/file-of-many gen/int 50)
                     #(util/typed-file-path (util/out-dir "ints_50") :int)
                     5 {})



  (do-file-gen (partial files/file-of-many gen/int 50)
                     #(util/typed-file-path (util/out-dir "ints_50") :int)
                     5 {:form-separator "\n"})

  (do-file-gen (partial files/file-of edn-gen/hierarchical-collection)
                     #(util/typed-file-path (util/out-dir "hierarchy_comments_10") :hierarachy-comments)
                     10 {:generator/comment edn-gen/comment-block})


  (do-file-gen (partial files/file-of edn-gen/hierarchical-collection)
                     #(util/typed-file-path (util/out-dir "hierarchy_noisy10") :hierarachy-comments)
                     10
                     {:generator/comment (edn-gen/occasional edn-gen/comment-block 3)
                      :generator/whitespace (edn-gen/occasional edn-gen/whitespace-str 30)
                      :generator/tag (edn-gen/occasional edn-gen/tag-keyword 5)
                      :generator/discard (edn-gen/occasional gen/scalar 5)})


  {:file-generator :file-of-many ;; file-of-forms, file-of
   :data-generator gen/int
   :form-sizer 50
   :file-path-gen  (util/typed-file-path (out-dir "ints_50") :int)
   :file-count 5}
  )
