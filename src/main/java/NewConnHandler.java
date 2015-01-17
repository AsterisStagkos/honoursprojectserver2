import java.net.Socket;


public class NewConnHandler implements Runnable {

	Socket newSocket = null;
	int port = 5000;
	
	public NewConnHandler(Socket socket, int portt) {
		this.newSocket = socket;
		this.port = portt;
	}
	@Override
	public void run() {
		int port = 12121;
		Sender senderInstance = new Sender();
		//newSocket.
		System.out.println("socket: " + newSocket);
		senderInstance.waitForAction(newSocket, port);
				
	}
}
