package kr.spring.board.controller;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import kr.spring.board.service.BoardService;
import kr.spring.board.vo.BoardReplyVO;
import kr.spring.board.vo.BoardVO;
import kr.spring.util.PagingUtil;
import kr.spring.util.StringUtil;

@Controller
public class BoardController {
	private static final Logger logger = LoggerFactory.getLogger(BoardController.class);
	private int rowCount = 20;
	private int pageCount = 10;
	
	@Autowired
	private BoardService boardService;
	
	//자바빈(VO) 초기화
	@ModelAttribute
	public BoardVO initCommand() {
		return new BoardVO();
	}
	
	//글쓰기 - 폼 호출
	@GetMapping("/board/write.do")
	public String form() {
		return "boardWrite";//타일스 식별자
	}
	
	//글쓰기 - 전송된 데이터 처리
	@PostMapping("/board/write.do")
	public String submit(@Valid BoardVO boardVO, BindingResult result,
			             HttpSession session, HttpServletRequest request) {
		
		logger.debug("<<글쓰기>> : " + boardVO);
		
		//유효성 체크 결과 오류가 있으면 폼을 호출
		if(result.hasErrors()) {
			return form();
		}
		
		//회원번호를 셋팅
		boardVO.setMem_num((Integer)session.getAttribute("user_num"));
		//ip 셋팅
		boardVO.setIp(request.getRemoteAddr());
		//글쓰기
		boardService.insertBoard(boardVO);
		
		return "redirect:/board/list.do";
	}
	
	//게시판 목록
	@RequestMapping("/board/list.do")
	public ModelAndView process(@RequestParam(value="pageNum",defaultValue="1") int currentPage,
			                    @RequestParam(value="keyfield",defaultValue="") String keyfield,
			                    @RequestParam(value="keyword",defaultValue="") String keyword) {
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("keyfield", keyfield);
		map.put("keyword", keyword);
		
		//글의 총갯수 또는 검색된 글의 갯수
		int count = boardService.selectRowCount(map);
		
		logger.debug("<<count>> : " + count);
		
		//페이지 처리
		PagingUtil page = 
			new PagingUtil(keyfield,keyword,currentPage,count,rowCount,pageCount,"list.do");
		
		map.put("start",page.getStartCount());
		map.put("end", page.getEndCount());
		
		List<BoardVO> list = null;
		if(count > 0) {
			list = boardService.selectList(map);
		}
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("boardList");//타일스 식별자
		mav.addObject("count", count);
		mav.addObject("list",list);
		mav.addObject("pagingHtml", page.getPagingHtml());
		
		return mav;
	}
	
	//글상세
	@RequestMapping("/board/detail.do")
	public ModelAndView process(@RequestParam int board_num) {
		
		//해당 글의 조회수 증가
		boardService.updateHit(board_num);
		
		BoardVO board = boardService.selectBoard(board_num);
		
		//HTML 태그 불허
		board.setTitle(StringUtil.useNoHtml(board.getTitle()));
		//HTML 태그 불허, 줄바꿈 허용
		//ckeditor 사용시 주석 처리
		//board.setContent(StringUtil.useBrNoHtml(board.getContent()));
		
		return new ModelAndView("boardView","board",board);
	}
	
	//이미지 출력
	@RequestMapping("/board/imageView.do")
	public ModelAndView viewImage(@RequestParam int board_num) {
		
		BoardVO board = boardService.selectBoard(board_num);
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("imageView");
		                //속성명         속성값(byte[]의 데이터)     
		mav.addObject("imageFile", board.getUploadfile());
		mav.addObject("filename", board.getFilename());
		
		return mav;
	}
	
	//글수정 - 폼 호출
	@GetMapping("/board/update.do")
	public String formUpdate(@RequestParam int board_num, Model model) {
		BoardVO boardVO = boardService.selectBoard(board_num);
		
		model.addAttribute("boardVO", boardVO);
		
		return "boardModify";//타일스 식별자
	}
	
	//글수정 - 전송된 데이터 처리
	@PostMapping("/board/update.do")
	public String submitUpdate(@Valid BoardVO boardVO, BindingResult result,
			                   HttpServletRequest request,
			                   Model model) {
		
		logger.debug("<<글수정>> : " + boardVO);
		
		//유효성 체크 결과 오류가 있으면 폼 호출
		if(result.hasErrors()) {
			return "boardModify";
		}
		
		//ip셋팅
		boardVO.setIp(request.getRemoteAddr());
		
		//글수정
		boardService.updateBoard(boardVO);
		
		//View에 표시할 메시지
		model.addAttribute("message", "글수정 완료!!");
		//이동할 경로
		model.addAttribute("url", request.getContextPath()+"/board/list.do");
		
		return "common/resultView";//JSP 경로 지정
	}
	
	//글수정 - 파일 삭제
	@RequestMapping("/board/deleteFile.do")
	@ResponseBody
	public Map<String,String> processFile(int board_num, HttpSession session){
		
		Map<String,String> map = new HashMap<String,String>();
		
		Integer user_num = (Integer)session.getAttribute("user_num");
		if(user_num==null) {
			map.put("result", "logout");
		}else {
			boardService.deleteFile(board_num);
			map.put("result", "success");
		}
		return map;
	}
	
	//ckeditor를 이용한 이미지 업로드
	@RequestMapping("/board/imageUploader.do")
	@ResponseBody
	public Map<String,Object> uploadImage(MultipartFile upload,
			                              HttpSession session,
			                              HttpServletRequest request)
	                                         throws Exception{
		
		//업로드할 절대 경로 구하기
		String realFolder = 
			session.getServletContext().getRealPath("/resources/image_upload");
		
		//업로드한 파일 이름
		String org_filename = upload.getOriginalFilename();
		String str_filename = System.currentTimeMillis() + org_filename;
		
		logger.debug("<<원본 파일명>> : " + org_filename);
		logger.debug("<<저장할 파일명>> : " + str_filename);
		
		Integer user_num = (Integer)session.getAttribute("user_num");
		
		String filepath = realFolder + "\\" + user_num + "\\" + str_filename;
		logger.debug("<<파일 경로>> : " + filepath);
		
		File f = new File(filepath);
		if(!f.exists()) {
			f.mkdirs();
		}
		
		upload.transferTo(f);
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("uploaded", true);
		map.put("url", request.getContextPath()+"/resources/image_upload/"+user_num+"/"+str_filename);
		
		return map;
	}
	
	//글삭제
	@RequestMapping("/board/delete.do")
	public String submitDelete(@RequestParam int board_num) {
		
		logger.debug("<<글삭제>> : " + board_num);
		
		//글삭제
		boardService.deleteBoard(board_num);
		
		return "redirect:/board/list.do";
	}
	
	//댓글 등록(ajax)
	@RequestMapping("/board/writeReply.do")
	@ResponseBody
	public Map<String,String> writeReply(BoardReplyVO boardReplyVO,
			                             HttpSession session,
			                             HttpServletRequest request){
		logger.debug("<<댓글 등록>> : " + boardReplyVO);
		
		Map<String,String> map = new HashMap<String,String>();
		
		Integer user_num = (Integer)session.getAttribute("user_num");
		if(user_num == null) {
			//로그인 안 됨
			map.put("result", "logout");
		}else {
			//로그인 됨
			//ip 등록
			boardReplyVO.setRe_ip(request.getRemoteAddr());
			//댓글 등록
			boardService.insertReply(boardReplyVO);
			map.put("result", "success");
		}
		
		return map;
	}
	
	//댓글 목록(ajax)
	@RequestMapping("/board/listReply.do")
	@ResponseBody
	public Map<String,Object> getList(
			      @RequestParam(value="pageNum",defaultValue="1") int currentPage,
			      @RequestParam int board_num){
		
		logger.debug("<<currentPage>> : " + currentPage);
		logger.debug("<<board_num>> : " + board_num);
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("board_num", board_num);
		
		//총 글의 갯수
		int count = boardService.selectRowCountReply(map);
		
		PagingUtil page = new PagingUtil(currentPage,count,rowCount,pageCount,
				                 null);
		map.put("start", page.getStartCount());
		map.put("end", page.getEndCount());
		
		List<BoardReplyVO> list = null;
		if(count > 0) {
			list = boardService.selectListReply(map);
		}else {
			list = Collections.emptyList();
		}
		
		Map<String,Object> mapJson = new HashMap<String,Object>();
		mapJson.put("count", count);
		mapJson.put("rowCount", rowCount);
		mapJson.put("list", list);
		
		return mapJson;
	}
	
	//댓글 삭제
	@RequestMapping("/board/deleteReply.do")
	@ResponseBody
	public Map<String,String> deleteReply(@RequestParam int re_num,
			                              @RequestParam int mem_num,
			                              HttpSession session){
		
		logger.debug("<<re_num>> : " + re_num);
		logger.debug("<<mem_num>> : " + mem_num);
		
		Map<String,String> map = new HashMap<String,String>();
		
		Integer user_num = (Integer)session.getAttribute("user_num");
		if(user_num == null) {
			//로그인이 되어있지 않음
			map.put("result", "logout");
		}else if(user_num != null && user_num==mem_num) {
			//로그인이 되어 있고 로그인한 아이디와 작성자 아이디가 일치
			boardService.deleteReply(re_num);
			map.put("result", "success");
		}else {
			//로그인 아이디와 작성자 아이디 불일치
			map.put("result", "wrongAccess");
		}
		return map;
	}
	
	//댓글 수정
	@RequestMapping("/board/updateReply.do")
	@ResponseBody
	public Map<String,String> modifyReply(BoardReplyVO boardReplyVO,
			                              HttpSession session,
			                              HttpServletRequest request){
		logger.debug("<<댓글 수정>> : " + boardReplyVO);
		
		Map<String,String> map = new HashMap<String,String>();
		
		Integer user_num = (Integer)session.getAttribute("user_num");
		if(user_num==null) {
			//로그인이 안 된 경우
			map.put("result", "logout");
		}else if(user_num!=null && user_num == boardReplyVO.getMem_num()) {
			//로그인 회원 번호와 작성자 회원 번호 일치
			//ip 등록
			boardReplyVO.setRe_ip(request.getRemoteAddr());
			
			//댓글 수정
			boardService.updateReply(boardReplyVO);
			map.put("result", "success");
		}else {
			//로그인 회원 번호와 작성자 회원 번호 불일치
			map.put("result", "wrongAccess");
		}
		
		return map;
	}
	
}














