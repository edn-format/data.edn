(ns edn-data-gen.printers
  "a basic edn printer"
  (:require [edn-data-gen.protocols.print :as print]))

(defn pr-sequential-writer
  [edn-printer coll begin end]
  (print/edn-write edn-printer begin)
  (when (seq coll)
    (print/print-edn-data (first coll) edn-printer))
  (doseq [o (next coll)]
    (when-let [sep (print/get-edn-separator coll)]
      (print/edn-write edn-printer sep))
    (print/print-separated-edn edn-printer)
    (print/print-edn-data o edn-printer))
  (print/edn-write edn-printer end))

(deftype SingleSpaceInjectingEDNPrinter [writer]
  print/IEDNPrinter
  (print-edn-collection [this coll begin end]
    (pr-sequential-writer this coll begin end))
  (print-separated-edn [this]
    (print/edn-write this " "))
  (print-edn [this x]
    (print/edn-write this (str x)))
  (edn-write [this s]
    (.write writer s)))


(comment

  (import '[edn_data_gen.printers SingleSpaceInjectingEDNPrinter])
  (require '[edn-data-gen.printable :as printable])
  (require '[edn-data-gen.protocols.print :as edn-print])
  (edn-print/print-edn-data [1 2 3] (SingleSpaceInjectingEDNPrinter. *out*))
  ;;=> [1 2 3]
  (edn-print/print-edn-data #{1 2 3} (SingleSpaceInjectingEDNPrinter. *out*))
  ;;=> #{1 2 3}
  (edn-print/print-edn-data {:a 1 :b 2} (SingleSpaceInjectingEDNPrinter. *out*))
  ;;=>{[:a 1] [:b 2]}
  ;;   ^ wrong!
  )
