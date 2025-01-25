package year2015

import com.google.auto.service.AutoService
import common.AOCSolution
import common.readResourceLines
import kotlin.math.max

@AutoService(AOCSolution::class)
class Day15 : AOCSolution {
    override val year = 2015
    override val day = 15

    private data class Ingredient(
        val name: String,
        val capacity: Int,
        val durability: Int,
        val flavor: Int,
        val texture: Int,
        val calories: Int
    )

    private fun parseIngredients(inputFile: String): List<Ingredient> {
        val lines = readResourceLines(inputFile)

        return buildList(lines.size) {
            lines.forEach { line ->
                val match = ingredientRegex.matchEntire(line) ?: error("Invalid input")
                add(
                    Ingredient(
                        match.groupValues[1],
                        match.groupValues[2].toInt(),
                        match.groupValues[3].toInt(),
                        match.groupValues[4].toInt(),
                        match.groupValues[5].toInt(),
                        match.groupValues[6].toInt()
                    )
                )
            }
        }
    }

    private fun cookieScore(ingredients: List<Ingredient>, amounts: IntArray, calorieLimit: Long?): Long {
        var capacity = 0L
        var durability = 0L
        var flavor = 0L
        var texture = 0L
        var calories = 0L

        for (i in ingredients.indices) {
            capacity += ingredients[i].capacity * amounts[i]
            durability += ingredients[i].durability * amounts[i]
            flavor += ingredients[i].flavor * amounts[i]
            texture += ingredients[i].texture * amounts[i]
            calories += ingredients[i].calories * amounts[i]
        }

        if (calorieLimit != null && calorieLimit != calories) {
            return 0
        }

        return max(0, capacity) * max(0, durability) * max(0, flavor) * max(0, texture)
    }

    private fun findHighestScoringAmounts(
        teaspoons: Int,
        ingredients: List<Ingredient>,
        amounts: IntArray = IntArray(ingredients.size),
        ingredientIndex: Int = 0,
        calorieLimit: Long? = null
    ): Long {
        if (ingredientIndex == ingredients.size - 1) {
            amounts[ingredientIndex] = teaspoons
            return cookieScore(ingredients, amounts, calorieLimit)
        }

        var highestScore = 0L
        for (i in 0..teaspoons) {
            amounts[ingredientIndex] = i
            highestScore = max(
                highestScore,
                findHighestScoringAmounts(teaspoons - i, ingredients, amounts, ingredientIndex + 1, calorieLimit)
            )
        }

        return highestScore
    }

    override fun part1(inputFile: String): String {
        val ingredients = parseIngredients(inputFile)

        val highestScore = findHighestScoringAmounts(100, ingredients)

        return highestScore.toString()
    }

    override fun part2(inputFile: String): String {
        val ingredients = parseIngredients(inputFile)

        val highestScore = findHighestScoringAmounts(100, ingredients, calorieLimit = 500)

        return highestScore.toString()
    }

    companion object {
        private val ingredientRegex =
            Regex("""(\w+): capacity (-?\d+), durability (-?\d+), flavor (-?\d+), texture (-?\d+), calories (-?\d+)""")
    }
}
