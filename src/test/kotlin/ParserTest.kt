import com.ifancc.jack_parser.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ParserTest {
    private val parser = Parser()

    @Test
    fun testIsValidLine() {
        assertFalse(isValidLine("// comment"))
        assertFalse(isValidLine(""))
        assertTrue(isValidLine("JMP;"))
    }

    @Test
    fun testIsValidJackChar() {
        val validStr = "@OUTPUT_FIRSTD=D-M@R10;JMP(OUTPUT_FIRST)"
        for (chr in validStr) {
            assertTrue(isValidJackChar(chr))
        }
        val failStr = " /\\#$%*"
        for (chr in failStr) {
            assertFalse(isValidJackChar(chr))
        }
    }

    @Test
    fun testRemoveInlineComment() {
        assertEquals("D=D-M", removeInlineComment("D=D-M            // D = first number - second number"))
        assertEquals("@R0", removeInlineComment("@R0"))
        assertEquals("(ball.new)", removeInlineComment("(ball.new)"))
    }

    @Test
    fun testParseJump() {
        var node = CNode()
        parseJump("JGT", node)
        assertEquals(CJump.JGT, node.jump)
        parseJump("JMP", node)
        assertEquals(CJump.JMP, node.jump)
    }

    @Test
    fun testParseComp() {
        val node = CNode()
        parseComp("-1", node)
        assertEquals(CComp._neg1, node.comp)
        parseComp("A-D", node)
        assertEquals(CComp.AminusD, node.comp)
    }

    @Test
    fun testParseDest() {
        val node = CNode()
        parseDest("A", node)
        assertEquals(CDest.A, node.dest)
        parseDest("AMD", node)
        assertEquals(CDest.AMD, node.dest)
    }

    @Test
    fun testParseSingleCInstruciton(){
        val node = CNode()
        parseCinstruction("JMP", node)
        assertEquals(CNode(jump = CJump.JMP), node)
        val node1 = CNode()
        parseCinstruction("D=D-1", node1)
        assertEquals(CNode(dest = CDest.D, comp = CComp.Dminus1), node1)
    }

}