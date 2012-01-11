<%@ page import="java.util.Set" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="ru.sincore.pipeline.PipelineFactory" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%
    if (session.getAttribute("nick") == null)
    {
        response.sendRedirect("index.jsp");
    }

    Set<String> actions = PipelineFactory.getProcessorsNames();

%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>jDcHub - Administration console</title>
    <style type="text/css">
        @import "css/core.css";
    </style>
    <script type="text/javascript" src="js/jquery.js"></script>
    <script type="text/javascript" src="js/bw.js"></script>
</head>
<body>
<c:import url="static/head.jsp"/>
<c:import url="static/top-menu.jsp"/>
<div class="content" align="center">
    <form action="bad_words.jsp" enctype="application/x-www-form-urlencoded" method="post">
        <table width="100%" style="margin-top: 5px; padding: 5px;">
            <tr>
                <td>RegEx</td>
                <td><input type="text" id="regex" size="50"></td>
            </tr>
            <tr>
                <td>Action</td>
                <td>
                    <select id="action">
                        <%
                            for (Iterator<String> stringIterator = actions.iterator(); stringIterator.hasNext(); )
                            {
                                String next = stringIterator.next();
                        %>
                             <option value="<%=next%>" ><%=next%></option>
                        <%
                            }
                        %>

                    </select>
                </td>
            </tr>
            <tr>
                <td>Params</td>
                <td><input type="text" id="param" size="50"></td>
            </tr>
            <tr>
                <td colspan="2"><button id="add" type="button">Add</button></td>
            </tr>
        </table>
        <div id="out"></div>
    </form>
</div>
</body>
</html>