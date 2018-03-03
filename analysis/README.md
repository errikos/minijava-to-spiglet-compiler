# Static Analysis on Spiglet code

Optimising Spiglet code via static analysis in Datalog.

Developed in the context of the 2015 Compilers course, taught in the
Department of Informatics and Telecommunications in University of Athens.

The project package tree looks like this:

```
- src
  - spiglet
    - parser
    - static_analyzer
      - fact_producer
        - structures
        - visitor
      - iris
    - syntaxtree
    - visitor
- utils
```

You will also find the `analysis_logic` directory, which contains the Datalog
rules for infering the analysis results, as well as the queries directory
which contains the `queries.iris` file.

The program's main class (`SpigletOptimizer`) accepts one or more `.spg` files
as arguments, runs the Optimizer on each one of them and places the analysis
results within a directory having the same path as the input spiglet file,
without the extension (for example, if `examples/Add.spg` is given as an
argument, then the analysis results will be placed within the `examples/Add`
directory).

For your convenience, a `Makefile` is included to build the project.
For your further convenience, a script that runs all the examples one by one
is also provided. Just place any `.spg` file you want in the examples directory
and run the script.
