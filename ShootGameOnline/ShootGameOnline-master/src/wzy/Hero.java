package wzy;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class Hero extends FlyingObject {

	private String			nickName;
	private int				score;
	private int				doubleFire;
	private int				life;
	private BufferedImage[]	images;
	private int				index;

	public Hero(String nickName) {
		this.nickName = nickName;
		image = ShootGame.getHero0();
		images = new BufferedImage[] { ShootGame.getHero0(), ShootGame.getHero1() };
		index = 0;
		ember = ShootGame.getHeroEmber();
		width = image.getWidth();
		height = image.getHeight();
		x = ShootGame.width / 2 - width / 2;
		y = 500;
		speed = 3;
		score = 0;
		doubleFire = 0;
		life = 3;
	}

	public String getNickName() {
		return nickName;
	}

	public int getScore() {
		return score;
	}

	public void addScore(int score) {
		this.score += score;
	}

	public int isDoubleFire() {
		return doubleFire;
	}

	public void addDoubleFire() {
		doubleFire += 40;
	}

	public void setDoubleFire(int doubleFire) {
		this.doubleFire = doubleFire;
	}

	public void subtractDoubleFire() {
		doubleFire = doubleFire - 100 >= 0 ? doubleFire - 100 : 0;
	}

	public int getLife() {
		return life;
	}

	public void addLife() {
		life++;
	}

	public void subtractLife() {
		life--;
	}

	@Override
	void step() {
		image = images[index++ / 10 % images.length];
	}

	public void moveTo(int x, int y) {
		this.x = x - width / 2;
		this.y = y - height / 2;
	}

	public List<Bullet> shoot() {
		int xStep = width / 4;
		int yStep = 20;
		int xSpeed = 3;
		int ySpeed = -3;
		int type = 0;
		int power = (int) Math.log(doubleFire / 40);
		List<Bullet> bullets = new LinkedList<Bullet>();
		if (power > 0) {
			bullets.add(new Bullet(nickName, x + xStep * 2, y - yStep, xSpeed, ySpeed - power, 1));
			bullets.add(new Bullet(nickName, x + xStep * 2, y - yStep, (type > 0) ? xSpeed : 0, ySpeed, type));
			for (int i = 1; i < power; i++) {
				bullets.add(new Bullet(nickName, x + xStep * 2, y - yStep, -1 * i, ySpeed, type));
				bullets.add(new Bullet(nickName, x + xStep * 2, y - yStep, i, ySpeed, type));
			}
			doubleFire -= 2 * power;
			return bullets;
		} else {
			bullets.add(new Bullet(nickName, x + xStep * 2, y - yStep, 0, ySpeed, 0));
			doubleFire = doubleFire > 0 ? --doubleFire : 0;
			return bullets;
		}
	}

	@Override
	public boolean outOfBounds() {
		return x < 0 || x > ShootGame.width - width || y < 0 || y > ShootGame.height - height;
	}

	public boolean hit(FlyingObject f) {
		int x1 = x - f.width;
		int x2 = x + width;
		int y1 = y - f.height;
		int y2 = y + height;
		return f.x >= x1 && f.x <= x2 && f.y >= y1 && f.y <= y2;

	}
}
