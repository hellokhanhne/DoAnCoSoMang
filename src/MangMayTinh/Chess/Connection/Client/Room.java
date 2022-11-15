package MangMayTinh.Chess.Connection.Client;

import javax.swing.JOptionPane;
import java.io.IOException;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.awt.event.ActionEvent;
import java.awt.Component;
import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.GridLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.LineBorder;

import MangMayTinh.Chess.Model.Enum.MessageType;

import java.awt.Container;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;

public class Room extends JFrame implements ActionListener {

	int ROOM;
	Socket socket = null;
	ObjectInputStream receiver = null;
	Thread roomThread;
	JPanel pn;
	JButton[] bt;
	String[] stt;

	public Room(Socket socket, ObjectInputStream receiver) {
		super("Chess - Khanh Sky");
		this.socket = socket;
	
		this.receiver = receiver;
	
		this.ROOM = 12;
		this.bt = new JButton[13];
		this.stt = new String[13];
		this.init();
		
	}

	public Container init() {
		final Container cn = this.getContentPane();
		(this.pn = new JPanel()).setLayout(new GridLayout(3, 4));
		for (int i = 1; i <= 12; ++i) {
			(this.bt[i] = new JButton()).setActionCommand(String.valueOf(i));
			this.bt[i].setBackground(Color.white);
			this.bt[i].addActionListener(this);
			JButton temp_button = this.bt[i];
			temp_button.setBorder(new LineBorder(Color.white, 2));
			this.bt[i].addMouseListener(new MouseAdapter() {

				public void mouseEntered(MouseEvent evt) {
					temp_button.setBorder(new LineBorder(Color.blue, 2));
				}

				public void mouseExited(MouseEvent evt) {
					temp_button.setBorder(new LineBorder(Color.white, 2));
				}
			});
			this.pn.add(this.bt[i]);
		}
		cn.add(this.pn);
		this.setVisible(true);
		this.setSize(760, 570);
		this.setDefaultCloseOperation(3);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		
		

		roomThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						MessageType type = (MessageType) receiver.readObject();
						switch (type) {
						case rooms:
							System.out.println(type);
						 @SuppressWarnings("unchecked") HashMap<String, MangMayTinh.Chess.Model.Room> listRooms = (HashMap<String, MangMayTinh.Chess.Model.Room>) receiver
									.readObject();
							for (HashMap.Entry<String, MangMayTinh.Chess.Model.Room> set : listRooms.entrySet()) {

								MangMayTinh.Chess.Model.Room roomTemp = (MangMayTinh.Chess.Model.Room) set.getValue();
								int tempId =Integer.parseInt(roomTemp.getId());
								if (tempId <= 12) {
									Room.this.stt[tempId] = roomTemp.getStatus();
									Room.this.bt[tempId].setText(Room.this.stt[tempId]);
									Room.this.bt[tempId].setIcon(Room.this.getIcon(roomTemp.getStatus()));
								}
							}
							break;

						default:
							break;
						}
					} catch (Exception e) {

					}
				}

			}
		});
		
		roomThread.start();

		return cn;
	}

	public String getStatusRoom(final int room) {
		return null;
	}



	private Icon getIcon(final String str) {
		Image image = null;
		final int width = 190;
		final int height = 190;
		image = new ImageIcon(this.getClass().getResource("RoomIcons/" + str + ".png")).getImage();
		final Icon icon = new ImageIcon(image.getScaledInstance(width, height, 4));
		return icon;
	}

	public void writeStatus(final int room, final String data) {

	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		final int K = Integer.parseInt(e.getActionCommand());
		final int I = this.stt[K].charAt(0) - '0';
		Client.sendMessageToServer(MessageType.updateRoom, "" + K);
	}



	
}
