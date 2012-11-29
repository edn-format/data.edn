(ns org.enclojure.edn-gen.examples.printable.basic
  "Basic example of extending IPrintable to core types.
Simply uses spaces for delimiters."
  (:require [org.enclojure.print.protocols.writer :as writer]
            [org.enclojure.print.protocols.printable :as printable]
            [org.enclojure.print.util :as util]))


(extend-protocol printable/IPrintable
  clojure.lang.IPersistentMap
  (print [this w opts]
    (util/print-map this w opts))

  clojure.lang.PersistentVector
  (print [this w opts]
    (util/print-sequential this w opts
                              "["
                              " "
                              "]"))

  clojure.lang.IPersistentSet
  (print [this w opts]
    (util/print-sequential this w opts
                              "#{"
                              ", "
                              "}"))

  clojure.lang.IPersistentList
  (print [this w opts]
    (util/print-sequential this w opts
                              "("
                              " "
                              ")"))

  java.lang.String
  (print [this w opts]
    (util/write-string this w))
  java.lang.Character
  (print [this w opts]
    (util/write-character this w))
  java.lang.Object
  (print [this w opts]
    (writer/write w (str this))))
