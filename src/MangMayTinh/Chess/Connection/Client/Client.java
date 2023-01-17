
package MangMayTinh.Chess.Connection.Client;

import MangMayTinh.Chess.Model.Chessboard;
import MangMayTinh.Chess.Model.Interface.ChessboardInterface;
import MangMayTinh.Chess.Model.Enum.MessageType;
import MangMayTinh.Chess.Model.Move;
import MangMayTinh.Chess.Model.Room;

import java.awt.Color;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JOptionPane;

public class Client extends javax.swing.JFrame implements ChessboardInterface {

	Socket socket = null;
	int port = 0;
	String host = "";
	static ObjectOutputStream sender = null;
	ObjectInputStream receiver = null;
	static String myName = "";
	String secondPlayerName = "";
	Thread listener;
	Chessboard chessboard;
	MangMayTinh.Chess.Connection.Client.Room room;
	WaitingRoom waitingRoom;
	boolean isMyTurn = false;
	boolean isFirstPlayer = false;
	boolean isRunning = false;

	public Client() {
		initComponents();
	}

	public void play() {
		this.isRunning = true;
		this.listener = new Thread(new Runnable() {
			@Override
			public void run() {
//				System.out.println(receiver);
				while (isRunning) {
					try {
						MessageType type = (MessageType) receiver.readObject();
						System.out.println("type: " + type);
						switch (type) {
						case string:
							String messageFromServer = (String) receiver.readObject();
							messageLabel.setText(messageFromServer);
							break;
						case move:
							Move move = (Move) receiver.readObject();
							Move newMove = move.clone();
							moveOpponent(newMove);
							break;
						case result:
							boolean isWinner = (boolean) receiver.readObject();
							informResult(isWinner);
							break;
						case startGame:
							if (waitingRoom != null) {
								waitingRoom.setVisible(false);
								waitingRoom.dispose();
							}
							if(room.ready != null) {
								room.ready.setVisible(false);
							   room.ready.dispose();
							}
							startGame();
							break;
						case turn:
							isMyTurn = true;
							break;
						case isFirstPlayer:
							boolean isFirst = (boolean) receiver.readObject();
							isFirstPlayer = isFirst;
							sendMessageToServer(MessageType.name, myName);
							break;
						case name:
							secondPlayerName = (String) receiver.readObject();
							break;
						case message:
							String message = (String) receiver.readObject();
							chessboard.addMessageHistory(message, !isFirstPlayer);
							break;
						case rooms:
							HashMap<String, MangMayTinh.Chess.Model.Room> listRooms = (HashMap<String, MangMayTinh.Chess.Model.Room>) receiver
									.readObject();
							System.out.println(listRooms.toString());
							room.renderRooms(listRooms);
							break;
						case receiveStatusRoom:
							Room roomObject = (Room) receiver.readObject();
							System.out.println(roomObject.toString());
							room.ready.renderUiStatus(roomObject);
							break;
						default:
							System.out.println("Client !!! .Can not cast data from socket to expect message type!");
							break;
						}
					} catch (IOException ex) {
						System.out.println("IO Exception: From client (play): " + ex.getMessage());
						System.out.println("caused by: " + ex.getCause().toString());
					} catch (ClassNotFoundException ex) {
						System.out.println("Class Not Found Exception: From client (play)");
						ex.printStackTrace();
					}
				}
			}
		});

	}

	private void moveOpponent(Move move) {
		System.out.println("Opponent's move: ");
		this.chessboard.move(move);
		this.isMyTurn = true;
		this.chessboard.setMessage("Your turn!");
		if (isFirstPlayer) {
			this.chessboard.switchTurn(1);
		} else {
			this.chessboard.switchTurn(2);
		}
	}

	private void informResult(boolean isWinner) {
		String title = this.myName;
		String message = "YOU WIN!";
		if (!isWinner) {
			message = "YOU LOSE!";
		}
		this.messageLabel.setText(message);
		this.chessboard.setMessage(message);
		JOptionPane.showMessageDialog(this, message, title, JOptionPane.INFORMATION_MESSAGE);
		this.chessboard.destruct();
		this.chessboard.dispose();
		this.joinButton.setEnabled(true);
		this.chooseRoomButton.setEnabled(true);
		this.isRunning = false;
		this.sendOperation(MessageType.endGame);
	}

	private void startGame() {
		this.chessboard = new Chessboard();
		this.chessboard.setIsFirstPlayer(isFirstPlayer);
		this.chessboard.drawChessboard();
		this.chessboard.setDelegate(this);
		this.chessboard.setVisible(true);
		if (this.isMyTurn) {
			chessboard.setMessage("It's your turn!");
		} else {
			this.chessboard.setMessage("Your opponent's turn!");
		}
		if (this.isFirstPlayer) {
			this.chessboard.setPlayerName(this.myName, secondPlayerName);
		} else {
			this.chessboard.setPlayerName(secondPlayerName, this.myName);
		}
	}

	private void didChangeInput() {
		if (!this.serverIPTextField.getText().equals("") && !this.portTextField.getText().equals("")
				&& !this.nameTextFiled.getText().equals("")) {
			this.joinButton.setEnabled(true);
			this.chooseRoomButton.setEnabled(true);
		} else {
			this.joinButton.setEnabled(false);
			this.chooseRoomButton.setEnabled(false);
		}
	}

	public static <T> void sendMessageToServer(MessageType type, T data) {
		try {
			sender.writeObject(type);
			sender.writeObject(data);
		} catch (Exception e) {
			System.out.print("Send data Error: Client");
			System.out.println(e.toString());
		}
	}

	public void sendOperation(MessageType type) {
		try {
			this.sender.writeObject(type);
		} catch (Exception e) {
			System.out.print("Send data Error: ");
			System.out.println(e.toString());
		}
	}

	private void initComponents() {

		jMenu1 = new javax.swing.JMenu();
		jMenu2 = new javax.swing.JMenu();
		jPopupMenu1 = new javax.swing.JPopupMenu();
		jCheckBoxMenuItem1 = new javax.swing.JCheckBoxMenuItem();
		jMenuItem1 = new javax.swing.JMenuItem();
		jMenu3 = new javax.swing.JMenu();
		jLabel1 = new javax.swing.JLabel();
		jLabel2 = new javax.swing.JLabel();
		jLabel3 = new javax.swing.JLabel();
		serverIPTextField = new javax.swing.JTextField();
		portTextField = new javax.swing.JTextField();
		nameTextFiled = new javax.swing.JTextField();
		joinButton = new javax.swing.JButton();
		messageLabel = new javax.swing.JLabel();
		chooseRoomButton = new javax.swing.JButton();

		jMenu1.setText("jMenu1");

		jMenu2.setText("jMenu2");

		jCheckBoxMenuItem1.setSelected(true);
		jCheckBoxMenuItem1.setText("jCheckBoxMenuItem1");

		jMenuItem1.setText("jMenuItem1");

		jMenu3.setText("jMenu3");

		setTitle("Chess client");
		setResizable(false);

		jLabel1.setText("Server IP:");

		jLabel2.setText("Port:");

		jLabel3.setText("Name:");

		serverIPTextField.setText("localhost");
		serverIPTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyTyped(java.awt.event.KeyEvent evt) {
				didChangeServerIP(evt);
			}
		});

		portTextField.setText("5555");
		portTextField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyTyped(java.awt.event.KeyEvent evt) {
				didChangePort(evt);
			}
		});

		nameTextFiled.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
		nameTextFiled.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyTyped(java.awt.event.KeyEvent evt) {
				nameDidChange(evt);
			}
		});

		joinButton.setText("Random New Game");
		joinButton.setEnabled(false);
		joinButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				joinButtonActionPerformed(evt);
			}
		});

		messageLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

		chooseRoomButton.setText("Choose room");
		chooseRoomButton.setEnabled(false);
		chooseRoomButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				actionPerformedChooseRoom(evt);
			}
		});

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(layout.createSequentialGroup().addGap(168, 168, 168)
						.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
								.addGroup(layout.createSequentialGroup().addComponent(jLabel3)
										.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
												javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
										.addComponent(nameTextFiled, javax.swing.GroupLayout.PREFERRED_SIZE, 322,
												javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
										layout.createSequentialGroup().addComponent(jLabel2)
												.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED,
														javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
												.addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
														322, javax.swing.GroupLayout.PREFERRED_SIZE))
								.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
										layout.createSequentialGroup().addComponent(jLabel1).addGap(29, 29, 29)
												.addComponent(serverIPTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
														322, javax.swing.GroupLayout.PREFERRED_SIZE)))
						.addContainerGap(193, Short.MAX_VALUE))
				.addGroup(javax.swing.GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup().addGap(0, 0, Short.MAX_VALUE).addComponent(messageLabel)
								.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
				.addGroup(layout.createSequentialGroup().addGap(256, 256, 256).addComponent(joinButton)
						.addGap(18, 18, 18).addComponent(chooseRoomButton)
						.addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
		layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout
				.createSequentialGroup().addGap(18, 18, 18).addComponent(messageLabel).addGap(93, 93, 93)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel1)
						.addComponent(serverIPTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGap(18, 18, 18)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel2)
						.addComponent(portTextField, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGap(18, 18, 18)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel3)
						.addComponent(nameTextFiled, javax.swing.GroupLayout.PREFERRED_SIZE,
								javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
				.addGap(51, 51, 51)
				.addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
						.addComponent(joinButton).addComponent(chooseRoomButton))
				.addContainerGap(205, Short.MAX_VALUE)));

		pack();
		setLocationRelativeTo(null);
	}

	private void didChangeServerIP(java.awt.event.KeyEvent evt) {
		this.didChangeInput();
	}

	private void didChangePort(java.awt.event.KeyEvent evt) {
		this.didChangeInput();
	}

	private void nameDidChange(java.awt.event.KeyEvent evt) {
		this.didChangeInput();
	}

	private void joinButtonActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_joinButtonActionPerformed
		String portString = this.portTextField.getText();
		String host = this.serverIPTextField.getText();
		String name = this.nameTextFiled.getText();
		try {
			int port = Integer.parseInt(portString);
			this.socket = new Socket(host, port);
			this.messageLabel.setForeground(Color.blue);
			this.messageLabel.setText("Connected to server!");
			this.sender = new ObjectOutputStream(this.socket.getOutputStream());
			this.receiver = new ObjectInputStream(this.socket.getInputStream());
			this.sendMessageToServer(MessageType.randomNewGame, "");
			this.host = host;
			this.port = port;
			this.myName = name;
			this.joinButton.setEnabled(false);
			this.play();
			this.listener.start();
			WaitingRoom room = new WaitingRoom();
			room.setVisible(true);
			room.setLocationRelativeTo(null);
			this.waitingRoom = room;
		} catch (Exception e) {
			System.out.println(e);
			this.messageLabel.setText("Failed to connect to server: " + host + " with port: " + portString);
		}
	}

	private void actionPerformedChooseRoom(java.awt.event.ActionEvent evt) {

		String portString = this.portTextField.getText();
		String host = this.serverIPTextField.getText();
		String name = this.nameTextFiled.getText();
		try {
			int port = Integer.parseInt(portString);
			this.socket = new Socket(host, port);
			System.out.println(socket.getLocalAddress() + " " + socket.getLocalPort());
			this.messageLabel.setForeground(Color.blue);
			this.messageLabel.setText("LOADING ...");
			this.sender = new ObjectOutputStream(this.socket.getOutputStream());
			this.receiver = new ObjectInputStream(this.socket.getInputStream());
			this.sendMessageToServer(MessageType.chooseRoomRequest, "");
			sender.writeObject(name);
			room = new MangMayTinh.Chess.Connection.Client.Room(this.socket, this.receiver);
			this.play();
			this.listener.start();
//			System.out.println(room);
			this.host = host;
			this.port = port;
			this.myName = name;
			this.chooseRoomButton.setEnabled(false);

		} catch (Exception e) {
			System.out.println(e);
			this.messageLabel.setText("Failed to connect to server: " + host + " with port: " + portString);
		}
	}

	public int getPort() {
		return port;
	}

	public String getHost() {
		return host;
	}

	public String getPlayerName() {
		return myName;
	}

	public static void main(String args[]) {

		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				new Client().setVisible(true);
			}
		});
	}

	private javax.swing.JButton chooseRoomButton;
	private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItem1;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JLabel jLabel2;
	private javax.swing.JLabel jLabel3;
	private javax.swing.JMenu jMenu1;
	private javax.swing.JMenu jMenu2;
	private javax.swing.JMenu jMenu3;
	private javax.swing.JMenuItem jMenuItem1;
	private javax.swing.JPopupMenu jPopupMenu1;
	private javax.swing.JButton joinButton;
	private javax.swing.JLabel messageLabel;
	private javax.swing.JTextField nameTextFiled;
	private javax.swing.JTextField portTextField;
	private javax.swing.JTextField serverIPTextField;

	@Override
	public void didMove(Move move) {

		if (!isMyTurn) {
			this.chessboard.setMessage("It's not your turn!");
			return;
		}

		this.chessboard.setMessage("Your opponent's turn!");
		this.sendMessageToServer(MessageType.move, move);
		this.chessboard.move(move);
		this.isMyTurn = false;
		if (isFirstPlayer) {
			this.chessboard.switchTurn(2);
		} else {
			this.chessboard.switchTurn(1);
		}
	}

	@Override
	public void didSendMessage(String message) {
		this.sendMessageToServer(MessageType.message, message);
	}

	@Override
	public void didClickCloseChessboard() {
		this.sendOperation(MessageType.surrender);
	}
}
