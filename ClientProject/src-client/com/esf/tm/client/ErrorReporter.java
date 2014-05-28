package com.esf.tm.client;


import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JOptionPane;

/**
 * 
 * Error reporter.
 * 
 * @author Jonathan Ni
 * @since 4/26/14
 * @version 0.0r1
 * 
 */

class ErrorReporter
{

	/**
	 * 
	 * Produce an error report. This will include a small message, as well as a
	 * possible stack trace.
	 * 
	 * @param o
	 */
	static void reportError(Object errormsg, Object stacktrace)
	{
		final String msg = errormsg.toString();
		final String stackTrace = stacktrace.toString();
		TestTaker.destroy(new Callback()
		{
			@Override
			public void cbFunction()
			{
				String addmsg = "", path = "";

				PrintStream out = null;
				File errorReport = null;
				try
				{
					out = new PrintStream(errorReport = new File(
							"error_report.log"));
					path = errorReport.getCanonicalPath();
				} catch (IOException e)
				{
					e.printStackTrace();
				}

				out.println("CLIENT Error report generated on "
						+ new SimpleDateFormat("yyyyMMdd_HHmmss")
								.format(Calendar.getInstance().getTime()));
				out.println();
				out.println("-------------------- [ BEGIN ERROR MESSAGE ] --------------------");
				out.println();

				out.println(msg);

				out.println();
				out.println("-------------------- [ BEGIN STACK TRACE ] --------------------");
				out.println();

				out.println(stackTrace);

				out.flush();

				out.close();

				addmsg = "\nAn error report has been generated at "
						+ path
						+ ".\nThis file may be emailed directly to support at phs.esfsystem@gmail.com";

				JOptionPane.showMessageDialog(null, msg.toString() + addmsg,
						"Crash Reporter", JOptionPane.ERROR_MESSAGE);
			}
		});
	}
}
