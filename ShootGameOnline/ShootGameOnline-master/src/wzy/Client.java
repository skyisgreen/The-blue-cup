package wzy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client {

	private String					nickName;
	private Socket					socket;
	private ShootGame				game;
	private BlockingQueue<String>	dataQueue;
	private BlockingQueue<String>	dataQueueReceived;

	public Client(String addr, int port) {
		try {
			System.out.println("正在连接服务端...");
			socket = new Socket(addr, port);
			System.out.println("已连接服务端！");
			dataQueueReceived = new LinkedBlockingQueue<String>();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("", e);
		}
	}

	private String inputNickName(Scanner scan) throws Exception {
		String nickName = null;
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);
		BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));

		while (true) {
			System.out.println("请输入昵称:");
			nickName = scan.nextLine();
			// nickName = "11wenwen";
			if (nickName.trim().equals("")) {
				System.out.println("昵称不能为空");
			} else {
				pw.println(nickName);
				String pass = br.readLine();
				if (pass != null && !pass.equals("OK")) {
					System.out.println("昵称已被占用，请更换。");
				} else {
					System.out.println("你好!" + nickName + ",开始快乐吧!");
					return nickName;
				}
			}
		}
	}

	public void start() {
		try {
			Scanner scan = new Scanner(System.in);
			nickName = inputNickName(scan);
			game = new ShootGame(nickName, dataQueueReceived);
			dataQueue = game.dataQueue;
			dataQueue.offer(ShootGame.HERO + "," + nickName);
			game.start(game);
			ServerHandler handler = new ServerHandler();
			new Thread(handler).start();
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("", e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("", e);
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					while (true) {
						OutputStream out = socket.getOutputStream();
						OutputStreamWriter osw = new OutputStreamWriter(out, "utf-8");
						PrintWriter pw = new PrintWriter(osw, true);

						while (true) {
							if (!dataQueue.isEmpty()) {
								pw.println(dataQueue.poll());
							} else {
								Thread.sleep(10);
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException("", e);
				}
			}
		}).start();
	}

	private class ServerHandler implements Runnable {

		@Override
		public void run() {
			try {
				InputStream in = socket.getInputStream();
				InputStreamReader isr = new InputStreamReader(in, "utf-8");
				BufferedReader br = new BufferedReader(isr);
				String data = null;
				while (true) {
					data = br.readLine();
					dataQueueReceived.offer(data);
				}
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("", e);
			}
		}
	}

	public static void main(String[] args) {
		// Client client = new Client("fubixingwzy.6655.la", 35171);
		Client client = new Client("localhost", 8088);
		client.start();
	}
}
