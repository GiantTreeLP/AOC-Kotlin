# AOC 2024 in Kotlin

[![GitHub License](https://img.shields.io/github/license/GiantTreeLP/AOC2024-Kotlin?style=for-the-badge)](LICENSE)


This repository contains my solutions for the [Advent of Code 2024](https://adventofcode.com/2024) puzzles. 
I will be solving them in Kotlin.

## Running the solutions

Each solution is a Kotlin file with a `main` function that reads the input from a file and prints the solution to the console.  
Each day has its own package and each part of the puzzle has its own file.
That way I am able to run each part separately.
Commonly used functions, classes and extensions can be found in the `common` package.

Input is expected to be in a file named `input` in the resources folder of the corresponding day.
The same goes for the sample input, which is expected to be in a file named `sample`.
The layout of the directories is as follows:
```
- src
  - main
    - kotlin
      - year2024
        - dayXX.kt
      - resources
        - dayXX
          - input
          - sample
```

To run a solution, simply run the `main` function of the [`common.Runner`](src/main/kotlin/common/runner.kt) file.

Don't expect the code to be perfect, I'm trying to solve the puzzles in a way that is good enough for me.
