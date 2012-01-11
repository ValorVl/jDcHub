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
        @import "../css/core.css";
    </style>
    <script type="text/javascript" src="../js/jquery.js"></script>
    <script type="text/javascript" src="../js/sendmessage.js"></script>
</head>
<body>
<c:import url="../static/head.jsp"/>
<c:import url="../static/top-menu.jsp"/>
<div class="content" align="left">
    <form action="sandmessage.jsp" method="post" enctype="application/x-www-form-urlencoded">
        <table width="100%" align="left">
            <tr>
                <td>
                    <span>Message type</span>
                    <select id="mtype">
                        <option value="p">Private from hub</option>
                        <option value="b">Broadcast from hub</option>
                    </select>
                    <input id="nick" type="text" name="nick">
                </td>
            </tr>
            <tr>
                <td>
                    <textarea rows="20" cols="100" id="messagebody">
                    </textarea>
                </td>
            </tr>
            <tr>
                <td>
                    <input id="submit" type="button" value="Send">
                    <div id="callback"></div>
                </td>
            </tr>
        </table>
    </form>
</div>
</body>
</html>