import com.ifancc.jack_parser.*
import org.junit.Test
import kotlin.test.*

class AssemblerTest {
    @Test
    fun testA(){
        assertFailsWith(BadAstException::class) {
            translateA(ANode(subType = AType.DIRECT_NUMBER, directValue = 1.shl(15)))
        }
        assertFailsWith(BadAstException::class) {
            translateA(ANode(subType = AType.DIRECT_NUMBER, directValue = null))
        }
        assertEquals(1, translateA(ANode(subType = AType.DIRECT_NUMBER, directValue = 1)))
    }
    @Test
    fun testC(){
        assertEquals(0b1110001110010111, translateC(CNode(comp=CComp.Dminus1, dest = CDest.D, jump = CJump.JMP)))
        assertEquals(0b1111010011011101, translateC(CNode(comp=CComp.DminusM, dest = CDest.MD, jump = CJump.JNE)))
        assertEquals(0b1111110000010000, translateC(CNode(comp=CComp.M, dest = CDest.D)))
        assertEquals(0b1111000010010000, translateC(CNode(comp=CComp.DplusM, dest = CDest.D)))
    }
}