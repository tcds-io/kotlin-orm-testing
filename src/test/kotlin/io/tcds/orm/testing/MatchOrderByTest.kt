package io.tcds.orm.testing

import fixtures.AddressTable
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.tcds.orm.extension.and
import io.tcds.orm.extension.differentOf
import io.tcds.orm.extension.equalsTo
import io.tcds.orm.extension.where
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.opentest4j.AssertionFailedError

class MatchOrderByTest {
    private val table: AddressTable = AddressTable(mockk())
    private val mock: AddressTable = mockk()

    @Test
    fun `given an order statement then make sure it is correct`() {
        every { mock.findBy(any(), any()) } returns emptySequence()
        val condition = where(table.id equalsTo "address-xxx") and (table.number differentOf "NA")

        mock.findBy(condition, listOf(table.id.desc(), table.createdAt.asc()))

        verify(exactly = 1) { mock.findBy(any(), matchOrderBy { "ORDER BY id DESC, created_at ASC" }) }
    }

    @Test
    fun `given an order statement when match is not correct then throw assert exception`() {
        every { mock.findBy(any(), any()) } returns emptySequence()
        val condition = where(table.id equalsTo "address-xxx") and (table.number differentOf "NA")
        mock.findBy(condition, listOf(table.id.desc()))

        val exception = assertThrows<AssertionFailedError> {
            verify { mock.findBy(any(), matchOrderBy { "ORDER BY id DESC, created_at ASC" }) }
        }

        assertEquals("matchOrderBy failed ==> expected: <ORDER BY id DESC, created_at ASC> but was: <ORDER BY id DESC>", exception.message)
    }
}
