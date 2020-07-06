import com.ifancc.jack_parser.*
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AstTest {
    @Test
    fun testCNode(){
        val node1 = CNode(jump = CJump.JMP)
        assertEquals(CJump.JMP, node1.jump)
    }
}