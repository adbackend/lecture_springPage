package kr.spring.member.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

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
import org.springframework.web.servlet.ModelAndView;

import kr.spring.member.service.MemberService;
import kr.spring.member.vo.MemberVO;
import kr.spring.util.AuthCheckException;

@Controller
public class MemberController {

	private static final Logger logger = LoggerFactory.getLogger(MemberController.class);
	
	@Autowired
	private MemberService memberService;
	
	//자바빈(VO)초기화
	@ModelAttribute
	public MemberVO initCommand() {
		return new MemberVO();
	}
	
	//회원가입 - 아이디 중복 체크(Ajax)
	@RequestMapping("/member/confirmId.do")
	@ResponseBody
	public Map<String,String> process(@RequestParam String id){
		
		logger.debug("<<id>> : " + id);
		
		Map<String,String> map = new HashMap<String,String>();
		
		MemberVO member = memberService.selectCheckMember(id);
		if(member!=null) {
			//아이디 중복
			map.put("result", "idDuplicated");
		}else {
			if(!Pattern.matches("^[A-Za-z0-9]{4,12}$", id)) {
				//패턴 불일치
				map.put("result", "notMatchPattern");
			}else{
				//아이디 미중복
				map.put("result", "idNotFound");
			}
		}
		
		return map;
	}
	
	//회원가입 - 회원가입 폼 호출
	@GetMapping("/member/registerUser.do")
	public String form() {
		
		logger.debug("<<회원가입 폼 호출>>");
		
		return "memberRegister";
	}
	
	//회원가입 - 회원가입 처리
	@PostMapping("/member/registerUser.do")
	public String submit(@Valid MemberVO memberVO, BindingResult result) {
		
		logger.debug("<<회원 정보>> : " + memberVO);
		
		//유효성 검사 결과 오류가 있으면 폼 호출
		if(result.hasErrors()) {
			return form();
		}
		
		//회원가입
		memberService.insertMember(memberVO);
		
		return "redirect:/main/main.do";
	}
	
	//로그인 - 로그인 폼 호출
	@GetMapping("/member/login.do")
	public String formLogin() {
		return "memberLogin";//타일스 식별자
	}
	
	//로그인 - 로그인 데이터 처리
	@PostMapping("/member/login.do")
	public String submitLogin(@Valid MemberVO memberVO,BindingResult result, 
			                  HttpSession session) {
		
		logger.debug("<<회원 로그인>> : " + memberVO);
		
		//유효성 체크 결과 오류가 있으면 폼 호출
		//id와 passwd 필드만 체크
		if(result.hasFieldErrors("id") || result.hasFieldErrors("passwd")) {
			return formLogin();
		}
		
		//로그인 체크(id,비밀번호 일치 여부 체크)
		try {
			MemberVO member = memberService.selectCheckMember(memberVO.getId());
			boolean check = false;
			
			if(member!=null) {//아이디 일치
				//비밀번호 일치 여부 체크               사용자가 입력한 비밀번호
				check = member.isCheckedPassword(memberVO.getPasswd());
			}
			if(check) {
				//인증 성공, 로그인 처리
				session.setAttribute("user_num", member.getMem_num());
				session.setAttribute("user_id", member.getId());
				session.setAttribute("user_auth", member.getAuth());
				session.setAttribute("user_photo", member.getPhoto());
				
				return "redirect:/main/main.do";
			}else {
				//인증 실패
				throw new AuthCheckException();
			}
		}catch(AuthCheckException e) {
			//인증 실패로 메시지 생성 및 로그인 폼 호출
			result.reject("invalidIdOrPassword");
			
			return formLogin();
		}
	}
	
	//로그아웃
	@RequestMapping("/member/logout.do")
	public String processLogout(HttpSession session) {
		//로그아웃
		session.invalidate();
		
		return "redirect:/main/main.do";
	}
	
	//회원 상세 정보
	@RequestMapping("/member/myPage.do")
	public String process(HttpSession session, Model model) {
		Integer user_num = (Integer)session.getAttribute("user_num");
		
		MemberVO member = memberService.selectMember(user_num);
		
		logger.debug("<<회원 상세 정보>> : " + member);
		
		model.addAttribute("member", member);
		
		return "memberView";//타일스 식별자
	}
	
	//회원 프로필 사진 업데이트(ajax)
	@RequestMapping("/member/updateMyPhoto.do")
	@ResponseBody
	public Map<String,String> processProfile(MemberVO memberVO, HttpSession session){
		Map<String,String> map = new HashMap<String,String>();
		
		Integer user_num = (Integer)session.getAttribute("user_num");
		if(user_num==null) {//로그인이 되지 않은 상태
			map.put("result", "logout");
		}else {//로그인 된 상태
			memberVO.setMem_num(user_num);
			memberService.updateProfile(memberVO);
			
			//이미지를 업로드한 후 세션에 저장된 프로필 사진 정보 갱신
			session.setAttribute("user_photo", memberVO.getPhoto());
			
			map.put("result", "success");
		}
		
		return map;
	}
	
	//프로필 사진 출력
	@RequestMapping("/member/photoView.do")
	public ModelAndView viewImage(HttpSession session) {
		
		Integer user_num = (Integer)session.getAttribute("user_num");
		MemberVO memberVO = memberService.selectMember(user_num);
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("imageView");
		mav.addObject("imageFile", memberVO.getPhoto());
		mav.addObject("filename", memberVO.getPhoto_name());
		
		return mav;
	}
	
	//회원정보수정 - 수정 폼
	@GetMapping("/member/update.do")
	public String formUpdate(HttpSession session, Model model) {
		Integer user_num = (Integer)session.getAttribute("user_num");
		
		MemberVO memberVO = memberService.selectMember(user_num);
		
		model.addAttribute("memberVO", memberVO);
		
		return "memberModify"; //타일스 설정
	}
	
	//회원정보수정 - 수정 데이터 처리
	@PostMapping("/member/update.do")
	public String submitUpdate(@Valid MemberVO memberVO,BindingResult result, 
			                   HttpSession session) {
		
		logger.debug("<<회원정보수정>> : " + memberVO);
		
		//유효성 체크 결과 오류가 있으면 폼 호출
		if(result.hasErrors()) {
			return "memberModify";
		}
		
		Integer user_num = (Integer)session.getAttribute("user_num");
		memberVO.setMem_num(user_num);
		
		//회원정보수정
		memberService.updateMember(memberVO);
		
		return "redirect:/member/myPage.do";
	}
	
	//비밀번호 수정 - 폼 호출
	@GetMapping("/member/changePassword.do")
	public String formChangePassword() {
		return "memberChangePassword";//타일스 식별자
	}
	
	//비밀번호 수정 - 수정 데이터 처리
	@PostMapping("/member/changePassword.do")
	public String submitChangePassword(@Valid MemberVO memberVO, BindingResult result,
			                           HttpSession session) {
		
		logger.debug("<<비밀번호수정>> : " + memberVO);
		
		//유효성 체크 결과 오류가 있으면 폼 호출
		if(result.hasFieldErrors("now_passwd") || result.hasFieldErrors("passwd")) {
			return formChangePassword();
		}
		
		Integer user_num = (Integer)session.getAttribute("user_num");
		MemberVO member = memberService.selectMember(user_num);
		//폼에서 전송한 현재 비밀번호와 DB에서 받아온 현재 비밀번호 일치 여부 체크
		  //DB에서 읽어온 비밀번호          사용자가 입력한 비밀번호
		if(!member.getPasswd().equals(memberVO.getNow_passwd())) {
			//비밀번호 불일치
			result.rejectValue("now_passwd", "invalidPassword");
			return formChangePassword();
		}
		
		//비밀번호수정
		memberVO.setMem_num(user_num);
		memberService.updatePassword(memberVO);
		
		return "redirect:/member/myPage.do";
	}
	
	//회원 탈퇴 - 폼 호출
	@GetMapping("/member/delete.do")
	public String formDelete() {
		return "memberDelete";//타일스 식별자
	}
	
	//회원 탈퇴 - 데이터 처리
	@PostMapping("/member/delete.do")
	public String submitDelete(@Valid MemberVO memberVO, BindingResult result, 
			                   HttpSession session) {
		
		logger.debug("<<회원탈퇴>> : " + memberVO);
		
		//id,passwd 필드의 에러만 체크
		if(result.hasFieldErrors("id") || result.hasFieldErrors("passwd")) {
			return formDelete();
		}
		
		String user_id = (String)session.getAttribute("user_id");
		
		MemberVO dbMember = memberService.selectCheckMember(memberVO.getId());
		boolean check = false;
		
		//사용자가 입력한 아이디가 db에 저장되어 있고 세션에 저장된 id와 일치함
		if(dbMember!=null && dbMember.getId().equals(user_id)) {
			//비밀버호 일치 여부 체크
			check = dbMember.isCheckedPassword(memberVO.getPasswd());
		}
		if(check) {
			//인증 성공, 회원정보 삭제
			memberVO.setMem_num((Integer)session.getAttribute("user_num"));
			memberService.deleteMember(memberVO.getMem_num());
			//로그아웃
			session.invalidate();
			return "redirect:/main/main.do";
		}else {
			//인증 실패
			result.reject("invalidIdOrPassword");
			return formDelete();
		}		
	}
	
}











