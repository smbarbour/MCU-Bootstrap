package org.mcupdater;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.ImageIcon;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class BootstrapForm extends JWindow
	implements TrackerListener {
	private static final ResourceBundle Customization = ResourceBundle.getBundle("customization"); //$NON-NLS-1$
	
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JProgressBar progressBar;
	private JLabel lblStatus;
	private Distribution distro;
	private File basePath = new File("/home/sbarbour/Bootstrap-test");
	private String[] passthroughParams;
	
	/**
	 * Launch the application.
	 */
	public static void main(final String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
						//System.out.println(info.getName() + " : " + info.getClassName());
				        if ("Nimbus".equals(info.getName())) {
				            UIManager.setLookAndFeel(info.getClassName());
				            break;
				        }
				    }
					if (UIManager.getLookAndFeel().getName().equals("Metal")) {
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					}
					BootstrapForm frame = new BootstrapForm();
					frame.setPassthroughParams(args);
					frame.setLocationRelativeTo( null );
					frame.setVisible(true);
					frame.doWork();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void setPassthroughParams(String[] args) {
		this.passthroughParams = args;
	}
	
	protected void doWork() {
// *** Debug section
		System.out.println("System.getProperty('os.name') == '" + System.getProperty("os.name") + "'");
		System.out.println("System.getProperty('os.version') == '" + System.getProperty("os.version") + "'");
		System.out.println("System.getProperty('os.arch') == '" + System.getProperty("os.arch") + "'");
		System.out.println("System.getProperty('java.version') == '" + System.getProperty("java.version") + "'");
		System.out.println("System.getProperty('java.vendor') == '" + System.getProperty("java.vendor") + "'");
		System.out.println("System.getProperty('sun.arch.data.model') == '" + System.getProperty("sun.arch.data.model") + "'");
// ***
		PlatformType thisPlatform = PlatformType.valueOf(System.getProperty("os.name").toUpperCase() + System.getProperty("sun.arch.data.model"));
		distro = DistributionParser.loadFromURL("file:///home/sbarbour/SampleDistribution.xml", "MCUDev-2.6.34", System.getProperty("java.version").substring(0,3), thisPlatform);
		Collection<Downloadable> dl = new ArrayList<Downloadable>();
		for (Library l : distro.getLibraries()) {
			Downloadable dlEntry = new Downloadable(l.getName(),l.getFilename(),l.getMd5(),l.getSize(),l.getDownloadURLs());
			dl.add(dlEntry);
		}
		DownloadQueue queue = new DownloadQueue("Bootstrap", this, dl, basePath );
		queue.processQueue(new ThreadPoolExecutor(0, 1, 500, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>()));		
	}

	/**
	 * Create the frame.
	 */
	public BootstrapForm() {
		// setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel progressPanel = new JPanel();
		progressPanel.setBorder(new EmptyBorder(3, 0, 0, 0));
		contentPane.add(progressPanel, BorderLayout.SOUTH);
		progressPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel primaryProgress = new JPanel();
		progressPanel.add(primaryProgress, BorderLayout.CENTER);
		primaryProgress.setLayout(new BorderLayout(0, 0));
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		primaryProgress.add(progressBar, BorderLayout.CENTER);
		
		lblStatus = new JLabel("Downloading MCUpdater v3.1.0");
		primaryProgress.add(lblStatus, BorderLayout.SOUTH);
		lblStatus.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				System.exit(0);
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
			
		});
		
		JPanel logoPanel = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 8686753828984892019L;
			ImageIcon image = new ImageIcon(BootstrapForm.class.getResource("/org/mcupdater/bg_main.png"));
			
			@Override
			protected void paintComponent(Graphics g) {
				Image source = this.image.getImage();
				int w = source.getWidth(null);
				int h = source.getHeight(null);
				BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g2d = (Graphics2D)image.getGraphics();
				g2d.drawImage(source, 0, 0, null);
				g2d.dispose();
		        int width = getWidth();  
		        int height = getHeight();  
		        int imageW = image.getWidth(this);  
		        int imageH = image.getHeight(this);  
		   
		        // Tile the image to fill our area.  
		        for (int x = 0; x < width; x += imageW) {  
		            for (int y = 0; y < height; y += imageH) {  
		                g.drawImage(image, x, y, this);  
		            }  
		        }  
			}
			
			
		};
		contentPane.add(logoPanel, BorderLayout.CENTER);
		logoPanel.setLayout(new BorderLayout(0, 0));
		
		JLabel lblLogo = new JLabel("");
		lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
		lblLogo.setIcon(new ImageIcon(BootstrapForm.class.getResource("/org/mcupdater/mcu-logo-new.png")));
		logoPanel.add(lblLogo, BorderLayout.CENTER);
		
		setSize(480, 250);
	}

	@Override
	public void onQueueFinished(DownloadQueue queue) {
		// TODO Auto-generated method stub
		System.out.println("Finished");
		lblStatus.setText("Finished!");
		StringBuilder sbClassPath = new StringBuilder();
		for (Library lib : distro.getLibraries()){
			sbClassPath.append(cpDelimiter() + (new File(basePath, lib.getFilename())).getAbsolutePath());
		}
		StringBuilder sbParams = new StringBuilder();
		sbParams.append(distro.getParams());
		try {
			String javaBin = "java";
			File binDir;
			if (System.getProperty("os.name").startsWith("Mac")) {
				binDir = new File(new File(System.getProperty("java.home")), "Commands");
			} else {
				binDir = new File(new File(System.getProperty("java.home")), "bin");
			}
			if( binDir.exists() ) {
				javaBin = (new File(binDir, "java")).getAbsolutePath();
			}
			List<String> args = new ArrayList<String>();
			args.add(javaBin);
			args.add("-cp");
			args.add(sbClassPath.toString().substring(1));
			args.add(distro.getMainClass());
			if (distro.getParams() != null) { args.add(distro.getParams());}
			for (String param : this.passthroughParams) {
				args.add(param);
			}
			String[] params = args.toArray(new String[0]);
			System.out.println(Arrays.toString(params));
			Process p = Runtime.getRuntime().exec(params);
			if (p != null) {
				Thread.sleep(5000);
				System.exit(0);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onQueueProgress(DownloadQueue queue) {
		lblStatus.setText("Downloading: " + queue.getName());
		progressBar.setValue((int) (queue.getProgress()*100.0F));
	}

	@Override
	public void printMessage(String msg) {
		System.out.println(msg);
	}

	private String cpDelimiter() {
		String osName = System.getProperty("os.name");
		if (osName.startsWith("Windows")) {
			return ";";
		} else {
			return ":";
		}
	}

}