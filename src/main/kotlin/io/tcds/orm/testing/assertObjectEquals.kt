package io.tcds.orm.testing

import io.mockk.MockKMatcherScope
import org.junit.jupiter.api.Assertions
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

fun <T : Any> T.toPlainMap(excludeProps: List<String> = emptyList()): Map<String, Any?> {
    return this.javaClass
        .kotlin
        .memberProperties
        .filter { prop -> prop.visibility == KVisibility.PUBLIC }
        .filter { prop -> !excludeProps.contains(prop.name) }
        .associateBy { it.name }
        .let { props ->
            props.keys.associateWith { toPlainMap(props[it]?.get(this)) }
        }
}

private fun toPlainMap(value: Any?): Any? {
    return when (value) {
        is String, is Int, is Float, is Boolean, is Double, null -> value
        is LocalDate, is LocalDateTime, is ZonedDateTime -> value.toString()
        is Enum<*> -> value.name
        is Lazy<*> -> toPlainMap(value.value)
        is List<*> -> value.map { item -> toPlainMap(item) }
        is Map<*, *> -> value.map { entry -> entry.key.toString() to entry.value?.let { toPlainMap(it) } }.toMap()
        else -> value.toPlainMap()
    }
}

fun <T : Any> T?.toPlain(): Any? = toPlainMap(this)

inline fun <reified T : Any> assertObjects(expected: T, actual: T?) {
    Assertions.assertEquals(expected.toPlain(), actual?.toPlain(), "objects are different")
}

inline fun <reified T : Any> MockKMatcherScope.matchObject(noinline expected: () -> T): T = matchNullable {
    assertObjects(expected(), it).let { true }
}
