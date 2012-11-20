(ns edn-data-gen.printers
  "Short package description."
  (:require [edn-data-gen.protocols.print :as print]))

;; (.write ^Writer *out* "#")

(defn pr-sequential-writer
  [coll edn-printer begin separator end]
  (print-edn edn-printer begin)
  (when (seq coll)
    (print/print-edn (first coll) edn-printer))
  (doseq [o (next coll)]
    (print separator)
    (print/print-edn o edn-printer))
  (print end))

(extend-protocol print/IEDNPrintable
  clojure.lang.PersistentVector
  (print-edn [this edn-printer]
    (pr-sequential-writer "["
                          " "
                          "]"
                          this))
  clojure.lang.IPersistentCollection
  (print-edn [this edn-printer] )
  java.lang.Object
  (print-edn [this edn-printer] (pr this)))
