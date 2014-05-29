package com.esf.tm;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.esf.tm.serializable.FIBQuestion;
import com.esf.tm.serializable.MCQuestion;
import com.esf.tm.serializable.Message;
import com.esf.tm.serializable.Question;
import com.esf.tm.serializable.Status;
import com.esf.tm.serializable.TFQuestion;
import com.esf.tm.serializable.Test;
import com.esf.tm.serializable.TestAnswer;

/**
 * 
 * Processes all the current clients, and responds to the data.
 * 
 * @author Jonathan Ni
 * @since 5/22/14
 * @version 0.0r1
 * 
 */

public class ClientProcessor implements Runnable
{
    public volatile boolean isRunning;

    private boolean first = true;

    /**
     * 
     * Creates a new client processor.
     * 
     */

    public ClientProcessor()
    {
	isRunning = true;
    }

    /**
     * 
     * Runs the client processing loop. Specifically, continuously goes through
     * a loop of all the communicators, reads them if input is available, and
     * processes them. Stops when no connections are left.
     * 
     */

    @Override
    public void run()
    {
	try
	{
	    while (isRunning)
	    {
		boolean flag = true;
		for (ClientCommunicator i : TestGenerator.getInstance()
			.getClients())
		{
		    if (i.isRunning && !i.getReader().getQueue().isEmpty())
			processMessage(i, i.getReader().getQueue().take());

		    if (i.isRunning)
			flag = false;
		}

		if (flag
			&& TestGenerator.getInstance().getClients().size() != 0)
		    isRunning = false;

		Thread.sleep(1000);
	    }
	} catch (InterruptedException e)
	{
	    e.printStackTrace();
	}
    }

    /**
     * 
     * Respond to a message.
     * 
     * These are the responses bound to each packet:
     * 
     * <table>
     * <th>Server Receives</th>
     * <th>Server Sends</th>
     * <tr>
     * requestLogin
     * </tr>
     * <tr>
     * requestPassword</li>
     * <tr>
     * login
     * </tr>
     * <tr>
     * If password is correct, loginSuccess, otherwise loginFailure and
     * requestPassword
     * </tr>
     * <tr>
     * status
     * </tr>
     * <tr>
     * Nothing, but updates the server GUI
     * </tr>
     * <tr>
     * sendTest
     * </tr>
     * <tr>
     * Grades the test then sends receiveScore
     * </tr>
     * <tr>
     * exit
     * </tr>
     * <tr>
     * exit
     * </tr>
     * </table>
     * 
     * @param cc
     *            the communicator
     * @param message
     *            the message input
     */

    private void processMessage(ClientCommunicator cc, Message message)
    {
	String header = message.getHeader();
	Object payload = message.getPayload();

	if (header.equals("requestLogin"))
	    cc.getWriter().getQueue().add(new Message("requestPassword", null));
	else if (header.equals("login"))
	    if (((String[]) payload)[1].equals(TestGenerator.PASSWORD))
	    {
		TestGenerator.getInstance().addClientNode();
		((JLabel) ((JPanel) TestGenerator
			.getInstance()
			.getNetPanel()
			.getComponent(
				TestGenerator.getInstance().getClients()
					.indexOf(cc))).getComponent(0))
			.setText("Name: " + ((String[]) payload)[0] + "<br />");

		cc.getWriter().getQueue()
			.add(new Message("loginSuccess", null));
	    } else
	    {
		System.err.println("loginFailure");
		cc.getWriter().getQueue()
			.add(new Message("loginFailure", null));
		cc.getWriter().getQueue()
			.add(new Message("requestPassword", null));
	    }
	else if (header.equals("status"))
	{
	    ((JLabel) ((JPanel) TestGenerator
		    .getInstance()
		    .getNetPanel()
		    .getComponent(
			    TestGenerator.getInstance().getClients()
				    .indexOf(cc))).getComponent(1))
		    .setText("<html>Progress: "
			    + ((Status) payload).getCurrentQuestion()
			    + "/"
			    + TestGenerator.getInstance().getTest()
				    .getQuestionCount() + "<br /></html>");
	} else if (header.equals("sendTest"))
	{
	    // TODO check test
	    TestAnswer answers = (TestAnswer) payload;
	    Test test = cc.getTest();

	    int points = 0;

	    for (int i = 0; i < test.getQuestionCount(); i++)
	    {
		Question j = test.getQuestion(i);

		if (j instanceof MCQuestion
			&& ((MCQuestion) j).getChoice(
				(Integer) answers.getAnswers().get(i))
				.getChoiceID() == ((MCQuestion) j)
				.getCorrectAnswer())
		    points++;
		else if (j instanceof TFQuestion
			&& ((TFQuestion) j).getCorrectAnswer() == (Boolean) answers
				.getAnswers().get(i))
		    points++;
		else
		{
		    for (int k = 0; k < ((FIBQuestion) j).getCorrectAnswers()
			    .size(); k++)
			for (String l : ((FIBQuestion) j).getCorrectAnswers()
				.get(k))
			{
			    String guess = new String(
				    ((ArrayList<String>) answers.getAnswers()
					    .get(i)).get(k));
			    String answer = new String(l);

			    if (!((FIBQuestion) j).isCaseSensitive())
			    {
				guess = guess.toUpperCase();
				answer = answer.toUpperCase();
			    }

			    if (!((FIBQuestion) j).isWhitespaceSensitive())
			    {
				guess = guess.replace(" ", "");
				answer = answer.replace(" ", "");
			    }

			    if (guess.equals(answer))
				points++;
			}
		}
	    }

	    cc.getWriter().getQueue().add(new Message("receiveScore", points));
	} else if (header.equals("exit"))
	{
	    cc.getWriter().getQueue().add(new Message("exit", null));

	    // pause
	    try
	    {
		Thread.sleep(1000);
	    } catch (InterruptedException e)
	    {
		e.printStackTrace();
	    }

	    cc.isRunning = false;

	    cc.getReader().isRunning = false;
	    cc.getWriter().isRunning = false;
	}
    }

    /**
     * 
     * Triggered by the distribution button. Sends tests to all clients.
     * 
     */

    void sendTest()
    {
	for (ClientCommunicator i : TestGenerator.getInstance().getClients())
	    if (i.isRunning)
		i.getWriter()
			.getQueue()
			.add(new Message("receiveTest", i.setTest(TestGenerator
				.getInstance().getTest().mix())));
    }
}
