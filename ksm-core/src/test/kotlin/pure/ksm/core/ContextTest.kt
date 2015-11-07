package pure.ksm.core

import org.junit.Test
import kotlin.test.assertTrue

class ContextTest {

    @Test
    fun test() {
        val c = Context(listOf(TestData("1")))
        assertTrue { c.mostRecent(TestData::class)?.data == "1" }

        val next = c.append(TestData("2"))
        assertTrue { next.mostRecent(TestData::class)?.data == "2" }
    }
}