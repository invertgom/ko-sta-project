<%@page import="com.reco.customer.vo.Customer"%>
<%@page import="com.reco.calendar.vo.CalPost"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%Customer c = (Customer)session.getAttribute("loginInfo"); %>
<%
	Date nowTime = new Date();
	SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM");
	String dateValue = sf.format(nowTime);
%>
    <script src="./js/callist.js"></script>
    
    <script>
	    $(function(){
			uNickname = "<%=c.getUNickName()%>"
			/*------- tab.js ----------*/
	    	//탭에서 메뉴클릭시 발생하는 이벤트(공지사항/faq/자유게시판/캘린더관리/커뮤니티글관리/개인정보관리)
			tabMenuClick(uNickname);

			//메뉴에서 커뮤니티 클릭시 탭바뀌는 이벤트
			tabChange();//커뮤니티용 탭 변화
			//tabChange2();//마이페이지용 탭 변화

			//탭에서 생성된 캘린더 카테고리 클릭시 발생하는 이벤트
			calMenuClick();

			//탭에서 캘린더 add 클릭시 발생하는 이벤트
			tabaddClick();
			
			/*------- title_list.js ----------*/
			//섹션영역에 캘린더 썸네일 클릭시 발생하는 이벤트
			calThumbnailClick();

			//섹션영역에 캘린더 add 클릭시 발생하는 이벤트
			caladdClick();

	    });
	</script>
 <div class="tab"><!-- tab.js -->
  	<jsp:include page="./tab.jsp"/>

</div>

<section><!--callist.js -->
<div class="articles"><!-- 오늘 날짜값 넣기-->
	 <div class="nowdate" style="display:none">
	 	<%=sf.format(nowTime)%>
	 </div>
	 <ul class="title_list">
	  	<jsp:include page="./title_list.jsp"/>

	 </ul>
 </div>
</section>

