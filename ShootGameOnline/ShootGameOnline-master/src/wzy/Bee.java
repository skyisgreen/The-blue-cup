package wzy;

import java.util.Random;

public class Bee extends FlyingObject implements Award {
	private int	xSpeed;
	private int	ySpeed;
	private int	awardType;

	public Bee() {
		image = ShootGame.getBee();
		ember = ShootGame.getBeeEmber();
		width = image.getWidth();
		height = image.getHeight();
		Random rand = new Random();
		x = rand.nextInt(ShootGame.width - width);
		y = -height;
		speed = 1;
		xSpeed = (rand.nextInt(2) == 1 ? -speed : speed);
		ySpeed = 2;
		awardType = rand.nextInt(2);
	}

	public Bee(int x) {
		image = ShootGame.getBee();
		ember = ShootGame.getBeeEmber();
		width = image.getWidth();
		height = image.getHeight();
		this.x = (x - width) >= 0 ? (x - width) : x;
		y = -height;
		speed = 1;
		xSpeed = (x % 2 == 1 ? -speed : speed);
		ySpeed = 2;
		awardType = x % 2;
	}

	@Override
	public int getType() {
		return awardType;
	}

	@Override
	void step() {
		x += xSpeed;
		if (x >= ShootGame.width - width) {
			xSpeed = -speed;
		}
		if (x <= 0) {
			xSpeed = speed;
		}
		y += ySpeed;
	}

	@Override
	public boolean outOfBounds() {
		return y > ShootGame.height;
	}
}
