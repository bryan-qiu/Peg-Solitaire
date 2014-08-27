/** 
 * The "MainFrame" class
 * Handles the menu and adds panels for a game of Peg Solitaire
 * @author Alvin Qiu and Bryan Qiu
 * @version January 2012
 */

import java.awt.*;
import javax.swing.*;

import java.awt.event.*;

@SuppressWarnings("serial")
public class MainFrame extends JFrame implements ActionListener, WindowListener
{
	private PegSolitaireBoard gamePanel;
	private SidePanel sidePanel;
	private JMenuItem newOption, exitOption, instructionsOption, aboutOption;
	private JRadioButtonMenuItem hintOnOption, hintOffOption, waitOption1, waitOption3, waitOption5, waitOption7;

	public MainFrame()
	{
		// Set up Frame
		super("Peg Solitaire");
		setResizable(false);
		this.addWindowListener(this);
		
		// Set icon
		setIconImage(Toolkit.getDefaultToolkit().getImage("Images\\PegSolitaireIcon.gif"));

		gamePanel = new PegSolitaireBoard();
		sidePanel = new SidePanel(gamePanel);
		gamePanel.addSidePanel(sidePanel);
		Container contentPane = getContentPane();
		contentPane.add(gamePanel, BorderLayout.WEST);
		contentPane.add(sidePanel, BorderLayout.EAST);

		addMenus();
		
		pack();
		// Centre the frame in the middle (almost) of the screen
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screen.width - this.getWidth()) / 2, (screen.height - this.getHeight()) / 2);
	} // Constructor

	/**
	 * Adds the menus to the main frame Includes adding ActionListeners to respond to menu commands
	 */
	private void addMenus()
	{
		// Set up the Game MenuItems
		newOption = new JMenuItem("New");
		newOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
		newOption.setMnemonic('N');
		newOption.addActionListener(this);

		exitOption = new JMenuItem("Exit");
		exitOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
		exitOption.setMnemonic('X');
		exitOption.addActionListener(this);

		// Add each MenuItem to the Game Menu (with a separator)
		JMenu gameMenu = new JMenu("Game");
		gameMenu.setMnemonic('G');
		gameMenu.add(newOption);
		gameMenu.addSeparator();
		gameMenu.add(exitOption);

		// Set up the Hint On/Off radio buttons
		ButtonGroup hintOnOffGroup = new ButtonGroup();
		hintOnOption = new JRadioButtonMenuItem("Hint On", false);
		hintOnOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
		hintOnOption.setMnemonic('O');
		hintOnOption.addActionListener(this);

		hintOffOption = new JRadioButtonMenuItem("Hint Off", true);
		hintOffOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
		hintOffOption.setMnemonic('F');
		hintOffOption.addActionListener(this);

		hintOnOffGroup.add(hintOnOption);
		hintOnOffGroup.add(hintOffOption);

		// Set up the Wait time menu and radio Buttons
		JMenu waitTimeSubMenu = new JMenu("Hint Wait Time");
		waitTimeSubMenu.setMnemonic('W');
		ButtonGroup waitTimeGroup = new ButtonGroup();

		waitOption1 = new JRadioButtonMenuItem("1 second", false);
		waitOption1.setMnemonic('1');
		waitOption1.addActionListener(this);
		waitOption3 = new JRadioButtonMenuItem("3 second", true);
		waitOption3.setMnemonic('3');
		waitOption3.addActionListener(this);
		waitOption5 = new JRadioButtonMenuItem("5 second", false);
		waitOption5.setMnemonic('5');
		waitOption5.addActionListener(this);
		waitOption7 = new JRadioButtonMenuItem("7 second", false);
		waitOption7.setMnemonic('7');
		waitOption7.addActionListener(this);

		waitTimeGroup.add(waitOption1);
		waitTimeGroup.add(waitOption3);
		waitTimeGroup.add(waitOption5);
		waitTimeGroup.add(waitOption7);
		waitTimeSubMenu.add(waitOption1);
		waitTimeSubMenu.add(waitOption3);
		waitTimeSubMenu.add(waitOption5);
		waitTimeSubMenu.add(waitOption7);

		// Add each MenuItem to Board Type Menu
		JMenu hintMenu = new JMenu("Hint");
		hintMenu.setMnemonic('H');
		hintMenu.add(hintOnOption);
		hintMenu.add(hintOffOption);
		hintMenu.addSeparator();
		hintMenu.add(waitTimeSubMenu);

		// Set up the Help MenuItems
		instructionsOption = new JMenuItem("Instructions", 'I');
		instructionsOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
		instructionsOption.setMnemonic('I');
		instructionsOption.addActionListener(this);

		aboutOption = new JMenuItem("About", 'A');
		aboutOption.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
		aboutOption.setMnemonic('A');
		aboutOption.addActionListener(this);

		// Add each MenuItem to the Help Menu (with a separator)
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('E');
		helpMenu.add(instructionsOption);
		helpMenu.add(aboutOption);

		JMenuBar mainMenu = new JMenuBar();
		mainMenu.add(gameMenu);
		mainMenu.add(hintMenu);
		mainMenu.add(helpMenu);
		// Set the menu bar for this frame to mainMenu
		setJMenuBar(mainMenu);
	}

	/**
	 * Responds to a Menu Event. This method is needed since our Connect Four frame implements ActionListener
	 * @param event the event that triggered this method
	 */
	@SuppressWarnings("deprecation")
	public void actionPerformed(ActionEvent event)
	{
		if (event.getSource() == newOption) // Selected "New"
		{
			gamePanel.newGame();
		}
		else if (event.getSource() == exitOption) // Selected "Exit"
		{
			hide();
			System.exit(0);
		}
		else if (event.getSource() == hintOnOption) // Selected hint on
		{
			gamePanel.hintOn();
			sidePanel.hintOn();
		}
		else if (event.getSource() == hintOffOption) // Selected hint off
		{
			gamePanel.hintOff();
			sidePanel.hintOff();
		}
		else if (event.getSource() == waitOption1) // Selected 1 sec
		{
			gamePanel.changeWaitTime(1);
			sidePanel.changeWaitTime(1);
		}
		else if (event.getSource() == waitOption3) // Selected 3 sec
		{
			gamePanel.changeWaitTime(3);
			sidePanel.changeWaitTime(3);
		}
		else if (event.getSource() == waitOption5) // Selected 5 sec
		{
			gamePanel.changeWaitTime(5);
			sidePanel.changeWaitTime(5);
		}
		else if (event.getSource() == waitOption7) // Selected 7 sec
		{
			gamePanel.changeWaitTime(7);
			sidePanel.changeWaitTime(7);
		}
		else if (event.getSource() == instructionsOption) // Selected "Instructions"
		{
			HelpInstructions.createAndShowGUI(); // Display
		}
		else if (event.getSource() == aboutOption) // Selected "About"
		{
			JOptionPane.showMessageDialog(this, "By Alvin Qiu and Bryan Qiu" + "\n\u00a9 2012", "About Peg Solitaire",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	// Window Listener methods
	
	/**
	 * Responds to a windowIconified event
	 * @param event information about the window event
	 */
	public void windowIconified(WindowEvent e)
	{
		gamePanel.minimizePause();
	}

	/**
	 * Responds to a windowDeiconified event
	 * @param event information about the window event
	 */
	public void windowDeiconified(WindowEvent e)
	{
		gamePanel.unminimizeUnpause();
	}
	
	// Un-used window listener methods
	public void windowOpened(WindowEvent e)
	{
	}
	
	public void windowClosing(WindowEvent e)
	{
	}
	
	public void windowClosed(WindowEvent e)
	{
	}

	public void windowActivated(WindowEvent e)
	{
	}

	public void windowDeactivated(WindowEvent e)
	{
	}

	public static void main(String[] args) throws Exception
	{
		MainFrame frame = new MainFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);

	} // main method
} // MainFrame class
