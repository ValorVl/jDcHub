<%
    String login = (String) session.getAttribute("nick");
    String ip = (String) session.getAttribute("ip");
    Integer weight = (Integer) session.getAttribute("weight");

%>
<div class="head">
    <%
      if (login != null){
    %>
    <div style="height: 100px; width: 400px; float: right;">

        <ui>
          <li><span>Your nick : <%= login%></span></li>
          <li><span>Your ip : <%= ip%></span></li>
          <li><span>Your weight : <%= weight%></span></li>
      </ui>

      <form action="../logout" method="post" enctype="application/x-www-form-urlencoded">
          <div style="text-align: center; margin: 2px;">
              <input type="hidden" name="ssid" value="<%= session.getId() %>">
              <button type="submit">LogOut</button>
          </div>
      </form>
    </div>
    <%} else {%>
        <div style="height: 100px; width: 400px; float: right;"></div>
    <%}%>
    <div style="height:100px; width: 100% ">
        <h1>Administration console</h1>
    </div>

</div>