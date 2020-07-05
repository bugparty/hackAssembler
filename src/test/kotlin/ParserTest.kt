import com.ifancc.jack_parser.Parser
import com.ifancc.jack_parser.isValidJackChar
import com.ifancc.jack_parser.isValidLine
import com.ifancc.jack_parser.removeInlineComment
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
    fun testIsValidJackChar(){
        val validStr = "@OUTPUT_FIRSTD=D-M@R10;JMP(OUTPUT_FIRST)"
        for(chr in validStr){
            assertTrue(isValidJackChar(chr))
        }
        val failStr = " /\\#$%*"
        for(chr in failStr){
            assertFalse(isValidJackChar(chr))
        }
    }
    @Test
    fun testRemoveInlineComment(){
        val testStr1 = "D=D-M            // D = first number - second number"
        val testStr1Result = "D=D-M"
        assertEquals(testStr1Result, removeInlineComment(testStr1))
        val testStr2 = "@R0"
        val testStr2Result = testStr2
        assertEquals(testStr2Result, removeInlineComment(testStr2))
    }
}