(ns org.enclojure.impls.printable.interjection
  "Extending IPrintable to core types with a method of interjecting
comments, whitespace, tags, and discarded data amongst the printing of the actual data."
  (:require [org.enclojure.print.protocols.writer :as writer]
            [org.enclojure.print.protocols.printable :as printable]
            [org.enclojure.print.util :as util]))

(defn maybe-gen
  "If gen-key is found in opts, calls the supplied generator."
  [gen-key opts]
  (when-let [gen (gen-key opts)]
    (gen)))

(defn write-whitespace
  "Uses the whitespace string generator in (opts :generator/whitespace) and writes output to the writer"
  [w opts]
  (when-let [ws (maybe-gen :generator/whitespace opts)]
    (writer/write w ws)))

(defn write-comment
  "Uses the comment string generator in (opts :generator/comment) and writes output to the writer"
  [w opts]
  (when-let [com (maybe-gen :generator/comment opts)]
    (writer/write w com)))

(defn write-tag
  "Uses the tag keyword generator in  (opts :generator/tag) and writes output to the writer"
  [w opts]
  (when-let [k (maybe-gen :generator/tag opts)]
    (if (keyword? k)
      (writer/write w (str "#" (namespace k) "/" (name k) " "))
      (writer/write w (str k)))))

(defn write-discard
  "Uses the data generator in (opts :generator/discard) writes the output
prefixed with the discard sequence to the writer"
  [w opts]
  (when-let [data (maybe-gen :generator/discard opts)]
    (writer/write w "#_")
    (write-whitespace w opts)
    (printable/print data w (dissoc opts :generator/discard :generator/comment))
    (writer/write w " ")))

(defn interject-noise
  "Using the writer, for the given settings on opts possibly interjects
comments, whitespace, discarded data and potentially prefixes a tag.
Uses value-printing-fn to print the actual data."
  [w opts value-printing-fn]
  (write-comment w opts)
  (write-discard w opts)
  (write-tag w opts)
  (value-printing-fn)
  (write-comment w opts))

(extend-protocol printable/IPrintable
  clojure.lang.IPersistentMap
  (print [this w opts]
    (interject-noise w opts
                         #(util/print-map this w opts ", ")))
  clojure.lang.PersistentVector
  (print [this w opts]
    (interject-noise w opts
                         #(util/print-sequential this w opts
                                                    "["
                                                    " "
                                                    "]")))
  clojure.lang.IPersistentSet
  (print [this w opts]
    (interject-noise w opts
                         #(util/print-sequential this w opts
                                                    "#{"
                                                    ", "
                                                    "}")))
  clojure.lang.IPersistentList
  (print [this w opts]
    (interject-noise w opts
                         #(util/print-sequential this w opts
                                                    "("
                                                    " "
                                                    ")")))
  clojure.lang.LazySeq
  (print [this w opts]
    (interject-noise w opts
                         #(util/print-sequential this w opts
                                                    "("
                                                    " "
                                                    ")")))
  java.lang.String
  (print [this w opts]
    (interject-noise w opts
                         #(util/write-string this w)))
  java.lang.Character
  (print [this w opts]
    (interject-noise w opts
                         #(util/write-character this w)))
  java.lang.Object
  (print [this w opts]
    (interject-noise w opts
                         #(writer/write w (str this))))
  nil
  (print [this w opts]
    (interject-noise w opts
                         #(writer/write w "nil"))))
