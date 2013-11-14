(defproject org.edn-format/data.edn "0.1.0-SNAPSHOT"
  :description "A library for creation of edn data."
  :url "http://github.com/edn-format/data.edn"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.generators "0.1.2"]]
  :jvm-opts ["-Xmx2g" "-server"])
