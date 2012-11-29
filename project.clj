(defproject org.enclojure/edn-gen "0.1.0-SNAPSHOT"
  :description "A library for creation of edn data."
  :url "http://github.com/enclojure/edn-data-gen"
  :license {:name "Eclipse Public License - v 1.0"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojure/data.generators "0.1.0"]]
  :jvm-opts ["-Xmx2g" "-server"])
