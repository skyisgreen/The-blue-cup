package wzy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class Server {
	private ServerSocket				serverSocket;
	private Map<String, PrintWriter>	allOut;
	private Executor					threadPool;
	private BlockingQueue<String>		dataQueue;
	List<Hero>							heroes;
	private Timer						timer;
	private int							interval;
	private Random						rand	= new Random();
	private int							state;
	private int							width;

	public Server(int port) {
		try {
			serverSocket = new ServerSocket(port);
			allOut = new HashMap<String, PrintWriter>();
			threadPool = Executors.newFixedThreadPool(30);
			dataQueue = new LinkedBlockingQueue<String>();
			heroes = new ArrayList<Hero>();
			timer = new Timer();
			interval = 10;
			width = ShootGame.width;
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("", e);
		}
	}

	private synchronized void addOut(String nickName, PrintWriter pw) {
		allOut.put(nickName, pw);
	}

	private synchronized void removeOut(String nickName) {
		allOut.remove(nickName);
	}

	private synchronized void sendData(String data) {
		for (PrintWriter out : allOut.values()) {
			out.println(data);
		}
	}

	private void start() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					while (true) {
						System.out.println("等待客户端连接...");
						Socket socket = serverSocket.accept();
						System.out.println("客户端已连接!");
						ClientHandler handler = new ClientHandler(socket);
						threadPool.execute(handler);
					}
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException("", e);
				}
			}
		}).start();
		// 生成数据
		new Thread(new Runnable() {
			int flyEnteredIndex = 0;

			@Override
			public void run() {
				timer.schedule(new TimerTask() {
					@Override
					public void run() {
						if (state == ShootGame.RUNNING) {
							if (flyEnteredIndex++ % (20000 / width) == 0) {
								int x = rand.nextInt(width);
								int type = rand.nextInt(100);
								dataQueue.offer(ShootGame.FLYINGS + "," + x + "," + type);
							}
						}
					}
				}, interval, interval);
			}
		}).start();
		// 发送数据
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					if (!dataQueue.isEmpty()) {
						sendData(dataQueue.poll());
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
	}

	private class ClientHandler implements Runnable {
		private String	nickName;
		private Socket	socket;

		public ClientHandler(Socket socket) {
			this.socket = socket;
		}

		private String getNickName() throws IOException {
			try {
				OutputStream out = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(out, "utf-8");
				PrintWriter pw = new PrintWriter(osw, true);

				InputStream in = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(in, "utf-8");
				BufferedReader br = new BufferedReader(isr);

				String nickName = br.readLine();
				while (true) {
					if (nickName.trim().equals("")) {
						pw.println("FAIL");
					}
					if (allOut.containsKey(nickName)) {
						pw.println("FAIL");
					} else {
						pw.println("OK");
						return nickName;
					}
					nickName = br.readLine();
				}
			} catch (IOException e) {
				throw e;
			}
		}

		@Override
		public void run() {
			try {
				OutputStream out = socket.getOutputStream();
				OutputStreamWriter osw = new OutputStreamWriter(out, "utf-8");
				PrintWriter pw = new PrintWriter(osw, true);
				nickName = getNickName();
				addOut(nickName, pw);
				sendData(ShootGame.HERO + "," + nickName);

				InputStream in = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(in, "utf-8");
				BufferedReader br = new BufferedReader(isr);

				String line = null;
				while ((line = br.readLine()) != null) {
					String[] data = line.split(",");
					if (Integer.parseInt(data[0]) == ShootGame.HERO) {
						Set<String> set = allOut.keySet();
						for (String nickName : set) {
							dataQueue.offer(ShootGame.HERO + "," + nickName);
						}
					} else if (Integer.parseInt(data[0]) == ShootGame.STATE) {
						state = Integer.parseInt(data[2]);
					} else if (Integer.parseInt(data[0]) == ShootGame.SIZE) {
						width = Integer.parseInt(data[2]);
					}
					dataQueue.offer(line);
				}
			} catch (IOException e) {

			} finally {
				removeOut(nickName);
				if (socket != null) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
						throw new RuntimeException("", e);
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		Server server = new Server(8088);
		server.start();
	}
}
