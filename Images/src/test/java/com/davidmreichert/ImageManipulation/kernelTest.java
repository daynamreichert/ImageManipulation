package com.davidmreichert.ImageManipulation;

import org.junit.Test;

public class kernelTest {
	
	/**
	 * Tests the to string method
	 */
	@Test
	public void test_to_string() {
		Kernel kernelR1 = new Kernel();
		Kernel kernelR2 = new Kernel(2, Kernel.Type.GAUSSIAN);
		Kernel kernelR3 = new Kernel(5, Kernel.Type.GAUSSIAN);
		
		System.out.println(kernelR1);
		System.out.println(kernelR2);
		System.out.println(kernelR3);
	}
}
