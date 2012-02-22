package ru.sincore.beans.servlets;

import ru.sincore.ConfigurationManager;
import ru.sincore.Main;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ServerManagment extends HttpServlet
{
	protected void doPost(HttpServletRequest request,
						  HttpServletResponse response)
			throws ServletException, IOException
	{
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		String action = request.getParameter("action");

		try{
			if (action.equals("stop"))
			{
				Main.exit();
				out.print("Server shutdown!");
			}
			else if (action.equals("restart"))
			{
				Main.restart();
				out.print("Server restarted!");
			}else if (action.equals("reload"))
			{

				boolean loadSt = ConfigurationManager.getInstance().loadConfigs();

				if (loadSt)
				{
					out.print("Hub config reloaded");
				}
				else
				{
					out.print("Hub config not reloaded");
				}

			}
		}finally {
			out.close();
		}
	}
}
