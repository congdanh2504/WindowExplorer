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
import javax.swing.filechooser.FileSystemView;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.SystemColor;

public class MainActivity {

	private JFrame frame;
	private JList list_1 = new JList();
	private JScrollPane pane;
	private Stack<String> backpath = new Stack<>();
	private Stack<String> prepath = new Stack<>();
	private JPopupMenu popupMenu;
	private JMenuItem cutItem, copyItem, pasteItem,propertiesItem,deleteItem;
	private String pathtemp;
	private JLabel address;
	private boolean iscut= false;
	private boolean isclick = false;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainActivity window = new MainActivity();
					window.frame.setVisible(true);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Error");
				}
			}
		});
	}

	public MainActivity() {
		initialize();
	}

	private void initialize(){
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setBounds(100, 100, 700, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setTitle("Windows Explorer");
		frame.setIconImage(new ImageIcon("folder.png").getImage());
		
		popupMenu = new JPopupMenu("Test Popup Menu");
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
		
	    JTree tree = new JTree();
	    tree.setModel(new DefaultTreeModel(
	    	new DefaultMutableTreeNode("Disk") {
	    		{
	    			List <File>files = Arrays.asList(File.listRoots());
	    			for (File drv : files) {  
	    		    	  String s1 = FileSystemView.getFileSystemView().getSystemDisplayName(drv);
	    		          DefaultMutableTreeNode disk = new DefaultMutableTreeNode(s1);
	    		          this.add(disk);
	    		          File f = new File(conver(s1));
		    		  	  String filenames[] =  f.list();
		    		  	  if (filenames!=null) {
		    		  		for (int i=0;i<filenames.length;i++) {
		    		  		  	File fi = new File(conver(s1)+"\\"+filenames[i]);
		    		  		  	DefaultMutableTreeNode child = new DefaultMutableTreeNode(filenames[i]);
		    		  		  	if (fi.isDirectory()) {
		    		  		  		DefaultMutableTreeNode child2 = new DefaultMutableTreeNode();
		    		  		  		child.add(child2);
		    		  		  	}		
			    		  		disk.add(child);
		    		  		}
		    		  	  }
		  		  	}
	    		
	    		}
	    	}
	    ));	    
	    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener((TreeSelectionListener) new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				String jTreeVarSelectedPath = "";
				node.removeAllChildren();
			    Object[] paths = tree.getSelectionPath().getPath();
			    for (int i=0; i<paths.length; i++) {
			        jTreeVarSelectedPath += paths[i];
			        if (i+1 <paths.length ) {
			            jTreeVarSelectedPath += File.separator;
			        }
			    }   
			    String path = conver2(jTreeVarSelectedPath);
			    read(node,path);
			    listFile(path);
			}
		});
		DefaultTreeCellRenderer renderer = (DefaultTreeCellRenderer) tree.getCellRenderer();
        Icon openIcon = new ImageIcon("folder.png");
        renderer.setClosedIcon(openIcon);
        renderer.setOpenIcon(openIcon);
        
        JPanel bottom = new JPanel();
        bottom.setLayout(new BorderLayout());
        frame.getContentPane().add(bottom, BorderLayout.CENTER);
        pane = new JScrollPane();
		bottom.add(pane, BorderLayout.CENTER);
		list_1.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				filecliked(e);
			}
		});;
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		bottom.add(panel,BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));
		JButton btnBack = new JButton();
		btnBack.setIcon(new ImageIcon("backt.png"));
		btnBack.setFocusable(false);
		JButton btnPre = new JButton();
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
		address = new JLabel();
		address.setForeground(Color.BLACK);
		address.setOpaque(true);
		address.setBackground(Color.WHITE);
		address.setFont(new Font("Dialog", Font.BOLD, 12));
		panel.add(address,BorderLayout.CENTER);
		btnBack.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!backpath.isEmpty()) {
					prepath.push(backpath.pop());
					if (!backpath.isEmpty()) {
						listFile(backpath.pop());					
					}				
				}
			}
		});
		JScrollPane panes = new JScrollPane(tree);
		bottom.add(panes, BorderLayout.WEST);	
		btnPre.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!prepath.isEmpty()) {
					listFile(prepath.pop());
				}
			}
		});
		
		cutItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				iscut = true;
				pathtemp = list_1.getSelectedValue().toString();
				pasteItem.setEnabled(true);
			}
		});
		copyItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				iscut = false;
				pathtemp = list_1.getSelectedValue().toString();	
				pasteItem.setEnabled(true);
			}
		});
		pasteItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				paste();
			}
		});
		deleteItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				delete();
			}
		});
		propertiesItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new properties(list_1.getSelectedValue().toString());
			}
		});	
	}
	
	public void read(DefaultMutableTreeNode par,String path) {
		File f = new File(path);
	  	String filenames[] =  f.list();
	  	if (filenames!=null) {
	  		for (int i=0;i<filenames.length;i++) {
		  		DefaultMutableTreeNode child = new DefaultMutableTreeNode(filenames[i]);
		  		par.add(child);
		  		File fi = new File(path+"\\"+filenames[i]);
		  		if (fi.isDirectory()) {
		  			DefaultMutableTreeNode child2 = new DefaultMutableTreeNode();
	  		  		child.add(child2);
		  		}
		  	}
	  	}
	}
	
	public String conver(String disk) {
		String sub = disk.substring(disk.length()-3, disk.length()-1);
		sub+="\\";
		return sub;
	}
	
	public String conver2(String disk) {
		char dis = '0';
		String sub="";
		for (int i=0;i<disk.length();i++) {
			if (disk.charAt(i)=='(') {
				dis= disk.charAt(i+1);
				break;
			}
		}
		int id = 0;
		for (int i=0;i<disk.length();i++) {
			if (disk.charAt(i)==')') {
				id = i+1;
				break;
			}
		}
		sub+=String.valueOf(dis)+":"+"\\"+disk.substring(id);
		sub = sub.replace("\\\\", "\\");
		return sub;
	}
	
	public String conver3(String str) {
		int id = 0;
		for (int i = str.length()-1;i>=0;i--) {
			if (str.charAt(i)=='\\') {
				id = i;
				break;
			}
		}
		String res = str.substring(id+1);
		return res;
	}
	
	public void listFile(String path) {
		try {
			String pathStack;
			if (backpath.isEmpty()) backpath.push(path);
			if ((pathStack= backpath.peek())!=path) backpath.push(path);
			list_1.setListData(new File(path).listFiles());
		    list_1.setCellRenderer(new MyCellRenderer());
		    list_1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
		    list_1.setLayoutOrientation(javax.swing.JList.VERTICAL);
			pane.setViewportView(list_1);
			address.setText(path);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error");
		}
	}
	
	public void delete() {
		String []list = {"Yes","No"};		
		int check = JOptionPane.showOptionDialog(null,"Do you want to delete it?","Messeger",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,list,0);
		if (check==0) {
			String path = list_1.getSelectedValue().toString();
			try  {      
				File f= new File(path);
				if (f.isFile()) {
					if(f.delete()) {  
	    				JOptionPane.showMessageDialog(null, "Successful!");
	    			}  
	    			else  {  
	    				JOptionPane.showMessageDialog(null, "Failed");  
	    			}  
				} else if (f.isDirectory()) {
					deleteDir(f);
					JOptionPane.showMessageDialog(null, "Successful!");
				}		
			}  
			catch(Exception e1)  {  
				e1.printStackTrace();  
			}  
			
			if (backpath!=null) listFile(backpath.peek());
			for (int i=0;i<backpath.size();i++) {
				if (backpath.elementAt(i).contains(path)) {
					backpath.remove(backpath.indexOf(backpath.elementAt(i)));
					i--;
				}
			}
			if (!prepath.isEmpty()) if (path.equals(prepath.peek())) prepath.clear();
		}
		
	}
	
	public void paste() {
		if (iscut) {
			InputStream inStream = null;
	        OutputStream outStream = null;
	 
	        try {
	            inStream = new FileInputStream(new File(pathtemp));
	            outStream = new FileOutputStream(new File(backpath.peek()+"\\"+conver3(pathtemp)));	 
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
	        try  {         
    			File f= new File(pathtemp);  
    			if (f.isFile()) {
    				if(f.delete()) {  
	    				JOptionPane.showMessageDialog(null, "Successful!");
	    			}  
	    			else  {  
	    				JOptionPane.showMessageDialog(null, "Failed");  
	    			}  
    			} else if (f.isDirectory()) {
    				
    			}
    			
    		}  
    		catch(Exception e1)  {  
    			e1.printStackTrace();  
    		}  
		} else {
			InputStream inStream = null;
	        OutputStream outStream = null;
	 
	        try {
	            inStream = new FileInputStream(new File(pathtemp));
	            outStream = new FileOutputStream(new File(backpath.peek()+"\\"+conver3(pathtemp)));	 
	            int length;
	            byte[] buffer = new byte[1024];
	            while ((length = inStream.read(buffer)) > 0) {
	                outStream.write(buffer, 0, length);
	            }
	            JOptionPane.showMessageDialog(null, "File is copied successful!");
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
		if (backpath!=null)
			listFile(backpath.peek());
		isclick = false;
		pasteItem.setEnabled(false);	
	}
	
	public void filecliked(MouseEvent e) {
		if (e.getClickCount() == 2 && !SwingUtilities.isRightMouseButton(e)) {
			try  {  
				String path = list_1.getSelectedValue().toString();
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
					listFile(path);
				}
			} 
			catch(Exception e1)  {  
				JOptionPane.showMessageDialog(null, "Error");
			}  
        }
		if(SwingUtilities.isRightMouseButton(e)) {
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
	
	private static class MyCellRenderer extends DefaultListCellRenderer  {

        private static final long serialVersionUID = 1L;

        @Override
        public Component getListCellRendererComponent(JList list, Object value,
            int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof File) {
                File file = (File) value;
                setText(file.getName());
                setIcon(FileSystemView.getFileSystemView().getSystemIcon(file));
                if (isSelected) {
                    setBackground(list.getSelectionBackground());
                    setForeground(list.getSelectionForeground());
                } else {
                    setBackground(list.getBackground());
                    setForeground(list.getForeground());
                }
                setEnabled(list.isEnabled());
                setFont(list.getFont());
                setOpaque(true);
            }
            return this;
        }
    }
}
