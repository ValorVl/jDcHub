package ru.sincore.beans.servlets;

import ru.sincore.db.dao.PipelineRulesDAOImpl;
import ru.sincore.pipeline.PipelineFactory;
import ru.sincore.pipeline.Processor;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class Bw extends HttpServlet
{

	private PipelineRulesDAOImpl pipelineDao = new PipelineRulesDAOImpl("MSG");

	protected void doPost(HttpServletRequest request,
						  HttpServletResponse response)
			throws ServletException, IOException
	{
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		
		String regExp = request.getParameter("regex");
		String action = request.getParameter("action");
		String params = request.getParameter("param");
		
		try{
			if (regExp == null || regExp.isEmpty())
			{
				out.print("<span>RegExp pattern is not empty!<span>");
				return;
			}

			Processor processor = PipelineFactory.createProcessor(action);

			processor.setMatcher(regExp);
			processor.setParameter(params);

			PipelineFactory.getPipeline("MSG").addProcessor(processor);

			boolean add = pipelineDao.addRule(regExp,action,params);

			if (!add)
			{
				out.print("<span>Error in DAO method</span>");
				return;
			}

			response.sendRedirect("bad_words.jsp");

		}finally {
			out.close();
		}
	}

}
