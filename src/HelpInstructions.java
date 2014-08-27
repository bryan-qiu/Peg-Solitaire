/** 
 * The "HelpInstructions" class
 * Handles the Help Instructions Manual
 * @author Alvin Qiu and Bryan Qiu
 * @version January 2012
 */

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import java.net.URL;
import java.io.IOException;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;

@SuppressWarnings("serial")
public class HelpInstructions extends JPanel implements TreeSelectionListener
{
	private JEditorPane contentPane;
	private JTree tree;

	/**
	 * Sets up the tree where the manual will be displayed
	 */
	public HelpInstructions()
	{
		super(new GridLayout(1, 0));

		// Create the nodes.
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(new ContentInfo("Instructions", "Instructions"));
		createNodes(top);

		// Create a tree that allows one selection at a time.
		tree = new JTree(top);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

		// Listen for when the selection changes.
		tree.addTreeSelectionListener(this);

		// Create the scroll pane and add the tree to it.
		JScrollPane treeView = new JScrollPane(tree);

		// Create the HTML viewing pane, where the instructions are
		contentPane = new JEditorPane();
		contentPane.setEditable(false);
		initHelp();
		JScrollPane contentView = new JScrollPane(contentPane);

		// Add the scroll panes to a split pane.
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		splitPane.setTopComponent(treeView);
		splitPane.setBottomComponent(contentView);

		// Sets up the dimensions of the panes
		Dimension minimumSize = new Dimension(100, 50);
		contentView.setMinimumSize(minimumSize);
		treeView.setMinimumSize(minimumSize);
		splitPane.setDividerLocation(205);
		splitPane.setPreferredSize(new Dimension(650, 350));

		// Add the split pane to this panel.
		add(splitPane);
	}

	/**
	 * Decides what to display when nodes are clicked
	 */
	public void valueChanged(TreeSelectionEvent e)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

		if (node == null)
			return;

		Object nodeInfo = node.getUserObject();
		// If the node is a leaf, display the HTML file relating to it
		if (node.isLeaf())
		{
			URL url = ((ContentInfo) nodeInfo).getURL();
			try
			{
				contentPane.setPage(url);
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
		// Otherwise, display the name of the node
		else
		{
			contentPane.setText(((ContentInfo) nodeInfo).toString());
		}
	}

	/**
	 * The "ContentInfo" nested class Handles information of each node
	 * @author Alvin Qiu and Bryan Qiu
	 * @version January 2012
	 */

	private class ContentInfo
	{
		private String contentName;
		private URL fileName;

		/**
		 * Creates a new ContentInfo Object
		 * @param contentName the name of the node
		 * @param fileName the name of the HTML file that contains information relating to the node
		 */
		public ContentInfo(String contentName, String fileName)
		{
			this.contentName = contentName;
			this.fileName = getClass().getResource("/Help Files/" + fileName);
			if (this.contentName == null)
			{
				System.err.println("Couldn't find file: " + fileName);
			}
		}

		/**
		 * Returns the name of the node
		 * @return the name of the node
		 */
		public String toString()
		{
			return contentName;
		}

		/**
		 * Returns the HTML file name
		 * @return the HTML file name
		 */
		public URL getURL()
		{
			return fileName;
		}
	}

	/**
	 * The initial display when the tree is opened
	 */
	private void initHelp()
	{
		contentPane.setText("Instructions about the game of Peg Solitaire");
	}

	/**
	 * Creates the nodes in the tree
	 * @param top
	 */
	private void createNodes(DefaultMutableTreeNode top)
	{
		DefaultMutableTreeNode category = null;
		DefaultMutableTreeNode ins = null;

		// New node, about Peg Solitaire
		category = new DefaultMutableTreeNode(new ContentInfo("About Peg Solitaire", "About Peg Solitaire"));
		top.add(category);

		// New tree node, What is Peg Solitaire
		ins = new DefaultMutableTreeNode(new ContentInfo("What is Peg Solitaire", "What is Peg Solitaire.html"));
		category.add(ins);

		// New tree node, How to Play
		ins = new DefaultMutableTreeNode(new ContentInfo("How to Play", "How to Play.html"));
		category.add(ins);

		// New node, About this Application
		category = new DefaultMutableTreeNode(new ContentInfo("About this Application", "About this Application"));
		top.add(category);

		// New tree node, Basic Controls
		ins = new DefaultMutableTreeNode(new ContentInfo("Basic Game Controls", "Basic Game Controls.html"));
		category.add(ins);

		// New tree node, Board Types and Setups
		ins = new DefaultMutableTreeNode(new ContentInfo("Board Types and Setups", "Board Types and Setups.html"));
		category.add(ins);

		// New tree node, Hints
		ins = new DefaultMutableTreeNode(new ContentInfo("Hints", "Hints.html"));
		category.add(ins);

		// New tree node, Find Solution
		ins = new DefaultMutableTreeNode(new ContentInfo("Find Solution", "Find Solution.html"));
		category.add(ins);

		// New node, Other Features
		category = new DefaultMutableTreeNode(new ContentInfo("Other Features", "Other Features"));
		top.add(category);

		// New tree node, Other Features
		ins = new DefaultMutableTreeNode(new ContentInfo("Other Features", "Other Features.html"));
		category.add(ins);

		// New tree node, List of Short keys
		ins = new DefaultMutableTreeNode(new ContentInfo("List of Short Keys", "List of Short Keys.html"));
		category.add(ins);
	}

	/**
	 * Creates the GUI and shows it.
	 */
	public static void createAndShowGUI()
	{
		// Create and set up the window.
		JFrame helpFrame = new JFrame("Peg Solitaire - Help Instructions");
		helpFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// Set icon
		helpFrame.setIconImage(Toolkit.getDefaultToolkit().getImage("Images\\PegSolitaireIcon.gif"));

		// Add content to the window.
		helpFrame.add(new HelpInstructions());

		// Display the window.
		helpFrame.pack();
		helpFrame.setVisible(true);
		
		// Centre the frame in the middle (a bit down and right) of the screen
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		helpFrame.setLocation((screen.width - helpFrame.getWidth()) / 2 + 50, (screen.height - helpFrame.getHeight()) / 2 + 50);
	}
}
