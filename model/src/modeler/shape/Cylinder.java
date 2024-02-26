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

	/**
	 * 
	 * @param flatnessTolerance the # set by the user in the program via scaler
	 * @return the number of facets to use to create the cylinder
	 */
	int generateFacets(float flatnessTolerance)
	{
		double angle = Math.acos(1.0 - flatnessTolerance);
		int numFacets = (int) Math.round(2 * Math.PI / angle);
		
		//ensure even
		numFacets = numFacets % 2 == 0 ? numFacets : numFacets + 1;
		//ensure >= 6
		numFacets = Math.max(6, numFacets);

		return numFacets;
	}

	Point3f[] generateVertices(int numFacets)
	{
		Point3f[] vertices = new Point3f[numFacets];

		//generate vertices for base layer
		float angleIncrement = 2 * (float) Math.PI / numFacets;
		for (int i = 0; i < numFacets; i++) {
            //calculate the angle for this vertex
            float angle = i * angleIncrement;

            //calculate the x, y, and z coordinates
            float x = CYLINDER_RADIUS * (float) Math.cos(angle);
            float y = CYLINDER_RADIUS * (float) Math.sin(angle);
            float z = 0.0f;

            // Set the vertex coordinates
            vertices[i] = new Point3f(x, y, z);
        }
		
		return vertices;
	}

	Vector3f[] generateNormals(int numFacets)
	{
		Vector3f[] normals = new Vector3f[numFacets];

		//generate normals for base layer
		Vector3f baseNormal = new Vector3f(0.0f, 0.0f, 1.0f);
		for (int i = 0; i < numFacets; i++) {
            normals[i] = baseNormal;
        }

		return normals;
	}

	Point3i[] generateTriangles(int numFacets)
	{
		Point3i[] triangles = new Point3i[numFacets];

		//generate triangles for base layer
		for (int i = 0; i < numFacets; i++) {
            int vertex1 = i;
            int vertex2 = (i + 1) % numFacets; // Wrap around to connect the last vertex to the first
            int vertex3 = numFacets; // Index of the center vertex (origin)

            // Create the triangle using the vertex indices
            triangles[i] = new Point3i(vertex1, vertex2, vertex3);
        }

		return triangles;
	}

}
