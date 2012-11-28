(ns edn-data-gen.files.injected-files
  "Example namespace. Create files of edn data using the printables.injection impl of IPrintable"
  (:require [edn-data-gen.edn-files :as files]
            [edn-data-gen.printables.injection :as injection]
            [edn-data-gen.print.writers.io-writer :as io-writer]
            [edn-data-gen.files.helpers :as helpers]
            [edn-data-gen.generators :as edn-gen]
            [clojure.data.generators :as gen]))

(defn do-file-of
  [generator n path-suffix opts]
  (files/file-of generator n (helpers/out-path path-suffix) opts)
  nil)

(defn do-file-of-many
  [generator n path-suffix opts]
  (dorun (files/file-of-many generator n (helpers/out-path path-suffix) opts)))

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
                 (helpers/out-path "ints.edn")
                 {:generator/tag (edn-gen/occasional edn-gen/tag-keyword 20)})

  (def _ (files/file-of #(gen/list gen/int 100)
                        (helpers/out-path "ints.edn")
                        {:generator/discard (edn-gen/occasional gen/scalar 20)}))
  (def _ (files/file-of #(gen/list gen/int 100)
                        (helpers/out-path "ints.edn")
                        {:generator/comment (edn-gen/occasional edn-gen/comment-block 3)
                         :generator/whitespace (edn-gen/occasional edn-gen/whitespace-str 30)
                         :generator/tag (edn-gen/occasional edn-gen/tag-keyword 5)
                         :generator/discard (edn-gen/occasional gen/scalar 5)}))


  (files/file-of-many gen/int 100 (helpers/out-path "ints.edn") {})
  (files/file-of-many gen/float 100 (helpers/out-path "floats.edn") {})
  (files/file-of-many edn-gen/number 100 (helpers/out-path "numbers.edn") {})
  (files/file-of-many edn-gen/any-keyword 100 (helpers/out-path "keywords.edn") {})
  (files/file-of-many edn-gen/hierarchical-anything 100 (helpers/out-path "hierarchical.edn") {})

  (files/file-of-many gen/int 100 (helpers/out-path "ints.edn") {:form-separator edn-gen/comment-block})
  (files/file-of-many gen/int 100 (helpers/out-path "ints.edn") {:form-separator "\n"})

  (files/file-of #(gen/list gen/int 100)
                 (helpers/out-path "ints.edn")
                 {:generator/tag (edn-gen/occasional edn-gen/tag-keyword 20)})

  (def _ (files/file-of #(gen/list gen/int 100)
                        (helpers/out-path "ints.edn")
                        {:generator/discard (edn-gen/occasional gen/scalar 20)}))
  (def _ (files/file-of #(gen/list gen/int 100)
                        (helpers/out-path "ints.edn")
                        {:generator/comment (edn-gen/occasional edn-gen/comment-block 3)
                         :generator/whitespace (edn-gen/occasional edn-gen/whitespace-str 30)
                         :generator/tag (edn-gen/occasional edn-gen/tag-keyword 5)
                         :generator/discard (edn-gen/occasional gen/scalar 5)}))



  (do-file-gen (partial files/file-of-many gen/int 50)
                     #(helpers/typed-file-path (helpers/out-dir "ints_50") :int)
                     5 {})



  (do-file-gen (partial files/file-of-many gen/int 50)
                     #(helpers/typed-file-path (helpers/out-dir "ints_50") :int)
                     5 {:form-separator "\n"})

  (do-file-gen (partial files/file-of edn-gen/hierarchical-collection)
                     #(helpers/typed-file-path (helpers/out-dir "hierarchy_comments_50") :hierarachy-comments)
                     50 {:generator/comment edn-gen/comment-block})


  (do-file-gen (partial files/file-of edn-gen/hierarchical-collection)
                     #(helpers/typed-file-path (helpers/out-dir "hierarchy_noisy2_50") :hierarachy-comments)
                     50
                     {:generator/comment (edn-gen/occasional edn-gen/comment-block 3)
                      :generator/whitespace (edn-gen/occasional edn-gen/whitespace-str 30)
                      :generator/tag (edn-gen/occasional edn-gen/tag-keyword 5)
                      :generator/discard (edn-gen/occasional gen/scalar 5)})


  {:file-generator :file-of-many ;; file-of-forms, file-of
   :data-generator gen/int
   :form-sizer 50
   :file-path-gen  (helpers/typed-file-path (out-dir "ints_50") :int)
   :file-count 5}
  )
