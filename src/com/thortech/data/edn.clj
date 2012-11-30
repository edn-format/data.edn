;;;; The use and distribution terms for this software are covered by the
;;;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;;; which can be found in the file epl-v10.html at the root of this distribution.
;;;; By using this software in any fashion, you are agreeing to be bound by
;;;; the terms of this license.
;;;; You must not remove this notice, or any other, from this software.

(ns ^{:author "Tom Hickey, Jim Altieri"}
  com.thortech.data.edn
  "Generate edn by printing using the IPrintble protocol
to a writer that impliments the IWriter protocol."
  (:require [clojure.data.generators :as gen]
            [clojure.java.io :as io]
            [com.thortech.data.edn.file-util :as file-util]
            [com.thortech.print.protocols.printable :as printable]
            [com.thortech.print.util :as print-util]))

(defn edn
  "Print edn data to writer using opts."
  [data writer opts]
  (printable/print data writer opts))

(defn edn-forms
  "Print the contents of coll to writer using opts"
  [coll writer opts]
  (let [opts-with-defaults (merge {:form-separator " "} opts)]
    (print-util/print-seq-contents coll writer opts (:form-separator opts-with-defaults))))

(defn many-of
  "using an edn-gen-fn (of signiture [data opts])"
  [edn-gen-fn data-generator n opts]
  (for [_ (range n)]
    (let [data (data-generator)]
      {:out (edn-gen-fn data opts)
       :data data})))


;;; Strings

(defn string
  "Creates an edn string from data. Passing opts through to a StringWriter."
  [data opts]
  (let [sw (java.io.StringWriter.)
        _ (edn data sw opts)]
    (str sw)))

(defn forms-string
  "Create an edn string with the contents of coll.
Passing opts through to a StringWriter."
  [coll opts]
  (let [sw (java.io.StringWriter.)
        _ (edn-forms coll sw opts)]
    (str sw)))

(def many-strings
  (partial many-of string))

(def many-forms-strings
  (partial many-of forms-string))


;;; Files

(defn write-file
  "Creates a file of edn data."
  [data writer opts]
  (with-open [w writer]
    (edn data w opts)))

(defn write-forms-file
  "Creates a files of edn containing the forms in coll."
  [coll writer opts]
  (with-open [w writer]
    (edn-forms coll w opts)))

(defn file
  "Creates a file of edn data."
  [file-path data opts]
  (let [path (file-util/ensure-path! file-path)]
    (write-file data (io/writer path) opts)
    path))

(defn forms-file
  "Creates a files of edn containing the forms in coll."
  [file-path coll opts]
  (let [path (file-util/ensure-path! file-path)]
    (write-forms-file coll (io/writer path) opts)
    path))

(defn file-gen
  "using a file-generation-fn (of signiture [path data opts]), a path-gen fn
and a data-gen fn, creates n files passing through opts"
  [file-generation-fn path-gen data-gen n opts]
  (many-of (partial file-generation-fn path-gen) data-gen n opts))

(defn many-files
  "using a path-gen fn and a data-gen fn
creates n files passing through opts"
  [path-gen data-gen n opts]
  (file-gen file path-gen data-gen n opts))

(defn many-forms-files
  "using a path-gen fn and a data-gen fn
creates n forms-files passing through opts"
  [path-gen data-gen n opts]
  (file-gen forms-file path-gen data-gen n opts))

(defn file-of-many
  "Using generator, creates a file of many generated things at top level."
  ([generator file-path opts]
     (forms-file file-path (gen/list generator) opts))
  ([generator sizer file-path opts]
     (forms-file file-path (gen/list generator sizer) opts)))

(defn gen-files-of-many
  ([generator file-path n opts]
     (many-forms-files file-path #(gen/list generator) n opts))
  ([generator sizer file-path n opts]
     (many-forms-files file-path #(gen/list generator sizer) n opts)))
