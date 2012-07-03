package br.usp.gl.shaders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import javax.media.opengl.GL3;

import com.jogamp.common.nio.Buffers;

public class ShaderProgram {

	private static final int[] TYPES = new int[] {
		GL3.GL_VERTEX_SHADER,
		GL3.GL_TESS_CONTROL_SHADER,
		GL3.GL_TESS_EVALUATION_SHADER,
		GL3.GL_GEOMETRY_SHADER,
		GL3.GL_FRAGMENT_SHADER
	};
	
	private static final String[] SHADERS = new String[] {
		"vertex",
		"tessControl",
		"tessEval",
		"geometry",
		"fragment"
	};
	
	private String sourcesFolder;

	private GL3 gl = null;

	private int handle = -1;

	private String[] sources;

	public ShaderProgram(String sourcesFolder) {
		
		this.sourcesFolder = sourcesFolder;
	}
	
	public void init(final GL3 gl) {

		this.gl = gl;
		
		readSources();
		
		int[] shadersHandles = new int[sources.length];
		
		for (int i = 0; i < sources.length; i++) {
			shadersHandles[i] = (sources[i] != null) ? compileSource(sources[i], TYPES[i]) : -1;
		}

		handle = linkShaders(shadersHandles);
		
		for (int i = 0; i < shadersHandles.length; i++) {
			if (shadersHandles[i] >= 0) {
				gl.glDeleteShader(shadersHandles[i]);
			}
		}
	}
	
	public void readSources() {
		
		System.out.println("Reading shaders from: " + sourcesFolder);

		sources = new String[TYPES.length];

		for (int i = 0; i < sources.length; i++) {
			String resource = sourcesFolder + SHADERS[i] + ".glsl";
			try {
				InputStream source = new FileInputStream(new File(resource));
				System.out.println("\t" + SHADERS[i] + " shader found.");
				sources[i] = readSource(source);
			} catch (Exception e) {
				System.err.println("\t" + SHADERS[i] + " shader not found.");
			}
		}
	}

	public void bind() {

		gl.glUseProgram(handle);
	}

	public void dispose() {

		gl.glDeleteProgram(handle);
	}
	
	public int getUniformLocation(String varName) {
		
		int location = gl.glGetUniformLocation(handle, varName);
		if (location < 0) {
			System.err.println(varName + " uniform not found.");
			return -1;
		} else {
			System.out.println(varName + " uniform found.");
			return location;
		}
	}
	
	public int getAttribLocation(String varName) {
		int location = gl.glGetAttribLocation(handle, varName);
		if (location < 0) {
			System.err.println(varName + " attribute not found");
			return -1;
		} else {
			System.out.println(varName + " attribute found");
			return location;
		}
	}

	private String readSource(final InputStream inputStream) {

		final StringBuilder source = new StringBuilder();
		final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

		try {
			String line;
			while ((line = bufferedReader.readLine()) != null)
				source.append(line + "\n");

			bufferedReader.close();
		} catch (final IOException e) {
			System.err.println("Invalid shader file.");
		}
		return source.toString();
	}

	private int compileSource(final String source, final int type) {

		final IntBuffer buffer = Buffers.newDirectIntBuffer(1);
		final int handle = gl.glCreateShader(type);

		gl.glShaderSource(handle, 1, new String[] { source }, null, 0);
		gl.glCompileShader(handle);

		gl.glGetShaderiv(handle, GL3.GL_COMPILE_STATUS, buffer);

		if (buffer.get(0) == 1)
			return handle;
		else {
			gl.glGetShaderiv(handle, GL3.GL_INFO_LOG_LENGTH, buffer);

			final ByteBuffer byteBuffer = Buffers.newDirectByteBuffer(buffer
					.get(0));
			gl.glGetShaderInfoLog(handle, byteBuffer.capacity(), buffer,
					byteBuffer);

			System.err.println("\nshader compile error: ");
			for (int i = 0; i < buffer.get(0); i++) {
				System.err.print((char) byteBuffer.get(i));
			}

			return -1;
		}
	}

	private int linkShaders(int[] shadersHandles) {

		final int programHandle = gl.glCreateProgram();

		for (int i = 0; i < shadersHandles.length; i++) {
			if (shadersHandles[i] >= 0) {
				gl.glAttachShader(programHandle, shadersHandles[i]);
			}
		}
		
		gl.glLinkProgram(programHandle);
		gl.glValidateProgram(programHandle);

		final IntBuffer buffer = Buffers.newDirectIntBuffer(1);
		gl.glGetProgramiv(programHandle, GL3.GL_VALIDATE_STATUS, buffer);

		if (buffer.get(0) == 1)
			return programHandle;
		else {

			gl.glGetProgramiv(programHandle, GL3.GL_INFO_LOG_LENGTH, buffer);

			final ByteBuffer byteBuffer = Buffers.newDirectByteBuffer(buffer
					.get(0));
			gl.glGetProgramInfoLog(programHandle, byteBuffer.capacity(), buffer,
					byteBuffer);

			System.err.println("\nshader link error: ");
			for (int i = 0; i < buffer.get(0); i++) {
				System.err.print((char) byteBuffer.get(i));
			}

			return -1;
		}
	}
}