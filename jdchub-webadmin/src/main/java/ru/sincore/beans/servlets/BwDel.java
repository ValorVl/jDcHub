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

			String[] ids = request.getParameterValues("id[]");

			if ((ids == null) || (ids.length == 0))
			{
				return;
			}

			PipelineRulesDAOImpl pipelineRulesDAO = new PipelineRulesDAOImpl("MSG");

			for (String id : ids)
			{
                Long idAsLong = Long.parseLong(id);
                pipelineRulesDAO.deleteRule(idAsLong);
			}

            out.write("Deleted!");

		}finally {
			out.close();
		}
	}
}
