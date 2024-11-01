package server;

import user.User;

import java.io.*;
import java.net.Socket;

public class ServerChatRunnable implements Runnable {
	private final Socket clientSocket;
	private static int count = 0;
	private boolean stop;

	public ServerChatRunnable(Socket socket) {
		clientSocket = socket;
		stop = false;
	}

	@Override
	public void run() {
		System.out.println("线程" + (++count) + "启动");
		int number = count;
		while (!stop) {
			String command = socketRead();
			String userinfo = socketRead();
			switch (command) {
				case "login" -> {
					String result = checkLogin(userinfo);
					socketWrite(result);
					if (result.equals("登录成功"))
						chat();
				}
				case "register" -> {
					String result = checkRegister(new User(userinfo));
					socketWrite(result);
					if (result.equals("注册成功"))
						updateUserinfo(userinfo);
				}
				case "exit" -> {
					System.out.println("线程" + number + "关闭");
					stop = true;
				}
			}
		}
	}

	private void chat() {
		while (stop) {

		}
	}

	private String checkLogin(String userinfo) {
		String status1 = "用户名不存在";
		String status2 = "密码错误";
		String status3 = "登陆成功";

		User userInput = new User(userinfo);
		String usernameInput = userInput.getUsername();
		String passwordInput = userInput.getPassword();
		boolean flag = false;
		User userTrue = null;
		for (User user : Server.userinfoList)
			if (user.getUsername().equals(usernameInput)) {
				flag = true;
				userTrue = user;
				break;
			}
		if (!flag)
			return status1;
		else if (!passwordInput.equals(userTrue.getPassword()))
			return status2;
		else
			return status3;
	}

	private String checkRegister(User userInput) {
		String status1 = "用户名格式有误";
		String status2 = "用户名已存在";
		String status3 = "密码格式有误";
		String status4 = "注册成功";

		String usernameInput = userInput.getUsername();
		String passwordInput = userInput.getPassword();

		int usernameInputLength = usernameInput.length();
		if (usernameInputLength < 6 || usernameInputLength > 18)
			return status1;

		for (char c : usernameInput.toCharArray())
			if (!Character.isLetter(c))
				return status1;

		for (User user : Server.userinfoList)
			if (user.getUsername().equals(usernameInput))
				return status2;

		int passwordInputLength = passwordInput.length();
		if (passwordInputLength < 3 || passwordInputLength > 8)
			return status3;

		char[] passwordInputArray = passwordInput.toCharArray();
		if (!Character.isLetter(passwordInputArray[0]))
			return status3;

		for (int i = 1; i < passwordInputLength; i++)
			if (Character.isLetter(passwordInputArray[i]))
				return status3;

		return status4;
	}

	private void updateUserinfo(String userinfo) {
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

	private void socketWrite(String message) {
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()))) {
			bw.write(message);
			bw.newLine();
			bw.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private String socketRead() {
		String message = "";
		try (BufferedReader br = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {
			message = br.readLine();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
		return message;
	}
}