package year2024

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResource
import kotlin.math.max
import kotlin.math.min

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

        var diskMapSize = 0

        for (i in inputDiskMap.indices) {
            diskMapSize += inputDiskMap[i].digitToInt()
        }

        val expandedDiskMap = IntArray(diskMapSize)

        var arrayIndex = 0
        for (index in inputDiskMap.indices) {
            val size = inputDiskMap[index]
            if (index and 1 == 0) {
                val fileId = index / 2
                repeat(size.digitToInt()) {
                    expandedDiskMap[arrayIndex++] = fileId
                }
            } else {
                repeat(size.digitToInt()) {
                    expandedDiskMap[arrayIndex++] = FREE_SPACE_ID
                }
            }
        }

        var lastInsertion = 0
        for (i in expandedDiskMap.indices.reversed()) {
            val block = expandedDiskMap[i]
            if (block != FREE_SPACE_ID) {
                for (j in lastInsertion until i) {
                    if (expandedDiskMap[j] == FREE_SPACE_ID) {
                        expandedDiskMap[j] = block
                        expandedDiskMap[i] = FREE_SPACE_ID
                        lastInsertion = j
                        break
                    }
                }
            }
        }

        var checksum = 0L
        for (i in expandedDiskMap.indices) {
            if (expandedDiskMap[i] != FREE_SPACE_ID) {
                checksum += expandedDiskMap[i].toLong() * i
            } else {
                break
            }
        }

        return checksum.toString()
    }

    override fun part2(inputFile: String): String {
        val inputDiskMap = readResource(inputFile).trim()

        val denseDiskMap = buildList(inputDiskMap.length * 2) {
            for (index in inputDiskMap.indices) {
                val size = inputDiskMap[index]
                if (index and 1 == 0) {
                    add(Cell.File((index / 2).toLong(), size.digitToInt()))
                } else {
                    add(Cell.FreeSpace(size.digitToInt()))
                }
            }
        }.toMutableList()

        // Keep track of the last insertion index for each file size
        // This is used to skip past free space cells that are too small to hold the file
        val lastInsertions = IntArray(10)
        // Move files from the back to free space in the front
        for (i in denseDiskMap.indices.reversed()) {
            val file = denseDiskMap[i]
            if (file is Cell.File) {

                // Find a free space cell that can hold the file
                for (j in lastInsertions[file.fileSize] until i) {
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
                // There is no need to look at more than the previous cell, the current cell after being swapped with
                // free space, and the next cell.
                for (k in max(i - 1, 0) until min(i, denseDiskMap.lastIndex - 1)) {
                    val currentCell = denseDiskMap[k]
                    val nextCell = denseDiskMap[k + 1]

                    if (currentCell is Cell.FreeSpace && nextCell is Cell.FreeSpace) {
                        denseDiskMap[k] = Cell.FreeSpace(currentCell.freeSpaceSize + nextCell.freeSpaceSize)
                        denseDiskMap.removeAt(k + 1)
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
                    // Sum from 1 to the end of the current file's blocks - sum from 1 to the start of the current file's blocks * file ID
                    checksum += (sumFrom1ToN((currentBlock + cell.fileSize).toLong()) - sumFrom1ToN(currentBlock - 1L)) * cell.fileId
                    currentBlock += cell.fileSize
                }
            }
        }

        return checksum.toString()
    }

    companion object {
        const val FREE_SPACE_ID = -1

        fun sumFrom1ToN(n: Long): Long = (n * (n + 1)) / 2
    }
}