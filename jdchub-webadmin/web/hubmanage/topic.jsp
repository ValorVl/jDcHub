<%@ page import="ru.sincore.ConfigurationManager" %>
<%@ page import="ru.sincore.db.dao.BigTextDataDAOImpl" %>
<%@ page import="ru.sincore.util.AdcUtils" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: valor
  Date: 02.12.11
  Time: 16:54
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
    if (session.getAttribute("nick") == null)
    {
        response.sendRedirect("index.jsp");
    }

    ConfigurationManager configInstance = ConfigurationManager.instance();

    BigTextDataDAOImpl textDataDAO = new BigTextDataDAOImpl();

    String topic = AdcUtils.fromAdcString(textDataDAO.getData("TOPIC", configInstance.getString(ConfigurationManager.HUB_DEFAULT_LOCALE)));
    List<String> list = textDataDAO.getLocales();

%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>jDcHub - Administration console</title>
    <style type="text/css">
        @import "../css/core.css";
    </style>
    <script type="text/javascript" src="../js/jquery.js"></script>
    <script type="text/javascript" src="../js/topic.js"></script>
</head>
<body>
<c:import url="../static/head.jsp"/>
<c:import url="../static/top-menu.jsp"/>
<div class="content" align="center">
    <form action="topic.jsp" method="post" enctype="application/x-www-form-urlencoded">
        <table width="100%" align="left">
            <tr>
                <td>
                    <label for="l">Select locale </label>
                    <select id="l" name="locale">
                        <%
                           for (String val : list){
                        %>
                        <option value="<%=val %>"><%=val %></option>
                        <% } %>
                    </select>
                    <input id="reload" type="button" value="Reload">
                </td>
            </tr>
            <tr>
                <td>
                    <textarea id="out" rows="20" cols="100" name="rulesbody">
                        <%= topic %>
                    </textarea>
                </td>
            </tr>
            <tr>
                <td><input id="submit" type="button" value="Save or Update"></td>
            </tr>
        </table>
    </form>
</div>
<div id="message"></div>
</body>
</html>