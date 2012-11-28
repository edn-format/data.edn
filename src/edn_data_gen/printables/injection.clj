(ns edn-data-gen.printables.injection
  "Extending IPrintable to core types with a method of injecting
comments, whitespace, tags, and discarded data."
  (:require [edn-data-gen.print.protocols.writer :as writer]
            [edn-data-gen.print.protocols.printable :as printable]
            [edn-data-gen.print.helpers :as helpers]))

(defn gen-noise
  [gen-key opts]
  (when-let [gen (gen-key opts)]
    (gen)))

(defn write-whitespace
  "Uses the generator (which generates a string) in the opts using the :generator/whitespace keyword to write some whitespace to the writer"
  [w opts]
  (when-let [ws (gen-noise :generator/whitespace opts)]
    (writer/write w ws)))

(defn write-comment
  "Uses the generator (which generates a string) in the opts using the :generator/comment keyword to write some comments to the writer"
  [w opts]
  (when-let [com (gen-noise :generator/comment opts)]
    (writer/write w com)))

(defn write-tag
  "Uses the generator (which generates a namespaced keyword) in the opts using the :generator/tag keyword to write a tag to the writer"
  [w opts]
  (when-let [k (gen-noise :generator/tag opts)]
    (writer/write w (str "#" (namespace k) "/" (name k) " "))))

(defn write-discard
  "Uses the generator (which generates any data structure) in the opts using the :generator/discard keyword to write a discarded data thing to the writer"
  [w opts]
  (when-let [data (gen-noise :generator/discard opts)]
    (writer/write w "#_")
    (write-whitespace w opts)
    (printable/print data w (dissoc opts :generator/discard :generator/comment))
    (writer/write w " ")))



(defn surrounded-by-noise [data w opts to-pr-fn]
  (write-comment w opts)

  ;; possible discard
  (write-discard w opts)

  ;; possible tag
  (write-tag w opts)

  ;; print the thing with the thing
  (to-pr-fn)

  ;; possible comment
  (write-comment w opts)

  )


(extend-protocol printable/IPrintable
  clojure.lang.IPersistentMap
  (print [this w opts]
    (surrounded-by-noise this w opts
                         #(helpers/print-map this w opts ", ")))

  clojure.lang.PersistentVector
  (print [this w opts]
    (surrounded-by-noise this w opts
                         #(helpers/print-sequential this w opts
                                                    "["
                                                    " "
                                                    "]")))

  clojure.lang.IPersistentSet
  (print [this w opts]
    (surrounded-by-noise this w opts
                         #(helpers/print-sequential this w opts
                                                    "#{"
                                                    ", "
                                                    "}")))

  clojure.lang.IPersistentList
  (print [this w opts]
    (surrounded-by-noise this w opts
                         #(helpers/print-sequential this w opts
                                                    "("
                                                    " "
                                                    ")")))

  clojure.lang.LazySeq
  (print [this w opts]
    (surrounded-by-noise this w opts
                         #(helpers/print-sequential this w opts
                                                    "("
                                                    " "
                                                    ")")))

  java.lang.String
  (print [this w opts]
    (surrounded-by-noise this w opts
                         #(helpers/write-string this w)))
  java.lang.Character
  (print [this w opts]
    (surrounded-by-noise this w opts
                         #(helpers/write-character this w)))
  java.lang.Object
  (print [this w opts]
    (surrounded-by-noise this w opts
                         #(writer/write w (str this))))
  nil
  (print [this w opts]
    (surrounded-by-noise this w opts
                         #(writer/write w "nil"))))
