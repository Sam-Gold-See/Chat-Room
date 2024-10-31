import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
	private static Socket socket;
	private static final Scanner scanner = new Scanner(System.in);
	private static BufferedWriter bw;
	private static BufferedReader br;

	public static void main(String[] args) throws IOException {
		init();

		frame();
	}

	private static void init() throws IOException {
		socket = new Socket("127.0.0.1", 8888);
		System.out.println("服务器已经连接成功");
		bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	private static void frame() {
		while (true) {
			System.out.println("====================欢迎来到SGS聊天室====================");
			System.out.println("1.登录");
			System.out.println("2.注册");
			System.out.println("3.退出");
			System.out.println("请输入您的选择：");

			String command = scanner.nextLine();
			switch (command) {
				case "1" -> login();
				case "2" -> register();
				case "3" -> exit();
				default -> {
					System.out.println("本操作指令不存在，请重新输入");
					frame();
				}
			}
		}
	}

	private static void login() {
		socketWrite("login");
		String[] userinfo = userinfoInput();
		socketWrite("username=" + userinfo[0] + "&password=" + userinfo[1]);
		String result = socketRead();
		if (!result.equals("true"))
			System.out.println("用户名或密码输入有误");
		else {
			System.out.println("欢迎您" + userinfo[0] + "成功登录");
		}
	}

	private static void register() {
		System.out.println("用户名为纯字母，长度6-18；密码首位必须是字母，后面纯数字，长度3-8");
		socketWrite("register");
		String[] userinfo = userinfoInput();
		socketWrite("username=" + userinfo[0] + "&password=" + userinfo[1]);
		if(socketRead().equals("true"))
			System.out.println("欢迎您成功注册");
		else
			System.out.println("用户名已存在或用户名密码格式有误）");
	}

	private static void exit() {
		try{
			socket.close();
			scanner.close();
		}catch (IOException ioe){
			ioe.printStackTrace();
		}
		System.out.println("已关闭与服务器的连接，成功退出");
		System.exit(0);
	}

	private static String[] userinfoInput() {
		System.out.println("请输入用户名：");
		String username = scanner.nextLine();
		System.out.println("请输入密码");
		String password = scanner.nextLine();
		return new String[]{username, password};
	}

	private static void socketWrite(String message) {
		try {
			bw.write(message);
			bw.newLine();
			bw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private static String socketRead() {
		String message = "";
		try {
			message = br.readLine();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return message;
	}
}
