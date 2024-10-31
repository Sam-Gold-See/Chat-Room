import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	private static ServerSocket serverSocket;

	public static ArrayList<User> userinfoList = new ArrayList<>();

	public static File userinfoFile = new File("local/userinfo.txt");

	public static void main(String[] args) throws IOException {
		init();

		while (true) {
			Socket socket = serverSocket.accept();
			new ChatRunnable(socket).run();
		}
	}

	private static void init() {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(userinfoFile)));
			String line;
			while ((line = br.readLine()) != null) {
				String[] userinfoTempArr = line.split("&");
				String username = userinfoTempArr[0].split("=")[1];
				String password = userinfoTempArr[1].split("=")[1];
				userinfoList.add(new User(username, password));
			}
			serverSocket = new ServerSocket(8888);
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		System.out.println("服务器启动成功");
	}
}

class ChatRunnable implements Runnable {
	private static Socket socket;
	private static BufferedReader br;
	private static BufferedWriter bw;

	public ChatRunnable(Socket socket) {
		ChatRunnable.socket = socket;
	}

	@Override
	public void run() {
		String command = socketRead();
		String userinfo = socketRead();
		switch (command) {
			case "login" -> {
				User userInput = new User(userinfo);
				boolean flag = false;
				for (User user : Server.userinfoList)
					if (user.equals(userInput)) {
						flag = true;
						break;
					}
				if (flag) {
					socketWrite("true");
					chat();
				} else
					socketWrite("false");
			}
			case "register" -> {
				if (check(new User(userinfo))) {
					socketWrite("true");
					updateUserinfo(userinfo);
				} else
					socketWrite("false");
			}
		}
	}

	private static void chat() {

	}

	private static boolean check(User userInput) {
		String usernameInput = userInput.getUsername();
		String passwordInput = userInput.getPassword();

		int usernameInputLength = usernameInput.length();
		if (usernameInputLength < 6 || usernameInputLength > 18)
			return false;

		int passwordInputLength = passwordInput.length();
		if (passwordInputLength < 3 || passwordInputLength > 8)
			return false;

		for (User user : Server.userinfoList)
			if (user.getUsername().equals(usernameInput))
				return false;

		for (char c : usernameInput.toCharArray())
			if (!Character.isLetter(c))
				return false;

		char[] passwordInputArray = passwordInput.toCharArray();
		for (int i = 0; i < passwordInputLength; i++)
			if (i == 0 && !Character.isLetter(passwordInputArray[i]))
				return false;
			else if (Character.isLetter(passwordInputArray[i]))
				return false;

		return true;
	}

	private static void updateUserinfo(String userinfo) {
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Server.userinfoFile, true)));
			bw.write(userinfo);
			bw.newLine();
			bw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		Server.userinfoList.add(new User(userinfo));
	}

	private static void socketWrite(String message) {
		try {
			bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
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
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			message = br.readLine();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return message;
	}
}