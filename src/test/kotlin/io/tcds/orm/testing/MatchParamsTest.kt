package io.tcds.orm.testing

import fixtures.AddressTable
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.tcds.orm.Param
import io.tcds.orm.extension.equalsTo
import io.tcds.orm.extension.where
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.opentest4j.AssertionFailedError

class MatchParamsTest {
    private val table: AddressTable = AddressTable(mockk())
    private val mock: AddressTable = mockk()

    @Test
    fun `given a where condition then match its params`() {
        every { mock.update(any(), any()) } returns mockk(relaxed = true)
        val condition = where(table.id equalsTo "address-xxx")
        val params = listOf(Param(table.main, false), Param(table.number, "890"), Param(table.street, "Foo Bar"))

        mock.update(params, condition)

        verify(exactly = 1) { mock.update(matchParams { listOf("main" to false, "number" to "890", "street" to "Foo Bar") }, any()) }
    }

    @Test
    fun `given a where condition when match is not correct then throw assert exception`() {
        every { mock.update(any(), any()) } returns mockk(relaxed = true)
        val condition = where(table.id equalsTo "address-xxx")
        val params = listOf(Param(table.main, false))
        mock.update(params, condition)

        val exception = assertThrows<AssertionFailedError> { verify { mock.update(matchParams { listOf("main" to true) }, any()) } }

        Assertions.assertEquals("matchParams failed ==> expected: <[(main, true)]> but was: <[(main, false)]>", exception.message)
    }
}
