
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Image;


public class Sprite {
	

	private Image image;
	
	//create new sprite
	public Sprite(Image image) {
		this.image = image;
	}
	

	public int getWidth() {
		return image.getWidth(null);
	}

	public int getHeight() {
		return image.getHeight(null);
	}
	
	
	public void draw(Graphics g,int x,int y) {
		g.drawImage(image,x,y,null);
	}
}

class SpriteStore {
	
	private static SpriteStore single = new SpriteStore();
	
	//use singleton implementation
	public static SpriteStore get() {
		return single;
	}
	
	/** The cached sprite map, from reference to sprite instance */
	private HashMap sprites = new HashMap();
	
	
	public Sprite getSprite(String ref) {
		// if we've already got the sprite in the cache

		// then just return the existing version

		if (sprites.get(ref) != null) {
			return (Sprite) sprites.get(ref);
		}
		
		// otherwise, go away and grab the sprite from the resource

		// loader

		BufferedImage sourceImage = null;
		
		try {
		

			URL url = this.getClass().getClassLoader().getResource(ref);
			
			if (url == null) {
				fail("Can't find ref: "+ref);
			}
			
			// use ImageIO to read the image in

			sourceImage = ImageIO.read(url);
		} catch (IOException e) {
			fail("Failed to load: "+ref);
		}
		
		// create an accelerated image of the right size to store our sprite in

		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		Image image = gc.createCompatibleImage(sourceImage.getWidth(),sourceImage.getHeight(),Transparency.BITMASK);
		
		// draw our source image into the accelerated image

		image.getGraphics().drawImage(sourceImage,0,0,null);
		
		// create a sprite, add it the cache then return it

		Sprite sprite = new Sprite(image);
		sprites.put(ref,sprite);
		
		return sprite;
	}
	
	
	private void fail(String message) {
		

		System.err.println(message);
		System.exit(0);
	}
}