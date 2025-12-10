package SOS;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * Test suite for all computer opponent related tests.
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
    ComputerPlayerTest.class,
    LLMServiceTest.class
})
public class AllComputerOpponentTests {
    // This class remains empty, it is used only as a holder for the above annotations
}
