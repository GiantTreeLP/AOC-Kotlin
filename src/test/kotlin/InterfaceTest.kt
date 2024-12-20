import com.google.auto.service.AutoService
import common.AOCSolution
import common.Runner
import common.readResource
import kotlin.test.Test

@AutoService(AOCSolution::class)
class TestImplementation : AOCSolution {
    override val year: Int
        get() = 2024
    override val day: Int
        get() = 0

    override fun part1(inputFile: String): String {
        val input = readResource(inputFile)
        return "Success in Part 1 using file $inputFile (read ${input.length} characters)"
    }

    override fun part2(inputFile: String): String {
        val input = readResource(inputFile)
        return "Success in Part 2 using file $inputFile (read ${input.length} characters)"
    }

}

class InterfaceTest {
    @Test
    fun implementationTest() {
        val runner = Runner()
        runner.discoverSolutions()
        runner.run()
    }
}