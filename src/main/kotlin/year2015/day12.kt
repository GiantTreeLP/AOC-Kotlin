package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResourceLines
import year2015.Day12.JsonValue.Companion.parseJson

@AutoService(AOCSolution::class)
class Day12 : AOCSolution {
    override val year = 2015
    override val day = 12

    private sealed interface JsonValue {

        class Number(@JvmField val value: Int) : JsonValue

        class StringValue(@JvmField val value: String) : JsonValue

        class Object(
            @JvmField val properties: List<JsonValue>,
            @JvmField val isRed: Boolean
        ) : JsonValue

        class Array(@JvmField val elements: List<JsonValue>) : JsonValue

        companion object {
            /*
            Parse a JSON string into a JsonValue object.
            This is a simple recursive descent parser that parses the JSON string character by character.
            The parser is implemented as a set of mutually recursive functions, one for each JSON value type.

            Given the actual problem, there is no need to parse the key of an object, so we can ignore it.
            There is also no need to keep track of string values, so we can ignore them as well.
            Only a string with the value "red" in an object is relevant for this problem, so its presence is tracked.
            Additionally, there is no whitespace in the input, so we can skip handling it as well.
             */

            private var index = 0

            fun parseJson(jsonString: String): JsonValue {
                index = 0
                return parseJsonInternal(jsonString)
            }

            private fun parseJsonInternal(jsonString: String): JsonValue {
                // Force a switch (LOOKUPSWITCH) instead of a list of if-else statements
                when (jsonString[index].code) {
                    '{'.code -> {
                        return parseObject(jsonString)
                    }

                    '['.code -> {
                        return parseArray(jsonString)
                    }

                    '"'.code -> {
                        return parseString(jsonString)
                    }

                    '0'.code,
                    '1'.code,
                    '2'.code,
                    '3'.code,
                    '4'.code,
                    '5'.code,
                    '6'.code,
                    '7'.code,
                    '8'.code,
                    '9'.code,
                    '-'.code -> {
                        return parseNumber(jsonString)
                    }

                    else -> error("Unexpected character at index $index")
                }
            }

            private fun parseObject(jsonString: String): Object {
                val properties = mutableListOf<JsonValue>()

                require(jsonString[index] == '{') { "Expected '{' at index $index" }
                ++index

                var isRed = false

                while (jsonString[index] != '}') {
                    parseString(jsonString)  // The key is not needed for this problem, so we can ignore it

                    if (jsonString[index] != ':') {
                        error("Expected ':' at index $index")
                    }
                    ++index
                    val value = parseJsonInternal(jsonString)
                    if (value !is StringValue) {
                        // Only add non-string values
                        properties.add(value)
                    } else if (value.value == "red") {
                        isRed = true
                    }

                    if (jsonString[index] == ',') {
                        ++index
                    }
                }

                ++index

                return Object(properties, isRed)
            }

            private fun parseString(jsonString: String): StringValue {
                require(jsonString[index] == '"') { "Expected '\"' at index $index" }
                ++index

                val start = index
                while (jsonString[index] != '"') {
                    ++index
                }
                val value = jsonString.substring(start, index)
                ++index
                return StringValue(value)
            }

            private fun parseNumber(jsonString: String): Number {
                var start = index

                if (jsonString[index] == '-') {
                    ++index
                }

                while (jsonString[index] in '0'..'9') {
                    ++index
                }
                val value = Integer.parseInt(jsonString, start, index, 10)
                return Number(value)
            }

            private fun parseArray(jsonString: String): Array {
                val elements = mutableListOf<JsonValue>()

                require(jsonString[index] == '[') { "Expected '[' at index $index" }
                ++index

                while (jsonString[index] != ']') {
                    val element = parseJsonInternal(jsonString)
                    elements.add(element)
                    if (jsonString[index] == ',') {
                        ++index
                    }
                }

                ++index

                return Array(elements)
            }
        }
    }

    private fun sumPart1(json: JsonValue): Int {
        return when (json) {
            is JsonValue.Number -> json.value
            is JsonValue.StringValue -> 0
            is JsonValue.Object -> json.properties.sumOf { sumPart1(it) }
            is JsonValue.Array -> json.elements.sumOf { sumPart1(it) }
        }
    }

    private fun sumPart2(json: JsonValue): Int {
        return when (json) {
            is JsonValue.Number -> json.value
            is JsonValue.StringValue -> 0
            is JsonValue.Object -> {
                if (json.isRed) {
                    0
                } else {
                    json.properties.sumOf { sumPart2(it) }
                }
            }

            is JsonValue.Array -> json.elements.sumOf { sumPart2(it) }
        }
    }

    override fun part1(inputFile: String): String {
        val inputs = readResourceLines(inputFile)

        val sums = inputs.map { json ->
            val jsonValue = parseJson(json)
            sumPart1(jsonValue)
        }

        return sums.joinToString(", ")
    }

    override fun part2(inputFile: String): String {
        val inputs = readResourceLines(inputFile)

        val sums = inputs.map { json ->
            val jsonValue = parseJson(json)
            sumPart2(jsonValue)
        }

        return sums.joinToString(", ")
    }
}
