(ns edn-data-gen.edn-files
  (:require [clojure.test.generative.generators :as gen]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [edn-data-gen.generators :as edn-gen]
            [clojure.pprint :as pprint]
            [edn-data-gen.files.helpers :as files]
            [edn-data-gen.print.protocols.printable :as printable]
            [edn-data-gen.print.writers.io-writer :as io-writer]
            [edn-data-gen.printables.comment-injection :as c-inj]
            [edn-data-gen.print.helpers :as print-helpers]))


(defn out-parent
  []
  (str (System/getProperty "user.dir")
       java.io.File/separator
       "output"))


(defn out-dir
  [dir-name]
  (str (out-parent)
       java.io.File/separator
       dir-name))

(defn out-path
  [filename]
  (str (out-parent)
       java.io.File/separator
       filename))

(defn edn-file
  [data filewriter opts]
  (with-open [f filewriter]
    (printable/print data f opts)))

(defn edn-forms-file
  [coll filewriter opts separator]
  (with-open [f filewriter]
    (print-helpers/print-seq-contents coll f opts separator)))

(defn file-of
  ([generator file-path]
     (files/ensure-parent-directory! file-path)
     (let [writer (io/writer file-path)
           data (generator)
           _ (edn-file data writer {})]
       data))
  ([generator sizer file-path]
     (file-of (partial generator sizer) file-path)))

(defn file-of-forms
  "Takes a generator which makes multiple things.
Creates a file with those things at top level."
  ([generator file-path]
     (files/ensure-parent-directory! file-path)
     (let [writer (io/writer file-path)
           data (generator)
           _ (edn-forms-file data writer {} " ")]
       data))
  ([generator sizer file-path]
     (file-of-forms (partial generator sizer) file-path)))

(defn file-of-many
  "Take a generator which makes a single thing.
Creates a file with many of those generated things at top level."
  ([generator file-path]
     (file-of-forms (partial gen/list generator) file-path))
  ([generator sizer file-path]
     (file-of-forms (partial gen/list generator sizer) file-path)))

(defn file-name
  []
  (str (java.util.UUID/randomUUID) ".edn"))

(defn typed-file-name
  [type & [suffix]]
  (let [suffix-str (when suffix (str "_" (name suffix)))]
    (format "%s_%s%s.edn"
            (name type)
            (str (java.util.UUID/randomUUID))
            (str suffix-str))))

(defn file-path
  ([]
     (file-path (out-parent) file-name))
  ([parent-dir file-name-gen]
     (str parent-dir
          java.io.File/separator
          (file-name-gen))))

(defn typed-file-path
  [parent-dir & type-params]
  (file-path parent-dir (apply partial (cons typed-file-name type-params))))

;; (defn files-of-type
;;   "Using a generator thunk, creates n files of generated data under parent-dir."
;;   [type file-generator n parent-dir]
;;   (doseq [_ (range n)]
;;     (file-generator (str parent-dir (typed-file-name type)))))

(defn file-gen
  "Using a generator thunk, creates n files of generated data using file-path-gen"
  [file-generator file-path-gen n]
  (for [_ (range n)]
    (let [path (file-path-gen)
          data (file-generator path)]
      {:path path
       :data data})))


(comment
  (file-of-many gen/int 100 (out-path "ints.edn"))
  (file-of-many gen/float 100 (out-path "floats.edn"))
  (file-of-many edn-gen/numbers 100 (out-path "numbers.edn"))
  (file-of-many edn-gen/any-keyword 100 (out-path "keywords.edn"))
  (file-of-many edn-gen/hierarchical-anything 100 (out-path "hierarchical.edn"))

  (files-of-type :int (partial file-of-many gen/int) 10 (out-dir "ints01"))
  (files-of-type :hierarachy (partial file-of edn-gen/hierarchical-anything) 2 (out-dir "hierarchy01"))
  (files-of-type :hierarachy (partial file-of-many edn-gen/hierarchical-anything 2) 2 (out-dir "hierarchy01"))


  (file-gen (partial file-of-many gen/int 50) (partial typed-file-path (out-dir "ints_50") :int) 5)


  {:file-generator :file-of-many ;; file-of-forms, file-of
   :data-generator gen/int
   :form-sizer 50
   :file-path-gen  (partial typed-file-path (out-dir "ints_50") :int)
   :file-count 5}
  )
