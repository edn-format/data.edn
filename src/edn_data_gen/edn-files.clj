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


(defn file-name
  [parent-dir])


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
     (let [writer (io/writer file-path)]
       (edn-file (generator) writer {})))
  ([generator sizer file-path]
     (file-of (partial generator sizer) file-path)))

(defn file-of-forms
  "Takes a generator which makes multiple things.
Creates a file with those things at top level."
  ([generator file-path]
     (let [writer (io/writer file-path)]
       (edn-forms-file (generator) writer {} " ")))
  ([generator sizer file-path]
     (file-of-forms (partial generator sizer) file-path)))


(defn file-of-many
  "Take a generator which makes a single thing.
Creates a file with many of those generated things at top level."
  ([generator file-path]
     (file-of-forms (partial gen/list generator) file-path))
  ([generator sizer file-path]
     (file-of-forms (partial gen/list generator sizer) file-path)))


(defn pr-edn
  [data]
  (with-out-str (pr data)))

(defn prn-edn
  [data]
  (with-out-str (prn data)))

(defn pprint-edn
  [data]
  (with-out-str (pprint/pprint data)))

(defn print-edn-forms
  [printer delimiter forms]
  (string/join delimiter (map printer forms)))



(defn out-parent
  []
  (str (System/getProperty "user.dir")
       java.io.File/separator
       "output"))

(defn out-path
  [filename]
  (str (out-parent)
       java.io.File/separator
       filename))

(defn write-edn
  "Prints out an data structure in edn format to a file."
  [printer filename data]
  (files/ensure-parent-directory! filename)
  (spit (str filename)
        (printer data)))

(defn write-edn-forms
  "Prints out an data structure in edn format to a file."
  [printer delimiter filename data]
  (files/ensure-parent-directory! filename)
  (spit (str filename)
        (print-edn-forms printer delimiter data)))




;; (gen/list gen/int)
(defn file-of-ints
  ([n]
     (file-of-ints (out-path "ints.edn") n))
  ([filename n]
     (write-edn filename (repeatedly n gen/int))))

(defn file-of-floats
  ([n]
     (file-of-floats (out-path "floats.edn") n))
  ([filename n]
     (write-edn filename (repeatedly n gen/float))))

(defn file-of-numbers
  ([n]
     (file-of-numbers (out-path "numbers.edn") n))
  ([filename n]
     (write-edn filename (repeatedly n edn-gen/numbers))))

(defn file-of-keywords
  ([n]
     (file-of-keywords (out-path "keywords.edn") n))
  ([filename n]
     (write-edn filename (repeatedly n edn-gen/any-keyword))))

(defn file-of-hierarchical-anything
  ([n]
     (file-of-hierarchical-anything (out-path "hierarchical.edn") n))
  ([filename n]
     (write-edn filename (repeatedly n edn-gen/hierarchical-anything))))

(comment
  ;;Would be nice to write these composably.
  (-> ())
  ;; what is the interface for an edn writer:
  ;; an edn printer outputs a string.
  ;; an edn writer uses an edn printer to create a string of the data and writes it to file
  ;; print-edn takes any data structure and outputs edn
  ;; print-edn-forms takes a sequence of edn forms and apply print-edn to the seq


  )
