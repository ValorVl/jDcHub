<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
if(session.getAttribute("nick") != null)
{
    response.sendRedirect("index1.jsp");
}
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>jDcHub - Administration console</title>
    <style type="text/css">
        @import "css/core.css";
    </style>
    <script type="text/javascript" src="js/jquery.js"></script>
</head>
<body>
<c:import url="static/head.jsp"/>
<div class="content" align="center">
    <div align="center" class="auth-block">
        <form action="../auth" enctype="application/x-www-form-urlencoded" method="post">
            <table align="center">
                <tr>
                    <td>
                        <input name="login" id="login" type="text">
                        <label for="login">Login</label>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input name="pwd" id="pwd" type="password">
                        <label for="pwd">Password</label>
                    </td>
                </tr>
                <tr>
                    <td>
                        <button id="enter" type="submit">Login</button>
                    </td>
                </tr>
                <tr>
                    <td>
                        <div id="mess"></div>
                    </td>
                </tr>
            </table>
        </form>
    </div>
</div>
</body>
</html>