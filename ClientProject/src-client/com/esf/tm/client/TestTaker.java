package com.esf.tm.client;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;

import org.javabuilders.BuildResult;
import org.javabuilders.swing.SwingJavaBuilder;

/**
 * 
 * Client front-end.
 * 
 * @author Jonathan Ni
 * @since 4/26/14
 * @version 0.0r2
 * 
 */

public class TestTaker extends JFrame implements MouseListener
{
    private static final long serialVersionUID = -5547354766805951582L;

    private static TestTaker instance;

    private BuildResult result;
    private JList ips;
    private DefaultListModel ipListModel = new DefaultListModel();

    private NodeScanner nScanner;
    private ServerCommunicator communicator;

    private Test currentTest;

    public static final int PORT = 3353;

    /**
     * 
     * Creates a new test generator message panel to be used by the teacher.
     * 
     */

    public TestTaker()
    {
	super();

	result = SwingJavaBuilder.build(this);

	ips.setModel(ipListModel);
	ips.addMouseListener(this);

	pack();
	setVisible(true);
	pack();

	new Thread(nScanner = new NodeScanner()).start();
    }

    void clearList()
    {
	ipListModel.removeAllElements();
    }

    void appendIP(String ip)
    {
	ipListModel.addElement(ip);
    }

    private void requestLogin(String ip)
    {
	SingleNodeScanner sns;
	new Thread(sns = new SingleNodeScanner(ip)).start();

	try
	{
	    while (!sns.isDone)
		Thread.sleep(10);
	} catch (InterruptedException e)
	{
	    e.printStackTrace();
	}

	if (!sns.isUp)
	{
	    JOptionPane.showMessageDialog(null, "The host " + ip
		    + " could not be connected to.", "Error",
		    JOptionPane.ERROR_MESSAGE);
	    return;
	}

	communicator = new ServerCommunicator(ip);

	// requestLogin

	communicator.getWriter().getQueue()
		.add(new Message("requestLogin", null));

	// receive requestPassword
	communicator.getReader().getQueue().poll();

	boolean flag = true;

	while (flag)
	{
	    String password = JOptionPane.showInputDialog("Enter the password");

	    // login
	    communicator.getWriter().getQueue()
		    .add(new Message("login", password));

	    Message recv = communicator.getReader().getQueue().poll();

	    if (recv.getHeader().equals("loginSuccess"))
		flag = false;
	    else if (recv.getHeader().equals("loginFailure"))
		// receive requestPassword
		communicator.getReader().getQueue().poll();
	}

	// receiveTest
	currentTest = (Test) communicator.getReader().getQueue().poll()
		.getPayload();
    }

    /**
     * 
     * Destroys the program.
     * 
     * @param callback
     *            the "function" to call before exiting.
     */
    static void destroy(Callback callback)
    {
	callback.cbFunction();
	System.exit(0);
    }

    /**
     * 
     * Function necessary for javabuilders to call in order to destroy the
     * window.
     * 
     */

    private void windowDestroy()
    {
	destroy(new Callback());
    }

    public static TestTaker getInstance()
    {
	return instance;
    }

    public static void main(String[] args)
    {
	instance = new TestTaker();
    }

    @Override
    public void mouseClicked(MouseEvent event)
    {
	JList list = (JList) event.getSource();

	if (event.getClickCount() == 2)
	{
	    String ip = (String) ipListModel.get(list.getSelectedIndex());
	    requestLogin(ip);
	}
    }

    @Override
    public void mouseEntered(MouseEvent event)
    {

    }

    @Override
    public void mouseExited(MouseEvent event)
    {

    }

    @Override
    public void mousePressed(MouseEvent event)
    {

    }

    @Override
    public void mouseReleased(MouseEvent event)
    {

    }

}
