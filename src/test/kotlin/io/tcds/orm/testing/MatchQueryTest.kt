package io.tcds.orm.testing

import fixtures.AddressTable
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.tcds.orm.extension.and
import io.tcds.orm.extension.differentOf
import io.tcds.orm.extension.equalsTo
import io.tcds.orm.extension.where
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.opentest4j.AssertionFailedError

class MatchQueryTest {
    private val table: AddressTable = AddressTable(mockk())
    private val mock: AddressTable = mockk()

    @Test
    fun `given a where condition then match run query`() {
        every { mock.findBy(any()) } returns emptySequence()
        val condition = where(table.id equalsTo "address-xxx") and (table.number differentOf "NA")

        mock.findBy(condition)

        verify(exactly = 1) { mock.findBy(matchQuery { "WHERE id = `address-xxx` AND number != `NA`" }) }
    }

    @Test
    fun `given a where condition when match is not correct then throw assert exception`() {
        every { mock.findBy(any()) } returns emptySequence()
        val condition = where(table.id equalsTo "xxx")
        mock.findBy(condition)

        val exception = assertThrows<AssertionFailedError> { verify { mock.findBy(matchQuery { "WHERE id = `address-xxx`" }) } }

        assertEquals("matchQuery failed ==> expected: <WHERE id = `address-xxx`> but was: <WHERE id = `xxx`>", exception.message)
    }
}
