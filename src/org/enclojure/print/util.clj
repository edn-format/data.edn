(ns org.enclojure.print.util
  "Helpers for printing collections, etc."
  (:require [org.enclojure.print.protocols.writer :as writer]
            [org.enclojure.print.protocols.printable :as printable]))

(defn- call-through
  "Recursively call x until it doesn't return a function."
  [x]
  (if (fn? x)
    (recur (x))
    x))

(defn write-character
  "Writes a character using the IWriter protocol. Named chars are looked up
using core/char-name-string collection."
  [c w]
  (writer/append w \\)
  (if-let [n (char-name-string c)]
    (writer/write w n)
    (writer/append w c)))

(defn write-string
  "Writres a string using the IWriter protocol. Escape chars are looked up
using core/char-escape-string collection."
  [s w]
  (writer/append w \")
  (dotimes [n (count s)]
    (let [c (.charAt s n)]
      (if-let [e (char-escape-string c)]
        (writer/write w e)
        (writer/append w c))))
  (writer/append w \"))

(defn print-seq-contents
  "prints the contents of a sequence separated by separator.
separator can be a string or a fn (uses call-through).
Elements default to printing via the IPrintable protocol
unless another print-one fn is supplied."
  ([coll w opts separator]
     (print-seq-contents coll w opts (fn [o w opts]
                                       (printable/print o w opts)) separator))
  ([coll w opts print-one separator]
     (when (seq coll)
       (print-one (first coll) w opts))
     (doseq [x (next coll)]
       (writer/write w (call-through separator))
       (print-one x w opts))))

(defn print-sequential
  "Prints a sequential collection with print-seq-contents delimited with begin & end"
  [coll w opts begin separator end]
  (writer/write w begin)
  (print-seq-contents coll w opts separator)
  (writer/write w end))

(defn print-map
  "Prints a map, with elements separated by separator (defaults to comma+pace).
Keys and values are printed via IPrintable protocol."
  ([m w opts]
     (print-map m w opts ", "))
  ([m w opts separator]
     (writer/append w "{")
     (print-seq-contents (seq m) w opts
                         (fn [e w opts]
                           (do (printable/print (key e) w opts)
                               (writer/append w \space)
                                (printable/print (val e) w opts)))
                         separator)
     (writer/write w "}")))
