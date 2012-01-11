<%@ page import="ru.sincore.db.dao.ModuleListDAOImpl" %>
<%@ page import="java.util.List" %>
<%@ page import="ru.sincore.db.pojo.ModuleListPOJO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%

    if (session.getAttribute("nick") == null)
    {
        response.sendRedirect("index.jsp");
    }

    ModuleListDAOImpl moduleList = new ModuleListDAOImpl();

    List<ModuleListPOJO> modules = moduleList.getModuleList();

%>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>jDcHub - Administration console</title>
    <style type="text/css">
        @import "css/core.css";
    </style>
    <script type="text/javascript" src="js/jquery.js"></script>
    <script type="text/javascript" src="js/module_management.js"></script>
</head>
<body>
<c:import url="static/head.jsp"/>
<c:import url="static/top-menu.jsp"/>
<div class="content" align="center" style="padding-top: 5px;">
    <div class="mod-list">
        <form action="" method="post" enctype="application/x-www-form-urlencoded">
        <select name="action" class="action">
            <option value="1">Enable</option>
            <option value="2">Disable</option>
            <option value="3">Reload</option>
            <option value="4">Unload</option>
            <option value="5">Load</option>
        </select>
        <button class="execute" type="button">Execute</button>
        <table width="100%" rules="rows">
            <thead id="m-t-head">
                <tr>
                    <td width="60px">Id</td>
                    <td >Module name</td>
                    <td width="55px">Status</td>
                </tr>
            </thead>
            <tbody id="m-t-body">
                <% for (ModuleListPOJO mod : modules){%>
                   <tr>
                       <td>
                           <input type="checkbox" value="<%= mod.getId()%>" name="selected" align="right"> |
                           <%= mod.getId()%>
                       </td>
                       <td>
                           <%= mod.getName()%>
                       </td>
                       <td>
                           <%= mod.isEnabled()%>
                           <input type="hidden" value="<%= mod.isEnabled()%>" class="state">
                       </td>
                   </tr>
                <%}%>
            </tbody>
        </table>
        </form>
    </div>

</div>
</body>
</html>