;;;; The use and distribution terms for this software are covered by the
;;;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;;; which can be found in the file epl-v10.html at the root of this distribution.
;;;; By using this software in any fashion, you are agreeing to be bound by
;;;; the terms of this license.
;;;; You must not remove this notice, or any other, from this software.

(ns ^{:author "Tom Hickey, Jim Altieri"}
  com.thortech.print.protocols.writer
  "Writer protocol"
  (:refer-clojure :exclude [flush]))

(defprotocol IWriter
  "Protocol for writers"
  (append [writer c] "Append a character.")
  (write [writer s] "Write a string.")
  (flush [writer] "Flush the stream.")
  (close [writer] "Close the stream, flushing it first."))
