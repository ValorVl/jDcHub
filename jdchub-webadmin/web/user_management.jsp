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
    <title>jDcHub - Administration console</title>
    <style type="text/css">
        @import "css/core.css";
    </style>
</head>
<body>
<c:import url="static/head.jsp"/>
<c:import url="static/top-menu.jsp"/>
<div class="content" align="center">
    <form action="" enctype="application/x-www-form-urlencoded" method="post">
    <table align="left" width="100%">
        <tr>
            <td>
                <span>Select client list type </span>
            </td>
            <td>
                <select id="listtype">
                    <option value="online">Online</option>
                    <option value="stored">Stored</option>
                </select>
            </td>
        </tr>
        <tr>
            <td>
                <span>If select stored client list, select client registration status</span>
            </td>
            <td>
                <select id="isreg">
                    <option value="reg">Registered only</option>
                    <option value="unreg">Unregistered only </option>
                </select>
            </td>
        </tr>
        <tr>
            <td>
                <span>Search user by nickname or ip</span>
            </td>
            <td>
                <input type="checkbox" id="bynick" value="bynick">
                <input type="checkbox" id="byip" value="byip">
                <input type="text" id="patern">
            </td>
        </tr>
        <tr>
            <td colspan="2"><button type="button" id="submit">Query</button></td>
        </tr>
   </table>
   </form>
   <div id="out">
   </div>
</div>
</body>
</html>