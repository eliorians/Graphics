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
		//throw new UnsupportedOperationException();

		Vector3[] basis = new Vector3[3];
		
		//get camera and relevant info
		Camera camera = scene.getCamera();
		Vector3 upVector = camera.viewUp;
		
		//w vector (projNormal = opposite view direction)
		Vector3 w = new Vector3(camera.projNormal);
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
	public static Vector3 computeRayDirection(Scene scene, Vector3[] basis, int x, int y) 
	{
		//throw new UnsupportedOperationException();
		//TODO shifted perspectives (oblique perspective views slide)

		//gather relavant info
		Camera camera = scene.getCamera();
		double viewWidth = camera.viewWidth;
		double viewHeight = camera.viewHeight;
		double d = camera.projDistance;
		Vector3 viewDir = camera.viewDir;

		//calculate u and v
		//see ch4 slide 32 for formula
		double nX = scene.getImage().width;
		double l = -viewWidth / 2;
		double r = viewWidth / 2;

		double nY = scene.getImage().height;
		double b = -viewHeight / 2;
		double t = viewHeight / 2;

		double u = l + (r - l) * (x + .5) / nX;
		double v = b + (t - b) * (y + .5) / nY;
		
		Vector3 rayDirection = new Vector3();

		//non-oblique formula
		// rayDirection.scaleAdd(-d, basis[2]); //-dW
		// rayDirection.scaleAdd(u, basis[0]);  //uU
		// rayDirection.scaleAdd(v, basis[1]);  //vV

		//oblique formula
		viewDir.normalize();
		rayDirection.scaleAdd(d, viewDir);   //dD
		rayDirection.scaleAdd(u, basis[0]);  //uU
		rayDirection.scaleAdd(v, basis[1]);  //vV
	
		return rayDirection;
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

		//compute basis
		Vector3[] basis = computeBasis(scene);

		//cycle through the rows and columns of the output image
		for (int y = 0; y < image.height; y++) {
			for (int x = 0; x < image.width; x++) {

				//produce a ray for each pixel
				Vector3 rayDirection = computeRayDirection(scene, basis, x, y);
				Ray ray = new Ray(scene.getCamera().viewPoint, rayDirection);

				//calculate the hit / intersection
				HitRecord hit = scene.getGroup().hit(ray, 0, Double.POSITIVE_INFINITY);

				//if the ray hits something
				if (hit != null) {
					//color the pixel according to the material properties and light illumination
					Color pixelColor = new Color();
					for (Light light : scene.getLights()) {
						Color lightContribution = light.illuminate(ray, hit, scene);
						pixelColor.add(lightContribution);
					}
					image.setPixelColor(pixelColor, x, y);
				}
			}
		}

		// Output time
		long totalTime = (System.currentTimeMillis() - startTime);
		System.out.println("Done.  Total rendering time: "
				+ (totalTime / 1000.0) + " seconds");
	}

}
