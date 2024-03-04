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
		while (flatness > flatnessTolerance) 
		{
			//flatness = 1 - cos(angle)
			flatness = 1 - cos(Math.toRadians(360.0f / numFacets / 2));
			//inc by 2 for even facets
			numFacets += 2;
		}

		numFacets -= 2;
		numFacets = 6;
		return numFacets;
	}

	Point3f[] generateVertices(int numFacets)
	{	
		int numVertices = numFacets+1;
		
		Point3f[] vertices = new Point3f[numVertices*2*2];
		
		int index = 0;
		for (int i = 0; i <= numFacets; i++) {
            for (int j = 0; j <= numFacets; j++) {
                double theta = i * Math.PI / numFacets;
                double phi = j * 2 * Math.PI / numFacets;

                float x = SPHERE_RADIUS * sin(theta) * cos(phi);
                float y = SPHERE_RADIUS * cos(theta);
                float z = SPHERE_RADIUS * sin(theta) * sin(phi);

                vertices[index++] = new Point3f(x, y, z);
            }
        }

		return vertices;
	}

	Vector3f[] generateNormals(int numFacets)
	{
		int numNormals = numFacets+1;
		Vector3f[] normals = new Vector3f[numNormals*2*2];

		int index = 0;
        for (int i = 0; i <= numFacets; i++) {
            for (int j = 0; j <= numFacets; j++) {
                double theta = i * Math.PI / numFacets;
                double phi = j * 2 * Math.PI / numFacets;

                float x = SPHERE_RADIUS * sin(theta) * cos(phi);
                float y = SPHERE_RADIUS * cos(theta);
                float z = SPHERE_RADIUS * sin(theta) * sin(phi);

                Vector3f normal = new Vector3f(x, y, z);
                normals[index++] = normal;
            }
        }

		return normals;
	}

	Point3i[] generateTriangles(int numFacets)
	{	
		Point3i[] triangles = new Point3i[numFacets*2*2];

		int index = 0;
        for (int i = 0; i < numFacets; i++) {
            for (int j = 0; j < numFacets; j++) {
                int p0 = i * (numFacets + 1) + j;
                int p1 = p0 + 1;
                int p2 = (i + 1) * (numFacets + 1) + j;
                int p3 = p2 + 1;

                triangles[index++] = new Point3i(p0, p1, p2);
                triangles[index++] = new Point3i(p1, p3, p2);
            }
        }

		return triangles;
	}
}
