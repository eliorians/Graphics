package modeler.shape;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

/**
 * @author ags
 */
public class Cylinder extends Shape {

	//default radius and height of cylinder
	private static final float CYLINDER_HEIGHT = 2.0f;
    private static final float CYLINDER_RADIUS = 1.0f;

	//static mesh for the cylinder
	private transient static Mesh cylinderMesh;


	/**
	 * Required for IO
	 */
	public Cylinder() {}

	/**
	 * @see modeler.shape.Shape#buildMesh()
	 */
	public void buildMesh() 
	{
		// TODO: Part 2: Implement this method	

		//Do nothing if the mesh has already been built
		if(cylinderMesh != null) {
			mesh = cylinderMesh;
			return;
		}

		//get the flatness tolerance
		float flatnessTolerance = Shape.TOLERANCE;

		//do all these calculations to get triangle points around cylinder
		int numFacets = generateFacets(flatnessTolerance);

		Point3f[] vertices = generateVertices(numFacets);
		Vector3f[] normals = generateNormals(numFacets);
		Point3i[] triangles = generateTriangles(numFacets);

		// System.out.println("-------------------------");
		// System.out.println("flatnessTolerance: " + flatnessTolerance);
		// System.out.println("numFacets: " + numFacets);
		// System.out.println("Vertices:");
		// for (Point3f vertex : vertices) {
		// 	System.out.println(vertex);
		// }

		// // Printing normals
		// System.out.println("Normals:");
		// for (Vector3f normal : normals) {
		// 	System.out.println(normal);
		// }

		// // Printing triangles
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
		cylinderMesh = new Mesh(vertData, triData, normData);
		mesh = cylinderMesh;
	}

	int generateFacets(float flatnessTolerance)
	{
		
		int numFacets = (int) (Math.PI / Math.asin((1-flatnessTolerance)/2));
		
		//ensure even
		numFacets = numFacets % 2 == 0 ? numFacets : numFacets + 1;
		//ensure >= 6
		numFacets = Math.max(6, numFacets);

		return numFacets;
	}

	Point3f[] generateVertices(int numFacets)
	{	
		int numVertices = numFacets+1;
		float angleIncrement = 360.0f / numFacets;
		Point3f[] vertices = new Point3f[numVertices];

		// Calculate vertices for the bottom layer
		for (int i = 0; i < numVertices; i++) {
			float angle = i * angleIncrement;
			float x = CYLINDER_RADIUS * cos(Math.toRadians(angle));
			float z = CYLINDER_RADIUS * sin(Math.toRadians(angle));
			vertices[i] = new Point3f(x, -1, z);
			//add origin as last point
			if (i == numVertices-1)
			{
				vertices[i] = new Point3f(0, -1, 0);
			}
		}		
		return vertices;
	}

	Vector3f[] generateNormals(int numFacets)
	{
		int numNormals = numFacets+1;
		Vector3f[] normals = new Vector3f[numNormals];

		//generate normals for base layer, all upward in the y direction
		Vector3f baseNormal = new Vector3f(0.0f, -1.0f, 0.0f);
		for (int i = 0; i < numNormals; i++) {
            normals[i] = baseNormal;
        }

		return normals;
	}

	Point3i[] generateTriangles(int numFacets)
	{
		int numTriangles = numFacets;
		Point3i[] triangles = new Point3i[numTriangles];
		int originIndex = numFacets; 
		//generate triangles for base layer
		for (int i = 0; i < numFacets; i++) {
            int vertex1 = i; 
            int vertex2 = (i+1) % numFacets;
            int vertex3 = originIndex;

			//last triangle, vertex doesnt exist so connect to the first
			if (i == numFacets-1)
			{
				vertex2 = 0;
			}

            // Create the triangle using the vertex indices
            triangles[i] = new Point3i(vertex1, vertex2, vertex3);
        }

		return triangles;
	}

}
