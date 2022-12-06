package MangMayTinh.Chess.Connection.Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import MangMayTinh.Chess.Connection.Server.Player;
import MangMayTinh.Chess.Model.Room;
import MangMayTinh.Chess.Model.Enum.MessageType;

import java.awt.GridLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class Ready extends JFrame {
	int roomId;
	Socket socket = null;
	ObjectInputStream receiver = null;
	Thread thread;
	JLabel lblNewLabel;
	JLabel lblYourFriendAre;
	JLabel lbMe;
	JLabel competitor;
	JButton btnNewButton;
	JButton btnNewButton_1;

	private JPanel contentPane;

	/**
	 * Create the frame.
	 * @throws IOException 
	 */
	public Ready(int idRoom, Socket socket, ObjectInputStream receiver) throws IOException {
		this.roomId = idRoom;
		this.socket = socket;
		this.receiver = receiver;
		init();
		emitGetStatusRoom();
	}

	public void init() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 676, 287);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		lblNewLabel = new JLabel("");
		lblNewLabel.setForeground(Color.BLUE);
		lblNewLabel.setFont(new Font("Snap ITC", Font.PLAIN, 16));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(26, 48, 268, 77);
		contentPane.add(lblNewLabel);

		btnNewButton = new JButton("Ready");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Client.sendMessageToServer(MessageType.updateReady, "" + roomId);
			}
		});
		btnNewButton.setFont(new Font("Snap ITC", Font.PLAIN, 14));
		btnNewButton.setBounds(76, 146, 142, 54);
		contentPane.add(btnNewButton);

		btnNewButton_1 = new JButton("Ready");
		btnNewButton_1.setFont(new Font("Snap ITC", Font.PLAIN, 14));
		btnNewButton_1.setEnabled(false);
		btnNewButton_1.setBounds(385, 146, 142, 54);
		contentPane.add(btnNewButton_1);

		lblYourFriendAre = new JLabel("");
		lblYourFriendAre.setForeground(Color.RED);
		lblYourFriendAre.setHorizontalAlignment(SwingConstants.CENTER);
		lblYourFriendAre.setFont(new Font("Snap ITC", Font.PLAIN, 16));
		lblYourFriendAre.setBounds(304, 48, 320, 77);
		contentPane.add(lblYourFriendAre);

		lbMe = new JLabel(Client.myName);
		lbMe.setHorizontalAlignment(SwingConstants.CENTER);
		lbMe.setForeground(Color.BLACK);
		lbMe.setFont(new Font("Snap ITC", Font.PLAIN, 16));
		lbMe.setBounds(26, 0, 268, 77);
		contentPane.add(lbMe);

		competitor = new JLabel("");
		competitor.setHorizontalAlignment(SwingConstants.CENTER);
		competitor.setForeground(Color.BLACK);
		competitor.setFont(new Font("Snap ITC", Font.PLAIN, 16));
		competitor.setBounds(330, 0, 268, 77);
		contentPane.add(competitor);

		this.thread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						MessageType type = (MessageType) receiver.readObject();
						System.out.println(type);
						switch (type) {
						case receiveStatusRoom:
							Room room = (Room) receiver.readObject();
                              System.out.println("room " +room.toString());
							String youName = null;
							String readyMe;
							String readyYou;

							if (room.getPlayers().get(0).isRoomReady()) {
								btnNewButton.setText("UnReady");
							} else {
								btnNewButton.setText("Ready");
							}

							if (room.getPlayers().size() == 1) {
								youName = "Waiting player ... ";

							} else if (room.getPlayers().size() == 2) {
								Player mePl;
								Player youPl;
								
								if (room.getPlayers().get(0).localAddress.equals(socket.getLocalAddress().toString())) {
									mePl = room.getPlayers().get(0);
									youPl = room.getPlayers().get(1);

								} else {
									mePl = room.getPlayers().get(1);
									youPl = room.getPlayers().get(0);
								}

								if (youPl.isRoomReady()) {
									btnNewButton_1.setText("Readying");
								} else {
									btnNewButton_1.setText("UnReadying");
								}

								youName = youPl.name;
							}
							readyMe = "YOU ARE NOT READY";
							readyYou = "YOUR FRIEND ARE NOT READY";

							lblNewLabel.setText(readyMe);
							lblYourFriendAre.setText(readyYou);
							competitor.setText(youName);
							break;
							
						case updateReady: 
							System.out.println("updateReady");
							break;
						default:
							break;
						}
					} catch (Exception e) {

					}
				}
			}
		});
		this.thread.start();
	}

	public void emitGetStatusRoom() {
		System.out.println("Emit room");
		Client.sendMessageToServer(MessageType.getStatusOneRoom, "" + this.roomId);
	}
}
