<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="com.reco.calendar.dao.CalendarDAOOracle" %>
<%@page import="com.reco.calendar.vo.CalInfo" %>
<%@page import="com.reco.customer.vo.Customer"%>
<%@page import="java.util.List"%>
 
 
 <!--캘린더 추가등록 페이지입니다. -->
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8"> <!--html에서 없으면 글씨 깨짐   -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>calInfowrite.html</title>
	<link href="./css/calInfowrite.css" rel=stylesheet>
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
	<script src="./js/calInfowrite.js"></script>
</head>

	<script>
	$(function(){		
		//let $thumbnailObj = $('fieldset>form>div.input_image>input[name=thumbnail]');
		//let $categoryObj = $('fieldset>form>div.input_text>input[name=category]');
		let $formObj = $('fieldset>form');
		
		
	     /*--캘린더 추가 등록버튼이 클릭되었을때 START--*/
		 addCalSubmit($formObj);
	     /*--캘린더 추가 등록버튼이 클릭되었을때 END--*/
	     
	     /*--캘린더 수정 버튼이 클릭되었을때 START--*/
	     /*  modifyCalClick(); */
		/*--캘린더 수정 버튼이 클릭되었을때 END--*/
	   });
	</script>
	
	
	<%
	Customer c = (Customer)session.getAttribute("loginInfo"); 
	CalInfo ci = (CalInfo)session.getAttribute("CalendarInfo"); 
	
	int uIdx  = c.getUIdx();
	int calIdx = ci.getCalIdx();
	
	%>
	 
	<fieldset>
	    <form  method="post" action="./caladd" autocomplete="off" enctype="multipart/form-data">
	        
	        <div class="input_image">
	         	<strong>대표사진 추가(컴퓨터에서 불러오기)</strong><br>
	         	<input type="file" name="calThumbnail" id="upload_file" accept="image/*" >
	        	
	        </div><br>
	        
	        <div class="input_text">
	        	<strong>캘린더 이름 입력 </strong><br>
	        	<input type="text" name="calCategory" id="input" placeholder="캘린더 이름을 입력해주세요.">
	        </div><br>
	        
			
	        <button type="submit">등록</button>
	        <button type="cancle"  onClick='self.close()'>취소</button>
	        
	
	    </form>
	</fieldset>
	
</html>