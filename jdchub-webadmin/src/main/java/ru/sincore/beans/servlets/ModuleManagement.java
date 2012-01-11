package ru.sincore.beans.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ModuleManagement extends HttpServlet
{
	protected void doPost(HttpServletRequest request,
						  HttpServletResponse response)
			throws ServletException, IOException
	{
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		try{
			String[] val = request.getParameterValues("selected");
			Integer action = Integer.valueOf(request.getParameter("action"));
			
			if (val == null || action == null)
			{
				return;
			}

			switch (action)
			{
				case 1:
					enable(val,out);
					break;
				case 2:
					disable();
					break;
				case 3:
					reload();
					break;
				case 4:
					unload();
					break;
				case 5:
					load();
					break;
			}
			
		}finally {
			out.close();
		}
	}


	private void enable(String[] val, PrintWriter out)
	{
		for (int i = 0; i < val.length; i++)
		{
			out.print(val[i]);
		}
	}

	private void disable()
	{

	}

	private void load()
	{

	}

	private void unload()
	{

	}

	private void reload()
	{

	}

}
