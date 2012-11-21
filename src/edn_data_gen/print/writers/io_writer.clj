(ns edn-data-gen.print.writers.io-writer
  "Short package description."
  (:require [edn-data-gen.print.protocols.writer :as writer]))

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
