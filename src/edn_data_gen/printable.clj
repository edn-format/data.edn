(ns edn-data-gen.printable
  "Extending the IEDNPrintable protocol to base types"
  (:require [edn-data-gen.protocols.print :as print]))

(def
  char-escape-string
    {\newline "\\n"
     \tab  "\\t"
     \return "\\r"
     \" "\\\""
     \\  "\\\\"
     \formfeed "\\f"
     \backspace "\\b"})


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
  clojure.lang.MapEntry
  (print-edn-data [this edn-printer]
    (let [[k v] this]
      (print/print-edn-data k edn-printer)
      (print/print-separated-edn edn-printer)
      (print/print-edn-data v edn-printer)))
  java.lang.String
  (print-edn-data [this edn-printer]
    (print/edn-write edn-printer "\"")
    (dotimes [n (count this)]
      (let [c (.charAt this n)
            e (char-escape-string c)]
        (print/edn-write edn-printer (if e e (str c)))))
    (print/edn-write edn-printer "\""))
  java.lang.Object
  (print-edn-data [this edn-printer]
    (print/print-edn edn-printer this)))

(extend-protocol print/IEDNPrintSeparable
  clojure.lang.IPersistentMap
  (get-edn-separator [this] ",")
  clojure.lang.IPersistentSet
  (get-edn-separator [this] ",")
  java.lang.Object
  (get-edn-separator [this] nil))
