import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridLayout;
import java.io.File;
import java.util.Date;

import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.SystemColor;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class properties extends JFrame {

	private JPanel contentPane;

	public properties(String pathh) {
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(600, 100, 261, 347);
		setTitle("Properties");
		File file = new File(pathh);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(5, 2));
		
		JLabel Name = new JLabel("Name");
		Name.setBackground(SystemColor.inactiveCaption);
		Name.setHorizontalAlignment(SwingConstants.CENTER);
		Name.setFont(new Font("Dialog", Font.BOLD, 12));
		contentPane.add(Name);
		
		JLabel Nameinfo = new JLabel("");
		Nameinfo.setHorizontalAlignment(SwingConstants.CENTER);
		Nameinfo.setFont(new Font("Dialog", Font.PLAIN, 12));
		Nameinfo.setText(file.getName());
		contentPane.add(Nameinfo);
		
		JLabel path = new JLabel("Absolute");
		path.setBackground(SystemColor.inactiveCaption);
		path.setHorizontalAlignment(SwingConstants.CENTER);
		path.setFont(new Font("Dialog", Font.BOLD, 12));
		
		contentPane.add(path);
		
		JLabel pathinfo = new JLabel("");
		pathinfo.setHorizontalAlignment(SwingConstants.CENTER);
		pathinfo.setFont(new Font("Dialog", Font.PLAIN, 12));
		pathinfo.setText(file.getAbsolutePath());
		contentPane.add(pathinfo);
		
		JLabel size = new JLabel("Size");
		size.setBackground(SystemColor.inactiveCaption);
		size.setHorizontalAlignment(SwingConstants.CENTER);
		size.setFont(new Font("Dialog", Font.BOLD, 12));
		contentPane.add(size);
		
		JLabel sizeinfo = new JLabel("");
		sizeinfo.setHorizontalAlignment(SwingConstants.CENTER);
		sizeinfo.setFont(new Font("Dialog", Font.PLAIN, 12));
		sizeinfo.setText(file.length()+" bytes");
		contentPane.add(sizeinfo);
		
		JLabel lastmod = new JLabel("Last modified");
		lastmod.setBackground(SystemColor.inactiveCaption);
		lastmod.setHorizontalAlignment(SwingConstants.CENTER);
		lastmod.setFont(new Font("Dialog", Font.BOLD, 12));
		contentPane.add(lastmod);
		
		JLabel lastmodinfo = new JLabel("");
		lastmodinfo.setHorizontalAlignment(SwingConstants.CENTER);
		lastmodinfo.setFont(new Font("Dialog", Font.PLAIN, 12));
		lastmodinfo.setText(new Date(file.lastModified()).toString());
		contentPane.add(lastmodinfo);
		
		JButton btnNewButton = new JButton("Ok");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btnNewButton.setFont(new Font("Tahoma", Font.BOLD, 12));
		btnNewButton.setBackground(Color.WHITE);
		contentPane.add(btnNewButton);
		setVisible(true);
	}

}
