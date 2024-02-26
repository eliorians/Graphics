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

		//!

		float flatnessTolerance = Shape.TOLERANCE;
		int numFacets = calculateNumFacets(flatnessTolerance);

		//The vertices of the cylinder
		Point3f[] vertices = generateVertices(numFacets);

		//The normals of the cylinder
		Vector3f[] normals = generateVertices(numFacets);

		//The triangles of the cylinder
		Point3i[] triangles = generateVertices(numFacets);

		//!

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
}
