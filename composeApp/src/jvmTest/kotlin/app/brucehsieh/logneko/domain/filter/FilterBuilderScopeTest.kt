package app.brucehsieh.logneko.domain.filter

import app.brucehsieh.logneko.domain.filter.FilterExpression.Companion.prettify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class FilterBuilderScopeTest {

    @Test
    fun `empty filter should return empty term`() {
        // Arrange
        val expected = FilterExpression.Term("")

        // Act
        val filterExpressions = listOf(
            filter { },
            filter { term("") },
            filter { FilterExpression.Term("") },

            filter { and { } },
            filter { and { term("") } },
            filter { and { FilterExpression.Term("") } },
            filter { or { } },
            filter { or { term("") } },
            filter { or { FilterExpression.Term("") } },

            filter { and { and { } } },
            filter { or { or { } } },
            filter { and { or { } } },
            filter { or { and { } } },
            filter { and { }; and { }; and { } },
            filter { or { };or { };or { } },
            filter { and { };or { };and { } },
            filter { or { };and { };or { } }
        )

        // Assert
        filterExpressions.forEach { filterExpression ->
            assertEquals(expected, filterExpression)
        }
    }

    @Test
    fun `single term filter should return term expression`() {
        // Arrange
        val expected = FilterExpression.Term("abc def")

        // Act
        val filterExpressions = listOf(
            filter { term("abc def") },
            filter { and { term("abc def") } },
            filter { or { term("abc def") } },
            filter { and { and { term("abc def") } } },
            filter { or { or { term("abc def") } } }
        )

        // Assert
        filterExpressions.forEach { filterExpression ->
            assertEquals(expected, filterExpression)
        }
    }

    @Test
    fun `multiple AND term filter should return group expression`() {
        // Arrange
        val expected = FilterExpression.Group(
            BooleanOp.AND,
            listOf(
                FilterExpression.Group(
                    BooleanOp.AND,
                    listOf(
                        FilterExpression.Term("abc"),
                        FilterExpression.Term("def")
                    )
                ),
                FilterExpression.Group(
                    BooleanOp.AND,
                    listOf(
                        FilterExpression.Term("123"),
                        FilterExpression.Term("456")
                    )
                )
            )
        )

        // Act
        val filterExpression = filter {
            and {
                term("abc")
                term("def")
            }
            and("123", "456")
        }

        // Assert
        assertEquals(expected, filterExpression)
    }

    @Test
    fun `multiple OR term filter should return group expression`() {
        // Arrange
        val expected = FilterExpression.Group(
            BooleanOp.AND,
            listOf(
                FilterExpression.Group(
                    BooleanOp.OR,
                    listOf(
                        FilterExpression.Term("abc"),
                        FilterExpression.Term("def")
                    )
                ),
                FilterExpression.Group(
                    BooleanOp.OR,
                    listOf(
                        FilterExpression.Term("123"),
                        FilterExpression.Term("456")
                    )
                )
            )
        )

        // Act
        val filterExpression = filter {
            or {
                term("abc")
                term("def")
            }
            or("123", "456")
        }

        // Assert
        assertEquals(expected, filterExpression)
    }

    @Test
    fun `multiple term filter should return group expression`() {
        // Arrange
        val expected = FilterExpression.Group(
            BooleanOp.OR,
            listOf(
                FilterExpression.Term("abc"),
                FilterExpression.Term("def"),
                FilterExpression.Group(
                    BooleanOp.AND,
                    listOf(
                        FilterExpression.Term("ghi"),
                        FilterExpression.Term("jkl")
                    )
                )
            )
        )

        // Act
        val filterExpression = filter {
            or {
                term("abc")
                term("def")
                and {
                    term("ghi")
                    term("jkl")
                }
            }
        }

        println(filterExpression.prettify())

        // Assert
        assertEquals(expected, filterExpression)
    }

}