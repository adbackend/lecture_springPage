package kr.spring.main.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import kr.spring.board.service.BoardService;
import kr.spring.board.vo.BoardVO;
import kr.spring.util.PagingUtil;

@Controller
public class MainController {
	
	@Autowired
	private BoardService boardService;
	private static final Logger logger = LoggerFactory.getLogger(MainController.class);
	
	@RequestMapping("/main/main.do")
	public String main(Model model) {
		
		Map<String,Object> map = new HashMap<String, Object>();
		
		int count = boardService.selectRowCount(map);
		
		logger.debug("=======count======="+count);
		
		PagingUtil page = new PagingUtil(1,count,12,10,null);
		
		map.put("start", page.getStartCount());
		map.put("end", page.getEndCount());
		
		List<BoardVO> list = null;
		if(count >0) {
			list = boardService.selectList(map);
			logger.debug("====list====="+list);
		}
		
		model.addAttribute("count",count);
		model.addAttribute("list",list);
		
		
		
		
		return "main";//타일스 식별자
	}
}
