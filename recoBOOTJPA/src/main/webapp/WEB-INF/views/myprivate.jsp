<%@page import="com.reco.customer.vo.Customer"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%Customer c = (Customer)session.getAttribute("loginInfo"); %>   

<link href="./css/myprivate.css" rel=stylesheet>   
<script src="./js/myprivate.js"></script>    

<script>
$(function(){
	uIdx = "<%=c.getUIdx()%>";
	
	modifypwdBtClick(uIdx);
	withdrawBtClick(uIdx);
});

</script>

<div class=myprivate>   
	<div class="modifypwd">    
		<h1 class="info">비밀번호 변경</h1>                     
		새비밀번호 : <input type="password" name="pwd" required><br>
		새비밀번호확인 : <input type="password" name="pwd1" required><br>
		<button class="modifypwdbutton">확인</button>
	</div>
	
	<div class="withdraw">       
		<h1 class="info">회원탈퇴</h1>
		현재 비밀번호 : <input type="password" name="pwd" required><br>
		현재 비밀번호 확인 : <input type="password" name="pwd1" required><br>
		<button class="withdrawbutton">확인</button>
	</div>
</div>	