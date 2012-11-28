(ns edn-data-gen.print.printers.basic
  "Basic printer using spaces for delimiters."
  (:require [edn-data-gen.print.protocols.writer :as writer]
            [edn-data-gen.print.protocols.printable :as printable]
            [edn-data-gen.print.helpers :as helpers]))


(extend-protocol printable/IPrintable
  clojure.lang.IPersistentMap
  (print [this w opts]
    (helpers/print-map this w opts))

  clojure.lang.PersistentVector
  (print [this w opts]
    (helpers/print-sequential this w opts
                              "["
                              " "
                              "]"))

  clojure.lang.IPersistentSet
  (print [this w opts]
    (helpers/print-sequential this w opts
                              "#{"
                              ", "
                              "}"))

  clojure.lang.IPersistentList
  (print [this w opts]
    (helpers/print-sequential this w opts
                              "("
                              " "
                              ")"))

  java.lang.String
  (print [this w opts]
    (helpers/write-string this w))
  java.lang.Character
  (print [this w opts]
    (helpers/write-character this w))
  java.lang.Object
  (print [this w opts]
    (writer/write w (str this))))
