package wzy;

import java.util.List;

public class Bullet extends FlyingObject {
	private String	nickName;
	private int		xSpeed;
	private int		ySpeed;
	private int		type;
	private boolean	enemy;
	private boolean	bomb;

	public Bullet(String nickName, int x, int y, int xSpeed, int ySpeed, int type) {
		this.nickName = nickName;
		image = ShootGame.getBullet();
		width = image.getWidth();
		height = image.getHeight();
		this.x = x - width / 2;
		this.y = y;
		this.xSpeed = xSpeed;
		this.ySpeed = ySpeed;
		this.type = type;
		enemy = false;
	}

	public String getNickName() {
		return nickName;
	}

	public boolean getEnemy() {
		return enemy;
	}

	public void setEnemy() {
		enemy = true;
	}

	public void setBomb(boolean bomb) {
		this.bomb = bomb;
	}

	public boolean ibBomb() {
		return bomb;
	}

	@Override
	public void step() {
		x += xSpeed;
		y += ySpeed;
	}

	public void step(List<FlyingObject> flyings) {
		if (type >= 1) {
			if (flyings.size() > 0) {
				FlyingObject nearest = flyings.get(0);
				int x = (int) Math.pow(nearest.x - this.x, 2);
				int y = (int) Math.pow(nearest.y - this.y, 2);
				int distance = (int) Math.sqrt(x + y);
				for (FlyingObject f : flyings) {
					x = (int) Math.pow(this.x - f.x, 2);
					y = (int) Math.pow(this.y - f.y, 2);
					int newDistance = (int) Math.sqrt(x + y);
					if (newDistance <= distance) {
						nearest = f;
					}
				}
				guide(nearest);
			} else {
				x += 0;
				y += ySpeed;
			}
		} else {
			x += xSpeed;
			y += ySpeed;
		}
	}

	public void guide(FlyingObject f) {
		int x = f.x + f.image.getWidth() / 2;
		int y = f.y + f.image.getHeight() / 2;
		this.x = this.x + (x - this.x) / 100 + (x > this.x ? 1 : -1) * xSpeed;
		this.y = this.y + (y - this.y) / 100 + (y > this.y ? -1 : 1) * ySpeed;
	}

	@Override
	public boolean outOfBounds() {
		return y <= -height || y >= ShootGame.height || x >= ShootGame.width || x <= -width;
	}
}
