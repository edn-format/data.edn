(ns edn-data-gen.protocols.print
  "Protocol for printing edn data")


(defprotocol IPrintEDN
  (print-edn [this] "Prints itslef to out"))

(defprotocol IEDNPrinter
  (print-collection [this data] "Prints given data via itself"))
