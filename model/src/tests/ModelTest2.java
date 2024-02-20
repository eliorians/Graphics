package tests;

import org.junit.Test;


public class ModelTest2 extends ModelHelpers {
	@Test
	public void testCylinder() throws InterruptedException {
		runTest("cylinder-gray");
		runTest("cylinder-red");
		runTest("cylinder-reverse");
	}

	@Test
	public void testCylinderWire() throws InterruptedException {
		runTest("cylinder-wire");
	}

	@Test
	public void testCylinderTolerance() throws InterruptedException {
		for (float tolerance : new float[] {0.2f, .11f, .05f, .005f, .002f, .001f}) {			
			runTest("cylinder-" + String.format("%.3f", tolerance));		}
	}

	@Test
	public void testSphere() throws InterruptedException {
		runTest("sphere-gray");
		runTest("sphere-red");
		runTest("sphere-reverse");
	}

	@Test
	public void testSphereWire() throws InterruptedException {
		runTest("sphere-wire");
	}

	@Test
	public void testSphereTolerance() throws InterruptedException {
		for (float tolerance : new float[] {0.2f, .11f, .05f, .005f, .002f, .001f}) {			
			runTest("sphere-" + String.format("%.3f", tolerance));
		}
	}

	@Test
	public void testTorus() throws InterruptedException {
		runTest("torus-gray");
		runTest("torus-red");
		runTest("torus-reverse");
	}

	@Test
	public void testTorusWire() throws InterruptedException {
		runTest("torus-wire");
	}

	@Test
	public void testTorusTolerance() throws InterruptedException {
		for (float tolerance : new float[] {0.2f, .11f, .05f, .005f, .002f, .001f}) {			
			runTest("torus-" + String.format("%.3f", tolerance));
		}
	}
	@Test
	public void testTorusAspect() throws InterruptedException {
		for (float aspectRatio : new float[] {2f, 3f, 4f}) {			
			runTest("torus-aspect-" + String.format("%.1f", aspectRatio));
		}
	}
	
}
