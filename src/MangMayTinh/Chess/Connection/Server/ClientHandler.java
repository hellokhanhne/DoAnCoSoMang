
package MangMayTinh.Chess.Connection.Server;

import MangMayTinh.Chess.Model.Room;
import MangMayTinh.Chess.Model.Enum.MessageType;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map.Entry;

public class ClientHandler extends Thread {

	Socket socket;
	ObjectOutputStream sender;
	ObjectInputStream receiver;
	Player player;
	Room room;
	boolean isStopHandle = false;
	String localAddress;

	public ClientHandler(Socket socket, ObjectOutputStream sender, ObjectInputStream receiver, Player player,
			String localAddress) {
		this.socket = socket;
		this.sender = sender;
		this.receiver = receiver;
		this.player = player;
		this.localAddress = localAddress;
	}

	@Override
	public void run() {
		this.isStopHandle = true;
		try {

			while (this.isStopHandle) {

				MessageType type = (MessageType) receiver.readObject();
				

				switch (type) {
				case updateRoom:

					String idRoom = (String) receiver.readObject();
					System.out.println("ID ROOM : " + idRoom);
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
								responseRoom.put(set.getKey(),
										new Room(set.getValue().getStatus(), set.getValue().getId()));

							}

							for (ClientHandler clientHandler : Server.clientHandlers) {
								clientHandler.sender.writeObject(MessageType.rooms);
								clientHandler.sender.writeObject(responseRoom);
							}

						}

						Room responseRoom = new Room(room.getStatus(), room.getId());

						if (room.getPlayers().size() == 2) {
							Player playerOne = room.getPlayers().get(0);
							Player playerTwo = room.getPlayers().get(1);

							responseRoom.addPlayer(
									new Player(playerOne.localAddress, playerOne.name, playerOne.isRoomReady));

							responseRoom.addPlayer(
									new Player(playerTwo.localAddress, playerTwo.name, playerTwo.isRoomReady));
							for (ClientHandler clientHandler : Server.clientHandlers) {
								if (clientHandler.localAddress.equals(room.getPlayers().get(0).localAddress)
										|| clientHandler.localAddress.equals(room.getPlayers().get(1).localAddress)) {
									clientHandler.sender.writeObject(MessageType.receiveStatusRoom);
									clientHandler.sender.writeObject(responseRoom);
								}
							}

						}

					}
					break;

				case getStatusOneRoom:
					String idRoom2 = (String) receiver.readObject();
					Room getRoom = Server.rooms.get(idRoom2);
					Room roomRes = new Room(getRoom.getStatus(), getRoom.getId());
					Player player_1 = getRoom.getPlayers().get(0);
					roomRes.addPlayer(new Player(player_1.localAddress, player_1.name, player_1.isRoomReady));
					Player player_2;
					if (getRoom.getPlayers().size() == 2) {
						player_2 = getRoom.getPlayers().get(1);
						roomRes.addPlayer(new Player(player_2.localAddress, player_2.name, player_2.isRoomReady));
					}
					sender.writeObject(MessageType.receiveStatusRoom);
					sender.writeObject(roomRes);
					this.room = roomRes;
					break;

				case updateReady:

					this.isStopHandle = false;
					String idRoom3 = (String) receiver.readObject();
					Room room2 = Server.rooms.get(idRoom3);
					Room roomRes1 = new Room(room2.getStatus(), room2.getId());
					if (room2.getPlayers().size() == 1) {
						room2.getPlayers().get(0).isRoomReady = room2.getPlayers().get(0).isRoomReady == true ? false
								: true;
						Player player_11 = room2.getPlayers().get(0);
						roomRes1.addPlayer(new Player(player_11.localAddress, player_11.name, player_11.isRoomReady));
						sender.writeObject(MessageType.receiveStatusRoom);
						sender.writeObject(roomRes1);
					} else if (room2.getPlayers().size() == 2) {
						this.isStopHandle = false;
						Player selectPlayer = null;
						if (room2.getPlayers().get(0).localAddress.equals(this.localAddress)) {
							selectPlayer = room2.getPlayers().get(0);
						} else {
							selectPlayer = room2.getPlayers().get(1);
						}
						selectPlayer.isRoomReady = selectPlayer.isRoomReady == true ? false : true;

						roomRes1.addPlayer(new Player(room2.getPlayers().get(0).localAddress,
								room2.getPlayers().get(0).name, room2.getPlayers().get(0).isRoomReady));
						roomRes1.addPlayer(new Player(room2.getPlayers().get(1).localAddress,
								room2.getPlayers().get(1).name, room2.getPlayers().get(1).isRoomReady));

						if (room2.getPlayers().get(0).isRoomReady && room2.getPlayers().get(1).isRoomReady) {
							sendReadyByRoom(room2, roomRes1, true);
							Player firstPlayer = room2.getPlayers().get(0);
							Player secondPlayer = room2.getPlayers().get(1);
							Match match = new Match(firstPlayer, secondPlayer);
							new Thread(match).start();
							System.out.println("Start a new match");
						} else {
							sendReadyByRoom(room2, roomRes1, false);
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

	public void sendReadyByRoom(Room room, Room roomRes, boolean stopThread) throws IOException {
		try {

			for (ClientHandler clientHandler : Server.clientHandlers) {
				if (clientHandler.localAddress.equals(room.getPlayers().get(0).localAddress)
						|| clientHandler.localAddress.equals(room.getPlayers().get(1).localAddress)) {
					clientHandler.sender.writeObject(MessageType.receiveStatusRoom);
					clientHandler.sender.writeObject(roomRes);
					if (stopThread) {
//						clientHandler.isStopHandle = false;
//						clientHandler.wait();
					}
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
