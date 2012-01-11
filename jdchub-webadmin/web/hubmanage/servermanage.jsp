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
    <link href="../css/core.css" rel="stylesheet" type="text/css"/>
    <script type="text/javascript" src="../js/jquery.js"></script>
    <script type="text/javascript" src="../js/servermanage.js"></script>
</head>
<body>
<c:import url="../static/head.jsp"/>
<c:import url="../static/top-menu.jsp"/>
<div class="content" align="left">
    <form action="sandmessage.jsp" enctype="application/x-www-form-urlencoded" method="post">
        <table rules="cols" class="control-t" align="left" width="100%">
            <tr>
                <td width="100px">
                    <button id="stop" type="button">Stop</button>
                </td>
                <td>
                    <span>Command for server shutdown.</span>
                </td>
            </tr>
            <tr>
                <td width="100px">
                    <button id="restart" type="button">Restart</button>
                </td>
                <td>
                    <span>Restart server command.</span>
                </td>
            </tr>
            <tr>
                <td width="100px">
                    <button id="reload" type="button">Reload</button>
                </td>
                <td>
                    <span>Reload server configuration files.</span>
                </td>
            </tr>
            <tr>
                <td colspan="2">
                    <textarea readonly="readonly" id="mess" rows="5" cols="100"></textarea>
                </td>
            </tr>
        </table>
    </form>
</div>
</body>
</html>