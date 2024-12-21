# AOC 2024 in Kotlin

[![GitHub License](https://img.shields.io/github/license/GiantTreeLP/AOC2024-Kotlin?style=for-the-badge)](LICENSE)


This repository contains my solutions for the [Advent of Code](https://adventofcode.com/) puzzles in the Kotlin programming language. 

## Running the solutions

The solutions are organized by year and day. Each day has its own file, 
which contains the solution for both parts of the puzzle.

The boilerplate for new solutions is:

```kotlin
package yearXXXX

import com.google.auto.service.AutoService
import common.AOCSolution

@AutoService(AOCSolution::class)
class DayXX : AOCSolution {
    override val year = XXXX
    override val day = XX
    
    override fun part1(inputFile: String): String {
        TODO("Solve part 1 here")
    }
    
    override fun part2(inputFile: String): String {
        TODO("Solve part 2 here")
    }
}
```

Commonly used functions, classes and extensions can be found in the `common` package.

Input is expected to be in a file named `input` in the resources folder of the corresponding year and day.
The same goes for the sample input, which is expected to be in a file named `sample`.
The layout of the directories is as follows:
```
- src
  - main
    - kotlin
      - year2024
        - dayXX.kt
      - resources
        -yearXXXX
          - dayXX
            - input
            - sample
```

To run the solutions, simply run the `main` function of the [`common.Runner`](src/main/kotlin/common/runner.kt) file.

Don't expect the code to be perfect, I'm trying to solve the puzzles in a way that is good enough for me.
