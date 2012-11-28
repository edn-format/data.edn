(ns edn-data-gen.edn-files
  (:require [clojure.data.generators :as gen]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [edn-data-gen.files.helpers :as file-helpers]
            [edn-data-gen.print.protocols.printable :as printable]
            [edn-data-gen.print.helpers :as print-helpers]))

(defn edn-file
  [data filewriter opts]
  (with-open [f filewriter]
    (printable/print data f opts)))

(defn edn-forms-file
  [coll filewriter opts]
  (with-open [f filewriter]
    (print-helpers/print-seq-contents coll f opts (:form-separator opts))))

(defn file-of
  ([generator file-path opts]
     (file-helpers/ensure-parent-directory! file-path)
     (let [writer (io/writer file-path)
           data (generator)
           _ (edn-file data writer opts)]
       data))
  ([generator sizer file-path opts]
     (file-of (partial generator sizer) file-path opts)))

(defn file-of-forms
  "Takes a generator which makes multiple things.
Creates a file with those things at top level."
  ([generator file-path opts]
     (file-helpers/ensure-parent-directory! file-path)
     (let [writer (io/writer file-path)
           data (generator)
           sep (:form-separator opts)
           opts (if sep opts (assoc opts :form-separator " "))
           _ (edn-forms-file data writer opts)]
       data))
  ([generator sizer file-path opts]
     (file-of-forms (partial generator sizer) file-path opts)))

(defn file-of-many
  "Take a generator which makes a single thing.
Creates a file with many of those gener]ated things at top level."
  ([generator file-path opts]
     (file-of-forms (partial gen/list generator) file-path opts))
  ([generator sizer file-path opts]
     (file-of-forms (partial gen/list generator sizer) file-path opts)))

(defn file-gen
  "Using a generator thunk, creates n files of generated data using file-path-gen"
  [file-generator file-path-gen n opts]
  (for [_ (range n)]
    (let [path (file-path-gen)
          data (file-generator path opts)]
      {:path path
       :data data})))
