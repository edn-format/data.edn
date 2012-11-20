(ns edn-data-gen.injecting-printable
  "DIE")

(defn write-awesome-comment
  [writer]
  (write writer (gen/comments)))

(defn pr-sequential-comment-injecting-writer
  [coll writer begin separator end]
  (write writer begin)
  (when (seq coll)
    (print/print-edn (first coll) writer))
  (doseq [o (next coll)]
    (write writer separator)
    (write-awesome-comment writer)
    (print/print-edn o writer))
  (write writer end))


(extend-protocol print/IEDNPrintable
  vector
  (print-edn [this writer]
    (pr-sequential-comment-injecting-writer this writer
                          "["
                          " "
                          "]"))
