package tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.vecmath.Tuple3f;
import javax.vecmath.Vector2f;

import modeler.MainFrame;
import modeler.manip.Manip;
import modeler.scene.Transformation;
import modeler.shape.Cube;


public class ModelHelpers {
	final static double EPS = 1e-2;
	final static double GAMMA = 2.2;
	final static int LOW_8_BITS = 0xFF;
	final static double MAX_BYTE = 255.0;				
	final String testDirectory = "scenes/";
    private static Pattern importPattern;
    private static Pattern ignoreFilePattern;
    private static String[] allowedImports;
    private static String importsError;
    private static final Vector2f z = new Vector2f();

	final static int WAIT = 1000;

    public ModelHelpers()
    {
    	importPattern = Pattern.compile("(^|\\s)import\\s+([A-Za-z0-9]+(\\s*\\.\\s*[A-Za-z0-9]+)+\\s*;)");
    	// ignore test files: ModelTest1.java, ModelTest2.java, ModelHelpers.java
    	ignoreFilePattern = Pattern.compile("^Model(?:Test[123]|Helpers)\\.java$");
    	allowedImports = new String[] {
        		"javax.vecmath.",
        		"javax.imageio.",
        		"javax.swing.",
        		"java.lang.",
        		"java.util.",
        		"java.awt.",
        		"java.io.",
        		"java.nio.",
        		"java.text.",
        		"javax.xml.",
        		"org.w3c.dom.",
        		"modeler.",
        		"solution.",
        		"jgl.",
        };
    	importsError = null;
    }
    
    /**
     * Wrapper for checkImports
     * 
     */
    public static void importsPass()
    {
    	if (importsError == null)
    	{
    		importsError = checkImports(".");
    	}
    	
    	if (importsError != "pass")
    	{
    		System.out.println("failed: " + importsError);
    		fail(importsError);
    	}
    	
    }
    
    /**
     * Check imports
     */
    public static String checkImports(String folderPath)
    {
    	final File folder = new File(folderPath);
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                String s = checkImports(folderPath + "/" + fileEntry.getName());
                if (s != "pass")
                {
                	return s;
                }
            } else if (fileEntry.isFile() & fileEntry.getName().toLowerCase().endsWith("java")
            		& !ignoreFilePattern.matcher(fileEntry.getName()).matches()) {
            	String filePath = folderPath + "/" + fileEntry.getName();
                // System.out.println(filePath);
                try
                {
                    String java = new String(Files.readAllBytes(Paths.get(filePath)));
                    Matcher importMatcher = importPattern.matcher(java);
                    while (importMatcher.find()) 
                    {
                    	String match = importMatcher.group();
                    	match = match.replaceAll("\\s", "");
                    	match = match.replace("import", "");
                    	// System.out.println(match);
                    	boolean allowed = false;
                    	for (String allowedImport : allowedImports)
                    	{
                    		if (match.startsWith(allowedImport))
                    		{
                    			allowed = true;
                    			break;
                    		}
                    	}
                    	if (!allowed)
                    	{
                    		System.out.println("Disallowed");
                    		return "The import " + match + " is not allowed in the file " + fileEntry.getName();
                    	}
                    }
                }
                catch (IOException e)
                {
                	e.printStackTrace();
                	return "Import code threw IOException.";
                }
            }
        }
        return "pass";
    }
	public void compareImages(MainFrame m, String test) {
		compareImages(m, test, 10, 2);
	}

	public void compareImages(MainFrame m, String test, int maxErrors) {
		compareImages(m, test, maxErrors, 2);
	}

	public void compareImages(MainFrame m, String test, int maxErrors, int tolerance) {
		BufferedImage studentImage = m.writeImage(testDirectory + test + ".png");
		
		m.setVisible(false);
		m.dispose();

		System.out.println("studentImage size: " + studentImage.getWidth() + " " + studentImage.getHeight());
		BufferedImage correctImage = null;
		try {
			correctImage = ImageIO.read(new File(testDirectory + test + ".correct.png"));
		} catch (IOException e) {
			fail("Failed to load correct image " + testDirectory + test + ".correct.png");
		}

		// compare images 
		assertEquals("Image has the wrong width.", correctImage.getWidth(), studentImage.getWidth());
		assertEquals("Image has the wrong height.", correctImage.getHeight(), studentImage.getHeight());
		int numErrors = 0;
		String s = null;
		for (int y = 0; y < correctImage.getHeight(); y++) {
			for (int x = 0; x < correctImage.getWidth(); x++) {
				int correctRGB = correctImage.getRGB(x, y);
				int studentRGB = studentImage.getRGB(x, y);
				int correctR = correctRGB & LOW_8_BITS;
				int correctG = (correctRGB >> 8) & LOW_8_BITS;
				int correctB = (correctRGB >> 16) & LOW_8_BITS;
				int studentR = studentRGB & LOW_8_BITS;
				int studentG = (studentRGB >> 8) & LOW_8_BITS;
				int studentB = (studentRGB >> 16) & LOW_8_BITS;
				
				if (
						(Math.abs(correctR - studentR) > tolerance) || 
						(Math.abs(correctG - studentG) > tolerance) ||
						(Math.abs(correctB - studentB) > tolerance)
					) {
					if (s == null) {
						s = "; correct(" + correctR + ", " + correctG + ", " + correctB + ")";
						s += " student(" + studentR + ", " + studentG + ", " + studentB + ")";
					}
					numErrors += 1;
					if (numErrors >= maxErrors) {
						fail("Exceeds " + maxErrors + " errors; Image(x=" + x + ", y=" + y +"); " + s);
					}
					
				}
				
//				assertEquals("Image(x=" + x + ", y=" + y + ") has the wrong red value" + s, correctRGB & LOW_8_BITS, studentRGB & LOW_8_BITS, 1);
//				assertEquals("Image(x=" + x + ", y=" + y + ") has the wrong green value" + s, (correctRGB >> 8) & LOW_8_BITS, (studentRGB >> 8) & LOW_8_BITS, 1);
//				assertEquals("Image(x=" + x + ", y=" + y + ") has the wrong blue value" + s, (correctRGB >> 16) & LOW_8_BITS, (studentRGB >> 16) & LOW_8_BITS, 1);

			}
		}		
	}

	public void runTest(String test, int maxErrors, int tolerance) throws InterruptedException {
		importsPass();
		String filePath = testDirectory + test + ".xml";
		MainFrame m = new MainFrame(false);
		m.setVisible(true);
		
		try { Thread.sleep(WAIT); } finally {};
		m.openTree(filePath);
		try { Thread.sleep(WAIT); } finally {};
		
		compareImages(m, test, maxErrors, tolerance);
		
	}
	public void runTest(String test, int maxErrors) throws InterruptedException {
		runTest(test, maxErrors, 2);
	}
	public void runTest(String test) throws InterruptedException {
		runTest(test, 10, 2);
	}

	public MainFrame setupManip(Class<? extends Manip> c, int axis) throws InterruptedException {
		return setupManip(c, axis, z, z);
	}

	
	public MainFrame setupManip(Class<? extends Manip> c, int axis, Vector2f previousPosition, Vector2f currentPosition) throws InterruptedException {
		MainFrame m = new MainFrame(false);
		m.setVisible(true);
		
		try { Thread.sleep(WAIT); } finally {};
		
		Transformation t = m.addNewShape(Cube.class);

		applyManip(m, t, c, axis, previousPosition, currentPosition);

		return m;
	}

	public void applyManip(MainFrame m, Transformation t, Class<? extends Manip> c, int axis) {
		applyManip(m, t, c, axis, z, z);
	}
	
	public void applyManip(MainFrame m, Transformation t, Class<? extends Manip> c, int axis, Vector2f previousPosition, Vector2f currentPosition) {
		Vector2f mouseDelta = new Vector2f(currentPosition);
		mouseDelta.sub(previousPosition);
		try {
			m.currentManip = c.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		m.currentManip.setTransformation(t);
		m.refresh();
		m.currentManip.setPickedInfo(axis, m.pViewCam, previousPosition);
		m.currentManip.dragged(currentPosition, mouseDelta);
		m.refresh();
		System.out.println("T: " + t.T + "; R: " + t.R + "; S: " + t.S);
	}
	
	public void assertTupleEquals(String message, Tuple3f expected, Tuple3f actual) {
		assertEquals(message, expected.x, actual.x, EPS);
		assertEquals(message, expected.y, actual.y, EPS);
		assertEquals(message, expected.z, actual.z, EPS);
	}
	
	public void assertTupleEquals(String message, Tuple3f expected, Tuple3f actual, double delta) {
		assertEquals(message, expected.x, actual.x, delta);
		assertEquals(message, expected.y, actual.y, delta);
		assertEquals(message, expected.z, actual.z, delta);
	}

}
