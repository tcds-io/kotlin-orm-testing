package io.tcds.orm.testing

import fixtures.FooBar
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.opentest4j.AssertionFailedError

class AssertObjectTest {
    @Test
    fun `given a non-data-class when values are equal then assert object values are equal`() {
        val first = FooBar("foo", "bar")
        val second = FooBar("foo", "bar")

        Assertions.assertNotEquals(first, second)
        assertObjects(first, second)
    }

    @Test
    fun `given a non-data-class when values are not equal then assert object values are not equal`() {
        val first = FooBar("foo", "bar")
        val second = FooBar("bar", "foo")

        val exception = assertThrows<AssertionFailedError> { assertObjects(first, second) }

        Assertions.assertEquals("objects are different ==> expected: <{bar=bar, foo=foo}> but was: <{bar=foo, foo=bar}>", exception.message)
    }
}
