package br.usp.gl.effects;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.media.opengl.GL4;

import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

public class Texture2D extends Effect {
	
	private BufferedImage image;
	public BufferedImage getImage() {return image;}
	
	private Texture texture;
	
	private int id;
	private int no;
	
	private int wrapAction;
	private int magFilter;
	private int minFilter;
	
	public Texture2D(String imageFile, int id, int no) {
		
		this(imageFile, id, no, GL4.GL_REPEAT);
	}
	
	public Texture2D(String imageFile, int id, int no, int wrapAction) {
		
		this(imageFile, id, no, wrapAction, 
				GL4.GL_LINEAR, GL4.GL_LINEAR_MIPMAP_NEAREST);
	}
	
	public Texture2D(String imageFile, int id, int no, int wrapAction,
			int magFilter, int minFilter) {
		
		try {
			image = ImageIO.read(new File(imageFile)); 
			ImageUtil.flipImageVertically(image); 
			
		} catch (Exception e) {
			System.err.println("Failed to load texture: " + imageFile);
			e.printStackTrace();
		}
		
		this.id = id;
		this.no = no;
		
		this.wrapAction = wrapAction;
		
		this.magFilter = magFilter;
		this.minFilter = minFilter;
	}
	
	@Override
	public void init(GL4 gl, int handle) {
		
		super.init(gl, handle);
		
		texture = AWTTextureIO.newTexture(gl.getGLProfile(), image, true);
		
		texture.bind(gl);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MAG_FILTER, magFilter);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_MIN_FILTER, minFilter);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_S, wrapAction);
		gl.glTexParameteri(GL4.GL_TEXTURE_2D, GL4.GL_TEXTURE_WRAP_T, wrapAction);
	}
	
	@Override
	public void bind() {
		
		gl.glUniform1i(handle, no);
		gl.glActiveTexture(id);
		texture.bind(gl);		
	}
}
