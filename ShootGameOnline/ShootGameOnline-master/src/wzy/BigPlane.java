package wzy;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class BigPlane extends FlyingObject implements Award, Enemy {

	private int	life;
	private int	score;
	private int	awardType;

	public BigPlane() {
		try {
			image = ImageIO.read(new File("img/bigplane.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ember = ShootGame.getBigPlaneEmber();
		width = image.getWidth();
		height = image.getHeight();
		Random rand = new Random();
		x = rand.nextInt(ShootGame.width - width);
		y = -height;
		speed = 1;
		life = 10;
		score = 50;
		awardType = Award.ALL;
	}

	public BigPlane(int x) {
		try {
			image = ImageIO.read(new File("img/bigplane.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ember = ShootGame.getBigPlaneEmber();
		width = image.getWidth();
		height = image.getHeight();
		this.x = (x - width) >= 0 ? (x - width) : x;
		y = -height;
		speed = 1;
		life = 5;
		score = 50;
		awardType = Award.ALL;
	}

	public int getLife() {
		return life;
	}

	@Override
	public int getScore() {
		return score;
	}

	@Override
	public int getType() {
		return awardType;
	}

	@Override
	void step() {
		y += speed;
	}

	@Override
	public boolean outOfBounds() {
		return y > ShootGame.height;
	}

	public BufferedImage getImage() {
		Graphics g = image.getGraphics();
		g.setColor(new Color(0xC3C8C9));
		g.fillRect(0, 0, 20, 20);
		g.setColor(Color.black);
		g.drawString("L:" + life, 0, 20);
		return image;
	}

	@Override
	public boolean shootBy(Bullet b) {
		if (super.shootBy(b)) {
			life--;
			return true;
		}
		return false;
	}

}
