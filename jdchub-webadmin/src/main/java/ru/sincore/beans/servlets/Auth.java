package ru.sincore.beans.servlets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sincore.ConfigurationManager;
import ru.sincore.db.dao.ClientListDAOImpl;
import ru.sincore.db.pojo.ClientListPOJO;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

public class Auth extends HttpServlet
{
	
	private static final Logger log = LoggerFactory.getLogger(Auth.class);
	
	protected void doPost(HttpServletRequest request,
						  HttpServletResponse response)
			throws ServletException, IOException
	{
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		ConfigurationManager configInstance = ConfigurationManager.instance();

		HttpSession session = request.getSession(true);

		String login = request.getParameter("login");
		String pwd = request.getParameter("pwd");
		
		try{


			if ((login == null || pwd == null) && (login.isEmpty() || pwd.isEmpty()))
			{
				response.sendRedirect("/static/fail.jsp");
				return;
			}

			ClientListDAOImpl clientInstance = new ClientListDAOImpl();

			ClientListPOJO client = clientInstance.getClientByNick(login.trim());

			if(client == null)
			{
				response.sendRedirect("/static/fail.jsp");
				return;
			}

			String nick = client.getNickName();
			String password = client.getPassword();
			String cid = client.getCid();
			Integer weight = client.getWeight();
			Boolean isReg = client.isRegistred();
			String  ip = client.getCurrentIp();

			if (!pwd.equals(password))
			{
				response.sendRedirect("/static/fail.jsp");
				return;
			}

			session.setAttribute("nick",nick);
			session.setAttribute("pwd",password.hashCode());
			session.setAttribute("cid",cid);
			session.setAttribute("ip",ip);
			session.setAttribute("weight",weight);
			session.setAttribute("isreg",isReg);

			String sessionId = session.getId();

			Cookie[] cookies = request.getCookies();
			Cookie   cookie = null;
			int maxAge;

			if (cookie == null)
			{
				try{
					maxAge = new Integer(getServletContext().getInitParameter("cookie-age")).intValue();
				}catch (Exception e)
				{
					maxAge = -1;
				}

				cookie = new Cookie("SUID",sessionId);
				cookie.setPath(request.getRequestURI());
				cookie.setMaxAge(maxAge);
				cookie.setSecure(true);
				cookie.setMaxAge(60);
			}

			if (weight >= configInstance.getInt(ConfigurationManager.CLIENT_WEIGHT_SUPER_USER))
			{
				response.sendRedirect("index1.jsp");
			}else if (weight >= configInstance.getInt(ConfigurationManager.CLIENT_WEIGHT_UNREGISTRED))
			{
				response.sendRedirect("chat_log.jsp");
			}else {
				response.sendRedirect("for_unreg.jsp");
			}

		}finally {
			out.close();
		}
	}

}
