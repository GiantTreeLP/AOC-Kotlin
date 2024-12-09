package day9

import common.readResource

private object Part1 {
    sealed class Cell {
        class FreeSpace() : Cell() {
            override fun toString(): String {
                return "FreeSpace()"
            }
        }

        data class FileBlock(val fileId: Long) : Cell()
    }
}

fun main() {
    val inputDiskMap = readResource("day9/input").trim()

    val expandedDiskMap = inputDiskMap.mapIndexed { index, size ->
        if (index % 2 == 0) {
            buildList {
                repeat(size.digitToInt()) {
                    add(Part1.Cell.FileBlock((index / 2).toLong()))
                }
            }
        } else {
            buildList {
                repeat(size.digitToInt()) {
                    add(Part1.Cell.FreeSpace())
                }
            }
        }
    }.flatten().toMutableList()

    for (i in expandedDiskMap.indices.reversed()) {
        val block = expandedDiskMap[i]
        if (block is Part1.Cell.FileBlock) {
            for (j in 0 until i) {
                if (expandedDiskMap[j] is Part1.Cell.FreeSpace) {
                    expandedDiskMap[j] = block
                    expandedDiskMap[i] = Part1.Cell.FreeSpace()
                    break
                }
            }
        }
    }

    val checksum = expandedDiskMap.mapIndexed { index, cell ->
        when (cell) {
            is Part1.Cell.FreeSpace -> 0
            is Part1.Cell.FileBlock -> cell.fileId * index
        }
    }.sum()

    println(checksum)
}
