package ru.sincore.beans.servlets;

import ru.sincore.Broadcast;
import ru.sincore.ClientManager;
import ru.sincore.client.AbstractClient;
import ru.sincore.util.ClientUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class SendMessage extends HttpServlet
{
	protected void doPost(HttpServletRequest request,
						  HttpServletResponse response)
			throws ServletException, IOException
	{
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		try{

			Broadcast broadcast = Broadcast.getInstance();

			String message = request.getParameter("message");
			String mType = request.getParameter("type");

			if (mType.equals("b"))
			{
				ClientUtils.broadcastTextMessageFromHub(message);
				out.print("Message successful delivered");
			}else if (mType.equals("p"))
			{
				String nick = request.getParameter("nick");

				if (!nick.isEmpty())
				{
					AbstractClient toClient = ClientManager.getInstance().getClientByNick(nick);

					if (toClient != null)
					{
						toClient.sendPrivateMessageFromHub(message);
					}else{
						out.print("Client not found");
					}

				}else {
					out.print("Nickname field is not empty!");
				}
			}
		}finally {
			out.close();
		}
	}
}
