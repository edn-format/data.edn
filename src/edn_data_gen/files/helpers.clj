(ns edn-data-gen.files.helpers
  "Utility functions for files and directories."
  (:require [clojure.java.io :as io]))

(defn- file-exists?
  "returns a bool indicating whether a dir or file exists"
  [path]
  (.exists (java.io.File. path)))

(defn- mkdir
  [path]
  (.mkdir (java.io.File. path)))

(defn ls
  [path]
  (let [file (java.io.File. path)]
    (if (.isDirectory file)
      (seq (.list file))
      (when (.exists file)
        [path]))))

(defn mkdirs [path]
  (.mkdirs (io/file path)))

(defn make-parent-dirs [path]
  (-> path
      io/file
      .getParent
      mkdirs))

(defn ensure-directory!
  [path]
  (when-not (ls path)
    (mkdirs path)))

(defn ensure-parent-directory!
  [path]
  (when-not (ls path)
    (make-parent-dirs path)))
