<%@page import="com.reco.calendar.vo.CalPost"%>
<%@page import="java.util.List"%>
<%@page import="com.reco.customer.vo.Customer"%>
<%@page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.util.Date"%>

<link rel=stylesheet href="./css/calendar.css" >
<script src="./js/calendar.js"></script>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script>
	$(function(){
		//달력에서 날짜 클릭시 발생하는 이벤트 
		dateClick();
	});
</script>
<%
Customer c = (Customer)session.getAttribute("loginInfo"); 
	List<CalPost> list = (List)request.getAttribute("list");
%>
<section>
<%	for(CalPost cp : list){
	//int uIdx = cp.getCalinfo().getCustomer().getUIdx();
%>
<div> 
	<ul>
		<span><%= cp.getCalinfo().getCalCategory() %></span>
	</ul>
</div>

<% } %>

<div class="container">
      <div class="body">
        <div class="calendar">
          <div class="header">
              <div class="year-month" style="display: block;" ></div>          
              <div class="nav">
                  <button class="nav-btn go-prev" onclick="prevMonth()">&lt;</button>
                  <button class="nav-btn go-today" onclick="goToday()">Today</button>
                  <button class="nav-btn go-next" onclick="nextMonth()">&gt;</button>
              </div>
          </div>

	<script>
            var dateChange = () => {
            var date_input = document.getElementById("date");
            //var text_input = document.getElementById("text");
            text_input.value = date_input.value;
            };
     </script>
        <input type="date" id="date" onchange="dateChange();" />
        <!-- <input type="text" id="text" /> -->

          <div class="main">
              <div class="days">
                  <div class="day">일</div>
                  <div class="day">월</div>
                  <div class="day">화</div>
                  <div class="day">수</div>
                  <div class="day">목</div>
                  <div class="day">금</div>
                  <div class="day">토</div>
              </div>
              <a class="dates" href ="#" target="_blank"></a>
          </div>
      </div>
    </div>  
  </div>
</section>	  

 <%-- <%	for(CalPost cp :list){ 
	/* int calIdx = cp.getCalinfo().getCalIdx(); */
	String calCategory = cp.getCalinfo().getCalCategory();
	String calMainImg = cp.getCalMainImg();
	
%>	
	<div class="calpost">
		<%=calCategory%>
	</div>
 <%    
    
 }%> --%>