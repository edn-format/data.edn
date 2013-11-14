;;;; The use and distribution terms for this software are covered by the
;;;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;;; which can be found in the file epl-v10.html at the root of this distribution.
;;;; By using this software in any fashion, you are agreeing to be bound by
;;;; the terms of this license.
;;;; You must not remove this notice, or any other, from this software.

(ns ^{:author "Tom Hickey, Jim Altieri"}
  org.edn-format.data.edn.examples.printable.basic
  "Basic example of extending IPrintable to core types.
Simply uses spaces for delimiters."
  (:require [org.edn-format.print.protocols.writer :as writer]
            [org.edn-format.print.protocols.printable :as printable]
            [org.edn-format.print.util :as util]))


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
