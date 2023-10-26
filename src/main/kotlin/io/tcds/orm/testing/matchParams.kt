package io.tcds.orm.testing

import io.mockk.MockKMatcherScope
import io.tcds.orm.Param
import org.junit.jupiter.api.Assertions

fun MockKMatcherScope.matchParams(expected: () -> List<Pair<String, Any>>): List<Param<String, Any>> = matchNullable {
    Assertions.assertEquals(expected(), it?.map { p -> Pair(p.column.name, p.value) }, "matchParams failed")

    true
}

