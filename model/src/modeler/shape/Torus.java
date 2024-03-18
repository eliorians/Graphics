package modeler.shape;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

public class Torus extends Shape {
	
	private transient static Mesh torusMesh;

	// aspect ratio = R / r
	protected float aspectRatio = 2.333f;
	
	/**
	 * Required for IO
	 */
	public Torus() {}

	@Override
	public void readXMLData(Element node) {
		super.readXMLData(node);
		aspectRatio = Float.parseFloat(node.getAttribute("aspectRatio"));
	}

	/**
	 * @see modeler.shape.Shape#buildMesh()
	 */
	public void buildMesh() {
		
		//get the flatness tolerance
		float flatnessTolerance = Shape.TOLERANCE;

		int numFacets = generateFacets(flatnessTolerance);

		Point3f[] vertices = generateVertices(numFacets);
		Vector3f[] normals = generateNormals(numFacets, vertices);
		Point3i[] triangles = generateTriangles(numFacets);

		//testing output
		// System.out.println("-------------------------");
		// System.out.println("Vertices Length: " + vertices.length);
		// System.out.println("Normals Length: " + normals.length);
		// System.out.println("Triangles Length: " + triangles.length);
		// System.out.println("flatnessTolerance: " + flatnessTolerance);
		// System.out.println("numFacets: " + numFacets);
		// System.out.println("Vertices:");
		// for (Point3f vertex : vertices) {
		// 	System.out.println(vertex);
		// }
		// System.out.println("Normals:");
		// for (Vector3f normal : normals) {
		// 	System.out.println(normal);
		// }
		// System.out.println("Triangles:");
		// for (Point3i triangle : triangles) {
		// 	System.out.println(triangle);
		// }
		// System.out.println("-------------------------");


		//Build the mesh data arrays from the static mesh data
		int vertLength = vertices.length;
		float[] vertData = new float[3*vertLength];
		float[] normData = new float[3*vertLength];
		//Create the triangle list
		int[] triData = new int[3*triangles.length];

		//Copy the vertex data
		for(int i = 0; i < vertLength; i++) {

			vertData[3*i] = vertices[i].x;
			vertData[3*i+1] = vertices[i].y;
			vertData[3*i+2] = vertices[i].z;
			
			normData[3*i] = normals[i].x;
			normData[3*i+1] = normals[i].y;
			normData[3*i+2] = normals[i].z;
		}

		//Copy the triangle data
		for(int i = 0; i < triangles.length; i++) {
			
			triData[3*i] = triangles[i].x;
			triData[3*i+1] = triangles[i].y;
			triData[3*i+2] = triangles[i].z;
		}

		//Create the mesh
		torusMesh = new Mesh(vertData, triData, normData);
		mesh = torusMesh;
	}

	int generateFacets(float flatnessTolerance)
	{
		int numFacets = 6;
		float flatness = 99999999f;
		//loop through until we find a numFacets that produces flatness greater than the flatnessTolerance. use one step less than that (greatest that doesnt exceed)
		while (flatness > flatnessTolerance) 
		{
			//flatness = 1 - cos(angle)
			flatness = 1 - cos(Math.toRadians(360.0f / numFacets / 2));
			//inc by 2 for even facets
			numFacets += 2;
		}

		numFacets -= 2;
		return numFacets;
	}

	Point3f[] generateVertices(int numFacets)
	{	
		int latitude = numFacets+1;			// top/bottom
		int longitude = numFacets;			// left/right
		Point3f[][] vertices2D = new Point3f[latitude][longitude];

		float R = 1.0f;
		float r = 0.3f;

		double latAngleIncrement = 360.0f / latitude;
    	double lonAngleIncrement = 360.0f / longitude;
				
		for (int i = 0; i < latitude; i++) 
		{
			double lat = latAngleIncrement * i;

            for (int j = 0; j < longitude; j++) 
			{
				double lon = lonAngleIncrement * j;

				float x = (R + r * cos(Math.toRadians(lon))) * cos(Math.toRadians(lat));
				float y = (R + r * cos(Math.toRadians(lon))) * sin(Math.toRadians(lat));
				float z = r * sin(Math.toRadians(lon));

                vertices2D[i][j] = new Point3f(x, y, z);
            }
        }

		// flatten 2d array to 1d
		int index = 0;
        Point3f[] vertices = new Point3f[latitude * longitude];
        for (int i = 0; i < latitude; i++) {
            for (int j = 0; j < longitude; j++) {
                vertices[index++] = vertices2D[i][j];
            }
        }

		//test output
		for (int i = 0; i < vertices2D.length; i++) {
			for (int j = 0; j < vertices2D[i].length; j++) {
				System.out.print("(" + vertices2D[i][j].x + ", " + vertices2D[i][j].y + ", " + vertices2D[i][j].z + ") ");
			}
			System.out.println("line end");
			System.out.println();
		}

		return vertices;
	}

	Vector3f[] generateNormals(int numFacets, Point3f[] vertices)
	{
		Vector3f[] normals = new Vector3f[vertices.length];

        for (int i = 0; i < vertices.length; i++) {
            normals[i] = new Vector3f(vertices[i]);
        }

        return normals;
	}

	Point3i[] generateTriangles(int numFacets)
	{	
		int latitude = numFacets;
        int longitude = numFacets;
		List<Point3i> triangleList = new ArrayList<>();

		for (int i = 0; i < latitude - 1; i++) {
			for (int j = 0; j < longitude; j++) {
				int currentVertex = i * longitude + j;
				int nextRowVertex = ((i + 1) % latitude) * longitude + j;
				int nextColumnVertex = i * longitude + (j + 1) % longitude;
				int nextRowColumnVertex = ((i + 1) % latitude) * longitude + (j + 1) % longitude;
	
				triangleList.add(new Point3i(currentVertex, nextRowVertex, nextColumnVertex));
				triangleList.add(new Point3i(nextRowVertex, nextRowColumnVertex, nextColumnVertex));
			}
		}

		//convert to array
		Point3i[] triangles = triangleList.toArray(new Point3i[triangleList.size()]);		

		return triangles;
	}
}
