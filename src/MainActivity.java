import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.Vector;

import javax.print.attribute.standard.NumberOfInterveningJobs;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;


import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.SystemColor;
import javax.swing.JProgressBar;

public class MainActivity extends JFrame implements ActionListener, TreeSelectionListener, MouseListener, KeyListener{

	private JTree tree = new JTree();
	private JList slideList = new JList();
	private JScrollPane pane;
	private Stack<String> backPathStack = new Stack<>();
	private Stack<String> prePathStack = new Stack<>();
	private JPopupMenu popupMenu;
	private JMenuItem cutItem, copyItem, pasteItem,propertiesItem,deleteItem;
	private File tempFile;
	private JTextField lblAddress;
	private JProgressBar progressBarLoading;
	private boolean isCut = false;
	private JButton btnBack, btnPre;

	public MainActivity() {
		initView();
		initAction();
	}

	public void initView() {
		getContentPane().setBackground(Color.WHITE);
		setBounds(350, 100, 700, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Windows Explorer");
		setIconImage(new ImageIcon("folder.png").getImage());
		
		popupMenu = new JPopupMenu("Menu");
		cutItem = new JMenuItem("Cut");
		popupMenu.add(cutItem);
		popupMenu.addSeparator();
		copyItem = new JMenuItem("Copy");
		popupMenu.add(copyItem);
		popupMenu.addSeparator();
		pasteItem = new JMenuItem("Paste");
		popupMenu.add(pasteItem);
		pasteItem.setEnabled(false);
		popupMenu.addSeparator();
		deleteItem = new JMenuItem("Delete");
		popupMenu.add(deleteItem);
		popupMenu.addSeparator();
		propertiesItem = new JMenuItem("Properties");
		popupMenu.add(propertiesItem);
		
	    tree.setModel(new DefaultTreeModel(
	    	new DefaultMutableTreeNode() {
	    		{
	    			List <File>files = Arrays.asList(File.listRoots());
	    			for (File drv : files) {  
	    		          DefaultMutableTreeNode disk = new DefaultMutableTreeNode();
	    		          disk.setUserObject(drv);
	    		          this.add(disk);
		  		  	}
	    			this.setUserObject(new File("This PC"));
	    		}
	    	}
	    ));	    
	    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellRenderer(new FileTreeCellRenderer());
        JPanel bottomPane = new JPanel();
        bottomPane.setLayout(new BorderLayout());
        getContentPane().add(bottomPane, BorderLayout.CENTER);
        pane = new JScrollPane();
        bottomPane.add(pane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		bottomPane.add(panel,BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		
		btnBack = new JButton();
		btnBack.setIcon(new ImageIcon("backt.png"));
		btnBack.setFocusable(false);
		btnPre = new JButton();
		btnPre.setIcon(new ImageIcon("pre.png"));
		btnPre.setBackground(Color.WHITE);
		btnPre.setFocusable(false);
		
		JPanel btn = new JPanel();
		btn.setBackground(Color.WHITE);
		btn.setLayout(new FlowLayout());
		btn.add(btnBack);
		btn.add(btnPre);
		btnBack.setBackground(Color.WHITE);
		panel.add(btn,BorderLayout.WEST);
		
		lblAddress = new JTextField();
		lblAddress.setForeground(Color.BLACK);
		lblAddress.setOpaque(true);
		lblAddress.setBackground(Color.WHITE);
		lblAddress.setFont(new Font("Dialog", Font.BOLD, 12));
		
		progressBarLoading = new JProgressBar();
		panel.add(progressBarLoading, BorderLayout.EAST);
		progressBarLoading.setVisible(false);
		progressBarLoading.setForeground(Color.GREEN);
		progressBarLoading.setBackground(Color.WHITE);
		panel.add(lblAddress,BorderLayout.CENTER);
		JScrollPane panes = new JScrollPane(tree);
		bottomPane.add(panes, BorderLayout.WEST);	
		
	}
	
	public void initAction() {
		btnBack.addActionListener(this);
		btnPre.addActionListener(this);	
		tree.addTreeSelectionListener(this);
		slideList.addMouseListener(this);
		cutItem.addActionListener(this);
		copyItem.addActionListener(this);
		deleteItem.addActionListener(this);
		pasteItem.addActionListener(this);
		propertiesItem.addActionListener(this);
		lblAddress.addKeyListener(this);
	}
	
	public void readFiles(DefaultMutableTreeNode par,String path) {
		File f = new File(path);
	  	File filenames[] =  FileSystemView.getFileSystemView().getFiles(f, true);
	  	if (filenames!=null) {
	  		for (int i=0;i<filenames.length;i++) {
		  		DefaultMutableTreeNode child = new DefaultMutableTreeNode();
		  		child.setUserObject(filenames[i]);
		  		par.add(child);
		  	}
	  	}
	}
		
	public void listFiles(String path) {
		try {
			String pathStack;
			if (backPathStack.isEmpty()) backPathStack.push(path);
			if ((pathStack= backPathStack.peek())!=path) backPathStack.push(path);
			File fi = new File(path);
			File list[] = FileSystemView.getFileSystemView().getFiles(fi,true);
			slideList.setListData(list);
			slideList.setCellRenderer(new FileListCellRenderer());
			slideList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			slideList.setLayoutOrientation(javax.swing.JList.VERTICAL);
			pane.setViewportView(slideList);
			lblAddress.setText(path);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error");
		}
	}
	
	public void delete() {	
		String path = slideList.getSelectedValue().toString();
		try  {      
			File f= new File(path);
			if (f.isFile()) {
				if(!f.delete()) {  
					JOptionPane.showMessageDialog(null, "Failed");  
    			}   
			} else if (f.isDirectory()) {
				deleteDir(f);
			}		
		}  
		catch(Exception e1)  {  
			e1.printStackTrace();  
		}  
		
		if (backPathStack!=null) executeInBackground("list", backPathStack.peek());
		for (int i=0;i<backPathStack.size();i++) {
			if (backPathStack.elementAt(i).contains(path)) {
				backPathStack.remove(backPathStack.indexOf(backPathStack.elementAt(i)));
				i--;
			}
		}
		if (!prePathStack.isEmpty()) if (path.equals(prePathStack.peek())) prePathStack.clear();		
	}
	
	public void paste() {
		File file = tempFile;
		if (isCut) {
			if (file.isFile()) {
				if (!file.getAbsolutePath().equals((backPathStack.peek()+"\\"+tempFile.getName()).replace("\\\\", "\\")))
				cutFile(file,new File(backPathStack.peek()+"\\"+tempFile.getName()));
			}
			if (file.isDirectory()) {
				String source = tempFile.getAbsolutePath();
				String target = backPathStack.peek()+"\\"+tempFile.getName();
				File theDir = new File(target);
				if (!theDir.exists()){
				    theDir.mkdirs();    
				    copyDir(source, target);
				    deleteDir(new File(source));
				}	
			}
		} else {
			if (file.isFile()) if (!file.getAbsolutePath().equals((backPathStack.peek()+"\\"+tempFile.getName()).replace("\\\\", "\\")))
				copyFile(file,new File(backPathStack.peek()+"\\"+tempFile.getName()));
			if (file.isDirectory()) {
				String source = tempFile.getAbsolutePath();
				String target = backPathStack.peek()+"\\"+tempFile.getName();
				File theDir = new File(target);
				if (!theDir.exists()){
				    theDir.mkdirs();    
				    copyDir(source, target);
				}
			}
		}
		if (backPathStack!=null)
			executeInBackground("list", backPathStack.peek());
		pasteItem.setEnabled(false);	
	}
	
	void copyDir(String source,String target) {
		File file = new File(source);
		File [] filenames = file.listFiles();
		if (filenames!=null) {
			for (int i=0;i<filenames.length;i++) {
				if (filenames[i].isDirectory()) {
					File theDir = new File(target+"\\"+filenames[i].getName());
				    theDir.mkdirs();    
				    copyDir(filenames[i].toString(), target+"\\"+filenames[i].getName());
				}
				if (filenames[i].isFile()) {
					copyFile(filenames[i],new File(target+"\\"+filenames[i].getName()));
				}
			}
		}	
	}
	
	void copyFile(File file, File output) {
		InputStream inStream = null;
        OutputStream outStream = null;
 
        try {
            inStream = new FileInputStream(file);
            outStream = new FileOutputStream(output);	 
            int length;
            byte[] buffer = new byte[1024];
            while ((length = inStream.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            try {
				inStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
            try {
				outStream.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
        }
	}
	
	void cutFile(File file, File output) {
		 try  {  
			copyFile(file, output);              
			if (file.isFile()) {
				if(!file.delete()) {  		
					JOptionPane.showMessageDialog(null, "Failed");  
				}   
			}	
		}  
		catch(Exception e1)  {  
			e1.printStackTrace();  
		}  
	}
	
	public void fileCliked(MouseEvent e) {
		if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)) {
			try  {  
				String path = slideList.getSelectedValue().toString();
				File file = new File(path);  
				if (file.isFile()) {
					if(!Desktop.isDesktopSupported()){
						JOptionPane.showMessageDialog(null, "Not supported");
					}  
					Desktop desktop = Desktop.getDesktop();  
					if(file.exists())      
						desktop.open(file);           
				}  
				if (file.isDirectory()) {
					executeInBackground("list", path);
				}
			} 
			catch(Exception e1)  {  
				JOptionPane.showMessageDialog(null, "Error");
			}  
        } else if(SwingUtilities.isRightMouseButton(e)) {
    			JList list = (JList)e.getSource();
                int row = list.locationToIndex(e.getPoint());
                list.setSelectedIndex(row);
                popupMenu.show(e.getComponent(),e.getX(), e.getY());
    	}
        	
	}
	
	private void deleteDir(File folder) {
	    File[] listofFiles = folder.listFiles();
    	for (int j = 0; j < listofFiles.length; j++) {
            File file = listofFiles[j];
            if (file.isFile()) {
            	file.delete();
            }
            if (file.isDirectory()) {
            	deleteDir(file);
            }   
        } 
        folder.delete();
	}
	
	void executeInBackground(String name, String path) {
		if (!name.equals("list")) {
			progressBarLoading.setVisible(true);
			progressBarLoading.setIndeterminate(true);
		}
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {	
            	if (name.equals("paste")) paste();
            	else if (name.equals("delete")) delete();
            	else if (name.equals("list")) listFiles(path);
                return null;
            }

            @Override
            protected void process(List<Void> chunks) {
                
            }

            @Override
            protected void done() {
            	if (!name.equals("list")) {
            		progressBarLoading.setIndeterminate(false);
            		progressBarLoading.setVisible(false);	    
        		}
        		          
            }
        };
        worker.execute();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		fileCliked(e);
	}	
	
	@Override
	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {
		
	}
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		try {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
			if (node.getParent()!=null) {
				File fi = (File)node.getUserObject();		
				String path = fi.getAbsolutePath();
				readFiles(node,path);
				executeInBackground("list", path);	
			}
		} catch(Exception e1) {	
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(copyItem)) {
			isCut = false;
			tempFile = new File(slideList.getSelectedValue().toString());	
			pasteItem.setEnabled(true);
		} else if (e.getSource().equals(cutItem)) {
			isCut = true;
			tempFile = new File(slideList.getSelectedValue().toString());
			pasteItem.setEnabled(true);
		} else if (e.getSource().equals(pasteItem)) {
			executeInBackground("paste","");
		} else if (e.getSource().equals(propertiesItem)) {
			new Properties(slideList.getSelectedValue().toString());
		} else if (e.getSource().equals(deleteItem)) {
			String []list = {"Yes","No"};
			int n = JOptionPane.showOptionDialog(null,"Do you want to delete it?","Messeger",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,list,0);
			if (n==0) {	
				executeInBackground("delete","");
			}
		} else if (e.getSource().equals(btnBack)) {
			if (!backPathStack.isEmpty()) {
				prePathStack.push(backPathStack.pop());
				if (!backPathStack.isEmpty()) {
					executeInBackground("list", backPathStack.pop());				
				}				
			}
		} else if (e.getSource().equals(btnPre)) {
			if (!prePathStack.isEmpty()) {
				executeInBackground("list", prePathStack.pop());
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode()==10) {
			String path = lblAddress.getText().toString();
			executeInBackground("list", path);
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		
	}
		
}
