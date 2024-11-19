package pv211.javaServer;
import javafx.application.Application;
import java.io.*;
import javafx.stage.Stage;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import javax.swing.*;
import java.awt.event.MouseListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.FlowLayout;
import java.awt.Image;
import javax.imageio.ImageIO;
import java.awt.Dimension;
/**
 * JavaFX App
 */
public class App extends Application {
	static DataOutputStream dataOutputStream = null;//stream object of data output
	static DataInputStream dataInputStream = null;//stream object of data input
    @Override
    public void start(Stage stage) {
    	new File("images").mkdirs();
    	while (true) {
    		try (ServerSocket serverSocket = new ServerSocket(900)) {//socket for server
        		Socket clientSocket = serverSocket.accept();//socket object
        		Thread thread = new Thread(new Runnable() {//thread of execution in program
        			public void run() {
    	    			try {
    	    				dataInputStream = new DataInputStream(clientSocket.getInputStream());//stream object of data input
    	    	        	dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());//stream object of data output
    	    	        	Random random = new Random();//generation stream of random numbers
    	    	        	String text = "images/file.txt", name = new String(), prefix = String.valueOf(random.nextInt(333));//path, title and prefix number
    	    	        	receiveFile(text);
    	    	        	try (FileReader reader = new FileReader(text)) {//providing functionality for reading text files
    	    	        		int c;
    	    	        		while ((c = reader.read()) != -1) name += ((char)c);//getting name from array
    	    	        	}
    	    	        	catch(IOException iOException) { }
    	    	        	String fullName = "images/" + prefix + name;//getting full address
    	    	        	receiveFile(fullName);
    	    	        	closeObj(dataInputStream, dataOutputStream, clientSocket);
    	    	        	JFrame jFrame = new JFrame();//frame object
    	    	        	File folder = new File("images");//file object
    	    	        	DefaultListModel<String> dlm = new DefaultListModel<String>();//presently delegation to vector
    	    	        	for (int i = 0; i < folder.listFiles().length; i++) dlm.add(i, folder
    	    	        			.listFiles()[i].getName());
    	    	        	JList<String> jList = new JList<String>(dlm);//displaying list of objects and allowing user to select item
    	    	        	MouseListener mouseListener = new MouseAdapter() { public void mouseClicked(MouseEvent mouseEvent) { if (mouseEvent.getClickCount() == 2) {//listener interface for receiving mouse events on component
    	    	        		String selectedItem = (String)jList.getSelectedValue();//selected feature
    	    	        		JFrame frame = new JFrame();//frame object
    	    	        		JPanel jPanel = new JPanel();//panel object
    	    	        		jPanel.setLayout(new FlowLayout(FlowLayout.LEADING));
    	    	        		frame.setTitle(selectedItem);
    	    	        		File file = new File(folder + "/" + selectedItem);//file object
    	    	        		try {
    	    	        			Image image = ImageIO.read(file);//image object
    	    	        			JLabel jLabel = new JLabel(new ImageIcon(image));//label object
    	    	        			addComp(frame, jPanel, jLabel);
    	    	        		}
    	    	        		catch(IOException exception) { }
    	    	        		frame.setPreferredSize(new Dimension(300, 300));
    	    	        		JFrame.setDefaultLookAndFeelDecorated(true);
    	    	        		frame.pack();
    	    	        		frame.setLocationRelativeTo(null);
    	    	        		frame.setVisible(true);
    	    	        	}
    	    	        	}
    	    	        	};
    	    	        	jList.addMouseListener(mouseListener);
    	    	        	jFrame.add(jList);
    	    	        	jFrame.setPreferredSize(new Dimension(300, 300));
    	    	        	JFrame.setDefaultLookAndFeelDecorated(true);
    	    	        	jFrame.pack();
    	    	        	jFrame.setLocationRelativeTo(null);
    	    	        	jFrame.setVisible(true);
    	    			}
    	    			catch(Exception exclusion) { }
        			}
        		});
        		thread.start();
        	}
        	catch(Exception e) { }
    	}
    }
    public static void main(String[] args) {
        launch();
    }
    static void receiveFile(String fileName) throws Exception {
    	int bytes = 0;//bytes for file download
    	FileOutputStream fileOutputStream = new FileOutputStream(fileName);//stream object of file output
    	long size = dataInputStream.readLong();//size of input file
    	byte[] buffer = new byte[4 * 1024];//byte array for file download
    	while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
    		fileOutputStream.write(buffer, 0, bytes);
    		size -= bytes;//size decrease of input file
    	}
    	fileOutputStream.close();
    }
    static void closeObj(DataInputStream inpStream, DataOutputStream outpStream, Socket custSocket) throws IOException {
    	inpStream.close();
    	outpStream.close();
    	custSocket.close();
    }
    static void addComp(JFrame shot, JPanel jPanel, JLabel label) {
		jPanel.add(label);
		shot.add(jPanel);
	}
}