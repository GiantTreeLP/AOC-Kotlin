package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResource

@AutoService(AOCSolution::class)
class Day09 : AOCSolution {
    override val year = 2024
    override val day = 9

    private sealed class Cell {
        data class FreeSpace(val freeSpaceSize: Int) : Cell()

        data class File(val fileId: Long, val fileSize: Int) : Cell()
    }

    override fun part1(inputFile: String): String {
        val inputDiskMap = readResource(inputFile).trim()

        val expandedDiskMap = buildList {
            inputDiskMap.forEachIndexed { index, size ->
                if (index % 2 == 0) {
                    repeat(size.digitToInt()) {
                        add(Cell.File((index / 2).toLong(), 1))
                    }
                } else {
                    repeat(size.digitToInt()) {
                        add(Cell.FreeSpace(1))
                    }
                }
            }
        }.toMutableList()

        var lastInsertion = 0
        for (i in expandedDiskMap.indices.reversed()) {
            val block = expandedDiskMap[i]
            if (block is Cell.File) {
                for (j in lastInsertion until i) {
                    if (expandedDiskMap[j] is Cell.FreeSpace) {
                        expandedDiskMap[j] = block
                        expandedDiskMap[i] = Cell.FreeSpace(1)
                        lastInsertion = j
                        break
                    }
                }
            }
        }

        val checksum = expandedDiskMap.mapIndexed { index, cell ->
            when (cell) {
                is Cell.FreeSpace -> 0
                is Cell.File -> cell.fileId * index
            }
        }.sum()

        return checksum.toString()
    }

    override fun part2(inputFile: String): String {
        val inputDiskMap = readResource(inputFile).trim()

        val denseDiskMap = buildList {
            inputDiskMap.forEachIndexed { index, size ->
                if (index % 2 == 0) {
                    add(Cell.File((index / 2).toLong(), size.digitToInt()))
                } else {
                    add(Cell.FreeSpace(size.digitToInt()))
                }
            }
        }.toMutableList()

        // Move files from the back to free space in the front
        val lastInsertions = mutableMapOf<Int, Int>().withDefault { 0 }
        for (i in denseDiskMap.indices.reversed()) {
            val file = denseDiskMap[i]
            if (file is Cell.File) {

                // Find a free space cell that can hold the file
                for (j in lastInsertions.getValue(file.fileSize) until i) {
                    val cell = denseDiskMap[j]
                    if (cell is Cell.FreeSpace && cell.freeSpaceSize >= file.fileSize) {
                        denseDiskMap[j] = file
                        lastInsertions[file.fileSize] = j

                        // Update free space cell
                        // If there is still free space left, add a new free space cell
                        // If the free space cell is empty, leave it out
                        val newFreeSpace = Cell.FreeSpace(cell.freeSpaceSize - file.fileSize)
                        if (newFreeSpace.freeSpaceSize > 0) {
                            denseDiskMap.add(j + 1, newFreeSpace)
                            denseDiskMap[i + 1] = Cell.FreeSpace(file.fileSize)
                        } else {
                            // Update the space that was occupied by the file
                            denseDiskMap[i] = Cell.FreeSpace(file.fileSize)
                        }
                        break
                    }
                }

                // Merge free space cells
                for (k in denseDiskMap.lastIndex downTo lastInsertions.getValue(file.fileSize) + 1) {
                    // Get the two cells to merge
                    val previousCell = denseDiskMap[k - 1]
                    val cellToMerge = denseDiskMap[k]

                    if (cellToMerge is Cell.FreeSpace &&
                        previousCell is Cell.FreeSpace
                    ) {
                        denseDiskMap[k - 1] =
                            Cell.FreeSpace(previousCell.freeSpaceSize + cellToMerge.freeSpaceSize)
                        denseDiskMap.removeAt(k)
                    }
                }
            }
        }

        // Calculate checksum
        var checksum = 0L
        var currentBlock = 0
        for (cell in denseDiskMap) {
            when (cell) {
                is Cell.FreeSpace -> {
                    currentBlock += cell.freeSpaceSize
                }

                is Cell.File -> {
                    repeat(cell.fileSize) { fileBlock ->
                        checksum += (currentBlock + fileBlock) * cell.fileId
                    }
                    currentBlock += cell.fileSize
                }
            }
        }

        return checksum.toString()
    }
}