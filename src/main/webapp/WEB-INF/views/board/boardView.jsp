<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-3.6.0.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/videoAdapter.js"></script>
<script type="text/javascript">
	$(function(){
		var currentPage;
		var count;
		var rowCount;
		//댓글 목록
		function selectData(pageNum,board_num){
			currentPage = pageNum;
			
			if(pageNum == 1){
				//처음 호출시는 해당 ID의 div의 내부 내용물을 제거
				$('#output').empty();
			}
			
			//로딩 이미지 노출
			$('#loading').show();
			
			$.ajax({
				type:'post',
				data:{pageNum:pageNum,board_num:board_num},
				url:'listReply.do',
				dataType:'json',
				cache:false,
				timeout:30000,
				success:function(param){
					//로딩 이미지 감추기
					$('#loading').hide();
					count = param.count;
					rowCount = param.rowCount;
					
					$(param.list).each(function(index,item){
						var output = '<div class="item">';
						output += '<h4>' + item.id + '</h4>';
						output += '<div class="sub-item">';
						output +='   <p>' + item.re_content.replace(/</gi,'&lt;').replace(/>/gi,'&gt;') + '</p>';
						output +='<span>최근수정일: '+item.re_mdate+', 등록일'+ item.re_date +'</span>';
						if($('#mem_num').val()==item.mem_num){
							//로그인한 회원 번호가 댓글 작성자 회원 번호와 같으면
							output += ' <input type="button" data-num="'+item.re_num+'" data-mem="'+item.mem_num+'" value="수정" class="modify-btn">';
							output += ' <input type="button" data-num="'+item.re_num+'" data-mem="'+item.mem_num+'" value="삭제" class="delete-btn">';
						}
						output += '  <hr size="1" noshade>';
						output += '</div>';
						output += '</div>';
						
						//문서 객체에 추가
						$('#output').append(output);						
					});//end of each
					
					//paging button 처리
					if(currentPage>=Math.ceil(count/rowCount)){
						//다음 페이지가 없음
						$('.paging-button').hide();
					}else{
						//다음 페이지가 존재
						$('.paging-button').show();
					}
					
				},
				error:function(){
					alert('네트워크 오류 발생');
				}
			});
			
		}
		//다음 댓글 보기 버튼 클릭시 데이터 추가
		$('.paging-button input').click(function(){
			var pageNum = currentPage + 1;
			selectData(pageNum,$('#board_num').val());
		});
		//댓글 등록
		$('#re_form').submit(function(event){
			if($('#re_content').val().trim()==''){
				alert('내용을 입력하세요!');
				$('#re_content').val('').focus();
				return false;
			}
			var data = $(this).serialize();
			//등록
			$.ajax({
				type:'post',
				data:data,
				url:'writeReply.do',
				dataType:'json',
				cache:false,
				timeout:30000,
				success:function(param){
					if(param.result == 'logout'){
						alert('로그인해야 작성할 수 있습니다.');
					}else if(param.result == 'success'){
						//폼초기화
						initForm();
						//댓글 작성이 성공하면 새로 삽입한 글을 포함해서 첫번째 페이지의
						//게시글들을 다시 호출
						selectData(1,$('#board_num').val());
					}else{
						alert('댓글 등록시 오류 발생!');
					}
				},
				error:function(){
					alert('네트워크 오류 발생');
				}
			});
			//기본 이벤트 제거
			event.preventDefault();
		});
		//댓글 작성 폼 초기화
		function initForm(){
			$('textarea').val('');
			$('#re_first .letter-count').text('300/300');
		}
		//textarea에 내용 입력시 글자수 체크
		$(document).on('keyup','textarea',function(){
			//남은 글자수를 구함
			var inputLength = $(this).val().length;
			
			if(inputLength>300){//300자를 넘어선 경우
				$(this).val($(this).val().substring(0,300));
			}else{//300자 이하인 경우
				var remain = 300 - inputLength;
				remain += '/300';
				if($(this).attr('id') == 're_content'){
					//등록폼 글자수
					$('#re_first .letter-count').text(remain);
				}else{
					//수정폼 글자수
					$('#mre_first .letter-count').text(remain);
				}
			}
		});
		//댓글 수정 버튼 클릭시 수정폼 노출
		$(document).on('click','.modify-btn',function(){
			//댓글 글번호
			var re_num = $(this).attr('data-num');
			//작성자 회원 번호
			var mem_num = $(this).attr('data-mem');
			//댓글 내용
			var content = $(this).parent().find('p').html().replace(/<br>/gi,'\n');
			
			//댓글 수정폼 UI
			var modifyUI = '<form id="mre_form">';
			   modifyUI += '  <input type="hidden" name="re_num" id="mre_num" value="'+re_num+'">';
			   modifyUI += '  <input type="hidden" name="mem_num" id="mmem_num" value="'+mem_num+'">';
			   modifyUI += '  <textarea rows="3" cols="50" name="re_content" id="mre_content" class="rep-content">'+content+'</textarea>';
			   modifyUI += '  <div id="mre_first"><span class="letter-count">300/300</span></div>';	
			   modifyUI += '  <div id="mre_second" class="align-right">';
			   modifyUI += '     <input type="submit" value="수정">';
			   modifyUI += '     <input type="button" value="취소" class="re-reset">';
			   modifyUI += '  </div>';
			   modifyUI += '  <hr size="1" noshade width="90%">';
			   modifyUI += '</form>';
			   
			//이전에 이미 수정하는 댓글이 있을 경우 수정버튼을 클릭하면
			//숨김 sub-item을 환원시키고 수정폼을 최기화함
			initModifyForm();
			
			//지금 클릭해서 수정하고자 하는 데이터는 감추기
			//수정버튼을 감싸고 있는 div
			$(this).parent().hide();
			
			//수정폼을 수정하고자하는 데이터가 있는 div에 노출
			$(this).parents('.item').append(modifyUI);
			
			//입력한 글자수 셋팅
			var inputLength = $('#mre_content').val().length;
			var remain = 300 - inputLength;
			remain += '/300';
			
			//문서 객체에 반영
			$('#mre_first .letter-count').text(remain);		
		});
		//수정폼에서 취소 버튼 클릭시 수정폼 초기화
		$(document).on('click','.re-reset',function(){
			initModifyForm();
		});
		//댓글 수정 폼 초기화
		function initModifyForm(){
			$('.sub-item').show();
			$('#mre_form').remove();
		}
		//댓글 수정
		$(document).on('submit','#mre_form',function(event){
			if($('#mre_content').val().trim()==''){
				alert('내용을 입력하세요');
				$('#mre_content').val('').focus();
				return false;
			}
			
			//폼에 입력한 데이터 반환
			 var data = $(this).serialize();
			 
			//수정
			$.ajax({
				url:'updateReply.do',
				type:'post',
				data:data,
				dataType:'json',
				cache:false,
				timeout:30000,
				success:function(param){
					if(param.result == 'logout'){
						alert('로그인해야 수정할 수 있습니다.');
					}else if(param.result == 'success'){
						$('#mre_form').parent().find('p').html($('#mre_content').val().replace(/</g,'&lt;').replace(/>/g,'&gt;'));
						//수정폼 초기화
						initModifyForm();
					}else if(param.result == 'wrongAccess'){
						alert('타인의 글을 수정할 수 없습니다.');
					}else{
						alert('댓글 수정시 오류 발생');
					}
				},
				error:function(){
					alert('네트워크 오류 발생');
				}
			});
			
			//기본 이벤트 제거
			event.preventDefault();			
		});
		//댓글 삭제
		$(document).on('click','.delete-btn',function(){
			//댓글 번호
			var re_num = $(this).attr('data-num');
			//작성자 회원 번호
			var mem_num = $(this).attr('data-mem');
			
			$.ajax({
				type:'post',
				url:'deleteReply.do',
				data:{re_num:re_num,mem_num:mem_num},
				dataType:'json',
				cache:false,
				timeout:30000,
				success:function(param){
					if(param.result == 'logout'){
						alert('로그인해야 삭제할 수 있습니다.');
					}else if(param.result == 'success'){
						alert('삭제 완료!');
						selectData(1,$('#board_num').val());
					}else if(param.result == 'wrongAccess'){
						alert('타인의 글을 삭제할 수 없습니다.');
					}else{
						alert('댓글 삭제시 오류 발생');
					}
				},
				error:function(){
					alert('네트워크 오류 발생');
				}
			});
			
		});
		//초기 데이터(목록) 호출
		selectData(1,$('#board_num').val());
	});
</script>
<!-- 중앙 내용 시작 -->
<div class="page-main">
	<h2>${board.title}</h2>
	<ul>
		<li>번호 : ${board.board_num}</li>
		<li>작성자 : ${board.id}</li>
		<li>조회수 : ${board.hit}</li>
		<li>작성일 : ${board.reg_date}</li>
		<li>최근수정일 : ${board.modify_date}</li>
	</ul>
	<hr size="1" width="100%">
	<c:if test="${!empty board.filename}">
	<div class="align-center">
		<img src="imageView.do?board_num=${board.board_num}" style="max-width:500px">
	</div>
	</c:if>
	<p>${board.content}</p>
	<hr size="1" width="100%">
	<div class="align-right">
		<c:if test="${!empty user_num && user_num == board.mem_num}">
		<input type="button" value="수정" 
		                onclick="location.href='update.do?board_num=${board.board_num}'">
		<input type="button" value="삭제" id="delete_btn">
		<script type="text/javascript">
			var delete_btn = document.getElementById('delete_btn');
			delete_btn.onclick=function(){
				var choice = confirm('삭제하시겠습니까?');
				if(choice){
					location.replace('delete.do?board_num=${board.board_num}');
				}
			};
		</script>
		</c:if>
		<input type="button" value="목록" onclick="location.href='list.do'">
	</div>
	<hr size="1" width="100%" noshade="noshade">
	<div id="reply_div">
		<span class="reply-title">댓글 달기</span>
		<form id="re_form">
			<input type="hidden" name="board_num" value="${board.board_num}"
			       id="board_num">
			<input type="hidden" name="mem_num" value="${user_num}" id="mem_num">
			<textarea rows="3" cols="50" name="re_content" 
			   id="re_content" class="rep-content" 
			   <c:if test="${empty user_num}">disabled="disabled"</c:if>
			   ><c:if test="${empty user_num}">로그인해야 작성할 수 있습니다.</c:if></textarea>
			<c:if test="${!empty user_num}">
			<div id="re_first">
				<span class="letter-count">300/300</span>
			</div>
			<div id="re_second" class="align-right">
				<input type="submit" value="전송">
			</div>
			</c:if>
		</form>
	</div>
	<!-- 댓글 목록 출력 -->
	<div id="output"></div>
	<div class="paging-button" style="display:none;">
		<input type="button" value="다음글 보기">
	</div>
	<div id="loading" style="display:none;">
		<img src="${pageContext.request.contextPath}/resources/images/ajax-loader.gif">
	</div>
</div>
<!-- 중앙 내용 끝 -->






