(ns org.enclojure.print.protocols.writer
  "Writer protocol"
  (:refer-clojure :exclude [flush]))

(defprotocol IWriter
  "Protocol for writers"
  (append [writer c] "Append a character.")
  (write [writer s] "Write a string.")
  (flush [writer] "Flush the stream.")
  (close [writer] "Close the stream, flushing it first."))
