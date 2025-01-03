package io.tcds.orm.testing

import fixtures.FooBar
import io.mockk.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.opentest4j.AssertionFailedError

class MatchObjectTest {
    interface FooBarInterface {
        fun process(fooBar: FooBar)
    }

    private val mock: FooBarInterface = mockk()

    @Test
    fun `given a mock then match called object`() {
        every { mock.process(any()) } just runs

        mock.process(FooBar("foo", "bar"))

        verify(exactly = 1) { mock.process(matchObject { FooBar("foo", "bar") }) }
    }

    @Test
    fun `given a mock then match called object asdf`() {
        every { mock.process(any()) } just runs
        mock.process(FooBar("bar", "foo"))

        val exception = assertThrows<AssertionFailedError> { verify(exactly = 1) { mock.process(matchObject { FooBar("foo", "bar") }) } }

        Assertions.assertEquals("objects are different ==> expected: <{bar=bar, foo=foo}> but was: <{bar=foo, foo=bar}>", exception.message)
    }
}
