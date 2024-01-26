package ray;

import ray.light.Light;
import ray.math.Color;
import ray.math.Point3;
import ray.math.Vector3;

import java.io.File;


/**
 * A simple ray tracer.
 *
 * @author ags
 */
public class RayTracer {

	/**
	 * The main method takes all the parameters an assumes they are input files
	 * for the ray tracer. It tries to render each one and write it out to a PNG
	 * file named <input_file>.png.
	 *
	 * @param args
	 */
	public static final void main(String[] args) {
		for (String inputString : args)
		{
			recursiveXML(inputString);
		}
	}
	
	public static void recursiveXML(String inputString)
	{
		File inputFile = new File(inputString);
		if (inputFile.isFile()) 
		{
			if (inputFile.getName().endsWith(".xml"))
			{

				runXML(inputFile.getPath());
			}
		}
		else if (inputFile.isDirectory())
		{
			File[] listing = inputFile.listFiles();
			if (listing != null) 
			{
				for (File file : listing) 
				{
					recursiveXML(inputString + "/" + file.getName());
				}
			}
		}
		else
		{
			System.out.println("Input argument \"" + inputString + "\" is neither an XML file nor a directory.");
		}
	}

	public static void runXML(String inputFilename)
	{
		Parser parser = new Parser();
		String outputFilename = inputFilename + ".png";
		System.out.println(inputFilename);
		// Parse the input file
		Scene scene = (Scene) parser.parse(inputFilename, Scene.class);

		// Render the scene
		renderImage(scene);

		// Write the image out
		System.out.println("Writing " + outputFilename);
		scene.getImage().write(outputFilename);
	}
	
	/**
	 * Return the basis for the camera coordinate system (u, v, w)
	 * @param scene the scene
	 * @return An array containing each vector [u, v, w]
	 */
	public static Vector3[] computeBasis(Scene scene) 
	{
		Vector3[] basis = new Vector3[3];

		// DONE: compute orthonormal basis from projection normal and up vector in scene.camera
		
		//get camera and relevant info
		Camera camera = scene.getCamera();
		Vector3 viewDirection = camera.viewDir;
		Vector3 upVector = camera.viewUp;
		
		//w vector (opposite view direction)
		Vector3 w = new Vector3(viewDirection);
		w.scale(-1);
		w.normalize();
		
		//u vector (up x w)
		Vector3 u = new Vector3();
		u.cross(upVector, w);
		u.normalize();

		//v vector (w x u)
		Vector3 v = new Vector3();
		v.cross(w, u);
		v.normalize();
	
		basis[0] = u;
		basis[1] = v;
		basis[2] = w;

		return basis;
	}
	
	/**
	 * Return the ray direction (from view point to pixel location on view rectangle).
	 * @param scene the scene
	 * @param basis the basis of the camera coordinate system (u, v, w).
	 * @param x the column index of the image
	 * @param y the row index of the image
	 * @return the ray direction as a Vector3
	 */
	public static Vector3 computeRayDirection(Scene scene, Vector3[] basis, int x, int y) {
		// TODO: compute ray direction using the camera and image in the scene.

		Vector3 d = new Vector3();
		
		//get relevant info
		Camera camera = scene.getCamera();

		//compute ray direction

		return d;
	}

	/**
	 * The renderImage method renders the entire scene.
	 *
	 * @param scene The scene to be rendered
	 */
	public static void renderImage(Scene scene) {

		// Get the output image
		@SuppressWarnings("unused")
		Image image = scene.getImage();
		// Timing counters
		long startTime = System.currentTimeMillis();

		// TODO: Render the image, writing the pixel values into image.

		// Output time
		long totalTime = (System.currentTimeMillis() - startTime);
		System.out.println("Done.  Total rendering time: "
				+ (totalTime / 1000.0) + " seconds");
	}
}
