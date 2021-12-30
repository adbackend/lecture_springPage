package kr.spring.board.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.spring.board.dao.BoardMapper;
import kr.spring.board.vo.BoardReplyVO;
import kr.spring.board.vo.BoardVO;

@Service
@Transactional
public class BoardServiceImpl implements BoardService{

	@Autowired
	private BoardMapper boardMapper;
	
	@Override
	public List<BoardVO> selectList(Map<String, Object> map) {
		return boardMapper.selectList(map);
	}

	@Override
	public int selectRowCount(Map<String, Object> map) {
		return boardMapper.selectRowCount(map);
	}

	@Override
	public void insertBoard(BoardVO board) {
		boardMapper.insertBoard(board);
	}

	@Override
	public BoardVO selectBoard(Integer board_num) {
		return boardMapper.selectBoard(board_num);
	}

	@Override
	public void updateHit(Integer board_num) {
		boardMapper.updateHit(board_num);
	}

	@Override
	public void updateBoard(BoardVO board) {
		boardMapper.updateBoard(board);
	}

	@Override
	public void deleteBoard(Integer board_num) {
		//댓글이 존재하면 댓글을 우선 삭제하고 부모글을 삭제
		boardMapper.deleteReplyByBoardNum(board_num);
		boardMapper.deleteBoard(board_num);
	}

	@Override
	public void deleteFile(Integer board_num) {
		boardMapper.deleteFile(board_num);
	}

	@Override
	public List<BoardReplyVO> selectListReply(Map<String, Object> map) {
		return boardMapper.selectListReply(map);
	}

	@Override
	public int selectRowCountReply(Map<String, Object> map) {
		return boardMapper.selectRowCountReply(map);
	}

	@Override
	public void insertReply(BoardReplyVO boardReply) {
		boardMapper.insertReply(boardReply);
	}

	@Override
	public void updateReply(BoardReplyVO boardReply) {
		boardMapper.updateReply(boardReply);
	}

	@Override
	public void deleteReply(Integer re_num) {
		boardMapper.deleteReply(re_num);
	}

}
