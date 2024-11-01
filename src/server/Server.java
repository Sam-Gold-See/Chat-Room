package server;

import user.User;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
	private static ServerSocket serverSocket;

	public static ArrayList<Socket> clientSocketList = new ArrayList<>();

	public static ArrayList<User> userinfoList = new ArrayList<>();

	public static File userinfoPath = new File("local/");

	public static File userinfoFile = new File(userinfoPath, "userinfo.txt");

	public static void main(String[] args) throws IOException {
		init();

		while (true) {
			Socket socket = serverSocket.accept();
			clientSocketList.add(socket);
			new Thread(new ServerChatRunnable(socket)).start();
		}
	}

	private static void init() throws IOException {
		if (!userinfoFile.exists()) {
			userinfoPath.mkdir();
			userinfoFile.createNewFile();
			try (BufferedWriter bw = new BufferedWriter(new FileWriter(userinfoFile))) {
				User admin = new User("admin", "123456");
				userinfoList.add(admin);
				bw.write(admin.toString());
				bw.newLine();
				bw.flush();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		} else {
			try (BufferedReader br = new BufferedReader(new FileReader(userinfoFile))) {
				String line;
				while ((line = br.readLine()) != null) {
					String[] userinfoTempArr = line.split("&");
					String username = userinfoTempArr[0].split("=")[1];
					String password = userinfoTempArr[1].split("=")[1];
					userinfoList.add(new User(username, password));
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		serverSocket = new ServerSocket(8888);
		System.out.println("服务器启动成功");
	}
}