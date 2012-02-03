<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    if (session.getAttribute("nick") == null)
    {
        response.sendRedirect("index.jsp");
    }
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>jDcHub - User chat</title>
    <style type="text/css">
        @import "css/core.css";
    </style>
</head>
<body>
<c:import url="static/head.jsp"/>
<div class="content" align="center">
    <form action="" enctype="application/x-www-form-urlencoded" method="post">
    <table align="center" width="100%">
        <tr>
            <td width="200px" style="border-right: 1px solid gray">
                <a href="chat.jsp">User chat</a><br>
                <a href="chatlog.jsp">Chat log</a>
                <div>

                </div>
            </td>
            <td>

            </td>
        </tr>
    </table>
    </form>
</div>
</body>
</html>