<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>     
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-3.6.0.min.js"></script>
<script type="text/javascript">
	$(function(){
		//비밀번호 수정 체크
		$('#passwd').keyup(function(){
			if($('#confirm_passwd').val()!='' && $('#confirm_passwd').val()!=$(this).val()){
				$('#message_id').text('비밀번호 불일치').css('color','red');
			}else if($('#confirm_passwd').val()!='' && $('#confirm_passwd').val()==$(this).val()){
				$('#message_id').text('비밀번호 일치').css('color','#000');
			}
		});
		
		$('#confirm_passwd').keyup(function(){
			if($('#passwd').val()!='' && $('#passwd').val()!=$(this).val()){
				$('#message_id').text('비밀번호 불일치').css('color','red');
			}else if($('#passwd').val()!='' && $('#passwd').val()==$(this).val()){
				$('#message_id').text('비밀번호 일치').css('color','#000');
			}
		});
		
		$('#change_form').submit(function(){
			if($('#now_passwd').val().trim()==''){
				alert('현재 비밀번호를 입력하세요');
				$('#now_passwd').val('').focus();
				return false;
			}
			if($('#passwd').val().trim()==''){
				alert('변경할 비밀번호를 입력하세요');
				$('#passwd').val('').focus();
				return false;
			}
			if($('#confirm_passwd').val().trim()==''){
				alert('변경할 비밀번호 확인을 입력하세요');
				$('#confirm_passwd').val('').focus();
				return false;
			}
			if($('#passwd').val()!=$('#confirm_passwd').val()){
				$('#message_id').text('비밀번호 불일치').css('color','red');
				return false;
			}
		});
		
	});
</script>    
<!-- 중앙 내용 시작 -->
<div class="page-main">
	<h2>비밀번호수정</h2>
	<form:form id="change_form" action="changePassword.do" modelAttribute="memberVO">
		<ul>
			<li>
				<label for="now_passwd">현재 비밀번호</label>
				<form:password path="now_passwd"/>
				<form:errors path="now_passwd" cssClass="error-color"/>
			</li>
			<li>
				<label for="passwd">변경할 비밀번호</label>
				<form:password path="passwd" placeholder="4~12자 영문,숫자만 허용"/>
				<form:errors path="passwd" cssClass="error-color"/>
			</li>
			<li>
				<label for="confirm_passwd">변경할 비밀번호 확인</label>
				<input type="password" id="confirm_passwd" placeholder="4~12자 영문,숫자만 허용"/>
				<span id="message_id" class="error-color"></span>
			</li>
		</ul>
		<div class="align-center">
			<form:button>전송</form:button>
			<input type="button" value="홈으로" 
			       onclick="location.href='${pageContext.request.contextPath}/main/main.do'">
		</div>
	</form:form>
</div>
<!-- 중앙 내용 끝 -->



