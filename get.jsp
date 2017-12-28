<%--
  Created by IntelliJ IDEA.
  User: lly
  Date: 2017/10/10
  Time: 18:37
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="source.FileIndexer" %>
<%@ page import="org.apache.lucene.search.TopDocs" %>
<%@ page import="org.apache.lucene.search.IndexSearcher" %>
<%@ page import="org.apache.lucene.document.Document" %>
<%@ page import="java.lang.Math"%>

<%
    String path = request.getContextPath();
    String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<%!
    String searchword;
    TopDocs topDocs;
    IndexSearcher searcher;
    Document document;
    int currentPage = 0; //当前页
    int pageSize = 10;      //页面新闻条数
    int pageNum;            //最大页数
%>
<html>
<head>
    <!--base标签设置当前页面的根路径，即当前页面的其他路径可以相对base标签设置的路径而设置-->
    <title>Search Result</title>
</head>
<body>
    <%searchword=request.getParameter("searchword");%>
    <form action="get.jsp" method="get">
        <input type="text" name="searchword" maxlength="20" size="30" value="<%=searchword%>"/>
        <input type="submit" value = "submit" />
        <input type="hidden" name="currentPage" value="0"/>
    </form>
    <%
        currentPage = Integer.parseInt(request.getParameter("currentPage"));
        topDocs = FileIndexer.filesearch(searchword);
        searcher = FileIndexer.searcher;
        pageSize = Math.min(10,topDocs.totalHits-currentPage*10);
        pageNum = Math.min((topDocs.totalHits-1)/10+1,10);
    %>
    Total hits: <%=topDocs.totalHits%>
    <%for(int i = currentPage*10; i < pageSize+currentPage*10; i++) {
        document = searcher.doc(topDocs.scoreDocs[i].doc);
        float score = topDocs.scoreDocs[i].score;
        String url = document.get("url");
        String title = document.get("title");
        String content = document.get("content");
    %>
    <H3><a href=<%=url%>><%=title%></a></H3>
    score: <%=score%>
    <br>
    <%if(content.length() < 200){%>
        <%=content%>
    <%}else{%>
        <%=content.substring(0,199)%>...
        <%}%>
    <%}%>
    <br>
    <%for(int i = 0; i < pageNum; i++) {
        String jumpUrl="get.jsp?searchword="+searchword+"&currentPage="+i;
        if(i != currentPage) {
     %>
        <a href=<%=jumpUrl%>><%=i%>&nbsp;</a>
        <%}else{%>
            <%=i%>&nbsp;
        <%}%>
    <%}%>
</body>
</html>
