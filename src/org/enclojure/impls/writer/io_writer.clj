(ns org.enclojure.impls.writer.io-writer
  "Implementation of the IWriter protocol extended to java.io.Writer"
  (:require [org.enclojure.print.protocols.writer :as writer]))

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
