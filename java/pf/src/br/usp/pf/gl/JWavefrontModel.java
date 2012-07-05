/* ***** BEGIN LICENSE BLOCK *****
 *
 * Copyright (c) 2005-2010 Universidade de Sao Paulo, Sao Carlos/SP, Brazil.
 * All Rights Reserved.
 *
 * This file is part of Java Wavefront (JWavefront) reader project.
 *
 * JWavefront is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * JWavefront is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * This code was developed by Fernando V. Paulovich <fpaulovich@gmail.com>,
 * member of Visualization, Imaging and Computer Graphics Group
 * (http://www.lcad.icmc.usp.br) at Instituto de Ciencias Matematicas e de
 * Computacao - ICMC - (http://www.icmc.usp.br) of Universidade de Sao Paulo,
 * Sao Carlos/SP, Brazil.
 *
 * You should have received a copy of the GNU General Public License along
 * with JWavefront. If not, see <http://www.gnu.org/licenses/>.
 *
 * ***** END LICENSE BLOCK ***** */

package br.usp.pf.gl;

import com.sun.opengl.util.ImageUtil;
import com.sun.opengl.util.texture.TextureData;
import com.sun.opengl.util.texture.TextureIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;

/**
 * This code was freely adapted from the original C code (http://www.xmission.com/~nate/index.html).
 * Some methods are not supported, but it now supports multiple textures, and some bugs on
 * parsing Wavefront files were fixed. Please contact the author (fpaulovich@gmail.com) if you
 * have any comments or found any bug. There is not any guarantee that it works!  *
 * @author Fernando V. Paulovich
 */
public class JWavefrontModel {

    /**
     * render with only vertices
     */
    public static final int WF_NONE = 0;
    /**
     * render with facet normals
     */
    public static final int WF_FLAT = 1;
    /**
     * render with vertex normals
     */
    public static final int WF_SMOOTH = (1 << 1);
    /**
     * render with texture coords
     */
    public static final int WF_TEXTURE = (1 << 2);
    /**
     * render with colors
     */
    public static final int WF_COLOR = (1 << 3);
    /**
     * render with materials
     */
    public static final int WF_MATERIAL = (1 << 4);
    
    /**
     * Construct a JWavefrontModel object.
     * @param file The file containing the object.
     * @throws IOException
     */
    public JWavefrontModel(File file) throws IOException {
        readOBJ(file);
        this.objectid = -1;
    }

    /**
     * "unitize" a model by translating it to the origin and
     * scaling it to fit in a unit cube around the origin.
     * @return  Returns the scalefactor used.
     */
    public float unitize() {
        assert (vertices != null);

        float maxx, minx, maxy, miny, maxz, minz;
        float cx, cy, cz, w, h, d;
        float scale;

        /* get the max/mins */
        maxx = minx = vertices[3 + 0];
        maxy = miny = vertices[3 + 1];
        maxz = minz = vertices[3 + 2];

        for (int i = 1; i <= numvertices; i++) {
            if (maxx < vertices[3 * i + 0]) {
                maxx = vertices[3 * i + 0];
            }
            if (minx > vertices[3 * i + 0]) {
                minx = vertices[3 * i + 0];
            }

            if (maxy < vertices[3 * i + 1]) {
                maxy = vertices[3 * i + 1];
            }
            if (miny > vertices[3 * i + 1]) {
                miny = vertices[3 * i + 1];
            }

            if (maxz < vertices[3 * i + 2]) {
                maxz = vertices[3 * i + 2];
            }
            if (minz > vertices[3 * i + 2]) {
                minz = vertices[3 * i + 2];
            }
        }

        /* calculate model width, height, and depth */
        w = Math.abs(maxx) + Math.abs(minx);
        h = Math.abs(maxy) + Math.abs(miny);
        d = Math.abs(maxz) + Math.abs(minz);

        /* calculate center of the model */
        cx = (maxx + minx) / 2.0f;
        cy = (maxy + miny) / 2.0f;
        cz = (maxz + minz) / 2.0f;

        /* calculate unitizing scale factor */
        scale = 2.0f / Math.max(Math.max(w, h), d);

        /* translate around center then scale */
        for (int i = 1; i <= numvertices; i++) {
            vertices[3 * i + 0] -= cx;
            vertices[3 * i + 1] -= cy;
            vertices[3 * i + 2] -= cz;
            vertices[3 * i + 0] *= scale;
            vertices[3 * i + 1] *= scale;
            vertices[3 * i + 2] *= scale;
        }

        return scale;
    }

    /**
     * Calculates the dimensions (width, height, depth) of a model.
     * @return The model dimensions (width, height, depth); array of 3 floats.
     */
    public float[] dimensions() {
        assert (vertices != null);
        float maxx, minx, maxy, miny, maxz, minz;
        float dimensions[] = new float[3];

        /* get the max/mins */
        maxx = minx = vertices[3 + 0];
        maxy = miny = vertices[3 + 1];
        maxz = minz = vertices[3 + 2];

        for (int i = 1; i <= numvertices; i++) {
            if (maxx < vertices[3 * i + 0]) {
                maxx = vertices[3 * i + 0];
            }
            if (minx > vertices[3 * i + 0]) {
                minx = vertices[3 * i + 0];
            }

            if (maxy < vertices[3 * i + 1]) {
                maxy = vertices[3 * i + 1];
            }
            if (miny > vertices[3 * i + 1]) {
                miny = vertices[3 * i + 1];
            }

            if (maxz < vertices[3 * i + 2]) {
                maxz = vertices[3 * i + 2];
            }
            if (minz > vertices[3 * i + 2]) {
                minz = vertices[3 * i + 2];
            }
        }

        /* calculate model width, height, and depth */
        dimensions[0] = Math.abs(maxx) + Math.abs(minx);
        dimensions[1] = Math.abs(maxy) + Math.abs(miny);
        dimensions[2] = Math.abs(maxz) + Math.abs(minz);

        return dimensions;
    }

    /**
     * Scales a model by a given amount (0.5 = half as large, 2.0 = twice as
     * large)
     * @param scale
     */
    public void scale(float scale) {
        for (int i = 1; i <= numvertices; i++) {
            vertices[3 * i + 0] *= scale;
            vertices[3 * i + 1] *= scale;
            vertices[3 * i + 2] *= scale;
        }
    }

    /**
     * Reverse the polygon winding for all polygons in this model.  Default
     * winding is counter-clockwise. Also changes the direction of the normals.
     */
    public void reverseWinding() {
        int swap;

        for (int i = 0; i < numtriangles; i++) {
            swap = triangles[i].vindices[0];
            triangles[i].vindices[0] = triangles[i].vindices[2];
            triangles[i].vindices[2] = swap;

            if (numnormals > 0) {
                swap = triangles[i].nindices[0];
                triangles[i].nindices[0] = triangles[i].nindices[2];
                triangles[i].nindices[2] = swap;
            }

            if (numtexcoords > 0) {
                swap = triangles[i].tindices[0];
                triangles[i].tindices[0] = triangles[i].tindices[2];
                triangles[i].tindices[2] = swap;
            }
        }

        /* reverse facet normals */
        for (int i = 1; i <= numfacetnorms; i++) {
            facetnorms[3 * i + 0] = -facetnorms[3 * i + 0];
            facetnorms[3 * i + 1] = -facetnorms[3 * i + 1];
            facetnorms[3 * i + 2] = -facetnorms[3 * i + 2];
        }

        /* reverse vertex normals */
        for (int i = 1; i <= numnormals; i++) {
            normals[3 * i + 0] = -normals[3 * i + 0];
            normals[3 * i + 1] = -normals[3 * i + 1];
            normals[3 * i + 2] = -normals[3 * i + 2];
        }
    }

    /**
     * Generates facet normals for a model (by taking the cross product of the
     * two vectors derived from the sides of each triangle).  Assumes a
     * counter-clockwise winding.
     */
    public void facetNormals() {
        assert (vertices != null);
        float u[] = new float[3];
        float v[] = new float[3];

        /* allocate memory for the new facet normals */
        numfacetnorms = numtriangles;
        facetnorms = new float[3 * (numfacetnorms + 1)];

        for (int i = 0; i < numtriangles; i++) {
            triangles[i].findex = i + 1;

            u[0] = vertices[3 * triangles[i].vindices[1] + 0]
                    - vertices[3 * triangles[i].vindices[0] + 0];
            u[1] = vertices[3 * triangles[i].vindices[1] + 1]
                    - vertices[3 * triangles[i].vindices[0] + 1];
            u[2] = vertices[3 * triangles[i].vindices[1] + 2]
                    - vertices[3 * triangles[i].vindices[0] + 2];

            v[0] = vertices[3 * triangles[i].vindices[2] + 0]
                    - vertices[3 * triangles[i].vindices[0] + 0];
            v[1] = vertices[3 * triangles[i].vindices[2] + 1]
                    - vertices[3 * triangles[i].vindices[0] + 1];
            v[2] = vertices[3 * triangles[i].vindices[2] + 2]
                    - vertices[3 * triangles[i].vindices[0] + 2];

            cross(u, v, facetnorms, (3 * (i + 1)));
            normalize(facetnorms, (3 * (i + 1)));
        }
    }

    /**
     * Generates smooth vertex normals for a model.  First builds a list of all
     * the triangles each vertex is in.  Then loops through each vertex in the
     * the list averaging all the facet normals of the triangles each vertex is
     * in.  Finally, sets the normal index in the triangle for the vertex to the
     * generated smooth normal.  If the dot product of a facet normal and the
     * facet normal associated with the first triangle in the list of triangles
     * the current vertex is in is greater than the cosine of the angle
     * parameter to the function, that facet normal is not added into the
     * average normal calculation and the corresponding vertex is given the
     * facet normal.  This tends to preserve hard edges.  The angle to use
     * depends on the model, but 90 degrees is usually a good start.
     * @param angle Maximum angle (in degrees) to smooth across.
     * @throws IOException
     */
    public void vertexNormals(float angle) throws IOException {
        assert (facetnorms != null);

        Node node;
        Node members[];
        float normals_aux[];
        int numnormals_aux;
        float average[] = new float[3];
        float dot, cos_angle;
        int avg;

        /* calculate the cosine of the angle (in degrees) */
        cos_angle = (float) Math.cos(angle * Math.PI / 180.0);

        /* allocate space for new normals */
        this.numnormals = this.numtriangles * 3; /* 3 normals per triangle */
        this.normals = new float[3 * (this.numnormals + 1)];

        /* allocate a structure that will hold a linked list of triangle
        indices for each vertex */
        members = new Node[this.numvertices + 1];
        for (int i = 1; i <= this.numvertices; i++) {
            members[i] = null;
        }

        /* for every triangle, create a node for each vertex in it */
        for (int i = 0; i < this.numtriangles; i++) {
            node = new Node();
            node.index = i;
            node.next = members[triangles[i].vindices[0]];
            members[triangles[i].vindices[0]] = node;

            node = new Node();
            node.index = i;
            node.next = members[triangles[i].vindices[1]];
            members[triangles[i].vindices[1]] = node;

            node = new Node();
            node.index = i;
            node.next = members[triangles[i].vindices[2]];
            members[triangles[i].vindices[2]] = node;
        }

        /* calculate the average normal for each vertex */
        numnormals_aux = 1;
        for (int i = 1; i <= this.numvertices; i++) {
            /* calculate an average normal for this vertex by averaging the
            facet normal of every triangle this vertex is in */
            node = members[i];
            if (node == null) {
                throw new IOException("vertexNormals(): vertex w/o a triangle\n");
            }
            average[0] = 0.0f;
            average[1] = 0.0f;
            average[2] = 0.0f;
            avg = 0;
            while (node != null) {
                /* only average if the dot product of the angle between the two
                facet normals is greater than the cosine of the threshold
                angle -- or, said another way, the angle between the two
                facet normals is less than (or equal to) the threshold angle */
                dot = dot(this.facetnorms, (3 * triangles[node.index].findex),
                        this.facetnorms, (3 * triangles[members[i].index].findex));
                if (dot > cos_angle) {
                    node.averaged = true;
                    average[0] += this.facetnorms[3 * triangles[node.index].findex + 0];
                    average[1] += this.facetnorms[3 * triangles[node.index].findex + 1];
                    average[2] += this.facetnorms[3 * triangles[node.index].findex + 2];
                    avg = 1;			/* we averaged at least one normal! */
                } else {
                    node.averaged = false;
                }
                node = node.next;
            }

            if (avg > 0) {
                /* normalize the averaged normal */
                normalize(average, 0);

                /* add the normal to the vertex normals list */
                this.normals[3 * numnormals_aux + 0] = average[0];
                this.normals[3 * numnormals_aux + 1] = average[1];
                this.normals[3 * numnormals_aux + 2] = average[2];
                avg = numnormals_aux;
                numnormals_aux++;
            }

            /* set the normal of this vertex in each triangle it is in */
            node = members[i];
            while (node != null) {
                if (node.averaged) {
                    /* if this node was averaged, use the average normal */
                    if (triangles[node.index].vindices[0] == i) {
                        triangles[node.index].nindices[0] = avg;
                    } else if (triangles[node.index].vindices[1] == i) {
                        triangles[node.index].nindices[1] = avg;
                    } else if (triangles[node.index].vindices[2] == i) {
                        triangles[node.index].nindices[2] = avg;
                    }
                } else {
                    /* if this node wasn't averaged, use the facet normal */
                    this.normals[3 * numnormals_aux + 0] =
                            this.facetnorms[3 * triangles[node.index].findex + 0];
                    this.normals[3 * numnormals_aux + 1] =
                            this.facetnorms[3 * triangles[node.index].findex + 1];
                    this.normals[3 * numnormals_aux + 2] =
                            this.facetnorms[3 * triangles[node.index].findex + 2];
                    if (triangles[node.index].vindices[0] == i) {
                        triangles[node.index].nindices[0] = numnormals_aux;
                    } else if (triangles[node.index].vindices[1] == i) {
                        triangles[node.index].nindices[1] = numnormals_aux;
                    } else if (triangles[node.index].vindices[2] == i) {
                        triangles[node.index].nindices[2] = numnormals_aux;
                    }
                    numnormals_aux++;
                }
                node = node.next;
            }
        }

        this.numnormals = numnormals_aux - 1;

        /* pack the normals array (we previously allocated the maximum
        number of normals that could possibly be created (numtriangles *
        3), so get rid of some of them (usually alot unless none of the
        facet normals were averaged)) */
        normals_aux = this.normals;
        this.normals = new float[3 * (this.numnormals + 1)];
        for (int i = 1; i <= this.numnormals; i++) {
            this.normals[3 * i + 0] = normals_aux[3 * i + 0];
            this.normals[3 * i + 1] = normals_aux[3 * i + 1];
            this.normals[3 * i + 2] = normals_aux[3 * i + 2];
        }
    }

    /**
     * Generates texture coordinates according to a linear projection of the
     * texture map.  It generates these by linearly mapping the vertices onto
     * a square.
     */
    public void linearTexture() {
        Group group;
        float dimensions[];
        float x, y, scalefactor;

        numtexcoords = numvertices;
        texcoords = new float[2 * (numtexcoords + 1)];

        dimensions = dimensions();

        scalefactor = 2.0f
                / Math.abs(Math.max(Math.max(dimensions[0], dimensions[1]), dimensions[2]));

        /* do the calculations */
        for (int i = 1; i <= numvertices; i++) {
            x = vertices[3 * i + 0] * scalefactor;
            y = vertices[3 * i + 2] * scalefactor;
            texcoords[2 * i + 0] = (x + 1.0f) / 2.0f;
            texcoords[2 * i + 1] = (y + 1.0f) / 2.0f;
        }

        /* go through and put texture coordinate indices in all the triangles */
        group = groups;
        while (group != null) {
            for (int i = 0; i < group.numtriangles; i++) {
                triangles[group.triangles[i]].tindices[0] = triangles[group.triangles[i]].vindices[0];
                triangles[group.triangles[i]].tindices[1] = triangles[group.triangles[i]].vindices[1];
                triangles[group.triangles[i]].tindices[2] = triangles[group.triangles[i]].vindices[2];
            }
            group = group.next;
        }
    }

    /**
     * Generates texture coordinates according to a
     * spherical projection of the texture map.  Sometimes referred to as
     * spheremap, or reflection map texture coordinates.  It generates
     * these by using the normal to calculate where that vertex would map
     * onto a sphere.  Since it is impossible to map something flat
     * perfectly onto something spherical, there is distortion at the
     * poles.  This particular implementation causes the poles along the X
     * axis to be distorted.
     */
    public void spheremapTexture() {
        assert (normals != null);

        Group group;
        float theta, phi, rho, x, y, z, r;

        numtexcoords = numnormals;
        texcoords = new float[2 * (numtexcoords + 1)];

        for (int i = 1; i <= numnormals; i++) {
            z = normals[3 * i + 0];	/* re-arrange for pole distortion */
            y = normals[3 * i + 1];
            x = normals[3 * i + 2];
            r = (float) Math.sqrt((x * x) + (y * y));
            rho = (float) Math.sqrt((r * r) + (z * z));

            if (r == 0.0) {
                theta = 0.0f;
                phi = 0.0f;
            } else {
                if (z == 0.0) {
                    phi = (float) (Math.PI / 2.0);

                } else {
                    phi = (float) Math.acos(z / rho);


                }
                if (y == 0.0) {
                    theta = (float) (Math.PI / 2.0);

                } else {
                    theta = (float) (Math.asin(y / r) + (Math.PI / 2.0));

                }
            }

            texcoords[2 * i + 0] = (float) (theta / Math.PI);
            texcoords[2 * i + 1] = (float) (phi / Math.PI);
        }

        /* go through and put texcoord indices in all the triangles */
        group = groups;
        while (group != null) {
            for (int i = 0; i < numtriangles; i++) {
                triangles[group.triangles[i]].tindices[0] = triangles[group.triangles[i]].nindices[0];
                triangles[group.triangles[i]].tindices[1] = triangles[group.triangles[i]].nindices[1];
                triangles[group.triangles[i]].tindices[2] = triangles[group.triangles[i]].nindices[2];
            }
            group = group.next;
        }
    }

    /**
     * Deletes a model structure (not need to be used).
     */
    public void delete() {
    }

    /**
     * Reads a model description from a Wavefront.
     * @param file The file containing the Wavefront model.
     * @throws IOException
     */
    private void readOBJ(File file) throws IOException {
        /* allocate a new model */
        pathname = file;
        mtllibname = null;
        numvertices = 0;
        vertices = null;
        numnormals = 0;
        normals = null;
        numtexcoords = 0;
        texcoords = null;
        numfacetnorms = 0;
        facetnorms = null;
        numtriangles = 0;
        triangles = null;
        nummaterials = 0;
        materials = null;
        numgroups = 0;
        groups = null;
        position = new float[3];
        Arrays.fill(position, 0.0f);

        /* make a first pass through the file to get a count of the number
        of vertices, normals, texcoords & triangles */
        firstPass(file);

        /* allocate memory */
        vertices = new float[3 * (numvertices + 1)];
        triangles = new Triangle[numtriangles];

        if (numnormals > 0) {
            normals = new float[3 * (numnormals + 1)];
        }

        if (numtexcoords > 0) {
            texcoords = new float[2 * (numtexcoords + 1)];
        }

        /* rewind to beginning of file and read in the data this pass */
        secondPass(file);
    }

    /**
     * Second pass at a Wavefront OBJ file that gets all the data. 
     * @param file The file containing the Wavefront model.
     * @throws IOException
     */
    private void secondPass(File file) throws IOException {
        int numvertices_aux;		/* number of vertices in model */
        int numnormals_aux;			/* number of normals in model */
        int numtexcoords_aux;		/* number of texcoords in model */
        int numtriangles_aux;		/* number of triangles in model */
        Group group = groups;			/* current group pointer */
        int material;			/* current material */
        int v, n, t;
        StringTokenizer tok, tok2;
        BufferedReader in = null;
        String line = null;


        /* on the second pass through the file, read all the data into the
        allocated arrays */
        numvertices_aux = numnormals_aux = numtexcoords_aux = 1;
        numtriangles_aux = 0;
        material = 0;

        try {
            in = new BufferedReader(new FileReader(file));

            while ((line = in.readLine()) != null) {
                line = line.trim();

                if (line.length() > 0) {
                    switch (line.charAt(0)) {
                        case '#':				/* comment */
                            /* eat up rest of line */
                            break;
                        case 'v':				/* v, vn, vt */
                            switch (line.charAt(1)) {
                                case ' ':			/* vertex */
                                    tok = new StringTokenizer(line, " ");
                                    tok.nextToken(); //ignores v
                                    vertices[3 * numvertices_aux + 0] = Float.parseFloat(tok.nextToken());
                                    vertices[3 * numvertices_aux + 1] = Float.parseFloat(tok.nextToken());
                                    vertices[3 * numvertices_aux + 2] = Float.parseFloat(tok.nextToken());
                                    numvertices_aux++;
                                    break;
                                case 'n':				/* normal */
                                    tok = new StringTokenizer(line, " ");
                                    tok.nextToken(); //ignores vn
                                    normals[3 * numnormals_aux + 0] = Float.parseFloat(tok.nextToken());
                                    normals[3 * numnormals_aux + 1] = Float.parseFloat(tok.nextToken());
                                    normals[3 * numnormals_aux + 2] = Float.parseFloat(tok.nextToken());
                                    numnormals_aux++;
                                    break;
                                case 't':				/* texcoord */
                                    tok = new StringTokenizer(line, " ");
                                    tok.nextToken(); //ignores vt
                                    texcoords[2 * numtexcoords_aux + 0] = Float.parseFloat(tok.nextToken());
                                    texcoords[2 * numtexcoords_aux + 1] = Float.parseFloat(tok.nextToken());
                                    numtexcoords_aux++;
                                    break;
                            }
                            break;


                        case 'u':
                            tok = new StringTokenizer(line, " ");
                            tok.nextToken(); //ignores usemtl
                            String token = tok.nextToken();
                            group.material = material = findMaterial(token);
                            break;
                        case 'g':
                        case 'o':				/* group */
                            tok = new StringTokenizer(line, " ");
                            tok.nextToken(); //ignores g
                            if (tok.hasMoreTokens()) {
                                group = findGroup(tok.nextToken());
                            } else {
                                group = findGroup("_blank_");
                            }
                            group.material = material;
                            break;
                        case 'f':				/* face */
                            line = line.trim().substring(1).trim(); //removing f

                            if (line.contains("//")) { /* v//n */
                                triangles[numtriangles_aux] = new Triangle();
                                tok = new StringTokenizer(line, " ");

                                tok2 = new StringTokenizer(tok.nextToken(), "/");
                                v = Integer.parseInt(tok2.nextToken());
                                n = Integer.parseInt(tok2.nextToken());
                                triangles[numtriangles_aux].vindices[0] = v;
                                triangles[numtriangles_aux].nindices[0] = n;

                                tok2 = new StringTokenizer(tok.nextToken(), "/");
                                v = Integer.parseInt(tok2.nextToken());
                                n = Integer.parseInt(tok2.nextToken());
                                triangles[numtriangles_aux].vindices[1] = v;
                                triangles[numtriangles_aux].nindices[1] = n;

                                tok2 = new StringTokenizer(tok.nextToken(), "/");
                                v = Integer.parseInt(tok2.nextToken());
                                n = Integer.parseInt(tok2.nextToken());
                                triangles[numtriangles_aux].vindices[2] = v;
                                triangles[numtriangles_aux].nindices[2] = n;

                                group.triangles[group.numtriangles++] = numtriangles_aux;
                                numtriangles_aux++;

                                while (tok.hasMoreTokens()) {
                                    triangles[numtriangles_aux] = new Triangle();
                                    tok2 = new StringTokenizer(tok.nextToken(), "/");
                                    v = Integer.parseInt(tok2.nextToken());
                                    n = Integer.parseInt(tok2.nextToken());

                                    triangles[numtriangles_aux].vindices[0] = triangles[numtriangles_aux - 1].vindices[0];
                                    triangles[numtriangles_aux].nindices[0] = triangles[numtriangles_aux - 1].nindices[0];
                                    triangles[numtriangles_aux].vindices[1] = triangles[numtriangles_aux - 1].vindices[2];
                                    triangles[numtriangles_aux].nindices[1] = triangles[numtriangles_aux - 1].nindices[2];
                                    triangles[numtriangles_aux].vindices[2] = v;
                                    triangles[numtriangles_aux].nindices[2] = n;

                                    group.triangles[group.numtriangles++] = numtriangles_aux;
                                    numtriangles_aux++;
                                }
                            } else {
                                triangles[numtriangles_aux] = new Triangle();
                                tok = new StringTokenizer(line, " ");
                                tok2 = new StringTokenizer(tok.nextToken(), "/");

                                if (tok2.countTokens() == 3) { /* v/t/n */
                                    v = Integer.parseInt(tok2.nextToken());
                                    t = Integer.parseInt(tok2.nextToken());
                                    n = Integer.parseInt(tok2.nextToken());
                                    triangles[numtriangles_aux].vindices[0] = v;
                                    triangles[numtriangles_aux].tindices[0] = t;
                                    triangles[numtriangles_aux].nindices[0] = n;

                                    tok2 = new StringTokenizer(tok.nextToken(), "/");
                                    v = Integer.parseInt(tok2.nextToken());
                                    t = Integer.parseInt(tok2.nextToken());
                                    n = Integer.parseInt(tok2.nextToken());
                                    triangles[numtriangles_aux].vindices[1] = v;
                                    triangles[numtriangles_aux].tindices[1] = t;
                                    triangles[numtriangles_aux].nindices[1] = n;

                                    tok2 = new StringTokenizer(tok.nextToken(), "/");
                                    v = Integer.parseInt(tok2.nextToken());
                                    t = Integer.parseInt(tok2.nextToken());
                                    n = Integer.parseInt(tok2.nextToken());
                                    triangles[numtriangles_aux].vindices[2] = v;
                                    triangles[numtriangles_aux].tindices[2] = t;
                                    triangles[numtriangles_aux].nindices[2] = n;

                                    group.triangles[group.numtriangles++] = numtriangles_aux;
                                    numtriangles_aux++;

                                    while (tok.hasMoreTokens()) {
                                        triangles[numtriangles_aux] = new Triangle();
                                        tok2 = new StringTokenizer(tok.nextToken(), "/");
                                        v = Integer.parseInt(tok2.nextToken());
                                        t = Integer.parseInt(tok2.nextToken());
                                        n = Integer.parseInt(tok2.nextToken());

                                        triangles[numtriangles_aux].vindices[0] = triangles[numtriangles_aux - 1].vindices[0];
                                        triangles[numtriangles_aux].tindices[0] = triangles[numtriangles_aux - 1].tindices[0];
                                        triangles[numtriangles_aux].nindices[0] = triangles[numtriangles_aux - 1].nindices[0];
                                        triangles[numtriangles_aux].vindices[1] = triangles[numtriangles_aux - 1].vindices[2];
                                        triangles[numtriangles_aux].tindices[1] = triangles[numtriangles_aux - 1].tindices[2];
                                        triangles[numtriangles_aux].nindices[1] = triangles[numtriangles_aux - 1].nindices[2];
                                        triangles[numtriangles_aux].vindices[2] = v;
                                        triangles[numtriangles_aux].tindices[2] = t;
                                        triangles[numtriangles_aux].nindices[2] = n;
                                        group.triangles[group.numtriangles++] = numtriangles_aux;
                                        numtriangles_aux++;
                                    }
                                } else if (tok2.countTokens() == 2) {  /* v/t */
                                    v = Integer.parseInt(tok2.nextToken());
                                    t = Integer.parseInt(tok2.nextToken());
                                    triangles[numtriangles_aux].vindices[0] = v;
                                    triangles[numtriangles_aux].tindices[0] = t;

                                    tok2 = new StringTokenizer(tok.nextToken(), "/");
                                    v = Integer.parseInt(tok2.nextToken());
                                    t = Integer.parseInt(tok2.nextToken());
                                    triangles[numtriangles_aux].vindices[1] = v;
                                    triangles[numtriangles_aux].tindices[1] = t;

                                    tok2 = new StringTokenizer(tok.nextToken(), "/");
                                    v = Integer.parseInt(tok2.nextToken());
                                    t = Integer.parseInt(tok2.nextToken());
                                    triangles[numtriangles_aux].vindices[2] = v;
                                    triangles[numtriangles_aux].tindices[2] = t;

                                    group.triangles[group.numtriangles++] = numtriangles_aux;
                                    numtriangles_aux++;

                                    while (tok.hasMoreTokens()) {
                                        triangles[numtriangles_aux] = new Triangle();
                                        tok2 = new StringTokenizer(tok.nextToken(), "/");
                                        v = Integer.parseInt(tok2.nextToken());
                                        t = Integer.parseInt(tok2.nextToken());

                                        triangles[numtriangles_aux].vindices[0] = triangles[numtriangles_aux - 1].vindices[0];
                                        triangles[numtriangles_aux].tindices[0] = triangles[numtriangles_aux - 1].tindices[0];
                                        triangles[numtriangles_aux].vindices[1] = triangles[numtriangles_aux - 1].vindices[2];
                                        triangles[numtriangles_aux].tindices[1] = triangles[numtriangles_aux - 1].tindices[2];
                                        triangles[numtriangles_aux].vindices[2] = v;
                                        triangles[numtriangles_aux].tindices[2] = t;
                                        group.triangles[group.numtriangles++] = numtriangles_aux;
                                        numtriangles_aux++;
                                    }
                                } else {/* v */
                                    tok = new StringTokenizer(line, " ");

                                    triangles[numtriangles_aux].vindices[0] = Integer.parseInt(tok.nextToken());
                                    triangles[numtriangles_aux].vindices[1] = Integer.parseInt(tok.nextToken());

                                    if (tok.hasMoreTokens()) {
                                        triangles[numtriangles_aux].vindices[2] = Integer.parseInt(tok.nextToken());
                                    } else {
                                        triangles[numtriangles_aux].vindices[2] = triangles[numtriangles_aux].vindices[0];
                                    }

                                    group.triangles[group.numtriangles++] = numtriangles_aux;
                                    numtriangles_aux++;

                                    while (tok.hasMoreTokens()) {
                                        triangles[numtriangles_aux] = new Triangle();
                                        v = Integer.parseInt(tok.nextToken());

                                        triangles[numtriangles_aux].vindices[0] = triangles[numtriangles_aux - 1].vindices[0];
                                        triangles[numtriangles_aux].vindices[1] = triangles[numtriangles_aux - 1].vindices[2];
                                        triangles[numtriangles_aux].vindices[2] = v;
                                        group.triangles[group.numtriangles++] = numtriangles_aux;
                                        numtriangles_aux++;
                                    }
                                }
                            }

                            break;
                        default:
                            /* eat up rest of line */
                            break;
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            throw new IOException(ex.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(JWavefrontModel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    /**
     * Find a group in the model.
     * @param name Group name.
     * @return Return the first group with the given name.
     */
    private Group findGroup(String name) {
        Group group = groups;
        while (group != null) {
            if (name.toLowerCase().equals(group.name.toLowerCase())) {
                break;
            }
            group = group.next;
        }
        return group;
    }

    /**
     * Add a group to the model.
     * @param name Group name.
     */
    private Group addGroup(String name) {
        Group group = findGroup(name);

        if (group == null) {
            group = new Group();
            group.name = name;
            group.material = 0;
            group.numtriangles = 0;
            group.triangles = null;
            group.next = groups;
            groups = group;
            numgroups++;
        }

        return group;
    }

    /**
     * Find a material in the model.
     * @param name Material name.
     * @return Return the first material with the given name.
     */
    private int findMaterial(String name) {
        /* XXX doing a linear search on a string key'd list is pretty lame,
        but it works and is fast enough for now. */
        for (int i = 0; i < nummaterials; i++) {
            if (materials[i].name.toLowerCase().equals(name.toLowerCase())) {
                return i;
            }
        }

        /* didn't find the name, so print a warning and return the default
        material (0). */
        Logger.getLogger(JWavefrontModel.class.getName()).log(Level.WARNING,
                "findMaterial() cannot find material {0}", name);

        return 0;
    }

    /**
     * Find a texture in the model.
     * @param name Texture name.
     * @return Return the first texture with the given name.
     */
    private int findTexture(String name) throws IOException {
        /* XXX doing a linear search on a string key'd list is pretty lame,
        but it works and is fast enough for now. */
        for (int i = 0; i < numtextures; i++) {
            if (textures[i].name.toLowerCase().equals(name.toLowerCase())) {
                return i;
            }
        }

        /* didn't find the name, so print a warning and return -1 */
        Logger.getLogger(JWavefrontModel.class.getName()).log(Level.WARNING,
                "findTexture() cannot find texture {0}", name);

        return -1;
    }

    /**
     * First pass at a Wavefront OBJ file that gets all the
     * statistics of the model (such as #vertices, #normals, etc).
     * @param file The file containing the Wavefront model.
     * @throws IOException
     */
    private void firstPass(File file) throws IOException {
        Group group;			/* current group */
        StringTokenizer tok;
        BufferedReader in = null;

        /* make a default group */
        group = addGroup("default");

        try {
            in = new BufferedReader(new FileReader(file));

            //Capturing the scalar names
            numvertices = numnormals = numtexcoords = numtriangles = 0;
            String line = null;
            while ((line = in.readLine()) != null) {
                line = line.trim();

                if (line.length() > 0) {
                    switch (line.charAt(0)) {
                        case '#':				/* comment */
                            /* eat up rest of line */
                            break;
                        case 'v':				/* v, vn, vt */
                            switch (line.charAt(1)) {
                                case ' ':			/* vertex */
                                    /* eat up rest of line */
                                    numvertices++;
                                    break;
                                case 'n':				/* normal */
                                    /* eat up rest of line */
                                    numnormals++;
                                    break;
                                case 't':				/* texcoord */
                                    /* eat up rest of line */
                                    numtexcoords++;
                                    break;
                                default:
                                    throw new IOException("firstPass(): Unknown token " + line);
                            }
                            break;
                        case 'm':
                            tok = new StringTokenizer(line, " ");
                            tok.nextToken(); //ignores mtllib
                            mtllibname = tok.nextToken();
                            readMTL(mtllibname);
                            break;
                        case 'u':
                            /* eat up rest of line */
                            break;
                        case 'g':
                        case 'o':				/* group */
                            /* eat up rest of line */
                            tok = new StringTokenizer(line, " ");
                            tok.nextToken();
                            if (tok.hasMoreTokens()) {
                                group = addGroup(tok.nextToken());
                            } else {
                                group = addGroup("_blank_");
                            }
                            break;
                        case 'f':				/* face */
                            line = line.trim().substring(1).trim(); //removing f
                            tok = new StringTokenizer(line, " ");

                            if (tok.countTokens() > 2) {
                                numtriangles += tok.countTokens() - 2;
                                group.numtriangles += tok.countTokens() - 2;
                            } else {
                                numtriangles++;
                                group.numtriangles++;
                            }

                        default:
                            /* eat up rest of line */
                            break;
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            throw new IOException(ex.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    Logger.getLogger(JWavefrontModel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        /* allocate memory for the triangles in each group */
        group = groups;
        while (group != null) {
            group.triangles = new int[group.numtriangles];
            group.numtriangles = 0;
            group = group.next;
        }

        /* assuring that there is at least one material available */
        if (nummaterials == 0) {
            nummaterials = 1;
            materials = new Material[nummaterials];
            materials[0] = new Material();
            materials[0].name = "default";
            materials[0].shininess = 65.0f;
            materials[0].diffuse[0] = 0.8f;
            materials[0].diffuse[1] = 0.8f;
            materials[0].diffuse[2] = 0.8f;
            materials[0].diffuse[3] = 1.0f;
            materials[0].ambient[0] = 0.2f;
            materials[0].ambient[1] = 0.2f;
            materials[0].ambient[2] = 0.2f;
            materials[0].ambient[3] = 1.0f;
            materials[0].specular[0] = 0.0f;
            materials[0].specular[1] = 0.0f;
            materials[0].specular[2] = 0.0f;
            materials[0].specular[3] = 1.0f;
        }
    }

    /**
     * Read a wavefront material library file. 
     * @param name The filename of the material file
     * @throws IOException
     */
    protected void readMTL(String name) throws IOException {
        int nummaterials_aux, numtextures_aux;
        File file = new File(pathname.getParent() + "/" + name);

        if (file.exists()) {
            BufferedReader in = null;
            StringTokenizer tok = null;

            try {
                in = new BufferedReader(new FileReader(file));

                /* count the number of materials in the file */
                HashSet<String> textures_names = new HashSet<String>();
                String line = null;
                nummaterials = 1;
                numtextures = 0;
                while ((line = in.readLine()) != null) {
                    line = line.trim();
                    if (line.length() > 0) {
                        switch (line.charAt(0)) {
                            case '#':				/* comment */
                                /* eat up rest of line */
                                break;
                            case 'n':				/* newmtl */
                                nummaterials++;
                                break;
                            case 'm':                               /* map_Kd */
                                tok = new StringTokenizer(line, " ");
                                String token = tok.nextToken();
                                if (token.equals("map_Kd")) {
                                    token = tok.nextToken();
                                    if (!textures_names.contains(token)) {
                                        textures_names.add(token);
                                        numtextures++;
                                    }
                                }
                                break;
                            default:
                                /* eat up rest of line */
                                break;
                        }
                    }
                }
            } catch (FileNotFoundException ex) {
                throw new IOException(ex.getMessage());
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ex) {
                        Logger.getLogger(JWavefrontModel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            materials = new Material[nummaterials];

            /* set the default material */
            for (int i = 0; i < nummaterials; i++) {
                materials[i] = new Material();
                materials[i].name = null;
                materials[i].shininess = 65.0f;
                materials[i].diffuse[0] = 0.8f;
                materials[i].diffuse[1] = 0.8f;
                materials[i].diffuse[2] = 0.8f;
                materials[i].diffuse[3] = 1.0f;
                materials[i].ambient[0] = 0.2f;
                materials[i].ambient[1] = 0.2f;
                materials[i].ambient[2] = 0.2f;
                materials[i].ambient[3] = 1.0f;
                materials[i].specular[0] = 0.0f;
                materials[i].specular[1] = 0.0f;
                materials[i].specular[2] = 0.0f;
                materials[i].specular[3] = 1.0f;
            }
            materials[0].name = "default";

            if (numtextures > 0) {
                textures = new Texture[numtextures];
                for (int i = 0; i < numtextures; i++) {
                    textures[i] = new Texture();
                    textures[i].name = "";
                    textures[i].texturedata = null;
                }
            }

            try {
                in = new BufferedReader(new FileReader(file));

                /* now, read in the data */
                nummaterials_aux = 0;
                numtextures_aux = 0;
                String line = null;
                while ((line = in.readLine()) != null) {
                    line = line.trim();

                    if (line.length() > 0) {
                        switch (line.charAt(0)) {
                            case '#':				/* comment */
                                /* eat up rest of line */
                                break;
                            case 'n':				/* newmtl */
                                tok = new StringTokenizer(line, " ");
                                tok.nextToken(); //ignores newmtl
                                nummaterials_aux++;
                                materials[nummaterials_aux].name = tok.nextToken();
                                break;
                            case 'N':
                                switch (line.charAt(1)) {
                                    case 'i':				/* comment */
                                        /* eat up rest of line */
                                        break;
                                    case 's':
                                        tok = new StringTokenizer(line, " ");
                                        tok.nextToken(); //ignores Ns
                                        materials[nummaterials_aux].shininess = Float.parseFloat(tok.nextToken());

                                        /* wavefront shininess is from [0, 1000], so scale for OpenGL */
                                        materials[nummaterials_aux].shininess /= 1000.0f;
                                        materials[nummaterials_aux].shininess *= 128.0f;
                                }
                                break;

                            case 'K':
                                switch (line.charAt(1)) {
                                    case 'd':
                                        tok = new StringTokenizer(line, " ");
                                        tok.nextToken(); //ignores Kd
                                        materials[nummaterials_aux].diffuse[0] = Float.parseFloat(tok.nextToken());
                                        materials[nummaterials_aux].diffuse[1] = Float.parseFloat(tok.nextToken());
                                        materials[nummaterials_aux].diffuse[2] = Float.parseFloat(tok.nextToken());
                                        break;
                                    case 's':
                                        tok = new StringTokenizer(line, " ");
                                        tok.nextToken(); //ignores Ks
                                        materials[nummaterials_aux].specular[0] = Float.parseFloat(tok.nextToken());
                                        materials[nummaterials_aux].specular[1] = Float.parseFloat(tok.nextToken());
                                        materials[nummaterials_aux].specular[2] = Float.parseFloat(tok.nextToken());
                                        break;
                                    case 'a':
                                        tok = new StringTokenizer(line, " ");
                                        tok.nextToken(); //ignores Ka
                                        materials[nummaterials_aux].ambient[0] = Float.parseFloat(tok.nextToken());
                                        materials[nummaterials_aux].ambient[1] = Float.parseFloat(tok.nextToken());
                                        materials[nummaterials_aux].ambient[2] = Float.parseFloat(tok.nextToken());
                                        break;
                                    default:
                                        /* eat up rest of line */
                                        break;
                                }
                                break;
                            case 'm':                  /* map_Kd */
                                tok = new StringTokenizer(line, " ");
                                String token = tok.nextToken(); //ignores map_Kd

                                if (token.equals("map_Kd")) {
                                    name = tok.nextToken();

                                    //loading the texture data
                                    int texindex = findTexture(name);
                                    if (texindex < 0) {
                                        file = new File(pathname.getParent() + "/" + name);
                                        if (file.exists()) {
                                            BufferedImage image = ImageIO.read(file);
                                            ImageUtil.flipImageVertically(image); //vertically flip the image
                                            textures[numtextures_aux].name = name;
                                            textures[numtextures_aux].texturedata = TextureIO.newTextureData(image, false);
                                            texindex = numtextures_aux;
                                            numtextures_aux++;
                                        }
                                    }

                                    materials[nummaterials_aux].texindex = texindex;
                                }

                                break;
                            default:
                                /* eat up rest of line */
                                break;
                        }
                    }
                }
            } catch (FileNotFoundException ex) {
                throw new IOException(ex.getMessage());
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ex) {
                        Logger.getLogger(JWavefrontModel.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } else {
            Logger.getLogger(JWavefrontModel.class.getName()).log(Level.WARNING,
                    "readMTL() warning: mtl file not found ({0})", file.getName());
        }
    }

    /**
     * Writes a model description in Wavefront format to a file (not implemented).
     * @param filename Name of the file to write the model.
     * @param mode a bitwise or of values describing what is written to the file
     *            WF_NONE    -  write only vertices
     *            WF_FLAT    -  write facet normals
     *            WF_SMOOTH  -  write vertex normals
     *            WF_TEXTURE -  write texture coords
     *            WF_FLAT and WF_SMOOTH should not both be specified.
     */
    public void writeOBJ(String filename, int mode) {
    }

    /**
     * Renders the model to the current OpenGL context using the mode specified.
     * @param gLAutoDrawable The OpenGL context to draw.
     * @param mode a bitwise OR of values describing what is to be rendered.
     *            WF_NONE    -  write only vertices
     *            WF_FLAT    -  write facet normals
     *            WF_SMOOTH  -  write vertex normals
     *            WF_TEXTURE -  write texture coords
     *            WF_FLAT and WF_SMOOTH should not both be specified.
     */
    private void draw(GLAutoDrawable gLAutoDrawable, int mode) {
        assert (vertices != null);

        GL gl = gLAutoDrawable.getGL();

        Group group;
        Triangle triangle;
        Material material;

        /* do a bit of warning */
        if ((mode & WF_FLAT) > 0 && facetnorms == null) {
            Logger.getLogger(JWavefrontModel.class.getName()).log(Level.WARNING,
                    "draw() warning: flat render mode requested "
                    + "with no facet normals defined ({0})", pathname.getName());
            mode &= ~WF_FLAT;
        }

        if ((mode & WF_SMOOTH) > 0 && normals == null) {
            Logger.getLogger(JWavefrontModel.class.getName()).log(Level.WARNING,
                    "draw() warning: smooth render mode requested "
                    + "with no normals defined ({0})", pathname.getName());
            mode &= ~WF_SMOOTH;
        }

        if ((mode & WF_TEXTURE) > 0 && texcoords == null) {
            Logger.getLogger(JWavefrontModel.class.getName()).log(Level.WARNING,
                    "draw() warning: texture render mode requested "
                    + "with no texture coordinates defined ({0})", pathname.getName());
            mode &= ~WF_TEXTURE;
        }

        if ((mode & WF_FLAT) > 0 && (mode & WF_SMOOTH) > 0) {
            Logger.getLogger(JWavefrontModel.class.getName()).log(Level.WARNING,
                    "draw() warning: flat render mode requested "
                    + "and smooth render mode requested - ({0})", pathname.getName());
            mode &= ~WF_FLAT;
        }

        if ((mode & WF_COLOR) > 0 && materials == null) {
            Logger.getLogger(JWavefrontModel.class.getName()).log(Level.WARNING,
                    "draw() warning: color render mode requested "
                    + "with no materials defined ({0})", pathname.getName());
            mode &= ~WF_COLOR;
        }

        if ((mode & WF_MATERIAL) > 0 && materials == null) {
            Logger.getLogger(JWavefrontModel.class.getName()).log(Level.WARNING,
                    "draw() warning: material render mode requested "
                    + "with no materials defined ({0})", pathname.getName());
            mode &= ~WF_MATERIAL;
        }

        if ((mode & WF_COLOR) > 0 && (mode & WF_MATERIAL) > 0) {
            Logger.getLogger(JWavefrontModel.class.getName()).log(Level.WARNING,
                    "draw() warning: color and material render mode requested "
                    + "using only material mode ({0})", pathname.getName());
            mode &= ~WF_COLOR;
        }

        if ((mode & WF_COLOR) > 0) {
            gl.glEnable(GL.GL_COLOR_MATERIAL);
        } else if ((mode & WF_MATERIAL) > 0) {
            gl.glDisable(GL.GL_COLOR_MATERIAL);
        }

        //activating tne texture
        if ((mode & WF_TEXTURE) > 0) {
            gl.glEnable(GL.GL_TEXTURE_2D);
        } else {
            gl.glDisable(GL.GL_TEXTURE_2D);
        }

        /* perhaps this loop should be unrolled into material, color, flat,
        smooth, etc. loops?  since most cpu's have good branch prediction
        schemes (and these branches will always go one way), probably
        wouldn't gain too much?  */

        group = groups;
        while (group != null) {
            if ((mode & WF_TEXTURE) > 0) {
                material = materials[group.material];

                if (material.texindex >= 0) {
                    TextureData texturedata = textures[material.texindex].texturedata;
                    com.sun.opengl.util.texture.Texture texture = TextureIO.newTexture(texturedata);
                    texture.setTexParameteri(GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
                    texture.setTexParameteri(GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
                    texture.bind();
                }
            }

            if ((mode & WF_MATERIAL) > 0) {
                material = materials[group.material];
                gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_AMBIENT, material.ambient, 0);
                gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_DIFFUSE, material.diffuse, 0);
                gl.glMaterialfv(GL.GL_FRONT_AND_BACK, GL.GL_SPECULAR, material.specular, 0);
                gl.glMaterialf(GL.GL_FRONT_AND_BACK, GL.GL_SHININESS, material.shininess);
            }

            if ((mode & WF_COLOR) > 0) {
                material = materials[group.material];
                gl.glColor3fv(material.diffuse, 0);
            }

            gl.glBegin(GL.GL_TRIANGLES);
            for (int i = 0; i < group.numtriangles; i++) {
                triangle = triangles[group.triangles[i]];

                if ((mode & WF_FLAT) > 0) {
                    gl.glNormal3fv(facetnorms, (3 * triangle.findex));
                }

                if ((mode & WF_SMOOTH) > 0) {
                    gl.glNormal3fv(normals, (3 * triangle.nindices[0]));
                }

                if ((mode & WF_TEXTURE) > 0) {
                    if ((2 * triangle.tindices[0]) < texcoords.length) {
                        gl.glTexCoord2fv(texcoords, (2 * triangle.tindices[0]));
                    }
                }
                gl.glVertex3fv(vertices, (3 * triangle.vindices[0]));

                if ((mode & WF_SMOOTH) > 0) {
                    gl.glNormal3fv(normals, (3 * triangle.nindices[1]));
                }

                if ((mode & WF_TEXTURE) > 0) {
                    if ((2 * triangle.tindices[1]) < texcoords.length) {
                        gl.glTexCoord2fv(texcoords, (2 * triangle.tindices[1]));
                    }
                }
                gl.glVertex3fv(vertices, (3 * triangle.vindices[1]));

                if ((mode & WF_SMOOTH) > 0) {
                    gl.glNormal3fv(normals, (3 * triangle.nindices[2]));
                }

                if ((mode & WF_TEXTURE) > 0) {
                    if ((2 * triangle.tindices[2]) < texcoords.length) {
                        gl.glTexCoord2fv(texcoords, (2 * triangle.tindices[2]));
                    }
                }
                gl.glVertex3fv(vertices, (3 * triangle.vindices[2]));
            }

            gl.glEnd();

            group = group.next;
        }
    }

    /**
     * Method used to draw an object. The method compile() should be previously
     * called to create the disply list.
     * @param gLAutoDrawable The OpenGL context to draw.
     */
    public void draw(GLAutoDrawable gLAutoDrawable) {
        if (objectid != -1) {
            GL gl = gLAutoDrawable.getGL();
            gl.glCallList(objectid);
        } else {
            Logger.getLogger(JWavefrontModel.class.getName()).log(Level.SEVERE,
                    "draw() error: method compile() should be previously "
                    + "called to create the proper disply list.");
        }
    }

    /**
     * Generates a display list for the model using the mode specified. The
     * model is drawn using the method draw().
     * @param gLAutoDrawable The OpenGL context to draw.
     * @param mode A bitwise OR of values describing what is to be rendered.
     *            WF_NONE    -  write only vertices
     *            WF_FLAT    -  write facet normals
     *            WF_SMOOTH  -  write vertex normals
     *            WF_TEXTURE -  write texture coords
     *            WF_FLAT and WF_SMOOTH should not both be specified.
     */
    public void compile(GLAutoDrawable gLAutoDrawable, int mode) {
        GL gl = gLAutoDrawable.getGL();
        objectid = gl.glGenLists(1);
        gl.glNewList(objectid, GL.GL_COMPILE);
        draw(gLAutoDrawable, mode);
        gl.glEndList();
    }

    /**
     * Eliminate (weld) vectors that are within an epsilon of each other (not
     * implemented).
     * @param epsilon maximum difference between vertices ( 0.00001 is a good
     * start for a unitized model).
     * @return
     */
    public int weld(float epsilon) {
        return 0;
    }

    /**
     * Print on the default output information about the model.
     * @param complete Indicates if the it should print the complete information
     * about the model or not.
     */
    public void dump(boolean complete) {
        Group group;
        Material material;

        System.out.println("Model: " + pathname.getName());
        System.out.println("Number groups: " + numgroups);
        System.out.println("Number normals: " + numnormals);
        System.out.println("Number texture coordinates: " + numtexcoords);
        System.out.println("Number triangles: " + numtriangles);
        System.out.println("Number vertices: " + numvertices);

        if (complete) {
            group = groups;
            while (group != null) {
                System.out.println("---");
                System.out.println("Group: " + group.name);
                System.out.println("Number triangles: " + group.numtriangles);

                material = materials[group.material];
                System.out.println("Ka: " + material.ambient[0] + ","
                        + material.ambient[1] + "," + material.ambient[2]);
                System.out.println("Kd: " + material.diffuse[0] + ","
                        + material.diffuse[1] + "," + material.diffuse[2]);
                System.out.println("Ks: " + material.specular[0] + ","
                        + material.specular[1] + "," + material.specular[2]);
                System.out.println("Ns: " + material.shininess);

                if (material.texindex >= 0) {
                    System.out.println("map_Kd: " + textures[material.texindex].name);
                }

                group = group.next;
            }
        }
    }

    /**
     * Compute the dot product of two vectors.
     * @param u First vector.
     * @param index_u Initial index of the first vector (it reads 3 positions
     * successvely).
     * @param v Second vector.
     * @param index_v Initial index of the second vector (it reads 3 positions
     * successvely).
     * @return
     */
    private float dot(float[] u, int index_u, float[] v, int index_v) {
        assert (u != null);
        assert (v != null);
        return u[index_u] * v[index_v] + u[index_u + 1] * v[index_v + 1] + u[index_u + 2] * v[index_v + 2];
    }

    /**
     * Compute the cross product of two vectors
     * @param u First vector.
     * @param v Second vector.
     * @param n The output vector.
     * @param index_n Initial index of the new vector (it writes 3 positions
     * successvely).
     */
    private void cross(float[] u, float[] v, float[] n, int index_n) {
        assert (u != null);
        assert (v != null);
        assert (n != null);

        n[index_n] = u[1] * v[2] - u[2] * v[1];
        n[1 + index_n] = u[2] * v[0] - u[0] * v[2];
        n[2 + index_n] = u[0] * v[1] - u[1] * v[0];
    }

    /**
     * Normalize a vector.
     * @param v Vector to be normalized
     * @param index Initial index of the vector (it writes 3 positions
     * successvely).
     */
    private void normalize(float[] v, int index) {
        assert (v != null);
        float l = (float) Math.sqrt(v[index] * v[index] + v[1 + index] * v[1 + index] + v[2 + index] * v[2 + index]);
        v[0 + index] /= l;
        v[1 + index] /= l;
        v[2 + index] /= l;
    }

    /**
     * Class that defines a material in a model.
     */
    protected class Material {

        String name;				// name of material
        float diffuse[] = new float[4];		// diffuse component
        float ambient[] = new float[4];		// ambient component
        float specular[] = new float[4];	// specular component
        float shininess;			// specular exponent
        int texindex = -1;
    }

    /**
     * Class that defines a triangle in a model.
     */
    protected class Triangle {

        int vindices[] = new int[3];	// array of triangle vertex indices
        int nindices[] = new int[3];	// array of triangle normal indices
        int tindices[] = new int[3];	// array of triangle texcoord indices
        int findex;			// index of triangle facet normal
    }

    /**
	 * Class that defines a group in a model.
	 */
    protected class Group {

        String name;		// name of this group
        int numtriangles;	// number of triangles in this group
        int triangles[];	// array of triangle indices
        int material;           // index to material for group
        /**
		 */
        Group next;		// pointer to next group in model
    }

    /**
     * Class that defines a texture in a model.
     */
    protected class Texture {

        String name;
        TextureData texturedata; //texture
    }

    /**
	 * General purpose node
	 */
    protected class Node {

        int index;
        boolean averaged;
        /**
		 */
        Node next;
    }
    /**
	 * Object id identifying the display list.
	 */
    protected int objectid;
    /**
	 * Path to this model.
	 */
    protected File pathname;
    /**
	 * Name of the material library
	 */
    protected String mtllibname;
    /**
	 * Number of vertices in model
	 */
    protected int numvertices;
    /**
	 * Array of vertices
	 */
    protected float vertices[];
    /**
	 * Number of normals in model
	 */
    protected int numnormals;
    /**
	 * Array of normals
	 */
    protected float normals[];
    /**
	 * Number of texcoords in model
	 */
    protected int numtexcoords;
    /**
	 * Array of texture coordinates
	 */
    protected float texcoords[];
    /**
	 * Number of facetnorms in model
	 */
    protected int numfacetnorms;
    /**
	 * Array of facetnorms
	 */
    protected float facetnorms[];
    /**
	 * Number of triangles in model
	 */
    protected int numtriangles;
    /**
	 * Array of triangles
	 */
    protected Triangle triangles[];
    /**
	 * Number of materials in model
	 */
    protected int nummaterials;
    /**
	 * Array of materials
	 */
    protected Material materials[];
    /**
	 * Number of textures in model
	 */
    protected int numtextures;
    /**
	 * Array of textures
	 */
    protected Texture textures[];
    /**
	 * Number of groups in model
	 */
    protected int numgroups;
    /**
	 * Linked list of groups
	 */
    protected Group groups;
    /**
	 * Position of the model
	 */
    protected float position[];
}
