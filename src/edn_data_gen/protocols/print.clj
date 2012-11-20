(ns edn-data-gen.protocols.print
  "Protocol for printing edn data")

(defprotocol IEDNPrintable
  "Protocol for objects that can be printable with an EDNPrinter"
  (print-edn-data [this edn-printer]
    "Dispatches printing to the appropriate protocol on the supplied edn-printer"))

(defprotocol IEDNPrintSeparable
  "Protocol for separating objects (e.g. space delim, comma delim, newline delim) that can be printable with an EDNPrinter.
This is only a separate protocol mainly to aid in printing comma btw map/set entries."
  (print-edn-separator [this edn-printer]
    "Prints a separator appropriate for between prev and next items"))

(defprotocol IEDNPrinter
  "Protocol for an edn printer, with functions for printing collections, scalars, delimiters.

The printers job is to take data and output strings to a writer

"
  (print-edn-collection [this coll begin end]
    "Prints the begin, the contents of coll separated by separator, and end.
content items are printed via print-edn protocol if extended, else fallback to print-single.")
  (print-separated-edn [this prev next]
    "Prints a separator appropriate for between prev and next items")
  (print-edn [this x]
    "Prints single item")
  (edn-write [this s]
    "dispatch writing the string to a writer")
  )


(defprotocol IWriter
  (write [writer s])
  (flush [writer]))
