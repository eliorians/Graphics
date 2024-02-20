package tests;

import static org.junit.Assert.assertEquals;

import javax.vecmath.Point3f;
import javax.vecmath.Vector2f;
import javax.vecmath.Vector3f;

import org.junit.Test;

import modeler.MainFrame;
import modeler.manip.Manip;
import modeler.manip.RotateManip;
import modeler.manip.ScaleManip;
import modeler.manip.TranslateManip;
import modeler.scene.Transformation;
import modeler.shape.Cube;


public class ModelTest3 extends ModelHelpers {
	
	private final int SIZE = 600;

	@Test
	public void testComputeViewingRay() throws InterruptedException {
		Vector3f d = new Vector3f();
		Point3f p = new Point3f();

		MainFrame m = setupManip(TranslateManip.class, Manip.PICK_X);
		
		Vector2f[] mice = new Vector2f[] {
				new Vector2f(0f, 0f),
				new Vector2f(-1f, -1f),
				new Vector2f(-1f, 1f),
				new Vector2f(1f, -1f),
				new Vector2f(1f, 1f),
		};
		Vector3f[] directions = new Vector3f[] {
				new Vector3f(-5f, -5f, -3f),
				new Vector3f(-4.8610115f, -7.4152594f, 0.79378486f),
				new Vector3f(-8.412864f, -2.5847406f, -1.3373263f),
				new Vector3f(-1.5871363f, -7.4152594f, -4.662674f),
				new Vector3f(-5.1389885f, -2.5847406f, -6.793785f),
		};
		Point3f o = new Point3f(5f, 5f, 3f);
		
		for (int i = 0; i < mice.length; i++ ) {
			Vector2f mouse = mice[i];
			Vector3f correct = directions[i];
			m.currentManip.computeViewingRay(mouse, p, d);
			correct.normalize();
			d.normalize();
			System.out.println(correct);
			assertTupleEquals("Incorrect ray origin for mouse = " + mouse, o, p, 1e-5);
			assertTupleEquals("Incorrect ray direction for mouse = " + mouse, correct, d, 1e-5);
		}
	}	
	
	@Test
	public void testComputeAxisRay() throws InterruptedException {
		MainFrame m = new MainFrame(false);
		m.setVisible(true);
		
		try { Thread.sleep(1000); } finally {};
		
		Vector2f z = new Vector2f();
		Vector3f d = new Vector3f();
		Point3f p = new Point3f();
		Point3f o = new Point3f();

		Transformation t = m.addNewShape(Cube.class);
		applyManip(m, t, TranslateManip.class, Manip.PICK_X, z, z);
		m.currentManip.computeAxisRay(p, d);
		assertTupleEquals("Incorrect axis ray origin", o, p, 1e-5);
		assertTupleEquals("Incorrect axis ray direction", d, new Vector3f(1f, 0f, 0f), 1e-5);
		applyManip(m, t, TranslateManip.class, Manip.PICK_Y, z, z);
		m.currentManip.computeAxisRay(p, d);
		assertTupleEquals("Incorrect axis ray origin", o, p, 1e-5);
		assertTupleEquals("Incorrect axis ray direction", d, new Vector3f(0f, 1f, 0f), 1e-5);
		applyManip(m, t, TranslateManip.class, Manip.PICK_Z, z, z);
		m.currentManip.computeAxisRay(p, d);
		assertTupleEquals("Incorrect axis ray origin", o, p, 1e-5);
		assertTupleEquals("Incorrect axis ray direction", d, new Vector3f(0f, 0f, 1f), 1e-5);
	}

	@Test
	public void testPseudoIntersection() throws InterruptedException {
		Point3f z3 = new Point3f();
		
		Vector3f xx = new Vector3f(1f, 0f, 0f);
		Vector3f yy = new Vector3f(0f, 1f, 0f);
		Vector3f zz = new Vector3f(0f, 0f, 1f);

		Vector2f[] mice = new Vector2f[] {
				new Vector2f(0f, 0f),
				new Vector2f(-1f, -1f),
				new Vector2f(-1f, 1f),
				new Vector2f(1f, -1f),
				new Vector2f(1f, 1f),
		};
		Vector3f[] directions = new Vector3f[] {
				new Vector3f(-5f, -5f, -3f),
				new Vector3f(-4.8610115f, -7.4152594f, 0.79378486f),
				new Vector3f(-8.412864f, -2.5847406f, -1.3373263f),
				new Vector3f(-1.5871363f, -7.4152594f, -4.662674f),
				new Vector3f(-5.1389885f, -2.5847406f, -6.793785f),
		};
		float[][] tt = new float[][] {
			{-4.281242E-7f, 1.9675632f, -11.822773f, 3.9437037f, 1.7606739f},
			{4.3075258E-7f, -1.7012824f, 3.3587716f, -1.70128f, 3.3587718f},
			{2.837954E-7f, 3.6197708f, 2.0506117f, -0.64969814f, -4.928865f},
		};
		
		MainFrame m = new MainFrame(false);
		m.setVisible(true);
		
		try { Thread.sleep(1000); } finally {};
		
		Transformation transformation = m.addNewShape(Cube.class);

		int j = 0;
		for (int axis : new int[] {Manip.PICK_X, Manip.PICK_Y, Manip.PICK_Z}) {
			applyManip(m, transformation, TranslateManip.class, axis);
			for (int i = 0; i < mice.length; i++) {
				Vector3f direction = directions[i];
				direction.normalize();
				Vector3f a = axis==Manip.PICK_X?xx:axis==Manip.PICK_Y?yy:zz;
				float t = m.currentManip.computePseudointersection(m.pViewCam.eye, direction, z3, a);
				System.out.println(t);
				assertEquals("Wrong t-value for ray1 = " + m.pViewCam.eye + " + s*" + direction + ", ray2 = " + z3 + " + t*" + a, tt[j][i], t, 1e-5);
			}
			j++;
		}
	}
	
	public Vector2f screenToView(int x, int y) {
		float xx = (2f * x - SIZE) / SIZE;
		float yy = (2f * (SIZE - y) - SIZE) / SIZE;
		return new Vector2f(xx, yy);
	}
	
	@Test
	public void testTranslateManipX() throws InterruptedException {
		Vector2f x1 = screenToView(353, 357);
		Vector2f x2 = screenToView(416, 426);
		
		MainFrame m = setupManip(TranslateManip.class, Manip.PICK_X, x1, x2);
		compareImages(m, "translate-manip-x", 11);
	}

	@Test
	public void testTranslateManipY() throws InterruptedException {
		Vector2f x1 = screenToView(299, 223);
		Vector2f x2 = screenToView(299, 129);
		MainFrame m = setupManip(TranslateManip.class, Manip.PICK_Y, x1, x2);
		compareImages(m, "translate-manip-y");
	}
	
	@Test
	public void testTranslateManipZ() throws InterruptedException {
		Vector2f x1 = screenToView(215, 332);
		Vector2f x2 = screenToView(119, 370);
		MainFrame m = setupManip(TranslateManip.class, Manip.PICK_Z, x1, x2);
		compareImages(m, "translate-manip-z");
	}
	
	@Test
	public void testTranslateManipO() throws InterruptedException {
		Vector2f x1 = screenToView(299, 299);
		Vector2f x2 = screenToView(250, 250);
		MainFrame m = setupManip(TranslateManip.class, Manip.PICK_OTHER, x1, x2);
		compareImages(m, "translate-manip-o");
	}
	
	@Test
	public void testScaleManipX() throws InterruptedException {
//		Vector2f x1 = screenToView(353, 357);
//		Vector2f x2 = screenToView(416, 426);
		Vector2f x1 = screenToView(323, 325);
		Vector2f x2 = screenToView(353, 357);
		
		MainFrame m = setupManip(ScaleManip.class, Manip.PICK_X, x1, x2);
		compareImages(m, "scale-manip-x");
	}

	@Test
	public void testScaleManipY() throws InterruptedException {
//		Vector2f x1 = screenToView(299, 223);
//		Vector2f x2 = screenToView(299, 129);
		Vector2f x1 = screenToView(299, 258);
		Vector2f x2 = screenToView(299, 223);
		MainFrame m = setupManip(ScaleManip.class, Manip.PICK_Y, x1, x2);
		compareImages(m, "scale-manip-y");
	}
	
	@Test
	public void testScaleManipZ() throws InterruptedException {
//		Vector2f x1 = screenToView(215, 332);
//		Vector2f x2 = screenToView(119, 370);
		Vector2f x1 = screenToView(266, 312);
		Vector2f x2 = screenToView(215, 332);
		MainFrame m = setupManip(ScaleManip.class, Manip.PICK_Z, x1, x2);
		compareImages(m, "scale-manip-z");
	}
	
	@Test
	public void testScaleManipO() throws InterruptedException {
		Vector2f x1 = screenToView(299, 299);
		Vector2f x2 = screenToView(250, 250);
		MainFrame m = setupManip(ScaleManip.class, Manip.PICK_OTHER, x1, x2);
		compareImages(m, "scale-manip-o");
	}

	@Test
	public void testRotateManipX() throws InterruptedException {
		Vector2f x1 = screenToView(441, 240);
		Vector2f x2 = screenToView(439, 180);
		MainFrame m = setupManip(RotateManip.class, Manip.PICK_X, x1, x2); 
		compareImages(m, "rotate-manip-x", 10, 2);
	}

	@Test
	public void testRotateManipY() throws InterruptedException {
		Vector2f x1 = screenToView(480, 350);
		Vector2f x2 = screenToView(470, 302);
		MainFrame m = setupManip(RotateManip.class, Manip.PICK_Y, x1, x2); 
		compareImages(m, "rotate-manip-y");				
	}

	@Test
	public void testRotateManipZ() throws InterruptedException {
		Vector2f x1 = screenToView(373, 495);
		Vector2f x2 = screenToView(370, 475);
		MainFrame m = setupManip(RotateManip.class, Manip.PICK_Z, x1, x2); 
		compareImages(m, "rotate-manip-z");				
	}

	
	@Test
	public void testRotateScaleManip() throws InterruptedException {
		MainFrame m = new MainFrame(false);
		m.setVisible(true);
		
		try { Thread.sleep(1000); } finally {};
		
		Transformation t = m.addNewShape(Cube.class);

		Vector2f x1 = screenToView(441, 240);
		Vector2f x2 = screenToView(439, 180);
		this.applyManip(m, t, RotateManip.class, Manip.PICK_X, x1, x2);
		
		x1 = screenToView(215, 332);
		x2 = screenToView(119, 370);
		this.applyManip(m, t, ScaleManip.class, Manip.PICK_Z, x1, x2);
		
		compareImages(m, "rotate-scale-manip");			
	}
	
	@Test
	public void testTranslateRotateManip() throws InterruptedException {
		MainFrame m = new MainFrame(false);
		m.setVisible(true);
		
		try { Thread.sleep(1000); } finally {};
		
		Transformation t = m.addNewShape(Cube.class);

		Vector2f x1 = screenToView(353, 357);
		Vector2f x2 = screenToView(416, 426);		
		applyManip(m, t, TranslateManip.class, Manip.PICK_X, x1, x2);
		
		x1 = screenToView(480, 350);
		x2 = screenToView(470, 302);
		applyManip(m, t, RotateManip.class, Manip.PICK_Y, x1, x2); 
		
		compareImages(m, "translate-rotate-manip");
	}
}
