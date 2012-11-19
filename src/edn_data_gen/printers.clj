(ns edn-data-gen.printers
  "Short package description."
  (:require [edn-data-gen.protocols.print :as print]))

(defn pr-sequential-writer
  [begin separator end coll]
  (print begin)
  (when (seq coll)
    (print/print-edn (first coll)))
  (doseq [o (next coll)]
    (print separator)
    (print/print-edn o))
  (print end))

(extend-protocol print/IPrintEDN
  clojure.lang.PersistentVector
  (print-edn [this]
    (pr-sequential-writer "["
                          " "
                          "]"
                          this))
  clojure.lang.IPersistentCollection
  (print-edn [this] )
  java.lang.Object
  (print-edn [this] (pr this)))
