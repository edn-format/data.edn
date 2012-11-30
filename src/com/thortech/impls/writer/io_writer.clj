;;;; The use and distribution terms for this software are covered by the
;;;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;;; which can be found in the file epl-v10.html at the root of this distribution.
;;;; By using this software in any fashion, you are agreeing to be bound by
;;;; the terms of this license.
;;;; You must not remove this notice, or any other, from this software.

(ns ^{:author "Tom Hickey, Jim Altieri"}
  com.thortech.impls.writer.io-writer
  "Implementation of the IWriter protocol extended to java.io.Writer"
  (:require [com.thortech.print.protocols.writer :as writer]))

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
