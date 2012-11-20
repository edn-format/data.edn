(ns edn-data-gen.printable
  "Extending the IEDNPrintable protocol to base types"
  (:require [edn-data-gen.protocols.print :as print]))

(extend-protocol print/IEDNPrintable
  clojure.lang.PersistentVector
  (print-edn-data [this edn-printer]
    (print/print-edn-collection edn-printer this "[" "]"))
  clojure.lang.IPersistentMap
  (print-edn-data [this edn-printer]
    (print/print-edn-collection edn-printer this "{" "}"))
  clojure.lang.IPersistentSet
  (print-edn-data [this edn-printer]
    (print/print-edn-collection edn-printer this "#{" "}"))
  clojure.lang.IPersistentList
  (print-edn-data [this edn-printer]
    (print/print-edn-collection edn-printer this "(" ")"))
  java.lang.Object
  (print-edn-data [this edn-printer]
    (print/print-edn edn-printer this)))

(extend-protocol print/IEDNPrintSeparable
  clojure.lang.IPersistentMap
  (print-edn-separator [this edn-printer] ",")
  clojure.lang.IPersistentSet
  (print-edn-separator [this edn-printer] ","))
