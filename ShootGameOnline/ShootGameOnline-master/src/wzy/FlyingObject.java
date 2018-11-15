package wzy;

import java.awt.image.BufferedImage;

public abstract class FlyingObject {
	protected BufferedImage		image;
	protected BufferedImage[]	ember;
	protected int				width;
	protected int				height;
	protected int				x;
	protected int				y;
	protected int				speed;

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	abstract void step();

	public abstract boolean outOfBounds();

	public boolean shootBy(Bullet b) {
		return b.x >= x && b.x <= x + width && b.y > y && b.y <= y + height;
	}
}
