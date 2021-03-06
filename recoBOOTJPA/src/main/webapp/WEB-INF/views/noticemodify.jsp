<%@page import="com.reco.customer.vo.Customer"%>
<%@page import="com.reco.notice.vo.Notice"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<% System.out.println("in noticemodify.jsp -- 0"); %>
<%
	Date nowTime = new Date();
	SimpleDateFormat sf = new SimpleDateFormat("yyyy년 MM월 dd일");
%>
<link href="./css/noticewrite.css" rel=stylesheet>
<script src="./js/noticemodify.js"></script> 
<title>noticemodify.jsp</title>
<%String image = (String)request.getAttribute("image"); %>
<%String ntcIdx= request.getParameter("ntcIdx");
String ntcTitle= request.getParameter("ntcTitle");
String ntcContent= request.getParameter("ntcContent");
String ntcAttachment= request.getParameter("ntcAttachment");
%>
<script>
$(function(){
	//이미지 다운로드후 보여주기
	<%if(image != null){%>
		let $img = $("fieldset.noticemodify>form>div.image>img");
		$.ajax({
			url: "./noticedownloadimage",
			method:'get',
			data:"imageFileName="+"<%=image%>",
			
			cache:false, //이미지 다운로드용 설정
	        xhrFields:{  //이미지 다운로드용 설정
	            responseType: 'blob'
	        },
			success:function(responseData){
				let url = URL.createObjectURL(responseData);
				$img.attr('src', url); 														
			},
			error:function(jqXHR, textStatus){
				alert("에러:" + jqXHR.status);
			}
		});
	<%}else{%>
		$('div.ntcDetail>ul.ntcDetail>li>div.image').css('display','none');
	<%}%>
	let $formObj = $('fieldset form');
	//글수정버튼클릭시 수정된글 보낸후 수정한 글 다시 보기
	modifyNoticeSubmit($formObj);
	//수정취소버튼 클릭시 수정할려고 한글 다시보기
	modifyCancelBtClick();
});
</script>

	<%
	Customer c = (Customer) session.getAttribute("loginInfo"); 
	%>
	<%
	if (session.getAttribute("loginInfo") != null) { 
	%>
	<%} else {  %>
	<script>location.href="./";</script>
	<%} %>
<fieldset class="noticemodify">
	<form autocomplete="off">
		<h1>공지사항 수정</h1>
		<table>
			<tr><td>날짜</td> <td><%= sf.format(nowTime)%></td></tr>
		</table>
		글번호: <input type ="text" id="ntcIdx" name="ntcIdx" value="<%=ntcIdx %>" readonly>   
		<br>
		<%if (image !=null){%><div class="image"><img style="width:300px; height:300px;"></div><%} %>
		<textarea rows="2" cols="100" style="resize:none;" name="ntcTitle" id="ntcTitle" placeholder="<%=ntcTitle %>" required><%=ntcTitle %></textarea> 	       
		<table>
			<tr><td><textarea rows="20" cols="100" style="resize:none;" name="ntcContent" id="ntcContent" placeholder="<%=ntcContent %>" required><%=ntcContent %></textarea></td></tr>		
		</table>
		<div class="data"><label>파일 첨부</label><input type="file" name="letterFiles"></div> 현재 저장된 첨부파일 : <%if(ntcAttachment!=null){%><%=ntcAttachment%> <%}%><br>
		<button class="modifycancel">수정 취소</button>
		<input type="button" value="글 수정">
	</form>
</fieldset>
