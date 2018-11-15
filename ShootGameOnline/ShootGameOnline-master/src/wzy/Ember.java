package wzy;

import java.awt.image.BufferedImage;

public class Ember {
	private BufferedImage[]	images		= {};
	private int				index;
	private int				i;
	private BufferedImage	image;
	private int				interval	= 10;
	private int				x;
	private int				y;

	public Ember(FlyingObject object) {
		images = object.ember;
		image = object.image;
		x = object.x;
		y = object.y;
		index = 0;
		i = 0;
	}

	public boolean burnDown() {
		if (i++ % interval == 0) {
			if (index == images.length) {
				return true;
			}
			image = images[index++];
		}
		return false;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public BufferedImage getImage() {
		return image;
	}
}
