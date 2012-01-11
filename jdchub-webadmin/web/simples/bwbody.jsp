<%@ page import="ru.sincore.db.dao.PipelineRulesDAOImpl" %>
<%@ page import="ru.sincore.db.pojo.PipelineRulesPOJO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  PipelineRulesDAOImpl pipelineDao = new PipelineRulesDAOImpl("MSG");

%>
<form action="../bwdel" accept-charset="UTF-8" enctype="application/x-www-form-urlencoded" method="post">
<div style="text-align: right; width: 100%;">
    <button type="submit">Del</button>
</div>
<table rules="all" width="100%" align="left">
   <%
   for (PipelineRulesPOJO row : pipelineDao.getRules())
   {
   %>
    <tr>
        <td><%= row.getMatcher()%></td>

        <td><%= row.getProcessor()%></td>

        <td><%= row.getParam()%></td>

        <td><input type="checkbox" name="id" value="<%= row.getId() %>"></td>
    </tr>
   <%}%>
</table>
</form>



