package ru.sincore.beans.servlets;

import ru.sincore.db.dao.PipelineRulesDAOImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class BwDel extends HttpServlet
{
	protected void doPost(HttpServletRequest request,
						  HttpServletResponse response)
			throws ServletException, IOException
	{
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		try{

			String[] id = request.getParameterValues("id");

			if (id == null)
			{
				return;
			}

			PipelineRulesDAOImpl pipelineRulesDAO = new PipelineRulesDAOImpl("MSG");

			for (int i = 0; i < id.length;i++)
			{

			}

		}finally {
			out.close();
		}
	}
}
