<%--
  Created by IntelliJ IDEA.
  User: valor
  Date: 01.12.11
  Time: 15:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    if (session.getAttribute("nick") == null)
    {
        response.sendRedirect("index.jsp");
    }
%>
<html>
  <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
      <title>jDcHub - Administration console</title>
      <style type="text/css">
          @import "css/core.css";
      </style>
  </head>
  <body>
       <c:import url="static/head.jsp"/>
       <c:import url="static/top-menu.jsp"/>
       <div class="content" align="left">
               <ul>
                   <li><a href="hubmanage/servermanage.jsp"><span class="hm-menu">Server management</span></a></li>
                   <li><a href="hubmanage/sandmessage.jsp"><span class="hm-menu">Sand message to hub</span></a></li>
               </ul>
               <hr align="left" width=" 500px">
               <ul>
                   <li><a href="hubmanage/motd.jsp"><span class="hm-menu">Manage MOTD</span></a></li>
                   <li><a href="hubmanage/rules.jsp"><span class="hm-menu">Manage RULES</span></a></li>
                   <li><a href="hubmanage/topic.jsp"><span class="hm-menu">Manage TOPIC</span></a></li>
                   <li><a href="hubmanage/faq.jsp"><span class="hm-menu">Manage FAQ</span></a></li>
               </ul>
       </div>
  </body>
</html>