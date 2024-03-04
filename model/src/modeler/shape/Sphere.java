package modeler.shape;

import javax.vecmath.Point3f;
import javax.vecmath.Point3i;
import javax.vecmath.Vector3f;

/**
 * @author ags
 */
public class Sphere extends Shape {

	private static final float SPHERE_RADIUS = 1.0f;

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
		
		//Do nothing if the mesh has already been built
		if(sphereMesh != null) {
			mesh = sphereMesh;
			return;
		}

		//get the flatness tolerance
		float flatnessTolerance = Shape.TOLERANCE;

		int numFacets = generateFacets(flatnessTolerance);

		Point3f[] vertices = generateVertices(numFacets);
		Vector3f[] normals = generateNormals(numFacets);
		Point3i[] triangles = generateTriangles(numFacets);

		//Print Result of Generating
		System.out.println("-------------------------");
		System.out.println("flatnessTolerance: " + flatnessTolerance);
		System.out.println("numFacets: " + numFacets);
		System.out.println("Vertices:");
		for (Point3f vertex : vertices) {
			System.out.println(vertex);
		}
		System.out.println("Normals:");
		for (Vector3f normal : normals) {
			System.out.println(normal);
		}
		System.out.println("Triangles:");
		for (Point3i triangle : triangles) {
			System.out.println(triangle);
		}
		System.out.println("-------------------------");

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
		int numVertices = numFacets+1;
		double angleIncrement = 360.0f / numFacets;
		Point3f[] vertices = new Point3f[numVertices*2*2];

		// Calculate vertices for the bottom layer
		for (int i=0; i < numVertices; i++) {
			double angle = i * angleIncrement;
			float x = SPHERE_RADIUS * cos(Math.toRadians(angle));
			float z = SPHERE_RADIUS * sin(Math.toRadians(angle));
			vertices[i] = new Point3f(x, -1, z);
			//add origin as last point
			if (i == numVertices-1)
			{
				vertices[i] = new Point3f(0, -SPHERE_RADIUS/2, 0);
			}
		}

		// Calculate vertices for the top layer
		//start where the last section left off
		for (int i=numVertices; i < numVertices*2; i++) {
			double angle = (i-numVertices) * angleIncrement;
			float x = SPHERE_RADIUS * cos(Math.toRadians(angle));
			float z = SPHERE_RADIUS * sin(Math.toRadians(angle));
			vertices[i] = new Point3f(x, 1, z);
			//add origin as last point
			if (i == (numVertices*2)-1)
			{
				vertices[i] = new Point3f(0, SPHERE_RADIUS/2, 0);
			}
		}

		// Duplicate the vertices for the bottom and top layers to be used for sides
		System.arraycopy(vertices, 0, vertices, numVertices * 2, numVertices);
		System.arraycopy(vertices, numVertices, vertices, numVertices * 3, numVertices);

		return vertices;
	}

	Vector3f[] generateNormals(int numFacets)
	{
		int numNormals = numFacets+1;
		double angleIncrement = 360.0f / numFacets;
		Vector3f[] normals = new Vector3f[numNormals*2*2];

		//generate normals for bottom layer
		Vector3f bottomNormal = new Vector3f(0.0f, -1.0f, 0.0f);
		for (int i = 0; i < numNormals; i++) 
		{
            normals[i] = bottomNormal;
        }

		//generate normals for top layer
		//start where the last section left off
		Vector3f topNormal = new Vector3f(0.0f, 1.0f, 0.0f);
		for (int i = numNormals; i < numNormals*2; i++) 
		{
			normals[i] = topNormal;
		}
		
		//generate normals for side vertices (bottom layer)
		for (int i = numNormals*2; i < numNormals*3; i++) 
		{
			double angle= (i-numNormals*2) * angleIncrement;
			float x = SPHERE_RADIUS * cos(Math.toRadians(angle));
			float z = SPHERE_RADIUS * sin(Math.toRadians(angle));
			normals[i] = new Vector3f(x, 0.0f, z);
			normals[i].normalize();
		}

		//generate normals for side vertices (top layer)
    	for (int i = numNormals*3; i < numNormals*4; i++) 
		{
			double angle=(i-numNormals*3) * angleIncrement;
			float x = SPHERE_RADIUS * cos(Math.toRadians(angle));
			float z = SPHERE_RADIUS * sin(Math.toRadians(angle));
			normals[i] = new Vector3f(x, 0.0f, z);
			normals[i].normalize();
    	}

		return normals;
	}

	Point3i[] generateTriangles(int numFacets)
	{	
		//numFacets triangles on the top
		//numFacets triangles on the bottom
		//numFacets*2 on sides (1 square for each facet, 2 triangles per square)
		Point3i[] triangles = new Point3i[numFacets*2*2];

		//generate triangles for bottom layer
		int bottomOriginIndex = numFacets; 
		for (int i = 0; i < numFacets; i++) {
            int vertex1 = i; 
            int vertex2 = i+1;
            int vertex3 = bottomOriginIndex;

			//last triangle, vertex doesnt exist so connect to the first
			if (i == numFacets-1)
			{
				vertex2 = 0;
			}
            triangles[i] = new Point3i(vertex1, vertex2, vertex3);
        }

		//generate triangles for top layer
		//start where the last section left off
		int start = numFacets+1;
		int topOriginIndex = (numFacets*2)+1;
		for (int i=start; i < topOriginIndex; i++) 
		{
			int vertex1 = i; 
			int vertex2 = i+1;
			int vertex3 = topOriginIndex;

			//last triangle, vertex doesnt exist so connect to the first
			if (i == topOriginIndex-1)
			{
				vertex2 = start;
			}
			// Create the triangle using the vertex indices, backwards to make it front facing
			triangles[i-1] = new Point3i(vertex3, vertex2, vertex1);
		}

		// generate sides of triangle using duplicated vertices
		// account for the extra origin vertices with the +2's
		int sideStart = numFacets * 2;
		for (int i = sideStart; i < numFacets*3; i++)
		{
			int bottomVertex1 = i+2;
			int bottomVertex2 = i+2+1;

			int topVertex1 = i+(numFacets+3);
			int topVertex2 = i+(numFacets+3)+1;

			//ensure last triangle loops back
			if (i == numFacets*3-1)
			{
				bottomVertex2 = sideStart+2;
				topVertex2 = sideStart+(numFacets+3);
			}

			triangles[i] = new Point3i(bottomVertex1, topVertex1, bottomVertex2);
			triangles[i + numFacets] = new Point3i(bottomVertex2, topVertex1, topVertex2);
		}

		return triangles;
	}
}
