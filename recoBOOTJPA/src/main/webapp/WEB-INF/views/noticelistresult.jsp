
<%@page import="java.util.ArrayList"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="com.reco.dto.PageDTO"%>
<%@page import="com.reco.customer.vo.Customer"%>
<%@page import="java.util.Date"%>
<%@page import="com.reco.notice.vo.Notice"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

  
    <meta name="viewport" content="width=device-width">
    <title>RECO</title>
    <link href="./css/noticelist.css" rel=stylesheet>
  

<!--noticelist서블릿에서 결과값 setting해서 noticelistresult.jsp로 보낸값 받아옴-->
<%
//List<Notice> list = (List)request.getAttribute("pageDTO");
String msg = (String)request.getAttribute("msg");
PageDTO<Notice> pageDTO = (PageDTO)request.getAttribute("pageDTO");
List<Notice> list = new ArrayList<>();
if(pageDTO != null){
list = pageDTO.getList();
}
%>

<!--END-->

<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script src="./js/noticelist.js"></script>
<script>
	$(function(){
		
		<!--공지사항 목록에서 글 쓰기 버튼 클릭되었을때 START-->
			noticeWriteClick();
		<!--공지사항 목록에서 글 쓰기 버튼 클릭되었을때 END-->
		
		<!-- 공지사항 목록에서 글 하나 클릭되었을때 START-->  
			noticeDetail();
		<!-- 공지사항 목록에서 글 하나 클릭되었을때 END-->
		
		<!-- 공지사항 목록에서 검색 클릭시 START-->
			searchClick();
		<!-- 공지사항 목록에서 검색 클릭시 END-->
	}); 

</script>
  

<div class="container">

<h1>공지사항</h1>

	<table class="ntc_info"> 
		<tr>
			<td>공지사항이 올라오는 게시판입니다. <br>                 
			</td>
		</tr>
	</table>  
 
 <form>
	<div class="search" style="float:right;">           
		<select  name="f">  <!-- 즉 url부분 쿼리스트링값이 선택한거랑 같으면 검색바에 춣력해라 -->
			<option ${(param.f == "ntc_title")? "selected" : "" }value="ntc_title">제목</option>
			<option ${(param.f == "ntc_content")? "selected" : "" } value="ntc_content">제목+내용</option>
		</select>
		
		<input type="text" name="word" value="${param.word}" placeholder="검색어를 입력하세요" > <!-- ${param.word}은 쿼리스트링에 있는word를 검색바에 출력해달라 -->
		
		<button class="searchButton">검색</button>             
	</div>
</form>



<!--공지사항 클릭시 출력될 공지사항 글 목록 출력 start-->

<div class="ntc_list">


	<ul class="ntc_top">
		<li>
			<span class="ntc">글번호</span>
			<span class="title">제목</span>
			<span class="nick">닉네임</span>
			<span class="view">조회수</span>
			<span class="date">작성일</span>
		</li>
	</ul>
<%if (pageDTO  == null) {%>
	<span class="noNtc"><%=msg %></span>
<%} else{%> 
	<%
	for(Notice n: list){
	  int ntcIdx = n.getNtcIdx();
	  
	  String ntcTitle = n.getNtcTitle();
	  String ntcuNickName = n.getNtcUNickName();
	  String ntcAttachment = n.getNtcAttachment();
	  int ntcViews = n.getNtcViews();
	  Date ntcCreatAt = n.getNtcCreateAt();
	  SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
	  String ntcCrt = sdf.format(ntcCreatAt);
	%>
	<div class ="noticelist" id="<%=ntcIdx%>"> 
		 <ul>
		    <li>
			 <span class="ntcIdx"><%=ntcIdx%></span>
			 <span class="ntcTitle"><%=ntcTitle%><%if(ntcAttachment != null){ %><img src="./images/클립.png"><%} %></span>
			 <span class="ntcuNickName"><%=ntcuNickName%></span>
			 <span class="ntcViews"><%=ntcViews%></span>
			 <span class="ntcCrt"><%=ntcCrt%></span>
			 
			 </li> 
		  </ul>
	</div>
<%} %>
<%} %>

<!-- 글쓰기버튼 시작 -->
<%
Customer c = (Customer) session.getAttribute("loginInfo"); 
%>
<%
if (session.getAttribute("loginInfo") != null) { 
%>
<%
int uAuthCode = c.getUAuthCode(); 
%>
<% 		if(uAuthCode == 1) {%>
			
	<div class="notice_write_button" style= "visibility:hidden">
		<label>
			<img src="./images/pencil.png">글쓰기
		</label>	
	</div>

	<%} else {%> <% uAuthCode = 0; %>
		
		<div class="notice_write_button" style= "visibility:visible">
			<label>
				<img src="./images/pencil.png">글쓰기
			</label>	
		</div>
		
		<%
		}
		%>
<%} else {  %>
<script>location.href="./";</script>
<%} %>


</div> 
<%if(pageDTO != null) {%>
<div class="pagegroup">
		 <%  
		 String backContextPath = request.getContextPath();
		 if(pageDTO.getStartPage() > 1){%>			
		 	<span class="<%= backContextPath%><%=pageDTO.getUrl()%>/<%=pageDTO.getStartPage()-1%> active">prev</span>
		 <%} %>
 
 		<%	for(int i = pageDTO.getStartPage() ; i<=pageDTO.getEndPage() ; i++){ %>
			<span class="<%= backContextPath%><%=pageDTO.getUrl() %>/<%=i%> <%if(i != pageDTO.getCurrentPage()){ %>active<%}%>"><%=i%></span>
		<%}%>
		
		<% 
		if(pageDTO.getEndPage() < pageDTO.getTotalPage()){%>
			<span class="<%= backContextPath%><%=pageDTO.getUrl()%>/<%=pageDTO.getEndPage()+1%> active">next</span>
		<%} %>
</div>
<%} %>
</div>



