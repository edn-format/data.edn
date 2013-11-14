# data.edn

A Clojure library for creation of edn data.

## Usage

FIXME

For now, you can see an example of printing edn data to a file in [examples/org/edn_format/data/edn/examples/create_files.clj](http://github.com/edn-format/data.edn/blob/master/examples/org/edn_format/data/edn/examples/create_files.clj)


## Releases and dependency information

Latest stable release: 0.1.0

With [Leiningen](http://github.com/technomancy/leiningen), add the following to you `project.clj` file:

    [org.edn-format/data.edn "0.1.0"]

With [Maven](http://maven.apache.org), add the following to your `pom.xml` file:

    <dependency>
      <groupId>org.edn-format</groupId>
      <artifactId>data.edn</artifactId>
      <version>0.1.0</version>
    </dependency>

## Reference

[API Documentation](http://edn-format.github.com/data.edn)

## Rationale

In order to test different implementations of edn data readers/writers on different platforms, we needed a way to generate comprehensive edn test data sets. We also wanted to be able to test introducing “noise” into the input streams to ensure the edn readers handles this. By “noise” we mean: comments, discard, whitespace (newlines, carriage returns).

This is built on top of clojure.data.generators to generate data for creating of edn.

## To Do

- write tests of round-tripping to/from edn strings
- create alternate version of gen-many that generates data and prints it with n print configurations
- create script for outputting a suite of edn test data files (bash & lein)
- printing doubles in various formats
- declarative spec for data generation
- service API

## YourKit

YourKit is kindly supporting open source projects with its full-featured Java Profiler.

YourKit, LLC is the creator of innovative and intelligent tools for profiling Java and .NET applications. Take a look at YourKit's leading software products:

- [YourKit Java Profiler](http://www.yourkit.com/java/profiler/index.jsp) and
- [YourKit .NET Profiler](http://www.yourkit.com/.net/profiler/index.jsp).

## License

Distributed under the [Eclipse Public License](http://www.eclipse.org/legal/epl-v10.html), the same as Clojure.
