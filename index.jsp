<%--
  Created by IntelliJ IDEA.
  User: lly
  Date: 2017/10/9
  Time: 21:45
  To change this template use File | Settings | File Templates.
--%>
<%@page import="test.*" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>$Title$</title>
  </head>
  <body>
  <div style="width:100%;align:center">
  <form action="get.jsp" method="get">
        <input type="text" name="searchword" maxlength="20" size="30" />
        <input type="hidden" name="currentPage" value="0"/>
        <input type="submit" value="submit" />
    </form>
  </div>
  </body>
</html>
