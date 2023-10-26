package io.tcds.orm.testing

import fixtures.AddressTable
import io.mockk.*
import io.tcds.orm.Param
import io.tcds.orm.extension.and
import io.tcds.orm.extension.differentOf
import io.tcds.orm.extension.equalsTo
import io.tcds.orm.extension.where
import org.junit.jupiter.api.Test

class MockkOrmMatchTesting {
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
    fun `given a where condition then match its params`() {
        every { mock.update(any(), any()) } just runs
        val condition = where(table.id equalsTo "address-xxx")
        val params = listOf(
            Param(table.main, false),
            Param(table.number, "890"),
            Param(table.street, "Foo Bar"),
        )

        mock.update(params, condition)

        verify(exactly = 1) { mock.update(matchParams { listOf("main" to false, "number" to "890", "street" to "Foo Bar") }, any()) }
    }

    @Test
    fun `given an order statement then make sure it is correct`() {
        every { mock.findBy(any(), any()) } returns emptySequence()
        val condition = where(table.id equalsTo "address-xxx") and (table.number differentOf "NA")

        mock.findBy(condition, listOf(table.id.desc(), table.createdAt.asc()))

        verify(exactly = 1) { mock.findBy(any(), matchOrderBy { "ORDER BY id DESC, created_at ASC" }) }
    }
}
