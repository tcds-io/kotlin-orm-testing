package io.tcds.orm.testing

import io.mockk.MockKMatcherScope
import io.tcds.orm.extension.OrderStatement
import io.tcds.orm.extension.toOrderByStatement
import org.junit.jupiter.api.Assertions

fun <T> MockKMatcherScope.matchOrderBy(expected: () -> String): OrderStatement<T> = matchNullable {
    Assertions.assertEquals(expected(), it?.toOrderByStatement(), "matchQuery failed")

    true
}
