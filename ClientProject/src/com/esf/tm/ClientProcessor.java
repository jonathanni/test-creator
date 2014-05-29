package com.esf.tm;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.esf.tm.serializable.FIBQuestion;
import com.esf.tm.serializable.MCQuestion;
import com.esf.tm.serializable.Message;
import com.esf.tm.serializable.Question;
import com.esf.tm.serializable.TFQuestion;
import com.esf.tm.serializable.Test;

public class ClientProcessor implements Runnable
{
	public volatile boolean isRunning;

	private boolean first = true;

	public ClientProcessor()
	{
		isRunning = true;
	}

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
					{
						System.out.println("PROCESSING");
						processMessage(i, i.getReader().getQueue().take());
						System.out.println("PROCESSED");
					}

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

	private void processMessage(ClientCommunicator cc, Message message)
	{
		String header = message.getHeader();
		Object payload = message.getPayload();

		if (header.equals("requestLogin"))
		{
			System.err.println("requestLogin");
			cc.getWriter().getQueue().add(new Message("requestPassword", null));
		} else if (header.equals("login"))
		{
			System.out.println(Arrays.toString((String[]) payload));
			if (((String[]) payload)[1].equals(TestGenerator.PASSWORD))
			{
				System.err.println("loginSuccess");
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
		} else if (header.equals("status"))
		{
			((JLabel) ((JPanel) TestGenerator
					.getInstance()
					.getNetPanel()
					.getComponent(
							TestGenerator.getInstance().getClients()
									.indexOf(cc))).getComponent(1))
					.setText("Progress: "
							+ ((Status) payload).getCurrentQuestion()
							+ "/"
							+ TestGenerator.getInstance().getTest()
									.getQuestionCount() + "<br />");
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
