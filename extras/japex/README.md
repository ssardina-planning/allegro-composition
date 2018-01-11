
[JAPEX](https://github.com/kohsuke/japex) is a framework to support micro-benchmarking in Java. since it is not supported/developed anymore, the version is fully included here for reproduction if desired.

The whole package to benchmark is in `extras/japex`. Information about JAPEX, including a copy of the the manual can be found `extras/japex/japex-info`

Some old how-to on JAPEX configuration [here](https://blogs.oracle.com/enterprisetechtips/performance-regression-testing-using-japex-and-wstest), but users should get enough by looking at the configuration files.

Basically, a JAPEX test benchmark is composed of:

* A main file `build-<XXXX>.xml` that defines the process to compile and run the benchmark test.
* A test definition file `<XXXX>.xml` that defines the structure of the test.
* The domain definition used (and pointed within file `<XXXX>.xml`). All those domains are under `extras/testbed/`

So, a test can be run as follows:

    ```
    cd extras/japex/
    ant run -f build-KR-simple.xml 
    ```

This will produce a subdir `dist/` with all the classes compiled to run the test, and more importantly a dated subdir in `reports/` with the HTML report after the test.

JavaCC needs to be installed in the system and as ECLIPSe plugin so that the
corresponding java sources for the src/behaviorComposition/parser/ grammars can be generated.

