(ns edn-data-gen.printables.comment-injection
  "Extending IPrintables with method of injecting comments."
  (:require [edn-data-gen.print.protocols.writer :as writer]
            [edn-data-gen.print.protocols.printable :as printable]
            [edn-data-gen.print.helpers :as helpers]))

(defn generate-comment
  [opts]
  (when-let [cgen (:generator/comment opts)]
    (str (cgen))))

(defn separator-with-comment
  [sep opts]
  (fn []
    (str sep (generate-comment opts))))


(extend-protocol printable/IPrintable
  clojure.lang.IPersistentMap
  (print [this w opts]
    (helpers/print-map this w opts
                       (separator-with-comment ", " opts)))

  clojure.lang.PersistentVector
  (print [this w opts]
    (helpers/print-sequential this w opts
                              "["
                              (separator-with-comment " " opts)
                              "]"))

  clojure.lang.IPersistentSet
  (print [this w opts]
    (helpers/print-sequential this w opts
                              "#{"
                              (separator-with-comment ", " opts)
                              "}"))

  clojure.lang.IPersistentList
  (print [this w opts]
    (helpers/print-sequential this w opts
                              "("
                              (separator-with-comment " " opts)
                              ")"))

  java.lang.String
  (print [this w opts]
    (helpers/write-string this w))
  java.lang.Character
  (print [this w opts]
    (helpers/write-character this w))
  java.lang.Object
  (print [this w opts]
    (writer/write w (str this)))
  nil
  (print [this w opts]
    (writer/write w "nil")))


(extend-type java.io.Writer
  writer/IWriter
  (append [w c]
    (.append w c))
  (write [w s]
    (.write w s))
  (flush [w]
    (.flush w))
  (close [w]
    (.close w)))

(comment
  (printable/print (edn-gen/hierarchical-anything) *out* {:generator/comment edn-gen/comment-block})

  )
