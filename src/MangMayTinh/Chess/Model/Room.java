package MangMayTinh.Chess.Model;

import java.io.Serializable;
import java.util.ArrayList;

import MangMayTinh.Chess.Connection.Server.Player;

public class Room  implements Serializable {
	private String status;
	private String id;
	private ArrayList<Player> players;

	public Room(String status, String id) {
		this.status = status;
		this.id = id;
		this.players =  new ArrayList<>();
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}
	
	public void addPlayer(Player player) {
		this.players.add(player);
		this.status = "" + (Integer.parseInt(this.status));
	}

	@Override
	public String toString() {
		return "Room [status=" + status + ", id=" + id + ", players=" + players + "]";
	}
	
	

}
