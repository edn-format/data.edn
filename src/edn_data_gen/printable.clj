(ns edn-data-gen.printable
  "DIE")


(defn pr-sequential-writer
  [coll writer begin separator end]
  (write writer begin)
  (when (seq coll)
    (print/print-edn (first coll) writer))
  (doseq [o (next coll)]
    (write writer separator)
    (print/print-edn o writer))
  (write writer end))


(extend-protocol print/IEDNPrintable
  vector
  (print-edn [this writer]
    (pr-sequential-writer this writer
                          "["
                          " "
                          "]"))
