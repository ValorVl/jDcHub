<%@ page import="ru.sincore.ConfigurationManager" %>
<%@ page import="ru.sincore.db.dao.BigTextDataDAOImpl" %>
<%@ page import="ru.sincore.util.AdcUtils" %>
<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: valor
  Date: 01.12.11
  Time: 15:44
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<%

    if (session.getAttribute("nick") == null)
    {
        response.sendRedirect("index.jsp");
    }

    ConfigurationManager configInstance = ConfigurationManager.instance();


    BigTextDataDAOImpl bigText = new BigTextDataDAOImpl();
    String motd = AdcUtils.fromAdcString(bigText.getData("MOTD", configInstance.getString(ConfigurationManager.HUB_DEFAULT_LOCALE)));
    List<String> list = bigText.getLocales("MOTD");

%>
<html>
  <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
      <title>jDcHub - Administration console</title>
      <style type="text/css">
          @import "../css/core.css";
      </style>
      <script type="text/javascript" src="../js/jquery.js"></script>
      <script type="text/javascript" src="../js/motd.js"></script>
  </head>
  <body>
       <c:import url="../static/head.jsp"/>
       <c:import url="../static/top-menu.jsp"/>
       <div class="content" align="center">
          <form action="motd.jsp" method="post" enctype="application/x-www-form-urlencoded">
            <table width="100%" align="left">
                <tr>
                    <td>
                        <label for="l">Select locale </label>
                        <select id="l" name="locale">
                            <% 
                            	for (String el : list) {
                            %>
                                <option value="<%= el %>"><%= el %></option>
							<%
                            	}
							%>                            
                        </select>
                        <input id="reload" type="button" value="Reload">
                    </td>
                </tr>
                <tr>
                    <td>
                        <textarea id="out" rows="20" cols="100" name="motdbody">
                            <%= motd %>
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