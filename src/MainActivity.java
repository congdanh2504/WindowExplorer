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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.SystemColor;
import javax.swing.JProgressBar;

public class MainActivity implements ActionListener, TreeSelectionListener, MouseListener{

	private JFrame frame;
	private JTree tree;
	private JList slidelist = new JList();
	private JScrollPane pane;
	private Stack<String> backpath = new Stack<>();
	private Stack<String> prepath = new Stack<>();
	private JPopupMenu popupMenu;
	private JMenuItem cutItem, copyItem, pasteItem,propertiesItem,deleteItem;
	private File pathtemp;
	private JLabel address;
	private JProgressBar progressBar;
	private boolean iscut= false;
	private JButton btnBack, btnPre;

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
		initView();
		initAction();
	}

	public void initView() {
		frame = new JFrame();
		frame.getContentPane().setBackground(Color.WHITE);
		frame.setBounds(350, 100, 700, 500);
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
		
	    tree = new JTree();
	    tree.setModel(new DefaultTreeModel(
	    	new DefaultMutableTreeNode() {
	    		{
	    			List <File>files = Arrays.asList(File.listRoots());
	    			for (File drv : files) {  
	    		          DefaultMutableTreeNode disk = new DefaultMutableTreeNode();
	    		          disk.setUserObject(drv);
	    		          this.add(disk);
		  		  	}
	    			this.setUserObject(new File("C:\\Users\\Admin\\OneDrive\\Desktop"));
	    		}
	    	}
	    ));	    
	    tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setCellRenderer(new FileTreeCellRenderer());
        JPanel bottom = new JPanel();
        bottom.setLayout(new BorderLayout());
        frame.getContentPane().add(bottom, BorderLayout.CENTER);
        pane = new JScrollPane();
		bottom.add(pane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		bottom.add(panel,BorderLayout.NORTH);
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
		address = new JLabel();
		address.setForeground(Color.BLACK);
		address.setOpaque(true);
		address.setBackground(Color.WHITE);
		address.setFont(new Font("Dialog", Font.BOLD, 12));
		progressBar = new JProgressBar();
		panel.add(progressBar, BorderLayout.EAST);
        progressBar.setVisible(false);
		progressBar.setForeground(Color.GREEN);
		progressBar.setBackground(Color.WHITE);
		panel.add(address,BorderLayout.CENTER);
		JScrollPane panes = new JScrollPane(tree);
		bottom.add(panes, BorderLayout.WEST);	
		
	}
	
	public void initAction() {
		btnBack.addActionListener(this);
		btnPre.addActionListener(this);	
		tree.addTreeSelectionListener(this);
		slidelist.addMouseListener(this);
		cutItem.addActionListener(this);
		copyItem.addActionListener(this);
		deleteItem.addActionListener(this);
		pasteItem.addActionListener(this);
		propertiesItem.addActionListener(this);
	}
	
	public void read(DefaultMutableTreeNode par,String path) {
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
		
	public void listFile(String path) {
		try {
			String pathStack;
			if (backpath.isEmpty()) backpath.push(path);
			if ((pathStack= backpath.peek())!=path) backpath.push(path);
			File fi = new File(path);
			File list[] = FileSystemView.getFileSystemView().getFiles(fi,true);
			slidelist.setListData(list);
			slidelist.setCellRenderer(new MyCellRenderer());
			slidelist.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
			slidelist.setLayoutOrientation(javax.swing.JList.VERTICAL);
			pane.setViewportView(slidelist);
			address.setText(path);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error");
		}
	}
	
	public void delete() {	
		String path = slidelist.getSelectedValue().toString();
		try  {      
			File f= new File(path);
			if (f.isFile()) {
				if(f.delete()) {  
    			}  
    			else  {  
    				JOptionPane.showMessageDialog(null, "Failed");  
    			}  
			} else if (f.isDirectory()) {
				deleteDir(f);
			}		
		}  
		catch(Exception e1)  {  
			e1.printStackTrace();  
		}  
		
		if (backpath!=null) execute("list", backpath.peek());
		for (int i=0;i<backpath.size();i++) {
			if (backpath.elementAt(i).contains(path)) {
				backpath.remove(backpath.indexOf(backpath.elementAt(i)));
				i--;
			}
		}
		if (!prepath.isEmpty()) if (path.equals(prepath.peek())) prepath.clear();		
	}
	
	public void paste() {
		File file = pathtemp;
		if (iscut) {
			if (file.isFile()) cutaFile(file);
			if (file.isDirectory()) {
				String source = pathtemp.getAbsolutePath();
				String target = backpath.peek()+"\\"+pathtemp.getName();
				File theDir = new File(target);
				if (!theDir.exists()){
				    theDir.mkdirs();    
				    copyaDir(source, target);
				}
				deleteDir(new File(source));
			}
		} else {
			if (file.isFile()) copyaFile(file,new File(backpath.peek()+"\\"+pathtemp.getName()));
			if (file.isDirectory()) {
				String source = pathtemp.getAbsolutePath();
				String target = backpath.peek()+"\\"+pathtemp.getName();
				File theDir = new File(target);
				if (!theDir.exists()){
				    theDir.mkdirs();    
				    copyaDir(source, target);
				}
			}
		}
		if (backpath!=null)
			execute("list", backpath.peek());
		pasteItem.setEnabled(false);	
	}
	
	void copyaDir(String source,String target) {
		File file = new File(source);
		File [] filenames = file.listFiles();
		if (filenames!=null) {
			for (int i=0;i<filenames.length;i++) {
				if (filenames[i].isDirectory()) {
					File theDir = new File(target+"\\"+filenames[i].getName());
				    theDir.mkdirs();    
				    copyaDir(filenames[i].toString(), target+"\\"+filenames[i].getName());
				}
				if (filenames[i].isFile()) {
					copyaFile(filenames[i],new File(target+"\\"+filenames[i].getName()));
				}
			}
		}	
	}
	
	void copyaFile(File file, File output) {
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
	
	void cutaFile(File file) {
		InputStream inStream = null;
        OutputStream outStream = null;
 
        try {
            inStream = new FileInputStream(file);
            outStream = new FileOutputStream(new File(backpath.peek()+"\\"+pathtemp.getName()));	 
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
			File f= pathtemp;  
			if (f.isFile()) {
				if(f.delete()) {  
					
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
	}
	
	public void filecliked(MouseEvent e) {
		if (e.getClickCount() == 2 && !SwingUtilities.isRightMouseButton(e)) {
			try  {  
				String path = slidelist.getSelectedValue().toString();
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
					execute("list", path);
					//listFile(path);
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
	
	void execute(String name, String path) {
		if (!name.equals("list")) {
			progressBar.setVisible(true);
	        progressBar.setIndeterminate(true);
		}
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            public Void doInBackground() {	
            	if (name.equals("paste")) paste();
            	else if (name.equals("delete")) delete();
            	else if (name.equals("list")) listFile(path);
                return null;
            }

            @Override
            protected void process(List<Void> chunks) {
                
            }

            @Override
            protected void done() {
            	if (!name.equals("list")) {
            		progressBar.setIndeterminate(false);
                    progressBar.setVisible(false);	    
        		}
        		          
            }
        };
        worker.execute();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		filecliked(e);
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
			    read(node,path);
			    execute("list", path);	
			}
		} catch(Exception e1) {	
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource().equals(copyItem)) {
			iscut = false;
			pathtemp = new File(slidelist.getSelectedValue().toString());	
			pasteItem.setEnabled(true);
		} else if (e.getSource().equals(cutItem)) {
			iscut = true;
			pathtemp = new File(slidelist.getSelectedValue().toString());
			pasteItem.setEnabled(true);
		} else if (e.getSource().equals(pasteItem)) {
			execute("paste","");
		} else if (e.getSource().equals(propertiesItem)) {
			new properties(slidelist.getSelectedValue().toString());
		} else if (e.getSource().equals(deleteItem)) {
			String []list = {"Yes","No"};
			int n = JOptionPane.showOptionDialog(null,"Do you want delete it?","Messeger",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE,null,list,0);
			if (n==0) {	
				execute("delete","");
			}
		} else if (e.getSource().equals(btnBack)) {
			if (!backpath.isEmpty()) {
				prepath.push(backpath.pop());
				if (!backpath.isEmpty()) {
					execute("list", backpath.pop());				
				}				
			}
		} else if (e.getSource().equals(btnPre)) {
			if (!prepath.isEmpty()) {
				execute("list", prepath.pop());
			}
		}
	}
	
	private static class MyCellRenderer extends DefaultListCellRenderer  {

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
	class FileTreeCellRenderer extends DefaultTreeCellRenderer {

	    private FileSystemView fileSystemView;
	    private JLabel label;
	    FileTreeCellRenderer() {
	        label = new JLabel();
	        label.setOpaque(true);
	        fileSystemView = FileSystemView.getFileSystemView();
	    }

	    @Override
	    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
	        boolean leaf, int row, boolean hasFocus) {

	        DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
	        File file = (File)node.getUserObject();
	        label.setIcon(fileSystemView.getSystemIcon(file));
	        label.setText(fileSystemView.getSystemDisplayName(file));
	        label.setToolTipText(file.getPath());

	        if (selected) {
	            label.setBackground(backgroundSelectionColor);
	            label.setForeground(textSelectionColor);
	        } else {
	            label.setBackground(backgroundNonSelectionColor);
	            label.setForeground(textNonSelectionColor);
	        }
	        return label;
	    }
	}
		
}
