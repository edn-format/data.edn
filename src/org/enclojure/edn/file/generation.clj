;;;; The use and distribution terms for this software are covered by the
;;;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;;; which can be found in the file epl-v10.html at the root of this distribution.
;;;; By using this software in any fashion, you are agreeing to be bound by
;;;; the terms of this license.
;;;; You must not remove this notice, or any other, from this software.

(ns org.enclojure.edn.file.generation
  (:require [clojure.data.generators :as gen]
            [clojure.string :as string]
            [clojure.java.io :as io]
            [clojure.pprint :as pprint]
            [org.enclojure.edn.file.util :as file-util]
            [org.enclojure.print.protocols.printable :as printable]
            [org.enclojure.print.util :as print-util]))

(defn edn-file
  [data filewriter opts]
  (with-open [f filewriter]
    (printable/print data f opts)))

(defn edn-forms-file
  [coll filewriter opts]
  (with-open [f filewriter]
    (print-util/print-seq-contents coll f opts (:form-separator opts))))

(defn file-of
  ([generator file-path opts]
     (file-util/ensure-parent-directory! file-path)
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
     (file-util/ensure-parent-directory! file-path)
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
