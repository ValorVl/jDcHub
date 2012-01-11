<%@ page import="ru.sincore.ClientManager" %>
<%@ page import="ru.sincore.client.AbstractClient" %>
<%@ page import="ru.sincore.db.dao.ClientListDAOImpl" %>
<%@ page import="ru.sincore.db.pojo.ClientListPOJO" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    /*
        Online or data base stored clients
     */
    String listType = request.getParameter("list_type");
    String regOnly = request.getParameter("regonly");

    if ((listType == null || !listType.matches("[a-z]")) && (regOnly == null || !regOnly.matches("[a-z]")))
    {
        out.print("<span style=\"color: red;\">Error request!</span>");
        return;
    }

    ArrayList<AbstractClient> onlineClients;
    List<ClientListPOJO> storedClients;

%>
<table align="center" width="100%" rules="all">
    <%
        if (listType.equals("online"))
        {
            onlineClients = (ArrayList<AbstractClient>) ClientManager.getInstance().getClients();
            for (AbstractClient client : onlineClients)
            {
                out.print("<tr>");
                    out.print("<td>"+client.getNick()+"</td>");
                    out.print("<td>"+client.getIpAddressV4()+"</td>");
                out.print("</tr>");
            }
        }
        else if (listType.equals("stored"))
        {
            ClientListDAOImpl clientListDAO = new ClientListDAOImpl();
            storedClients = clientListDAO.getClientList(false);
            for (ClientListPOJO client : storedClients)
            {
                out.print("<tr>");
                    out.print("<td>"+client.getNickName()+"</td>");
                    out.print("<td>"+client.getRealIp()+"</td>");
                out.print("</tr>");
            }

        }
    %>



</table>