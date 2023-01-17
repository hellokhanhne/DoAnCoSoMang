package MangMayTinh.Chess.Connection.Client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;

public class WaitingRoom extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WaitingRoom frame = new WaitingRoom();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public WaitingRoom() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 745, 436);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Waiting the second player ...");
		lblNewLabel.setForeground(Color.BLUE);
		lblNewLabel.setFont(new Font("Snap ITC", Font.PLAIN, 30));
		lblNewLabel.setBounds(117, 22, 496, 81);
		contentPane.add(lblNewLabel);
		
		Icon imgIcon = new ImageIcon(this.getClass().getResource("./RoomIcons/loading.gif"));
		JLabel lblNewLabel_1 = new JLabel(imgIcon);
//		lblNewLabel_1.setIcon(new ImageIcon(WaitingRoom.class.getResource("/MangMayTinh/Chess/Connection/Client/RoomIcons/loading_.gif")));
		lblNewLabel_1.setBounds(183, 135, 309, 183);
		contentPane.add(lblNewLabel_1);
	}
	

}
