package kr.spring.interceptor;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import kr.spring.board.service.BoardService;
import kr.spring.board.vo.BoardVO;

public class WriterCheckInterceptor extends HandlerInterceptorAdapter{
	private static final Logger logger = 
			LoggerFactory.getLogger(WriterCheckInterceptor.class);
	
	@Autowired
	private BoardService boardService;
	
	@Override
	public boolean preHandle(HttpServletRequest request, 
			                 HttpServletResponse response,
			                 Object handler)throws Exception{
		
		logger.debug("====로그인 회원번호와 작성자 회원번호 일치여부 체크");
		
		//작성자의 회원 번호 구하기
		int board_num = Integer.parseInt(request.getParameter("board_num"));
		BoardVO board = boardService.selectBoard(board_num);
		
		//로그인 회원 번호 구하기
		HttpSession session = request.getSession();
		Integer user_num = (Integer)session.getAttribute("user_num");
		
		logger.debug("<<로그인 회원 번호>> : " + user_num);
		logger.debug("<<작성자 회원 번호>> : " + board.getMem_num());
		
		//로그인 회원 번호와 작성자 회원 번호 일치 여부 체크
		if(user_num != board.getMem_num()) {
			logger.debug("<<로그인 회원 번호와 작성자 회원 번호 불일치>>");
			
			request.setAttribute("accessMsg", "로그인 회원 번호와 작성자 회원 번호 불일치");
			request.setAttribute("accessBtn", "게시판 목록");
			request.setAttribute("accessUrl", request.getContextPath()+"/board/list.do");
			
			RequestDispatcher dispatcher = 
					request.getRequestDispatcher(
							       "/WEB-INF/views/common/notice.jsp");
			dispatcher.forward(request, response);
			
			return false;
		}//end of if
		
		logger.debug("<<로그인 회원 번호와 작성자 회원 번호 일치>>");
		
		return true;
	}
	
}






