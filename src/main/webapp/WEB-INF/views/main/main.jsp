<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!-- 메인 시작 -->
<div class="page-main">
	<div>
		<h3>게시판 최신글</h3>
		<c:if test="${count==0 }">
			<div class="result-display">
				등록된 게시물이 없습니다.
			</div>
		</c:if>
		
		<c:if test="${count>0}">
			<div>
				<c:forEach var="board" items="${list}">
				<div class="horizontal-area">
					<a href="${pageContext.request.contextPath}/board/detail.do?board_num=${board.board_num}">
						<c:if test="${!empty board.filename}">
							<img src="${pageContext.request.contextPath}/board/imageView.do?board_num=${board.board_num}">
						</c:if>
						<c:if test="${empty board.filename}">
							<img src="${pageContext.request.contextPath}/resources/image/blank.jpg">
						</c:if>
						
						<span>${board.title}</span>
					</a>
				</div>
				</c:forEach>
			</div>
		</c:if>
		<div style="clear:both;">
			<hr width="100%" size="1" noshade="noshade">
		</div>
	</div>
</div>
<!-- 메인 끝 -->