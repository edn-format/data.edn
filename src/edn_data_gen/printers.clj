(ns edn-data-gen.printers
  "Short package description."
  (:require [edn-data-gen.protocols.print :as print]))


(extend-protocol print/IPrintEDN
  clojure.lang.IPersistentCollection
  (print-edn [this] (pr (str "DO IT " this)))
  java.lang.Object
  (print-edn [this] (pr this)))
