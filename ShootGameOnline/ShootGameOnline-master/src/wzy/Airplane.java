package wzy;

import java.util.Random;

public class Airplane extends FlyingObject implements Enemy {
	private int score;

	public Airplane() {
		image = ShootGame.getAirplane();
		ember = ShootGame.getAirplaneEmber();
		width = image.getWidth();
		height = image.getHeight();
		Random rand = new Random();
		x = rand.nextInt(ShootGame.width - width);
		y = -height;
		speed = 3;
		score = 5;
	}

	public Airplane(int x) {
		image = ShootGame.getAirplane();
		ember = ShootGame.getAirplaneEmber();
		width = image.getWidth();
		height = image.getHeight();
		this.x = (x - width) >= 0 ? (x - width) : x;
		y = -height;
		speed = 3;
		score = 5;
	}

	@Override
	public int getScore() {
		return score;
	}

	@Override
	void step() {
		y += speed;
	}

	@Override
	public boolean outOfBounds() {
		return y > ShootGame.height;
	}
}
