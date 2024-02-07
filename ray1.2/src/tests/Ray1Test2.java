package tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

import ray.Camera;
import ray.Image;
import ray.RayTracer;
import ray.Scene;
import ray.math.Point2;
import ray.math.Vector3;

/**
 * JUnit test cases for the Ray1 ray tracer.
 * 
 * @author Mitch Parry
 * @version 2022-01-18
 * 
 */
public class Ray1Test2 extends Ray1Helpers
{
    /**
     * This tests camera coordinate system.
     */
	@Test
	public void testCameraCoordinates()
	{
		importsPass();
		
		Vector3 vd, up, pn;
		ArrayList<Vector3[]> cameraParams = new ArrayList<Vector3[]>();
		ArrayList<Vector3[]> correctBasis = new ArrayList<Vector3[]>();

		// vp = z
		cameraParams.add(new Vector3[] {
			new Vector3( 1, 0,-1),
			new Vector3( 0, 1, 0),
			new Vector3( 0, 0, 1), 
		});
		correctBasis.add(new Vector3[] {
			new Vector3( 1, 0, 0), 
			new Vector3( 0, 1, 0),
			new Vector3( 0, 0, 1),
		});

		// vp = -z
		cameraParams.add(new Vector3[] {
			new Vector3( 1, 0, 1), 
			new Vector3( 0, 1, 0),
			new Vector3( 0, 0,-1), 
		});
		correctBasis.add(new Vector3[] {
			new Vector3(-1, 0, 0), 
			new Vector3( 0, 1, 0),
			new Vector3( 0, 0,-1),
		});

		// vp = x
		cameraParams.add(new Vector3[] {
			new Vector3(-1, 0, 1), 
			new Vector3( 0, 1, 0),
			new Vector3( 1, 0, 0),
		});
		correctBasis.add(new Vector3[] {
			new Vector3( 0, 0,-1), 
			new Vector3( 0, 1, 0),
			new Vector3( 1, 0, 0),
		});

		// vp = -x
		cameraParams.add(new Vector3[] {
			new Vector3( 1, 0, 1), 
			new Vector3( 0, 1, 0),
			new Vector3(-1, 0, 0),
		});
		correctBasis.add(new Vector3[] {
			new Vector3( 0, 0, 1), 
			new Vector3( 0, 1, 0),
			new Vector3(-1, 0, 0),
		});

		// vp = (1, 1, 0)
		cameraParams.add(new Vector3[] {
			new Vector3(-1,-1, 1), 
			new Vector3( 0, 1, 0),
			new Vector3( 1, 1, 0),
		});
		correctBasis.add(new Vector3[] {
			new Vector3( 0, 0,-1), 
			new Vector3(-.707, .707, 0),
			new Vector3( .707, .707, 0),
		});

		// vp = (-1, 0, 1)
		cameraParams.add(new Vector3[] {
			new Vector3( 1, 1,-1), 
			new Vector3( 0, 1, 0),
			new Vector3(-1, 0, 1),
		});
		correctBasis.add(new Vector3[] {
			new Vector3( .707, 0,  .707), 
			new Vector3(0,     1, 0),
			new Vector3(-.707, 0, .707),
		});

		// vp = (1, 1, 1)
		cameraParams.add(new Vector3[] {
			new Vector3(-1, 0,-1),
			new Vector3( 0, 1, 0),
			new Vector3( 1, 1, 1),
		});
		correctBasis.add(new Vector3[] {
			new Vector3(.707, 0,-.707),
			new Vector3(-.408, .816,-.408),
			new Vector3( .577, .577,  .577), 
		});
		
		// vp = (-1, -1, -1)
		cameraParams.add(new Vector3[] {
			new Vector3( 1, 0, 1),
			new Vector3( 0, 1, 0),
			new Vector3(-1,-1,-1),
		});
		correctBasis.add(new Vector3[] {
			new Vector3(-.707, 0,.707),
			new Vector3(-.408, .816,-.408),
			new Vector3(-.577,-.577,-.577),
		});


		Scene scene = new Scene();

		for (int i = 0; i < cameraParams.size(); i++)
		{
			Vector3[] cp = cameraParams.get(i);
			Vector3[] cb = correctBasis.get(i);
			vd = cp[0]; up = cp[1]; pn = cp[2];
			
			Camera camera = new Camera();
			camera.setViewDir(vd);
			camera.setViewUp(up);
			camera.setProjNormal(pn);
			scene.setCamera(camera);
			
			Vector3[] basis = RayTracer.computeBasis(scene);
			String message = "computeBasis fails for projNormal=" + pn + "; viewDir=" + vd + "; viewUp=" + up;
			for (int k = 0; k < basis.length; k++)
			{
				assertEquals(message, cb[k].x, basis[k].x, EPS);
				assertEquals(message, cb[k].y, basis[k].y, EPS);
				assertEquals(message, cb[k].z, basis[k].z, EPS);
			}
		}
	}

	/**
	 * This tests the ray direction computation.
	 */
	@Test
	public void testComputeRayDirection()
	{
		importsPass();
		
		Vector3 vd, up, pn;
		ArrayList<Vector3[]> cameraParams = new ArrayList<Vector3[]>();
		ArrayList<Vector3[]> bases = new ArrayList<Vector3[]>();
		ArrayList<Point2> viewSizes = new ArrayList<Point2>();
		ArrayList<Point2> imageSizes = new ArrayList<Point2>();
		ArrayList<Point2> imageCoordinates = new ArrayList<Point2>();
		ArrayList<Vector3> correctDirections = new ArrayList<Vector3>();
		ArrayList<Double> projectionDistances = new ArrayList<Double>();

		// vp = z
		cameraParams.add(new Vector3[] {
			new Vector3( 1, 0,-1),
			new Vector3( 0, 1, 0),
			new Vector3( 0, 0, 1), 
		});
		bases.add(new Vector3[] {
			new Vector3( 1, 0, 0), 
			new Vector3( 0, 1, 0),
			new Vector3( 0, 0, 1),
		});
		viewSizes.add(new Point2(1, 1));
		imageSizes.add(new Point2(2, 2));
		imageCoordinates.add(new Point2(0, 0));
		correctDirections.add(new Vector3(0.45710678118654746,-0.25,-0.7071067811865475));
		projectionDistances.add(1.0);


		// vp = -z
		cameraParams.add(new Vector3[] {
			new Vector3( 1, 0, 1), 
			new Vector3( 0, 1, 0),
			new Vector3( 0, 0,-1), 
		});
		bases.add(new Vector3[] {
			new Vector3(-1, 0, 0), 
			new Vector3( 0, 1, 0),
			new Vector3( 0, 0,-1),
		});
		viewSizes.add(new Point2(2, 2));
		imageSizes.add(new Point2(2, 8));
		imageCoordinates.add(new Point2(0, 7));
		correctDirections.add(new Vector3(1.914213562373095,0.875,1.414213562373095));
		projectionDistances.add(2.0);

		// vp = x
		cameraParams.add(new Vector3[] {
			new Vector3(-1, 0, 1), 
			new Vector3( 0, 1, 0),
			new Vector3( 1, 0, 0),
		});
		bases.add(new Vector3[] {
			new Vector3( 0, 0,-1), 
			new Vector3( 0, 1, 0),
			new Vector3( 1, 0, 0),
		});
		viewSizes.add(new Point2(1, 2));
		imageSizes.add(new Point2(8, 8));
		imageCoordinates.add(new Point2(7, 0));
		correctDirections.add(new Vector3(-0.7071067811865475,-0.875,0.26960678118654746));
		projectionDistances.add(1.0);

		// vp = -x
		cameraParams.add(new Vector3[] {
			new Vector3( 1, 0, 1), 
			new Vector3( 0, 1, 0),
			new Vector3(-1, 0, 0),
		});
		bases.add(new Vector3[] {
			new Vector3( 0, 0, 1), 
			new Vector3( 0, 1, 0),
			new Vector3(-1, 0, 0),
		});
		viewSizes.add(new Point2(2, 1));
		imageSizes.add(new Point2(8, 2));
		imageCoordinates.add(new Point2(7, 1));
		correctDirections.add(new Vector3(1.414213562373095,0.25,2.289213562373095));
		projectionDistances.add(2.0);

		// vp = (1, 1, 0)
		cameraParams.add(new Vector3[] {
			new Vector3(-1,-1, 1), 
			new Vector3( 0, 1, 0),
			new Vector3( 1, 1, 0),
		});
		bases.add(new Vector3[] {
			new Vector3( 0, 0,-1), 
			new Vector3(-.707, .707, 0),
			new Vector3( .707, .707, 0),
		});
		viewSizes.add(new Point2(1, 1));
		imageSizes.add(new Point2(2, 2));
		imageCoordinates.add(new Point2(0, 0));
		correctDirections.add(new Vector3(-0.4006002691896259,-0.7541002691896258,0.8273502691896258));
		projectionDistances.add(1.0);

		// vp = (-1, 0, 1)
		cameraParams.add(new Vector3[] {
			new Vector3( 1, 1,-1), 
			new Vector3( 0, 1, 0),
			new Vector3(-1, 0, 1),
		});
		bases.add(new Vector3[] {
			new Vector3( .707, 0,  .707), 
			new Vector3(0,     1, 0),
			new Vector3(-.707, 0, .707),
		});
		viewSizes.add(new Point2(2, 2));
		imageSizes.add(new Point2(2, 8));
		imageCoordinates.add(new Point2(0, 7));
		correctDirections.add(new Vector3(0.8012005383792518,2.0297005383792515,-1.5082005383792516));
		projectionDistances.add(2.0);

		// vp = (1, 1, 1)
		cameraParams.add(new Vector3[] {
			new Vector3(-1, 0,-1),
			new Vector3( 0, 1, 0),
			new Vector3( 1, 1, 1),
		});
		bases.add(new Vector3[] {
			new Vector3(.707, 0,-.707),
			new Vector3(-.408, .816,-.408),
			new Vector3( .577, .577,  .577), 
		});
		viewSizes.add(new Point2(1, 2));
		imageSizes.add(new Point2(8, 8));
		imageCoordinates.add(new Point2(7, 7));
		correctDirections.add(new Vector3(-0.7547942811865475,0.714,-1.3734192811865473));
		projectionDistances.add(1.0);
		
		// vp = (-1, -1, -1)
		cameraParams.add(new Vector3[] {
			new Vector3( 1, 0, 1),
			new Vector3( 0, 1, 0),
			new Vector3(-1,-1,-1),
		});
		bases.add(new Vector3[] {
			new Vector3(-.707, 0,.707),
			new Vector3(-.408, .816,-.408),
			new Vector3(-.577,-.577,-.577),
		});
		viewSizes.add(new Point2(2, 1));
		imageSizes.add(new Point2(8, 2));
		imageCoordinates.add(new Point2(7, 0));
		correctDirections.add(new Vector3(0.8975885623730949,-0.204,2.1348385623730946));
		projectionDistances.add(2.0);


		Scene scene = new Scene();
		Image image = new Image(5, 5);
		scene.setImage(image);

		for (int k = 0; k < cameraParams.size(); k++)
		{
			Vector3[] cp = cameraParams.get(k);
			Vector3[] basis = bases.get(k);
			Point2 viewSize = viewSizes.get(k);
			Point2 imageSize = imageSizes.get(k);
			Point2 imageCoordinate = imageCoordinates.get(k);
			Vector3 correctDirection = correctDirections.get(k);
			vd = cp[0]; up = cp[1]; pn = cp[2];
			double viewWidth = viewSize.x;
			double viewHeight = viewSize.y;
			double projDistance = projectionDistances.get(k);
			int nx = (int) imageSize.x;
			int ny = (int) imageSize.y;
			int i = (int) imageCoordinate.x;
			int j = (int) imageCoordinate.y;
			
			Camera camera = new Camera();
			camera.setViewDir(vd);
			camera.setViewUp(up);
			camera.setProjNormal(pn);
			camera.setprojDistance(projDistance);
			camera.setViewWidth(viewWidth);
			camera.setViewHeight(viewHeight);
			scene.setCamera(camera);
			image.setSize(nx, ny);
			
			Vector3 d = RayTracer.computeRayDirection(scene, basis, i, j);
			System.out.println(d);
			String message = "computeRayDirection fails for" +
			        " viewWidth=" + viewWidth + 
					"; viewHeight=" + viewHeight + 
					"; projDistance=" + projDistance +
					"; image.width=" + nx + 
					"; image.height=" + ny + 
					"; i=" + i + 
					"; j=" + j +
					"; viewDir=" + vd + 
					"; projNormal=" + pn + 
					"; u=" + basis[0] + 
					"; v=" + basis[1] + 
					"; w=" + basis[2];
			d.normalize();
			correctDirection.normalize();
			System.out.println("Correct vector is: " + correctDirection + " \nYour vector was: " + d);
			assertEquals(message, correctDirection.x, d.x, EPS);
			assertEquals(message, correctDirection.y, d.y, EPS);
			assertEquals(message, correctDirection.z, d.z, EPS);
		}
	}

    @Test
    public void testAmbient()
    {
    	runTests("ambient");
    }

	@Test
    public void testDiffuse()
    {
    	runTests("diffuse");
    }

	@Test
    public void testSpecular()
    {
    	runTests("specular");
    }

	@Test
    public void testEllipsoidSphereConstant()
    {
    	runTests("ellipsoid/ellipsoid-sphere-constant");
    }

	@Test
    public void testEllipsoidSphereNormal()
    {
    	runTests("ellipsoid/ellipsoid-sphere-normal");
    }

	@Test
    public void testEllipsoidStretchedConstant()
    {
    	runTests("ellipsoid/ellipsoid-stretched-constant");
    }

	@Test
    public void testEllipsoidStretchedNormal()
    {
    	runTests("ellipsoid/ellipsoid-stretched-normal");
    }

	@Test
    public void testEllipsoidShiftedConstant()
    {
    	runTests("ellipsoid/ellipsoid-shifted-constant");
    }

	@Test
    public void testEllipsoidShiftedNormal()
    {
    	runTests("ellipsoid/ellipsoid-shifted-normal");
    }

	@Test
    public void testEllipsoidRotatedConstant()
    {
    	runTests("ellipsoid/ellipsoid-rotated-constant");
    }

	@Test
    public void testEllipsoidRotatedNormal()
    {
    	runTests("ellipsoid/ellipsoid-rotated-normal");
    }

	@Test
    public void testEasterEggs()
    {
    	runTests("ellipsoid/easter-eggs");
    }

    /**
     * This tests specular shading.
     */
    @Test
    public void testShadows()
    {
    	runTests("shadows");
    }

    /**
     * This tests the wire box model.
     */
    @Test
    public void testWireBox()
    {
    	runTests("wire-box");
    }

    /**
     * This tests the wire box model.
     */
    @Test
    public void testWireBoxShiftedPerspective()
    {
    	runTests("shifted-perspective");
    }
}
