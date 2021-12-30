<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-3.6.0.min.js"></script>
<script type="text/javascript">
	$(function(){
		//검색 유효성 체크
		$('#search_form').submit(function(){
			if($('#keyword').val().trim() == ''){
				alert('검색어를 입력하세요!');
				$('#keyword').val('').focus();
				return false;
			}
		});
	});
</script>
<!-- 중앙 내용 시작 -->
<div class="page-main">
	<h2>게시판 목록</h2>
	<form id="search_form" action="list.do" method="get">
		<ul class="search">
			<li>
				<select name="keyfield" id="keyfield">
					<option value="1">제목</option>
					<option value="2">ID</option>
					<option value="3">내용</option>
					<option value="4">제목+내용</option>
				</select>
			</li>
			<li>
				<input type="search" name="keyword" id="keyword">
			</li>
			<li>
				<input type="submit" value="찾기">
				<input type="button" value="목록" onclick="location.href='list.do'">
			</li>
		</ul>
	</form>
	<c:if test="${!empty user_num}">
	<div class="align-right">
		<input type="button" value="글쓰기" onclick="location.href='write.do'">
	</div>
	</c:if>
	<c:if test="${count == 0}">
	<div class="result-display">
		등록된 게시물이 없습니다.
	</div>	
	</c:if>
	<c:if test="${count > 0}">
	<table>
		<tr>
			<th>번호</th>
			<th width="400">제목</th>
			<th>작성자</th>
			<th>작성일</th>
			<th>조회수</th>
		</tr>
		<c:forEach var="board" items="${list}">
		<tr>
			<td>${board.board_num}</td>
			<td><a href="detail.do?board_num=${board.board_num}">${board.title}</a></td>
			<td>${board.id}</td>
			<td>${board.reg_date}</td>
			<td>${board.hit}</td>
		</tr>	
		</c:forEach>
	</table>
	<div class="align-center">${pagingHtml}</div>
	</c:if>
</div>
<!-- 중앙 내용 끝 -->





