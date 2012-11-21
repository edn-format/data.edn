(ns edn-data-gen.print.protocols.printable
  "Protocol for making types printable."
  (:refer-clojure :exclude [print]))

(defprotocol IPrintable
  "Protocol for printing to a writer via the IWriter protocol."
  (print [this writer opts]
    "Print this using writer."))
