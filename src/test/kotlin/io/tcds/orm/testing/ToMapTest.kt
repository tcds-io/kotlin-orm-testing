package io.tcds.orm.testing

import fixtures.FooBar
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime

class ToMapTest {
    enum class Type { BAR }

    data class TestObject(
        val string: String,
        val integer: Int,
        val float: Float,
        val double: Double,
        val nullable: String?,
        val localDate: LocalDate,
        val localDateTime: LocalDateTime,
        val zonedDateTime: ZonedDateTime,
        val enum: Type,
        val lazy: Lazy<FooBar>,
        val list: List<FooBar>,
        val map: Map<String, FooBar>,
        val fooBar: FooBar,
    )

    @Test
    fun `given a object then convert to a play map`() {
        val testObject = TestObject(
            string = "String",
            integer = 100,
            float = 109.65.toFloat(),
            double = 112.98,
            nullable = null,
            localDate = LocalDate.parse("2007-08-08"),
            localDateTime = LocalDateTime.parse("2007-08-08T15:16:17.000"),
            zonedDateTime = ZonedDateTime.parse("2007-12-25T17:18:19.000Z"),
            enum = Type.BAR,
            lazy = lazy { FooBar("lazyFoo", "lazyBar") },
            list = listOf(FooBar("listFirstFoo", "listFirstBar"), FooBar("listSecondFoo", "listSecondBar")),
            map = mapOf(
                "first" to FooBar("mapFirstFoo", "mapFirstBar"),
                "second" to FooBar("mapSecondFoo", "mapSecondBar"),
            ),
            fooBar = FooBar("foo", "bar"),
        )

        val map = testObject.toPlain()

        Assertions.assertEquals(
            mapOf(
                "string" to "String",
                "integer" to 100,
                "float" to 109.65.toFloat(),
                "double" to 112.98,
                "nullable" to null,
                "localDate" to "2007-08-08",
                "localDateTime" to "2007-08-08T15:16:17",
                "zonedDateTime" to "2007-12-25T17:18:19Z",
                "enum" to "BAR",
                "lazy" to mapOf(
                    "foo" to "lazyFoo",
                    "bar" to "lazyBar",
                ),
                "list" to listOf(
                    mapOf("foo" to "listFirstFoo", "bar" to "listFirstBar"),
                    mapOf("foo" to "listSecondFoo", "bar" to "listSecondBar"),
                ),
                "map" to mapOf(
                    "first" to mapOf("foo" to "mapFirstFoo", "bar" to "mapFirstBar"),
                    "second" to mapOf("foo" to "mapSecondFoo", "bar" to "mapSecondBar"),
                ),
                "fooBar" to mapOf("foo" to "foo", "bar" to "bar"),
            ),
            map,
        )
    }
}
