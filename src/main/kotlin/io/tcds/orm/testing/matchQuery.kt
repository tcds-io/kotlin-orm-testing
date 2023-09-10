package io.tcds.orm.testing

import io.mockk.MockKMatcherScope
import io.tcds.orm.statement.Statement
import org.junit.jupiter.api.Assertions

fun MockKMatcherScope.matchQuery(expected: () -> String): Statement = match {
    Assertions.assertEquals(expected(), it.toSql(), "matchQuery failed")

    true
}
