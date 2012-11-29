;;;; The use and distribution terms for this software are covered by the
;;;; Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;;;; which can be found in the file epl-v10.html at the root of this distribution.
;;;; By using this software in any fashion, you are agreeing to be bound by
;;;; the terms of this license.
;;;; You must not remove this notice, or any other, from this software.

(ns org.enclojure.edn-gen.examples.printable.comment-injection
  "Extending IPrintables with method of injecting comments."
  (:require [org.enclojure.print.protocols.writer :as writer]
            [org.enclojure.print.protocols.printable :as printable]
            [org.enclojure.print.util :as util]))

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
    (util/print-map this w opts
                       (separator-with-comment ", " opts)))

  clojure.lang.PersistentVector
  (print [this w opts]
    (util/print-sequential this w opts
                              "["
                              (separator-with-comment " " opts)
                              "]"))

  clojure.lang.IPersistentSet
  (print [this w opts]
    (util/print-sequential this w opts
                              "#{"
                              (separator-with-comment ", " opts)
                              "}"))

  clojure.lang.IPersistentList
  (print [this w opts]
    (util/print-sequential this w opts
                              "("
                              (separator-with-comment " " opts)
                              ")"))

  clojure.lang.LazySeq
  (print [this w opts]
    (util/print-sequential this w opts
                              "("
                              (separator-with-comment " " opts)
                              ")"))

  java.lang.String
  (print [this w opts]
    (util/write-string this w))
  java.lang.Character
  (print [this w opts]
    (util/write-character this w))
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
