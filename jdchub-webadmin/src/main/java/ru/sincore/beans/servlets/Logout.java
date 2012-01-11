package ru.sincore.beans.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

public class Logout extends HttpServlet
{

	private static final Logger log = LoggerFactory.getLogger(Logout.class);

	protected void doPost(HttpServletRequest request,
						  HttpServletResponse response)
			throws ServletException, IOException
	{
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		HttpSession session = request.getSession(false);

		log.info("Session request");
		try{
			if (session != null)
			{
				session.invalidate();
				response.sendRedirect("index.jsp");
			}else {
				response.sendRedirect("index.jsp");
			}
		}finally {
			out.close();
		}
	}
}
