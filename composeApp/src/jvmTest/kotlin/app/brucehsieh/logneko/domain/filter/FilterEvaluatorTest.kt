package app.brucehsieh.logneko.domain.filter

import app.brucehsieh.logneko.data.modal.LineItem
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class FilterEvaluatorTest {

    @Test
    fun `empty text term should return false`() {
        // Arrange
        val filterExpression = FilterExpression.Term("")

        // Act
        val result = FilterEvaluator.matches(lineItem1, filterExpression)

        // Assert
        Assertions.assertFalse(result)
    }

    @CsvSource(
        "connected, false, false, false, false, 1",
        "retrying, false, false, false, false, 2",
        "operation, false, false, false, false, 3",
        "Disk, false, false, false, false, 4",
        "possible, false, false, false, false, 5",
        "DEBUG, false, false, false, false, 6",
    )
    @ParameterizedTest
    fun `LineItem that contains the text should return true`(
        text: String,
        caseSensitive: Boolean,
        wholeWord: Boolean,
        regex: Boolean,
        negated: Boolean,
        lineItemIndex: Int
    ) {
        // Arrange
        val filterExpression = FilterExpression.Term(text, caseSensitive, wholeWord, regex, negated)
        val lineItem = getLineItem(lineItemIndex)

        // Act
        val result = FilterEvaluator.matches(lineItem, filterExpression)

        // Assert
        Assertions.assertTrue(result)
    }

    @CsvSource(
        "aaaa, false, false, false, false, 1",
        "aaaa, false, false, false, false, 2",
        "aaaa, false, false, false, false, 3",
        "aaaa, false, false, false, false, 4",
        "aaaa, false, false, false, false, 5",
        "aaaa, false, false, false, false, 6",
    )
    @ParameterizedTest
    fun `LineItem that does not contain the text should return false`(
        text: String,
        caseSensitive: Boolean,
        wholeWord: Boolean,
        regex: Boolean,
        negated: Boolean,
        lineItemIndex: Int
    ) {
        // Arrange
        val filterExpression = FilterExpression.Term(text, caseSensitive, wholeWord, regex, negated)
        val lineItem = getLineItem(lineItemIndex)

        // Act
        val result = FilterEvaluator.matches(lineItem, filterExpression)

        // Assert
        Assertions.assertFalse(result)
    }

    @CsvSource(
        "aaaa, false, false, false, true, 1",
        "aaaa, false, false, false, true, 2",
        "aaaa, false, false, false, true, 3",
        "aaaa, false, false, false, true, 4",
        "aaaa, false, false, false, true, 5",
        "aaaa, false, false, false, true, 6",
    )
    @ParameterizedTest
    fun `LineItem that does not contain the text, but is negated, should return true`(
        text: String,
        caseSensitive: Boolean,
        wholeWord: Boolean,
        regex: Boolean,
        negated: Boolean,
        lineItemIndex: Int
    ) {
        // Arrange
        val filterExpression = FilterExpression.Term(text, caseSensitive, wholeWord, regex, negated)
        val lineItem = getLineItem(lineItemIndex)

        // Act
        val result = FilterEvaluator.matches(lineItem, filterExpression)

        // Assert
        Assertions.assertTrue(result)
    }

    @CsvSource(
        "connected, true, false, false, false, 1",
        "retrying, true, false, false, false, 2",
        "operation, true, false, false, false, 3",
        "Disk, true, false, false, false, 4",
        "possible, true, false, false, false, 5",
        "DEBUG, true, true, false, false, 6",
    )
    @ParameterizedTest
    fun `LineItem that matches the case sensitive text should return true`(
        text: String,
        caseSensitive: Boolean,
        wholeWord: Boolean,
        regex: Boolean,
        negated: Boolean,
        lineItemIndex: Int
    ) {
        // Arrange
        val filterExpression = FilterExpression.Term(text, caseSensitive, wholeWord, regex, negated)
        val lineItem = getLineItem(lineItemIndex)

        // Act
        val result = FilterEvaluator.matches(lineItem, filterExpression)

        // Assert
        Assertions.assertTrue(result)
    }

    @CsvSource(
        "CONNECTED, true, false, false, false, 1",
        "RETRYING, true, false, false, false, 2",
        "OPERATION, true, false, false, false, 3",
        "DISK, true, false, false, false, 4",
        "POSSIBLE, true, false, false, false, 5",
        "debug, true, true, false, false, 6",
    )
    @ParameterizedTest
    fun `LineItem that does not match the case sensitive text should return false`(
        text: String,
        caseSensitive: Boolean,
        wholeWord: Boolean,
        regex: Boolean,
        negated: Boolean,
        lineItemIndex: Int
    ) {
        // Arrange
        val filterExpression = FilterExpression.Term(text, caseSensitive, wholeWord, regex, negated)
        val lineItem = getLineItem(lineItemIndex)

        // Act
        val result = FilterEvaluator.matches(lineItem, filterExpression)

        // Assert
        Assertions.assertFalse(result)
    }

    @CsvSource(
        "CONNECTED, false, true, false, false, 1",
        "RETRYING, false, true, false, false, 2",
        "OPERATION, false, true, false, false, 3",
        "DISK, false, true, false, false, 4",
        "POSSIBLE, false, true, false, false, 5",
        "debug, false, true, false, false, 6",
    )
    @ParameterizedTest
    fun `LineItem that does match the whole text should return true`(
        text: String,
        caseSensitive: Boolean,
        wholeWord: Boolean,
        regex: Boolean,
        negated: Boolean,
        lineItemIndex: Int
    ) {
        // Arrange
        val filterExpression = FilterExpression.Term(text, caseSensitive, wholeWord, regex, negated)
        val lineItem = getLineItem(lineItemIndex)

        // Act
        val result = FilterEvaluator.matches(lineItem, filterExpression)

        // Assert
        Assertions.assertTrue(result)
    }

    @CsvSource(
        "ONNECTED, false, true, false, false, 1",
        "ETRYING, false, true, false, false, 2",
        "PERATION, false, true, false, false, 3",
        "ISK, false, true, false, false, 4",
        "OSSIBLE, false, true, false, false, 5",
        "ebug, false, true, false, false, 6",
    )
    @ParameterizedTest
    fun `LineItem that does not match the whole text should return false`(
        text: String,
        caseSensitive: Boolean,
        wholeWord: Boolean,
        regex: Boolean,
        negated: Boolean,
        lineItemIndex: Int
    ) {
        // Arrange
        val filterExpression = FilterExpression.Term(text, caseSensitive, wholeWord, regex, negated)
        val lineItem = getLineItem(lineItemIndex)

        // Act
        val result = FilterEvaluator.matches(lineItem, filterExpression)

        // Assert
        Assertions.assertFalse(result)
    }

    @CsvSource(
        "\\[, false, false, true, false, 1",
        "\\[, false, false, true, false, 2",
        "\\[, false, false, true, false, 3",
        "\\[, false, false, true, false, 4",
        "\\[, false, false, true, false, 5",
        "\\[, false, false, true, false, 6",
    )
    @ParameterizedTest
    fun `LineItem that matches the regex should return true`(
        text: String,
        caseSensitive: Boolean,
        wholeWord: Boolean,
        regex: Boolean,
        negated: Boolean,
        lineItemIndex: Int
    ) {
        // Arrange
        val filterExpression = FilterExpression.Term(text, caseSensitive, wholeWord, regex, negated)
        val lineItem = getLineItem(lineItemIndex)

        // Act
        val result = FilterEvaluator.matches(lineItem, filterExpression)

        // Assert
        Assertions.assertTrue(result)
    }

    @CsvSource(
        "\\p, false, false, true, false, 1",
        "\\p, false, false, true, false, 2",
        "\\p, false, false, true, false, 3",
        "\\p, false, false, true, false, 4",
        "\\p, false, false, true, false, 5",
        "\\p, false, false, true, false, 6",
    )
    @ParameterizedTest
    fun `LineItem that dose not match the regex should return false`(
        text: String,
        caseSensitive: Boolean,
        wholeWord: Boolean,
        regex: Boolean,
        negated: Boolean,
        lineItemIndex: Int
    ) {
        // Arrange
        val filterExpression = FilterExpression.Term(text, caseSensitive, wholeWord, regex, negated)
        val lineItem = getLineItem(lineItemIndex)

        // Act
        val result = FilterEvaluator.matches(lineItem, filterExpression)

        // Assert
        Assertions.assertFalse(result)
    }

    @CsvSource(
        "connected, false, false, false, true, 1",
        "retrying, false, false, false, true, 2",
        "operation, false, false, false, true, 3",
        "Disk, false, false, false, true, 4",
        "possible, false, false, false, true, 5",
        "DEBUG, false, false, false, true, 6",
    )
    @ParameterizedTest
    fun `LineItem that contains the text, but is negated, should return false`(
        text: String,
        caseSensitive: Boolean,
        wholeWord: Boolean,
        regex: Boolean,
        negated: Boolean,
        lineItemIndex: Int
    ) {
        // Arrange
        val filterExpression = FilterExpression.Term(text, caseSensitive, wholeWord, regex, negated)
        val lineItem = getLineItem(lineItemIndex)

        // Act
        val result = FilterEvaluator.matches(lineItem, filterExpression)

        // Assert
        Assertions.assertFalse(result)
    }


    @Test
    fun `x AND y, return true`() {
        // Arrange
        val childFilter1 = FilterExpression.Term("connected")
        val childFilter2 = FilterExpression.Term("server")
        val filterExpression = FilterExpression.Group(BooleanOp.AND, listOf(childFilter1, childFilter2))

        // Act
        val result = FilterEvaluator.matches(lineItem1, filterExpression)

        // Assert
        Assertions.assertTrue(result)
    }

    @Test
    fun `x AND y, only one of them, return false`() {
        // Arrange
        val childFilter1 = FilterExpression.Term("connected")
        val childFilter2 = FilterExpression.Term("timeout")
        val filterExpression = FilterExpression.Group(BooleanOp.AND, listOf(childFilter1, childFilter2))

        // Act
        val result = FilterEvaluator.matches(lineItem1, filterExpression)

        // Assert
        Assertions.assertFalse(result)
    }

    @Test
    fun `x AND y, none of them, return false`() {
        // Arrange
        val childFilter1 = FilterExpression.Term("possible")
        val childFilter2 = FilterExpression.Term("timeout")
        val filterExpression = FilterExpression.Group(BooleanOp.AND, listOf(childFilter1, childFilter2))

        // Act
        val result = FilterEvaluator.matches(lineItem1, filterExpression)

        // Assert
        Assertions.assertFalse(result)
    }

    @Test
    fun `x OR y, return true`() {
        // Arrange
        val childFilter1 = FilterExpression.Term("connected")
        val childFilter2 = FilterExpression.Term("server")
        val filterExpression = FilterExpression.Group(BooleanOp.OR, listOf(childFilter1, childFilter2))

        // Act
        val result = FilterEvaluator.matches(lineItem1, filterExpression)

        // Assert
        Assertions.assertTrue(result)
    }

    @Test
    fun `x OR y, only one of them, return true`() {
        // Arrange
        val childFilter1 = FilterExpression.Term("connected")
        val childFilter2 = FilterExpression.Term("timeout")
        val filterExpression = FilterExpression.Group(BooleanOp.OR, listOf(childFilter1, childFilter2))

        // Act
        val result = FilterEvaluator.matches(lineItem1, filterExpression)

        // Assert
        Assertions.assertTrue(result)
    }

    @Test
    fun `x OR y, none of them, return false`() {
        // Arrange
        val childFilter1 = FilterExpression.Term("possible")
        val childFilter2 = FilterExpression.Term("timeout")
        val filterExpression = FilterExpression.Group(BooleanOp.OR, listOf(childFilter1, childFilter2))

        // Act
        val result = FilterEvaluator.matches(lineItem1, filterExpression)

        // Assert
        Assertions.assertFalse(result)
    }

    @Test
    fun `x AND y but NOT z, return true`() {
        // Arrange
        val filter1And2ButNot3 = filter {
            and {
                term("connected")
                term("server")
                notTerm("timeout")
            }
        }

        // Act
        val result = FilterEvaluator.matches(lineItem1, filter1And2ButNot3)

        // Assert
        Assertions.assertTrue(result)
    }

    @Test
    fun `x AND y but NOT z, where NOT z is not present, return false`() {
        // Arrange
        val filter1And2ButNot3 = filter {
            and {
                term("connected")
                term("server")
                notTerm("INFO")
            }
        }

        // Act
        val result = FilterEvaluator.matches(lineItem1, filter1And2ButNot3)

        // Assert
        Assertions.assertFalse(result)
    }

    @Test
    fun `filter with AND`() {
        // Arrange
        val filterExpression = filter {
            and {
                term("error")
                term("timeout")
            }
        }

        // Act
        val result = FilterEvaluator.filter(allLineItems, filterExpression)

        // Assert
        Assertions.assertTrue { lineItem3 in result }
    }

    @Test
    fun `filter with multiple AND`() {
        // Arrange
        val filterExpression = filter {
            and("error", "timeout")
            and {
                term(":")
            }
        }

        // Act
        val result = FilterEvaluator.filter(allLineItems, filterExpression)

        // Assert
        Assertions.assertTrue { lineItem3 in result }
    }

    @Test
    fun `filter with OR`() {
        // Arrange
        val filterExpression = filter {
            or {
                term("error")
                term("Err")
            }
        }

        // Act
        val result = FilterEvaluator.filter(allLineItems, filterExpression)

        // Assert
        Assertions.assertTrue { lineItem3 in result }
        Assertions.assertTrue { lineItem4 in result }
        Assertions.assertTrue { lineItem5 in result }
        Assertions.assertTrue { lineItem6 in result }
    }

    @Test
    fun `filter with multiple OR`() {
        // Arrange
        val filterExpression = filter {
            or("error", "Err")
            or { term("DEBUG") }
        }

        // Act
        val result = FilterEvaluator.filter(allLineItems, filterExpression)

        // Assert
        Assertions.assertTrue { lineItem6 in result }
    }

    companion object {
        private val lineItem1 = LineItem(1, "[INFO] connected to server")
        private val lineItem2 = LineItem(2, "[DEBUG] retrying after timeout")
        private val lineItem3 = LineItem(3, "[ERROR] operation failed: timeout")
        private val lineItem4 = LineItem(4, "[ERROR] Disk FULL")
        private val lineItem5 = LineItem(5, "[WARN] possible error case")
        private val lineItem6 = LineItem(6, "[error] DEBUG payload ignored")

        private val allLineItems = listOf(lineItem1, lineItem2, lineItem3, lineItem4, lineItem5, lineItem6)

        private fun getLineItem(lineItemIndex: Int) = when (lineItemIndex) {
            1 -> lineItem1
            2 -> lineItem2
            3 -> lineItem3
            4 -> lineItem4
            5 -> lineItem5
            6 -> lineItem6
            else -> throw IllegalArgumentException("Invalid line item index")
        }
    }
}