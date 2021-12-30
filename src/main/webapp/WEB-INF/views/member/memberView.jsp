<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-3.6.0.min.js"></script>    
<script type="text/javascript">
	$(function(){
		//MyPage 프로필 사진 수정
		$('#photo_btn').click(function(){
			$('#photo_choice').show();
			$(this).hide();
		});
		
		//파일 미리 보기
		var photo_path;
		var my_photo;
		$('#upload').change(function(){
			var upload = document.getElementById('upload');
			my_photo = upload.files[0];
			if(my_photo){
				var reader = new FileReader();
				
				reader.readAsDataURL(my_photo);
				
				reader.onload = function(){
					photo_path = $('.my-photo').attr('src');
					$('.my-photo').attr('src',reader.result);
				};
			}
		});
		
		//ajax를 이용한 파일 전송
		$('#photo_submit').click(function(){
			if($('#upload').val()==''){
				alert('파일을 선택하세요!');
				$('#upload').focus();
				return;
			}
			
			//파일 전송
			var form_data = new FormData();
			form_data.append('upload',my_photo);
			$.ajax({
				data:form_data,
				type:'post',
				url:'updateMyPhoto.do',
				dataType:'json',
				cache:false,
				contentType:false,
				enctype:'multipart/form-data',
				processData:false,
				success:function(param){
					if(param.result == 'logout'){
						alert('로그인 후 사용하세요');
					}else if(param.result == 'success'){
						alert('프로필 사진이 수정되었습니다.');
						$('#upload').val('');
						$('#photo_choice').hide();
						$('#photo_btn').show();
					}else{
						alert('파일 전송 오류 발생');
					}
				},
				error:function(){
					alert('네트워크 오류 발생');
				}
			});
		});
		
		//프로필 사진 전송 취소
		$('#photo_reset').click(function(){
			$('.my-photo').attr('src',photo_path);
			$('#upload').val('');
			$('#photo_choice').hide();
			$('#photo_btn').show();
		});
		
	});
</script>
<!-- 중앙 내용 시작 -->
<div class="page-main">
	<h2>프로필 사진</h2>
	<ul>
		<li>
			<c:if test="${empty member.photo_name}">
			<img src="${pageContext.request.contextPath}/resources/images/blank.jpg" width="100" height="100" class="my-photo">
			</c:if>
			<c:if test="${!empty member.photo_name}">
			<img src="${pageContext.request.contextPath}/member/photoView.do" width="100" height="100" class="my-photo">
			</c:if>
		</li>
		<li>
			<div class="align-center">
				<input type="button" value="수정" id="photo_btn">
			</div>
			<div id="photo_choice" style="display:none;">
				<input type="file" id="upload" accept="image/gif,image/png,image/jpeg">
				<input type="button" value="전송" id="photo_submit">
				<input type="button" value="취소" id="photo_reset">
			</div>
		</li>
	</ul>
	<h2>회원 상세 정보</h2>
	<ul>
		<li>이름 : ${member.name}</li>
		<li>전화번호 : ${member.phone}</li>
		<li>이메일 : ${member.email}</li>
		<li>우편번호 : ${member.zipcode}</li>
		<li>주소 : ${member.address1}</li>
		<li>상세주소 : ${member.address2}</li>
		<li>가입날짜 : ${member.reg_date}</li>
		<li>정보 수정일 : ${member.modify_date}</li>
	</ul>
	<hr size="1" width="100%">
	<p class="align-right">
		<input type="button" value="회원정보수정" onclick="location.href='update.do'">
		<input type="button" value="비밀번호변경" onclick="location.href='changePassword.do'">
		<input type="button" value="회원탈퇴" onclick="location.href='delete.do'">
	</p>
</div>
<!-- 중앙 내용 끝 -->






