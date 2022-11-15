
package MangMayTinh.Chess.Connection.Server;

import MangMayTinh.Chess.Model.Enum.MessageType;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ClientHandler extends Thread {

	Socket socket;
	ObjectOutputStream sender;
	ObjectInputStream receiver;

	public ClientHandler(Socket socket, ObjectOutputStream sender, ObjectInputStream receiver) {
		this.socket = socket;
		this.sender = sender;
		this.receiver = receiver;
	}

	@Override
	public void run() {

		try {

			while (true) {
				
				MessageType type = (MessageType) receiver.readObject();
				System.out.println("type : " + type);
				switch (type) {

				case updateRoom:
					String idRoom = (String) receiver.readObject();
					System.out.println("id Room : " + idRoom);
					break;

				default:
					System.out.println("Can not cast data from socket to expect message type!");
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public ObjectOutputStream getSender() {
		return sender;
	}

	public void setSender(ObjectOutputStream sender) {
		this.sender = sender;
	}

	public ObjectInputStream getReceiver() {
		return receiver;
	}

	public void setReceiver(ObjectInputStream receiver) {
		this.receiver = receiver;
	}
}
