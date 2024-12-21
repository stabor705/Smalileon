package xyz.stabor.smalileon

import kotlin.test.Test
import kotlin.test.assertEquals

class GenerationTests {
    @Test
    fun `Should generate appropriate identifiers`() {
        val identifier = generateRandomIdentifier(n = 10)
        assertEquals(10u, identifier.length.toUInt())
    }
}