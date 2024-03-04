package modeler.shape;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.vecmath.Matrix4f;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

//import com.jogamp.opengl.GL2;
//import com.jogamp.common.nio.Buffers;

import jgl.GL;

/**
 * Basic packed triangle mesh. The triangle mesh is primarily data storage, all
 * geometric interaction is handled through MeshTriangle objects.
 *
 * @author arbree Aug 19, 2005 TriangleMesh.java Copyright 2005 Program of
 *         Computer Graphics, Cornell University
 */
public class Mesh {

	/** The number of vertices in the mesh * */
	protected int numVertices;

	/** The number of triangles in the mesh * */
	protected int numTriangles;

	/** The vertex array -- always present in each mesh * */
	protected FloatBuffer verts;

	/** The normal coordinate array -- may be null * */
	protected FloatBuffer normals;

	/** Mesh triangle objects for each triangle. */
	protected IntBuffer triangles = null;

	/**
	 * Default constructor creates an empty mesh
	 */
	public Mesh() { }

	/**
	 * Basic constructor. Sets mesh data array into the mesh structure. IMPORTANT:
	 * The data array are not copies so changes to the input data array will
	 * affect the mesh structure. The number of vertices and the number of
	 * triangles are inferred from the lengths of the verts and tris array. If
	 * either is not a multiple of three, an error is thrown.
	 *
	 * @param verts the vertex data
	 * @param tris the triangle data
	 * @param normals the normal data
	 */
	public Mesh(float[] verts, int[] tris, float[] normals) {

		// check the inputs
		if (verts.length % 3 != 0)
			throw new Error("Vertex array for a triangle mesh is not a multiple of 3.");
		if (tris.length % 3 != 0)
			throw new Error("Triangle array for a triangle mesh is not a multiple of 3.");

		// Set data
		setMeshData(verts, tris, normals);
	}

	/**
	 * @return Returns the normals.
	 */
	public FloatBuffer getNormals() {

		return normals;

	}

	/**
	 * @return Returns the verts.
	 */
	public FloatBuffer getVerts() {

		return verts;
	}

	/**
	 * Sets the mesh data and builds the triangle array.
	 * @param verts the vertices
	 * @param tris the triangles
	 * @param normals the normals
	 */
	private void setMeshData(float[] verts, int[] tris, float[] normals) {
		this.verts = FloatBuffer.allocate(verts.length);
		this.triangles = IntBuffer.allocate(tris.length);
		this.normals = FloatBuffer.allocate(normals.length);

		this.numVertices = verts.length / 3;
		this.numTriangles = tris.length / 3;
		this.verts.put(verts);
		this.triangles.put(tris);
		this.normals.put(normals);
	}

	/**
	 * Returns the number of triangles
	 *
	 * @return the number of triangles
	 */
	public int getNumTriangles() {

		return numTriangles;

	}

	/**
	 * Returns the number of vertices
	 *
	 * @return the number of vertices
	 */
	public int getNumVertices() {

		return numVertices;

	}

	/**
	 * Set the data in this mesh to the data in fileName
	 * @param fileName the name of a .msh file
	 */
	public void setData(String fileName) {
		readMesh(this, fileName);
	}

	/**
	 * Draws this mesh
	 * @param glD
	 */
	public void render(GL gl) {
		///// TODO: Part 1: Implement this method.

		gl.glEnable(GL.GL_CULL_FACE);
		gl.glCullFace(GL.GL_BACK);
		gl.glBegin(GL.GL_TRIANGLES);

		for (int i = 0 ; i < numTriangles; i++)
		{
			//vertex indeces
			int v1Index = triangles.get(i * 3);
			int v2Index = triangles.get(i * 3 + 1);
			int v3Index = triangles.get(i * 3 + 2);

			// vertex / normal 1
			gl.glNormal3f(normals.get(v1Index * 3), normals.get(v1Index * 3 + 1), normals.get(v1Index * 3 + 2));
			gl.glVertex3f(verts.get(v1Index * 3), verts.get(v1Index * 3 + 1), verts.get(v1Index * 3 + 2));

			// vertex / normal 2
			gl.glNormal3f(normals.get(v2Index * 3), normals.get(v2Index * 3 + 1), normals.get(v2Index * 3 + 2));
			gl.glVertex3f(verts.get(v2Index * 3), verts.get(v2Index * 3 + 1), verts.get(v2Index * 3 + 2));

			// vertex / normal 3
			gl.glNormal3f(normals.get(v3Index * 3), normals.get(v3Index * 3 + 1), normals.get(v3Index * 3 + 2));
			gl.glVertex3f(verts.get(v3Index * 3), verts.get(v3Index * 3 + 1), verts.get(v3Index * 3 + 2));

			//testing output
			// System.out.println("Triangles #: " + i);
			// System.out.println("normal 1: (" + normals.get(v1Index * 3) + ", " + normals.get(v1Index * 3+1) + ", " + normals.get(v1Index * 3+2) + ")");
			// System.out.println("normal 2: (" + normals.get(v2Index * 3) + ", " + normals.get(v2Index * 3+1) + ", " + normals.get(v2Index * 3+2) + ")");
			// System.out.println("normal 3: (" + normals.get(v3Index * 3) + ", " + normals.get(v3Index * 3+1) + ", " + normals.get(v3Index * 3+2) + ")");
			// System.out.println("vert 1: (" + verts.get(v1Index * 3) + ", " + verts.get(v1Index * 3+1) + ", " + verts.get(v1Index * 3+2) + ")");
			// System.out.println("vert 2: (" + verts.get(v2Index * 3) + ", " + verts.get(v2Index * 3+1) + ", " + verts.get(v2Index * 3+2) + ")");
			// System.out.println("vert 3: (" + verts.get(v3Index * 3) + ", " + verts.get(v3Index * 3+1) + ", " + verts.get(v3Index * 3+2) + ")");
			// System.out.println("");
		}

		gl.glEnd();
		gl.glDisable(GL.GL_CULL_FACE);
	}

	/**
	 * Write this mesh to the supplied print writer under the given transform
	 * @param currMat
	 */
	public void writeMesh(Matrix4f currMat, PrintWriter pw) {

		pw.println(numVertices);
		pw.println(numTriangles);

		//Write the vertices
		Point3f currVert = new Point3f();
		pw.println("vertices");
		for(int i = 0; i < numVertices; i++) {
			currVert.x = verts.get(3*i);
			currVert.y = verts.get(3*i+1);
			currVert.z = verts.get(3*i+2);
			currMat.transform(currVert);
			pw.println(currVert.x);
			pw.println(currVert.y);
			pw.println(currVert.z);
		}

		//Triangles
		pw.println("triangles");
		for(int i = 0; i < triangles.capacity(); i++) {
			pw.println(triangles.get(i));
		}

		//Normals
		Vector3f currNorm = new Vector3f();
		Matrix4f invTrans = new Matrix4f(currMat);
		invTrans.transpose();
		invTrans.invert();
		pw.println("normals");
		for(int i = 0; i < numVertices; i++) {
			currNorm.x = normals.get(3*i);
			currNorm.y = normals.get(3*i+1);
			currNorm.z = normals.get(3*i+2);
			invTrans.transform(currNorm);
			currNorm.normalize();
			pw.println(currNorm.x);
			pw.println(currNorm.y);
			pw.println(currNorm.z);
		}
	}

	/**
	 * Reads a .msh file into outputMesh.
	 *
	 * @param outputMesh the mesh to store the read data
	 * @param fileName the name of the mesh file to read
	 * @return the TriangleMesh from the file
	 */
	public static final void readMesh(Mesh outputMesh, String fileName) {

		float[] vertices;
		int[] triangles;
		float[] normals;

		try {

			// Create a buffered reader for the mesh file
			BufferedReader fr = new BufferedReader(new FileReader(fileName));

			// Read the size of the file
			int nPoints = Integer.parseInt(fr.readLine());
			int nPolys = Integer.parseInt(fr.readLine());

			// Create arrays for mesh data
			vertices = new float[nPoints*3];
			triangles = new int[nPolys*3];
			normals = null;

			// read vertices
			if (!fr.readLine().equals("vertices")) {
				fr.close();
				throw new RuntimeException("Broken file - vertices expected");
			}
			for (int i=0; i<vertices.length; ++i) {
				vertices[i] = Float.parseFloat(fr.readLine());
			}

			// read triangles
			if (!fr.readLine().equals("triangles")) {
				fr.close();
				throw new RuntimeException("Broken file - triangles expected.");
			}
			for (int i=0; i<triangles.length; ++i) {
				triangles[i] = Integer.parseInt(fr.readLine());
			}

			String line = fr.readLine();
			if (line != null && line.equals("normals")) {
				normals = new float[nPoints*3];
				for (int i=0; i<normals.length; ++i) {
					normals[i] = Float.parseFloat(fr.readLine());
				}
			}
			fr.close();
		}
		catch (Exception e) {
			throw new Error("Error reading mesh file: "+fileName);
		}

		//Set the data in the output Mesh
		outputMesh.setMeshData(vertices, triangles, normals);

		System.out.println("Read mesh of " + vertices.length + " verts");
	}
}
