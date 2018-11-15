package wzy;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ShootGame extends JPanel {

	/**
	 * 
	 */
	private static final long		serialVersionUID	= 1L;
	// 游戏窗口宽
	public static int				width;
	// 游戏窗口高
	public static int				height;
	// 背景宽
	private static int				imageWidth;
	// 背景高
	private static int				imageHeight;
	private static int				heroWidth;
	private static int				heroHeight;

	private static BufferedImage	airplane;
	private static BufferedImage[]	airplaneEmber		= new BufferedImage[4];
	private static BufferedImage	background;
	private static BufferedImage	bee;
	private static BufferedImage[]	beeEmber			= new BufferedImage[4];
	private static BufferedImage	bigPlane;
	private static BufferedImage[]	bigPlaneEmber		= new BufferedImage[4];
	private static BufferedImage	bullet;
	private static BufferedImage	hero0;
	private static BufferedImage	hero1;
	private static BufferedImage[]	heroEmber			= new BufferedImage[4];
	/*
	 * 游戏运行状态
	 */
	public static final int			START				= 0;
	public static final int			RUNNING				= 1;
	public static final int			PAUSE				= 2;
	public static final int			GAME_OVER			= 3;

	public static int				level				= 1;
	public int						state				= 0;
	// 玩家数
	private int						player;
	// 目前存活的英雄机
	private int						alive;
	private Timer					timer;
	private int						interval;
	JFrame							frame;
	private String					nickName;
	/*
	 * 数据类型
	 */
	// 新英雄机
	public static final int			HERO				= 0;
	// 英雄机坐标
	public static final int			HERO_DATA			= 1;
	// 新飞行物
	public static final int			FLYINGS				= 2;
	// 状态
	public static final int			STATE				= 3;
	// 窗口尺寸
	public static final int			SIZE				= 4;

	public static final int			DEAD				= 5;

	// 要发送的数据
	public BlockingQueue<String>	dataQueue;
	// 从服务端接收的打包好的数据
	private BlockingQueue<String>	dataQueueReceived;
	// 其它英雄机
	private Map<String, Hero>		heroMap;
	private Map<String, Hero>		runtimeHeroMap;
	// 坐标
	private BlockingQueue<String>	heroDataQueue;
	// 飞行物
	private BlockingQueue<String>	flyingsDataQueue;

	static {
		try {
			airplane = ImageIO.read(new File("img/airplane.png"));
			background = ImageIO.read(new File("img/background.png"));
			bee = ImageIO.read(new File("img/bee.png"));
			bullet = ImageIO.read(new File("img/bullet.png"));
			hero0 = ImageIO.read(new File("img/hero0.png"));
			hero1 = ImageIO.read(new File("img/hero1.png"));
			for (int i = 0; i < 4; i++) {
				airplaneEmber[i] = ImageIO.read(new File("img/airplane_ember" + i + ".png"));
				beeEmber[i] = ImageIO.read(new File("img/bee_ember" + i + ".png"));
				bigPlaneEmber[i] = ImageIO.read(new File("img/bigplane_ember" + i + ".png"));
				heroEmber[i] = ImageIO.read(new File("img/hero_ember" + i + ".png"));
			}
			imageWidth = background.getWidth();
			imageHeight = background.getHeight();
			width = imageWidth;
			height = imageHeight;
			heroWidth = hero0.getWidth();
			heroHeight = hero0.getHeight();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("", e);
		}
	}

	/**
	 * 构造方法：初始化数据
	 * 
	 * @param nickName
	 * 
	 * @param dataQueueReceived
	 *            Client从Server接收到的数据
	 */
	public ShootGame(String nickName, BlockingQueue<String> dataQueueReceived) {
		this.nickName = nickName;
		hero = new Hero(nickName);
		// 将Client从Server接收到的数据引用过来
		this.dataQueueReceived = dataQueueReceived;
		dataQueue = new LinkedBlockingQueue<String>();
		heroMap = new HashMap<String, Hero>();
		runtimeHeroMap = new HashMap<String, Hero>();
		heroDataQueue = new LinkedBlockingQueue<String>();
		flyingsDataQueue = new LinkedBlockingQueue<String>();
		state = 0;
		alive = 0;
		timer = new Timer();
		// 定时器间隔
		interval = 10;
	}

	public static BufferedImage getAirplane() {
		return airplane;
	}

	public static BufferedImage[] getAirplaneEmber() {
		return airplaneEmber;
	}

	public static BufferedImage getBee() {
		return bee;
	}

	public static BufferedImage[] getBeeEmber() {
		return beeEmber;
	}

	public static BufferedImage getBigPlane() {
		return bigPlane;
	}

	public static BufferedImage[] getBigPlaneEmber() {
		return bigPlaneEmber;
	}

	public static BufferedImage getBullet() {
		return bullet;
	}

	public static BufferedImage getHero0() {
		return hero0;
	}

	public static BufferedImage getHero1() {
		return hero1;
	}

	public static BufferedImage[] getHeroEmber() {
		return heroEmber;
	}

	Hero				hero;
	List<Hero>			heroes	= new ArrayList<Hero>();
	List<FlyingObject>	flyings	= new ArrayList<FlyingObject>();
	List<Bullet>		bullets	= new ArrayList<Bullet>();
	List<Ember>			embers	= new ArrayList<Ember>();

	private int			mmTime	= 0;
	private int			sTime	= 0;
	private int			mTime	= 0;
	private int			hTime	= 0;

	/*
	 * 时间
	 */
	void timeAction() {
		mmTime++;
		if (mmTime % 100 == 0) {
			sTime++;
			if (sTime % 59 == 0) {
				mTime++;
				if (mTime % 59 == 0) {
					hTime++;
					mTime = 0;
				}
				sTime = 0;
			}
			mmTime = 0;
		}
	}

	// 飞行物入场计数器
	private int flyEnteredIndex = 0;

	/**
	 * 飞行物入场
	 */
	private void enterAction() {
		// 根据窗口尺寸调节飞行物生成速率：越宽越多
		if (flyEnteredIndex++ % (20000 / ShootGame.width) == 0) {
			// 如果接收了新飞行物数据
			if (!flyingsDataQueue.isEmpty()) {
				String[] data = flyingsDataQueue.poll().split(",");
				// 生成飞行物的横坐标
				int x = Integer.parseInt(data[1]);
				// 生成飞行物的类型
				int type = Integer.parseInt(data[2]);
				if (type < 10) {
					flyings.add(new BigPlane(x));
				} else if (type < 30) {
					flyings.add(new Bee(x));
				} else {
					flyings.add(new Airplane(x));
				}
			}
		}
	}

	/**
	 * 飞行物走一步
	 */
	private void stepAction() {
		if (hero != null) {
			hero.step();
		}
		for (Hero h : runtimeHeroMap.values()) {
			if (h != null) {
				h.step();
			}
		}
		for (FlyingObject f : flyings) {
			f.step();
		}
		for (Bullet b : bullets) {
			b.step(flyings);
		}
	}

	// 英雄机发射子弹计数器
	private int shootIndex = 0;

	/**
	 * 英雄机发射子弹
	 */
	private void shootAction() {
		if (shootIndex++ % 30 == 0) {
			if (hero != null) {
				bullets.addAll(hero.shoot());
			}
			for (Hero hero : runtimeHeroMap.values()) {
				if (hero != null) {
					bullets.addAll(hero.shoot());
				}
			}
		}
	}

	/**
	 * 子弹打飞机
	 */
	void bangAction() {
		// 遍历每一发一弹
		Iterator<Bullet> iB = bullets.iterator();
		while (iB.hasNext()) {
			Bullet b = iB.next();
			String bNickName = b.getNickName();
			// 遍历每一架敌机
			Iterator<FlyingObject> iF = flyings.iterator();
			while (iF.hasNext()) {
				FlyingObject f = iF.next();
				// 如果敌机被子弹打中了
				if (f.shootBy(b)) {
					// 判断是不是BOSS机
					if (f instanceof BigPlane) {
						BigPlane p = (BigPlane) f;
						// 判断BOSS机是否还有生命
						if (p.getLife() > 0) {
							// 有的话，让子弹消失，
							iB.remove();
							// 更新BOSS机的生命值
							p.setImage(p.getImage());
							// 由于还有生命，所以直接进行下一次遍历
							break;
						}
					}
					// 判断是不是敌人
					if (f instanceof Enemy) {
						// 加分
						if (bNickName.equals(nickName) && hero != null) {
							hero.addScore(((Enemy) f).getScore());
						} else if (runtimeHeroMap.get(bNickName) != null) {
							runtimeHeroMap.get(bNickName).getScore();
						}
					}
					// 判断有没有奖励
					if (f instanceof Award) {
						// 获得奖励类型
						int awardType = ((Award) f).getType();
						switch (awardType) {
						case Award.DOUBLE_FIRE:
							if (bNickName.equals(nickName) && hero != null) {
								hero.addDoubleFire();
							} else if (runtimeHeroMap.get(bNickName) != null) {
								runtimeHeroMap.get(bNickName).addDoubleFire();
							}
							break;
						case Award.LIFE:
							if (bNickName.equals(nickName) && hero != null) {
								hero.addLife();
							} else if (runtimeHeroMap.get(bNickName) != null) {
								runtimeHeroMap.get(bNickName).addLife();
							}
							break;
						case Award.ALL:
							if (bNickName.equals(nickName) && hero != null) {
								hero.addLife();
								hero.addDoubleFire();
							} else if (runtimeHeroMap.get(bNickName) != null) {
								runtimeHeroMap.get(bNickName).addLife();
								runtimeHeroMap.get(bNickName).addDoubleFire();
							}
							break;
						}
					}
					// 删除子弹和敌机
					iB.remove();
					iF.remove();
					// 生成灰烬
					Ember e = new Ember(f);
					embers.add(e);
					break;
				}
			}
		}
	}

	/**
	 * 生成灰烬
	 */
	void emberAction() {
		// 遍历所有还没有烧完的灰烬
		Iterator<Ember> i = embers.iterator();
		while (i.hasNext()) {
			Ember e = i.next();
			// 断续烧一下，如果烧完了，则删除
			if (e.burnDown()) {
				i.remove();
			}
		}
	}

	/**
	 * 飞行物越界检查
	 */
	void outOfBounds() {
		// 遍历子弹
		Iterator<Bullet> iB = bullets.iterator();
		while (iB.hasNext()) {
			// 如果越界则删除
			if (iB.next().outOfBounds()) {
				iB.remove();
			}
		}
		// 遍历飞行物
		Iterator<FlyingObject> iF = flyings.iterator();
		while (iF.hasNext()) {
			// 如果越界则删除
			if (iF.next().outOfBounds()) {
				iF.remove();
			}
		}
	}

	/**
	 * 检查游戏是否结束
	 */
	void checkGameOverAction() {
		// 如果结束，更改游戏状态
		if (isGameOver()) {
			state = GAME_OVER;
			dataQueue.offer(STATE + "," + nickName + "," + state);
		}
	}

	/**
	 * 判断游戏是否结束
	 * 
	 * @return true 结束
	 *         false 没有结束
	 */
	boolean isGameOver() {
		// 遍历敌机
		Iterator<FlyingObject> i = flyings.iterator();
		while (i.hasNext()) {
			FlyingObject f = i.next();
			// 如果英雄机被敌机撞到
			if (hero != null) {
				if (hero.hit(f)) {
					i.remove();
					hero.subtractLife();
					hero.subtractDoubleFire();
					Ember e = new Ember(hero);
					embers.add(e);
					// 如果英雄机没有生命则删除英雄机
					if (hero.getLife() <= 0) {
						hero = null;
						dataQueue.offer(DEAD + "," + nickName);
					}
				}
			}
			// 遍历其它玩家的英雄机
			for (Hero h : runtimeHeroMap.values()) {
				// 如果英雄机被敌机撞到
				if (h != null) {
					if (h.hit(f)) {
						i.remove();
						h.subtractLife();
						h.subtractDoubleFire();
						Ember e = new Ember(h);
						embers.add(e);
					}
				}
			}
		}
		//
		return hero == null && runtimeHeroMap.size() == 0;
	}

	/**
	 * 画英雄机
	 * 
	 * @param g
	 */
	private void paintHero(Graphics g) {
		if (hero != null) {
			g.drawImage(hero.image, hero.x, hero.y, null);
		}
		for (Hero h : runtimeHeroMap.values()) {
			if (h != null) {
				g.drawImage(h.image, h.x, h.y, null);
			}
		}
	}

	/**
	 * 画飞机物
	 * 
	 * @param g
	 */
	private void paintFlyingObject(Graphics g) {
		try {
			for (FlyingObject f : flyings) {
				g.drawImage(f.image, f.x, f.y, null);
			}
			for (FlyingObject b : bullets) {
				g.drawImage(b.image, b.x, b.y, null);
			}
		} catch (Exception e) {}
	}

	/**
	 * 画灰烬
	 * 
	 * @param g
	 */
	void paintEmber(Graphics g) {
		try {
			for (Ember e : embers) {
				g.drawImage(e.getImage(), e.getX(), e.getY(), null);
			}
		} catch (Exception e) {}
	}

	/**
	 * 画游戏信息
	 * 
	 * @param g
	 */
	void paintInfo(Graphics g) {
		g.setColor(new Color(0x3A3B3B));
		g.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
		g.drawString("LEVEL:" + level, 10, 20);
		g.drawString("SCORE:" + (hero == null ? 0 : hero.getScore()), 10, 40);
		g.drawString("LIFE:" + (hero == null ? 0 : hero.getLife()), 10, 60);
		g.drawString("BULLETS:" + (hero == null ? 0 : hero.isDoubleFire()), 10, 80);
		g.drawString("ALIVE:" + alive, 10, 100);
		g.drawString("TIME:" + hTime + ":" + mTime + ":" + sTime, 10, height - 50);
	}

	/**
	 * 画所有的东西
	 */
	@Override
	public void paint(Graphics g) {
		// 画平铺背景
		// width = this.getWidth();
		// height = this.getHeight();
		for (int x = 0; x < width; x += imageWidth) {
			for (int y = 0; y < height; y += imageHeight) {
				g.drawImage(background, x, y, this);
			}
		}
		paintHero(g);
		paintFlyingObject(g);
		paintEmber(g);
		paintInfo(g);
	}

	// 拆包数据：分析服务端发送过来的数据，按不同的类型存入不同的集合中
	private void action() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					// 如果接收到数据
					if (!dataQueueReceived.isEmpty()) {
						// 取出一条数据
						String data = dataQueueReceived.poll();
						// 分解
						String[] metaData = data.split(",");
						// 如果数据类型是加入了新英雄机
						if (Integer.parseInt(metaData[0]) == ShootGame.HERO) {
							// 如果英雄机不存在，则加入当前游戏当中
							if (!(nickName.equals(metaData[1])) && !heroMap.containsKey(metaData[1])) {
								// key:昵称，value：新英雄机
								runtimeHeroMap.put(metaData[1], new Hero(metaData[1]));
								heroMap.put(metaData[1], new Hero(metaData[1]));
								// 其它玩家人数
								player = heroMap.size();
								// 当前存活玩家
								alive++;
							}
							// 如果数据类型是英雄机的坐标变化
						} else if (Integer.parseInt(metaData[0]) == ShootGame.HERO_DATA) {
							heroDataQueue.offer(data);
							// 如果数据类型是新生成的敌机
						} else if (Integer.parseInt(metaData[0]) == ShootGame.FLYINGS) {
							flyingsDataQueue.offer(data);
							// 如果数据类型是游戏的状态
						} else if (Integer.parseInt(metaData[0]) == ShootGame.STATE && !nickName.equals(metaData[1])) {
							state = Integer.parseInt(metaData[2]);
							if (state == START) {
								mmTime = 0;
								sTime = 0;
								mTime = 0;
								hTime = 0;
								level = 1;
								hero = new Hero(nickName);
								Set<Entry<String, Hero>> entries = heroMap.entrySet();
								for (Entry<String, Hero> entry : entries) {
									runtimeHeroMap.put(entry.getKey(), entry.getValue());
								}
								flyings.clear();
								bullets.clear();
								embers.clear();
								alive = player;
							}
							// if (Integer.parseInt(metaData[2]) ==
							// START) {
							// state = Integer.parseInt(metaData[2]);
							// } else if
							// (Integer.parseInt(metaData[2]) ==
							// RUNNING && state == START) {
							// state = Integer.parseInt(metaData[2]);
							// } else if
							// (Integer.parseInt(metaData[2]) ==
							// RUNNING &&
							// metaData[1].equals(lastNickName)) {
							// state = Integer.parseInt(metaData[2]);
							// } else if
							// (Integer.parseInt(metaData[2]) == PAUSE
							// && state == RUNNING) {
							// lastNickName = metaData[1];
							// } else if
							// (Integer.parseInt(metaData[2]) ==
							// GAME_OVER) {
							// state = Integer.parseInt(metaData[2]);
							// }

							// 如果数据类型是游戏窗口的尺寸变化
						} else if (Integer.parseInt(metaData[0]) == ShootGame.SIZE && !nickName.equals(metaData[1])) {
							frame.setSize(Integer.parseInt(metaData[2]), Integer.parseInt(metaData[3]));
							// 如果数据类型是玩家死亡消息
						} else if (Integer.parseInt(metaData[0]) == ShootGame.DEAD && !nickName.equals(metaData[1])) {
							runtimeHeroMap.remove(metaData[1]);
						}
					} else {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
							throw new RuntimeException("", e);
						}
					}
				}
			}
		}).start();
		// 处理接收的数据
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					// 英雄机的坐标变化了
					if (!heroDataQueue.isEmpty()) {
						String[] data = heroDataQueue.poll().split(",");
						// 如果有这架英雄机的话(以昵称检查)
						if (runtimeHeroMap.get(data[1]) != null) {
							// x,y坐标
							runtimeHeroMap.get(data[1]).x = Integer.parseInt(data[2]) - heroWidth / 2;
							runtimeHeroMap.get(data[1]).y = Integer.parseInt(data[3]) - heroHeight / 2;
						}
					} else {
						try {
							Thread.sleep(10);
						} catch (InterruptedException e) {
							e.printStackTrace();
							throw new RuntimeException("", e);
						}
					}
				}
			}
		}).start();
		/**
		 * 监听鼠标事件
		 */
		MouseAdapter l = new MouseAdapter() {

			// 单击
			@Override
			public void mouseClicked(MouseEvent e) {
				if (state == START) {
					state = RUNNING;
					// 发送游戏的状态数据
					dataQueue.offer(STATE + "," + nickName + "," + state);
				}
				if (state == GAME_OVER) {
					mmTime = 0;
					sTime = 0;
					mTime = 0;
					hTime = 0;
					level = 1;
					hero = new Hero(nickName);
					Set<Entry<String, Hero>> entries = heroMap.entrySet();
					for (Entry<String, Hero> entry : entries) {
						runtimeHeroMap.put(entry.getKey(), entry.getValue());
					}
					flyings.clear();
					bullets.clear();
					embers.clear();
					alive = player;
					state = START;
					// 发送游戏的状态数据
					dataQueue.offer(STATE + "," + nickName + "," + state);
				}
			}

			// 移动
			@Override
			public void mouseMoved(MouseEvent e) {
				if (state == RUNNING) {
					int x = e.getX();
					int y = e.getY();
					if (hero != null) {
						hero.moveTo(x, y);
					}
					// 发送英雄机的坐标数据
					dataQueue.offer(HERO_DATA + "," + nickName + "," + x + "," + y);
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if (state == RUNNING) {
					state = PAUSE;
					// 发送游戏的状态数据
					dataQueue.offer(STATE + "," + nickName + "," + state);
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (state == PAUSE) {
					state = RUNNING;
					// 发送游戏的状态数据
					dataQueue.offer(STATE + "," + nickName + "," + state);
				}
			}
		};
		// 给当前游戏加上鼠标监听器
		this.addMouseListener(l);
		this.addMouseMotionListener(l);
		// 定时器
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (state == RUNNING) {
					timeAction();// 时钟
					enterAction();// 飞行物入场
					stepAction();// 飞行物走一步
					shootAction();// 英雄机发射子弹
					outOfBounds();// 越界检查
					bangAction();// 子弹打中敌机
					emberAction();// 灰烬
					checkGameOverAction();// 检查游戏是否结束
				}
				repaint();
			}
		}, interval, interval);
	}

	public void start(ShootGame game) {
		frame = new JFrame();
		frame.add(game);
		frame.setSize(width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);// 居中
		frame.setVisible(true);
		game.action();
		// 监听当前窗口大小的改变
		frame.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				width = e.getComponent().getWidth();
				height = e.getComponent().getHeight();
				// 发送更改后的游戏窗口尺寸数据
				dataQueue.offer(SIZE + "," + nickName + "," + width + "," + height);
			}
		});
	}
}
