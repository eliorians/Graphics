package modeler.shape;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

/**
 * @author ags
 */
public class Sphere extends Shape {

	private transient static Mesh sphereMesh;

	/**
	 * Required for IO
	 */
	public Sphere() {}

	/**
	 * @see modeler.shape.Shape#buildMesh()
	 */
	public void buildMesh() {
		//TODO: Part 2: Implement this method.

		//get the flatness tolerance
		float flatnessTolerance = Shape.TOLERANCE;

		int numFacets = generateFacets(flatnessTolerance);

		Point3f[] vertices = generateVertices(numFacets);
		Vector3f[] normals = generateNormals(numFacets);
		Point3i[] triangles = generateTriangles(numFacets);

		//Print Result of Generating
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
		sphereMesh = new Mesh(vertData, triData, normData);
		mesh = sphereMesh;
	}

	int generateFacets(float flatnessTolerance)
	{
		int numFacets = 6;
		float flatness = 99999999f;
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
		//x = cos(lat) * cos(lon)
		//y = sin(lat)
		//z = - cos(lat) * sin(lon) 
		//radius = cos(lat)

		int latitude = numFacets;		// top/bottom
		int longitude = numFacets*2;	// left/right
		Point3f[][] vertices2D = new Point3f[latitude][longitude];

		float latAngleIncrement = 180.0f / (latitude - 1);
    	float lonAngleIncrement = 360.0f / longitude;
		
		for (int i = 0; i < latitude; i++) {
			//calc latitude angle from -90 to 90
			float lat = latAngleIncrement * i - 90.0f;

            for (int j = 0; j < longitude; j++) {
				//calc longitude angle from -180 to 180
				float lon = lonAngleIncrement * j - 180.0f;

				float x = cos(Math.toRadians(lat)) * cos(Math.toRadians(lon));
				float y = sin(Math.toRadians(lat));
				float z = -cos(Math.toRadians(lat)) * sin(Math.toRadians(lon));

                vertices2D[i][j] = new Point3f(x, y, z);
            }
        }

		// convert 2D array to 1D array
		int index = 0;
        Point3f[] vertices = new Point3f[latitude * longitude];
        for (int i = 0; i < latitude; i++) {
            for (int j = 0; j < longitude; j++) {
                vertices[index++] = vertices2D[i][j];
            }
        }

		// test output
		// for (int i = 0; i < vertices2D.length; i++) {
		// 	for (int j = 0; j < vertices2D[i].length; j++) {
		// 		System.out.print("(" + vertices2D[i][j].x + ", " + vertices2D[i][j].y + ", " + vertices2D[i][j].z + ") ");
		// 	}
		// 	System.out.println("line end");
		// 	System.out.println();
		// }

		return vertices;
	}

	Vector3f[] generateNormals(int numFacets)
	{
		
		Point3f[] vertices = generateVertices(numFacets);
        Vector3f[] normals = new Vector3f[vertices.length];

        for (int i = 0; i < vertices.length; i++) {
            normals[i] = new Vector3f(vertices[i]);
        }

        return normals;
	}

	Point3i[] generateTriangles(int numFacets)
	{	
		int latitude = numFacets;
        int longitude = numFacets * 2;
		List<Point3i> triangleList = new ArrayList<>();
        
		for (int i = 0; i < latitude-1; i++) 
		{
			for (int j = 0; j < longitude-1; j++) 
			{
				int currentVertex = i * longitude + j;
				int nextVertex = currentVertex + 1;
				int nextRowVertex = currentVertex + longitude;
				int nextRowNextVertex = nextRowVertex + 1;

				triangleList.add(new Point3i(currentVertex, nextVertex, nextRowVertex));
				triangleList.add(new Point3i(nextVertex, nextRowNextVertex, nextRowVertex));
			}
		}

		//convert to array
		Point3i[] triangles = triangleList.toArray(new Point3i[triangleList.size()]);		

		return triangles;
	}
}
