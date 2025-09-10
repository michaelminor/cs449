package simpleCalc;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SimpleCalculatorTest {

	@Test
	void onePlusOneEqualsTwo() {
	 	var calculator1 = new SimpleCalculator();
		
		assertEquals(2, calculator1.add(1, 1));
		
	}
	@Test
	void threePlusThreeEqualsSix() {
		var calculator1 = new SimpleCalculator();
		assertEquals(6, calculator1.add(3,3));
	}

}
