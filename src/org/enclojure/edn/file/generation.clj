;;;; The use and distribution terms for this software are covered by the
;;;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;;; which can be found in the file epl-v10.html at the root of this distribution.
;;;; By using this software in any fashion, you are agreeing to be bound by
;;;; the terms of this license.
;;;; You must not remove this notice, or any other, from this software.

(ns org.enclojure.edn.file.generation
  "Functions for generating files of edn."
  (:require [clojure.data.generators :as gen]
            [clojure.java.io :as io]
            [org.enclojure.edn.file.util :as file-util]
            [org.enclojure.edn.gen :as edn-gen]
            [org.enclojure.print.protocols.printable :as printable]
            [org.enclojure.print.util :as print-util]))

(defn write-file
  "Creates a file of edn data."
  [data writer opts]
  (with-open [w writer]
    (edn-gen/edn data w opts)))

(defn write-forms-file
  "Creates a files of edn containing the forms in coll."
  [coll writer opts]
  (with-open [w writer]
    (edn-gen/edn-forms coll w opts)))

(defn file
  "Creates a file of edn data."
  [file-path data opts]
  (let [path (file-util/ensure-path! file-path)]
    (write-file data (io/writer file-path) opts)
    path))

(defn forms-file
  "Creates a files of edn containing the forms in coll."
  [file-path coll opts]
  (let [path (file-util/ensure-path! file-path)]
    (write-forms-file coll (io/writer path) opts)
    path))

(defn many-files
  "using a file-generation-fn (of signiture [data opts]) and a data-gen fn
creates n files passing through opts"
  ([path-gen data-gen n opts]
     (edn-gen/many-of (partial file path-gen) data-gen n opts))
  ([data-gen n opts]
     (edn-gen/many-of file data-gen n opts)))

(defn many-forms-files
  "using a file-generation-fn (of signiture [data opts]) and a data-gen fn
creates n files passing through opts"
  ([path-gen data-gen n opts]
     (edn-gen/many-of (partial forms-file path-gen) data-gen n opts))
  ([data-gen n opts]
     (edn-gen/many-of forms-file data-gen n opts)))


;; (defn edn-file
;;   [data filewriter opts]
;;   (with-open [f filewriter]
;;     (printable/print data f opts)))

;; (defn edn-forms-file
;;   [coll filewriter opts]
;;   (with-open [f filewriter]
;;     (print-util/print-seq-contents coll f opts (:form-separator opts))))

;; (defn file-of
;;   "Creates a file of edn data created using generator. Returns data."
;;   ([generator file-path opts]
;;      (let [data (generator)]
;;        (file data file-path opts)
;;        data))
;;   ([generator sizer file-path opts]
;;      (file-of (partial generator sizer) file-path opts)))

;; (defn file-of-forms
;;   "Takes a generator which makes multiple things.
;; Creates a file with those things at top level."
;;   ([generator file-path opts]
;;      (file-util/ensure-parent-directory! file-path)
;;      (let [data (generator)]
;;        (with-open [writer (io/writer file-path)]
;;          (edn-gen/edn-forms data writer opts))
;;        data))
;;   ([generator sizer file-path opts]
;;      (file-of-forms (partial generator sizer) file-path opts)))

;; (defn file-of-many
;;   "Take a generator which makes a single thing.
;; Creates a file with many of those gener]ated things at top level."
;;   ([generator file-path opts]
;;      (file-of-forms (partial gen/list generator) file-path opts))
;;   ([generator sizer file-path opts]
;;      (file-of-forms (partial gen/list generator sizer) file-path opts)))

;; (defn file-gen
;;   "Using a generator thunk, creates n files of generated data using file-path-gen"
;;   [file-generator file-path-gen n opts]
;;   (for [_ (range n)]
;;     (let [path (file-path-gen)
;;           data (file-generator path opts)]
;;       {:path path
;;        :data data})))
