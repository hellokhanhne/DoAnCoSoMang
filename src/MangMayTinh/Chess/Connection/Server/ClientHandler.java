
package MangMayTinh.Chess.Connection.Server;

import MangMayTinh.Chess.Model.Room;
import MangMayTinh.Chess.Model.Enum.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class ClientHandler extends Thread {
	Player player;
	ObjectOutputStream sender;
	ObjectInputStream receiver;
	Socket socket;

	public ClientHandler(Player player) throws IOException {
		this.player = player;
		this.sender = player.sender;
		this.receiver = player.receiver;
		this.socket = player.socket;
	}
	
	

	public ClientHandler(ObjectOutputStream sender, ObjectInputStream receiver, Socket socket) {
		super();
		this.sender = sender;
		this.receiver = receiver;
		this.socket = socket;
	}



	@SuppressWarnings("unlikely-arg-type")
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
					MangMayTinh.Chess.Model.Room room = Server.rooms.get(idRoom);

					if (room != null) {
						if (room.getPlayers().size() == 0) {
							room.setStatus("10");
						} else if (room.getPlayers().size() == 1) {
							room.setStatus("20");
						}
						if (room.getPlayers().size() < 2) {
							room.addPlayer(player);

							HashMap<String, Room> responseRoom = new HashMap<>();
							for (Entry<String, Room> set : Server.rooms.entrySet()) {
								if (set.getValue().getPlayers().size() > 0) {
									Room r = null;
									responseRoom.put(set.getKey(), r);
								} else {
									responseRoom.put(set.getKey(), set.getValue());
								}

							}
							for (ClientHandler clientHandler : Server.clientHandlers) {
								clientHandler.sender.writeObject(MessageType.rooms);
								clientHandler.sender.writeObject(responseRoom);
							}
						}
					}
					break;

				case getStatusOneRoom:
					String idRoom2 = (String) receiver.readObject();
					Room getRoom = Server.rooms.get(idRoom2);
					System.out.println("get room " + getRoom.toString());
					Room roomRes = new Room(getRoom.getStatus(), getRoom.getId());
					Player player_1 = getRoom.getPlayers().get(0);
					roomRes.addPlayer(new Player(player_1.localAddress, player_1.name, player_1.isRoomReady()));
					Player player_2;
					if (getRoom.getPlayers().size() == 2) {
						player_2 = getRoom.getPlayers().get(1);
						roomRes.addPlayer(new Player(player_2.localAddress, player_2.name, player_2.isRoomReady()));
					}
					sender.writeObject(MessageType.receiveStatusRoom);
					sender.writeObject(roomRes);

					break;

				case updateReady:
					String idRoom3 = (String) receiver.readObject();
					Room room2 = Server.rooms.get(idRoom3);
					Room roomRes1 = new Room(room2.getStatus(), room2.getId());
					if (room2.getPlayers().size() == 1) {
						room2.getPlayers().get(0)
								.setRoomReady(room2.getPlayers().get(0).isRoomReady() == true ? false : true);
						Player player_11 = room2.getPlayers().get(0);
						roomRes1.addPlayer(new Player(player_11.localAddress, player_11.name, player_11.isRoomReady()));
						System.out.println(roomRes1.toString());
						sender.writeObject(MessageType.receiveStatusRoom);
						sender.writeObject(roomRes1);
					} else if (room2.getPlayers().size() == 2) {
						Player selectPlayer = null;
						if (room2.getPlayers().get(0).localAddress.equals(socket.getRemoteSocketAddress())) {
							selectPlayer = room2.getPlayers().get(0);
						} else {
							selectPlayer = room2.getPlayers().get(1);
						}
						selectPlayer.setRoomReady(selectPlayer.isRoomReady() == true ? false : true);
						if (room2.getPlayers().get(0).isRoomReady() && room2.getPlayers().get(1).isRoomReady()) {
							Player firstPlayer = room2.getPlayers().get(0);
							Player secondPlayer = room2.getPlayers().get(1);
							System.out.println(firstPlayer.toString());
							System.out.println(secondPlayer.toString());
							Match match = new Match(firstPlayer, secondPlayer);
							new Thread(match).start();
							System.out.println("Start a new match");
						}
					}

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

}
