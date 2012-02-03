<%@ page import="ru.sincore.ConfigurationManager" %>
<%@ page import="ru.sincore.ClientManager" %>
<%@ page import="ru.sincore.db.HibernateUtils" %>
<%@ page import="ru.sincore.db.dao.ClientListDAOImpl" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%
    if (session.getAttribute("nick") == null)
    {
        response.sendRedirect("index.jsp");
    }

    ConfigurationManager configInstance = ConfigurationManager.instance();

    ClientListDAOImpl clientList = new ClientListDAOImpl();
    
    //Long clientsRegistredCount = clientList.
%>
<div class="main-desc" align="center">
    <table id="info" align="left" rules="rows" width="100%">
          <tr>
            <td colspan="2">
                <span class="s-head">Runtime Info</span>
            </td>
        </tr>
        <tr>
            <td width="400px">Memory usage (total/free/usage)</td>
            <td ><%= Runtime.getRuntime().totalMemory() / (1024 * 1024)%> / <%= Runtime.getRuntime().freeMemory() / (1024 * 1024) %> / <%= (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024) %></td>
        </tr>
         <tr>
            <td width="400px">Available cpu </td>
            <td ><%= Runtime.getRuntime().availableProcessors()%></td>
        </tr>
        <tr>
            <td colspan="2">
                <span class="s-head">Hub server info</span>
            </td>
        </tr>
        <tr>
            <td width="400px">Hub name</td>
            <td ><%= configInstance.getString(ConfigurationManager.HUB_NAME)%></td>
        </tr>
        <tr>
            <td width="400px">Hub description</td>
            <td ><%= configInstance.getString(ConfigurationManager.HUB_DESCRIPTION)%></td>
        </tr>
        <tr>
            <td width="400px">Hub greeting</td>
            <td ><%= configInstance.getString(ConfigurationManager.HUB_GREETING)%></td>
        </tr>
        <tr>
            <td width="400px">Hub owner</td>
            <td ><%= configInstance.getString(ConfigurationManager.HUB_OWNER)%></td>
        </tr>
        <tr>
            <td width="400px">Hub version</td>
            <td><%= configInstance.getString(ConfigurationManager.HUB_VERSION)%></td>
        </tr>
        <tr>
            <td width="400px">Hub default locale</td>
            <td><%= configInstance.getString(ConfigurationManager.HUB_DEFAULT_LOCALE)%></td>
        </tr>
        <tr>
            <td width="400px">Hub share (define/current)</td>
            <td><%= Long.valueOf(configInstance.getString(ConfigurationManager.MAX_SHARE_SIZE)) / (1024 * 1024) %> / <%= ClientManager.getInstance().getTotalShare() / (1024 * 1024) %></td>
        </tr>
        <tr>
            <td width="400px">Hub shared files count</td>
            <td><%= ClientManager.getInstance().getTotalFileCount()%></td>
        </tr>
         <tr>
            <td width="400px">Hub on-line</td>
            <td><%= ClientManager.getInstance().getClientsCount() %></td>
        </tr>
        <tr>
            <td width="400px">Hub clients stored</td>
            <td></td>
        </tr>

        <tr>
            <td colspan="2">
                <span class="s-head">JVM Info</span>
            </td>
        </tr>
        <tr>
            <td width="400px">Java runtime name</td>
            <td><%= System.getProperty("java.runtime.name")%></td>
        </tr>
        <tr>
            <td width="400px">Java runtime version</td>
            <td><%= System.getProperty("java.runtime.version")%></td>
        </tr>
        <tr>
            <td width="400px">Java runtime arch</td>
            <td><%= System.getProperty("os.arch")%></td>
        </tr>
        <tr>
            <td width="400px">Working dir</td>
            <td><%= System.getProperty("user.dir")%></td>
        </tr>
        <tr>
            <td width="400px">Os name</td>
            <td><%= System.getProperty("os.name")%></td>
        </tr>
        <tr>
            <td width="400px">Server run on user</td>
            <td><%= System.getProperty("user.name")%></td>
        </tr>
    </table>
</div>
