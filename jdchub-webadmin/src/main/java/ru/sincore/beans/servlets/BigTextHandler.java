package ru.sincore.beans.servlets;

import ru.sincore.ConfigurationManager;
import ru.sincore.db.dao.BigTextDataDAOImpl;
import ru.sincore.util.AdcUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class BigTextHandler extends HttpServlet
{
	protected void doPost(HttpServletRequest request,
						  HttpServletResponse response)
			throws ServletException, IOException
	{
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		ConfigurationManager configInstance = ConfigurationManager.instance();
		BigTextDataDAOImpl bigText = new BigTextDataDAOImpl();
		
		

		try{
			/*
				Action available "send" or "update".
			 */
			String action = request.getParameter("action");
			String actionSrc = request.getParameter("rsrc");

			if (action.equals("update"))
			{
				String messageBody = request.getParameter("body");
				String selectedLocale = request.getParameter("locale");
				
				if (actionSrc.equals("motd"))
				{
					if (!selectedLocale.isEmpty() && !messageBody.isEmpty())
					{
						bigText.updateData("MOTD",selectedLocale, AdcUtils.toAdcString(messageBody.trim()));
					}
				}
				else if (actionSrc.equals("topic"))
				{
					if (!selectedLocale.isEmpty() && !messageBody.isEmpty())
					{
						bigText.updateData("TOPIC",selectedLocale, AdcUtils.toAdcString(messageBody.trim()));
					}
				}
				else if (actionSrc.equals("rules"))
				{
					if (!selectedLocale.isEmpty() && !messageBody.isEmpty())
					{
						bigText.updateData("RULES",selectedLocale, AdcUtils.toAdcString(messageBody.trim()));
					}
				}
				else {
					out.print("Unknown src :"+actionSrc);
				}
			}
			else if (action.equals("send"))
			{
				String id = request.getParameter("id");
				
				if (actionSrc.equals("motd"))
				{
					if (!id.isEmpty())
					{
						String body = bigText.getData("MOTD",id);
						out.print(AdcUtils.fromAdcString(body));
					}else {
						String body = bigText.getData("MOTD",configInstance.getString(ConfigurationManager.HUB_DEFAULT_LOCALE));
						out.print(AdcUtils.fromAdcString(body));
					}
				}
				else if (actionSrc.equals("topic"))
				{
					if (!id.isEmpty())
					{
						String body = bigText.getData("TOPIC",id);
						out.print(AdcUtils.fromAdcString(body));
					}else {
						String body = bigText.getData("TOPIC",configInstance.getString(ConfigurationManager.HUB_DEFAULT_LOCALE));
						out.print(AdcUtils.fromAdcString(body));
					}
				}
				else if (actionSrc.equals("rules"))
				{
					if (!id.isEmpty())
					{
						String body = bigText.getData("RULES",id);
						out.print(AdcUtils.fromAdcString(body));
					}else {
						String body = bigText.getData("RULES",configInstance.getString(ConfigurationManager.HUB_DEFAULT_LOCALE));
						out.print(AdcUtils.fromAdcString(body));
					}
				}
				else {
					out.print("Unknown src :"+actionSrc);
				}
			}else
			{
				out.print("Unknown action : "+action);
			}
			
			
		}finally {
			out.close();
		}
	}
}
