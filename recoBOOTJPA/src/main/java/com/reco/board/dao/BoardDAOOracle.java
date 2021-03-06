package com.reco.board.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.reco.board.service.BoardService;
import com.reco.board.vo.Board;
import com.reco.board.vo.Comment;
import com.reco.dto.PageDTO;
import com.reco.exception.AddException;
import com.reco.exception.FindException;
import com.reco.exception.ModifyException;
import com.reco.exception.RemoveException;
import com.reco.notice.vo.Notice;
import com.reco.sql.MyConnection;

@Repository
public class BoardDAOOracle implements BoardDAOInterface {
	
	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	private Logger logger = LoggerFactory.getLogger(BoardService.class.getName());
	
	@Override
	public int findCount() throws FindException{
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			return session.selectOne("com.reco.board.BoardMapper.findCount");
		}catch(Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		}finally {
			if(session != null) {
				session.close();
			}
		}
	}
	
	@Override
	public int findCountWord(String word) throws FindException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			return session.selectOne("com.reco.board.BoardMapper.findCountWord",word);
		}catch(Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		}finally {
			if(session != null) {
				session.close();
			}
		}		
	}
	
	
	@Override
	public int findCountTitle(String word) throws FindException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			return session.selectOne("com.reco.board.BoardMapper.findCountTitle",word);
		}catch(Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		}finally {
			if(session != null) {
				session.close();
			}
		}		
	}
	
	@Override
	public int findCountType(int intBrdType) throws FindException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			return session.selectOne("com.reco.board.BoardMapper.findCountType", intBrdType);
		}catch(Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		}finally {
			if(session != null) {
				session.close();
			}
		}	
	}
	
	@Override
	public int findCountUNickName(String word) throws FindException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			return session.selectOne("com.reco.board.BoardMapper.findCountUNickName", word);
		}catch(Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		}finally {
			if(session != null) {
				session.close();
			}
		}	
	}
	
	@Override
	public int findCountUNickNameMy(String uNickname) throws FindException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			return session.selectOne("com.reco.board.BoardMapper.findCountUNickNameMy", uNickname);
		}catch(Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		}finally {
			if(session != null) {
				session.close();
			}
		}	
	}
	
	@Override
	public int findCmtCount(int brdIdx) throws FindException{
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			return session.selectOne("com.reco.board.BoardMapper.findCmtCount", brdIdx);
		}catch(Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		}finally {
			if(session != null) {
				session.close();
			}
		}	
	}
	
	//??????????????? ?????? ??? ????????? ??? ?????? ??????
	public int findCmtCountUNickName(String uNickname) throws FindException {
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			return session.selectOne("com.reco.board.BoardMapper.findCmtCountUNickName", uNickname);
		}catch(Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		}finally {
			if(session != null) {
				session.close();
			}
		}	
	}

	@Override
	public List<Board> findBrdAll() throws FindException{
		return findBrdAll(1,5);
	}
	
	
	@Override
	public List<Board> findBrdAll(int currentPage, int cntperpage) throws FindException {
		SqlSession session = null;
		try {
			logger.info("dao???????????????"+currentPage);
			session = sqlSessionFactory.openSession();
			Map<String,Integer> map= new HashMap<>();
			map.put("currentPage", currentPage);//???????????????
			map.put("cntperpage", cntperpage);//???????????? ??? ??????
			List<Board> list = session.selectList("com.reco.board.BoardMapper.findBrdAll", map);
			
			if(list.size() == 0) {
				throw new FindException("????????? ?????? ????????????");
			}
			return list;
		}catch (Exception e) {
			e.printStackTrace();
			throw new FindException(e.getMessage());
		}finally {
			if(session != null) {
				session.close();
			}			
		}
	}
	
	@Override
	public Board findBrdByIdx(int brdIdx) throws FindException {
		return findBrdByIdx(brdIdx, 1, 5);
	}
	
	@Override
	public Board findBrdByIdx(int brdIdx, int cp, int cntperpage) throws FindException {
		SqlSession session =null;
		try {
			session = sqlSessionFactory.openSession();
			Map<String,Integer> map= new HashMap<>();
			map.put("brdIdx", brdIdx); //???????????????
			map.put("currentPage", cp);//???????????????
			map.put("cntperpage", cntperpage);//???????????? ??? ??????
			Board board = session.selectOne("com.reco.board.BoardMapper.findBrdByIdx", map);
			//logger.info(" findBrdByIdxdao??? board:"+board);
			//logger.info("??????????????? dao"+board.getBrdIdx());			
			plusViewCount(brdIdx);
			//board.setBrdIdx(brdIdx);
			return board;
		}catch (Exception e) {
			throw new FindException(e.getMessage());
			
		}finally {
			if(session != null) {
				session.close();
			}
		}
	}
	
	
	
	@Override
	public int addBrd(Board b) throws AddException{
		SqlSession session =null;
		try {
			session = sqlSessionFactory.openSession();
			logger.info("addBrdDAO???:" + b);
			session.insert("com.reco.board.BoardMapper.addBrd",b);
			session.commit();
//		    System.out.println("addBrddao" + brdIdx); //?????????
			logger.info("find??? addDAO???:" + b);
			//List<Comment> comments1 = b.getComments();
			//logger.info("comments.size1:" + comments1.size()); //null
			int brdIdx = b.getBrdIdx();
				//PageDTO2<Board> board = findBrdByIdx(brdIdx);
//			int testBrd = board.getBrdIdx(); 
//			System.out.println("addBrddao2" + testBrd); //0??????
//			logger.info("find??? addDAO???:" + board);
//			List<Comment> comments2 = board.getComments();
//			logger.info("comments.size2:" + comments2.size());
			return brdIdx;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AddException(e.getMessage());
		}finally {
			if(session != null) {
				session.close();
			}
		}		
	}
	
	
	
	public void plusViewCount(int brdIdx) {

		SqlSession session =null;
		session = sqlSessionFactory.openSession();
		session.update("com.reco.board.BoardMapper.plusViewCount",brdIdx);
	}


	
	
	@Override //???????????? ??????
	public List<Board> findBrdByTitle(String word, int currentPage, int cntperpage) throws FindException {
		SqlSession session =null;
		try {
			session = sqlSessionFactory.openSession();
			Map<String,Object> map= new HashMap<>();
			map.put("word", word);
			//String cp = Integer.toString(currentPage);
			//String cpp = Integer.toString(cntperpage);
			map.put("currentPage", currentPage);//???????????????
			map.put("cntperpage", cntperpage);//???????????? ?????????
			List<Board> list = session.selectList("com.reco.board.BoardMapper.findBrdByTitle",map);
			if(list.size() == 0) {
				throw new FindException("????????? ???????????? ?????? ????????????.");
			}
			return list;			
		} catch (Exception e) {
			throw new FindException(e.getMessage());
		} finally {
			if(session != null) {
				session.close();
			}
		}
	}
	
	
	@Override
	public List<Board> findBrdByType(int intBrdType, int currentPage, int cntperpage) throws FindException{
		SqlSession session =null;
		try {
			session = sqlSessionFactory.openSession();
			Map<String,Integer> map= new HashMap<>();
			map.put("intBrdType",intBrdType);
			map.put("currentPage", currentPage);//???????????????
			map.put("cntperpage", cntperpage);//???????????? ?????????
			List<Board> list = session.selectList("com.reco.board.BoardMapper.findBrdByType",map);
			if(list.size() == 0) {
				throw new FindException("????????? ????????? ????????????.");
			}
			return list;			
		} catch (Exception e) {
			throw new FindException(e.getMessage());
		} finally {
			if(session != null) {
				session.close();
			}
		}
	}

	@Override //??????+???????????? ???????????????
	public List<Board> findBrdByWord(String word, int currentPage, int cntperpage) throws FindException {
		SqlSession session =null;	
		try {
			session = sqlSessionFactory.openSession();
			Map<String,Object> map= new HashMap<>();
			map.put("word", word);
//			String cp = Integer.toString(currentPage);
//			String cpp = Integer.toString(cntperpage);
			map.put("currentPage", currentPage);//???????????????
			map.put("cntperpage", cntperpage);//???????????? ?????????
			List<Board> list = session.selectList("com.reco.board.BoardMapper.findBrdByWord",map);
			if(list.size() == 0) {
				throw new FindException("????????? ???????????? ?????? ????????????.");
			}
			return list;		
		} catch (Exception e) {
			throw new FindException(e.getMessage());
		} finally {
			if(session != null) {
				session.close();
			}
		}
	}
		
	
	@Override
	public List<Board> findBrdByUNickName(String word, int currentPage, int cntperpage) throws FindException {
		SqlSession session =null;		
		try {
			session = sqlSessionFactory.openSession();
			Map<String,String> map= new HashMap<>();
			map.put("uNickname", word);
			String cp = Integer.toString(currentPage);
			String cpp = Integer.toString(cntperpage);
			map.put("currentPage", cp);//???????????????
			map.put("cntperpage", cpp);//???????????? ?????????
			List<Board> list = session.selectList("com.reco.board.BoardMapper.findBrdByUNickName",map);
//			if(list.size() == 0) {
//				throw new FindException("????????? ???????????? ?????? ????????????.");
//			}
			return list;		
		} catch (Exception e) {
			throw new FindException(e.getMessage());
		} finally {
			if(session != null) {
				session.close();
			}
		}
	}
	
	
	@Override
	public List<Board> findBrdByUNickNameMy(String uNickname, int currentPage, int cntperpage) throws FindException {
		SqlSession session =null;		
		try {
			session = sqlSessionFactory.openSession();
			Map<String,String> map= new HashMap<>();
			map.put("uNickname", uNickname);
			String cp = Integer.toString(currentPage);
			String cpp = Integer.toString(cntperpage);
			map.put("currentPage", cp);//???????????????
			map.put("cntperpage", cpp);//???????????? ?????????
			List<Board> list = session.selectList("com.reco.board.BoardMapper.findBrdByUNickName",map);
//			if(list.size() == 0) {
//				throw new FindException("????????? ???????????? ?????? ????????????.");
//			}
			return list;		
		} catch (Exception e) {
			throw new FindException(e.getMessage());
		} finally {
			if(session != null) {
				session.close();
			}
		}
	}
	
	
	@Override
	public List<Board> findBrdByUNickNameMy(String uNickname) throws FindException {
		SqlSession session =null;		
		try {
			session = sqlSessionFactory.openSession();
			List<Board> list = session.selectList("com.reco.board.BoardMapper.findBrdByUNickNameMy",uNickname);
//			if(list.size() == 0) {
//				throw new FindException("????????? ???????????? ?????? ????????????.");
//			}
			return list;		
		} catch (Exception e) {
			throw new FindException(e.getMessage());
		} finally {
			if(session != null) {
				session.close();
			}
		}
	}
	
	
	//??????????????? ?????? ??? ???????????? ????????????
	public List<Comment> findCmtByUNickName(String uNickname, int currentPage, int cntperpage) throws FindException{
		SqlSession session =null;		
		try {
			session = sqlSessionFactory.openSession();
			Map<String,String> map= new HashMap<>();
			map.put("uNickname", uNickname);
			String cp = Integer.toString(currentPage);
			String cpp = Integer.toString(cntperpage);
			map.put("currentPage", cp);//???????????????
			map.put("cntperpage", cpp);//???????????? ?????????
			List<Comment> cmtList = session.selectList("com.reco.board.BoardMapper.findCmtByUNickName",map);
//			if(cmtList.size() == 0) {
//				throw new FindException("????????? ????????? ????????????.");
//			}
			return cmtList;		
		} catch (Exception e) {
			throw new FindException(e.getMessage());
		} finally {
			if(session != null) {
				session.close();
			}
		}
	}

	


	
	@Override
	public void addCmt(Comment comment) throws AddException {
		SqlSession session =null;
		try {
			session = sqlSessionFactory.openSession();
			session.insert("com.reco.board.BoardMapper.addCmt",comment);
			//int brdIdx = comment.getBrdIdx();			
			//Board board = findBrdByIdx(brdIdx);
			//return brdIdx;
			
		}finally {
			if(session != null) {
				session.close();
			}
		}
	}
	
	
	@Override
	public void modifyBrd(Board b) throws ModifyException {
		SqlSession session =null;
		try {
			session = sqlSessionFactory.openSession();
			session.update("com.reco.board.BoardMapper.modifyBrd",b);
			//int brdIdx = b.getBrdIdx();
				//Board board = findBrdByIdx(b.getBrdIdx());
			//return brdIdx;
		}catch(Exception e) {
			throw new ModifyException(e.getMessage());
		}finally {
			if(session != null) {
				session.close();
			}
		}	
	}
	
	
	@Override
	public void modifyCmt(Comment comment) throws ModifyException{
		SqlSession session =null;
		try {
			session = sqlSessionFactory.openSession();
			session.update("com.reco.board.BoardMapper.modifyCmt",comment);
		}catch(Exception e) {
			throw new ModifyException(e.getMessage());
		}finally {
			if(session != null) {
				session.close();
			}
		}	
	}
	
	
	@Override
	public void removeBrd(int brdIdx) throws RemoveException {
		SqlSession session =null;
		try {
			session = sqlSessionFactory.openSession();
			session.delete("com.reco.board.BoardMapper.removeCmtAll", brdIdx);
			int deleterow = session.delete("com.reco.board.BoardMapper.removeBrd",brdIdx);
			if(deleterow == 0) { 
				System.out.println("?????? ???????????? ???????????? ????????????.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(session != null) {
				session.close();
			}
		}		
	}	
							
	
	@Override
	public void removeCmt(int brdIdx, int cmtIdx) throws RemoveException {
		SqlSession session =null;
		try {
			session = sqlSessionFactory.openSession();
			Map<String,Integer> map= new HashMap<>();
			map.put("brdIdx", brdIdx);
			map.put("cmtIdx", cmtIdx);
			session.delete("com.reco.board.BoardMapper.removeCmt", map);//????????????
			session.delete("com.reco.board.BoardMapper.removeCmtReplies", map);//????????? ????????? ???????????? ????????? ????????? ????????? ?????? ??????			
//			if(deleterow == 0) {
//				System.out.println("?????? ????????? ???????????? ????????????.");
//			}else {
//				//????????? 
//				int cmtParentIdxCnt = session.delete("com.reco.board.BoardMapper.nullprotection", map);
//				if(cmtParentIdxCnt == 0) {
//					session.delete("com.reco.board.BoardMapper.removeCmtAll", map);
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(session != null) {
				session.close();
			}
		}				
	}
	
	
	//??????????????? ?????? ??? ????????? ????????? ?????? ??????
//	public String findBrdTitle(int brdIdx) throws FindException{
//		SqlSession session =null;
//		try {
//			session = sqlSessionFactory.openSession();
//			String brdTitle = session.selectOne("com.reco.board.BoardMapper.findBrdTitle", brdIdx);
//			return brdTitle;
//		} catch (Exception e) {
//			throw new FindException(e.getMessage());
//		} finally {
//			if(session != null) {
//				session.close();
//			}
//		}
//	}
//	
	
	
	public void removeCmtAllFromDB(String uNickname) throws RemoveException{
		SqlSession session =null;
		try {
			session = sqlSessionFactory.openSession();
			session.delete("com.reco.board.BoardMapper.removeCmtAllFromDB", uNickname);
			//session.delete("com.reco.board.BoardMapper.removeCmtAllFromDB2", uNickname);
		}finally {
			if(session != null) {
				session.close();
			}
		}		
	}
	
	public void removeBrdAllFromDB(String uNickname) throws RemoveException{
		SqlSession session =null;
		try {
			session = sqlSessionFactory.openSession();
			session.delete("com.reco.board.BoardMapper.removeBrdAllFromDB", uNickname);
		}finally {
			if(session != null) {
				session.close();
			}
		}		
	}
	
	
	public static void main(String[] args) {
		BoardDAOOracle dao = new BoardDAOOracle();
		Board b;
		try {
			b = dao.findBrdByIdx(9);
			System.out.println(b.getBrdTitle() + ":?????? ??? = " + b.getComments().size());
		
		} catch (FindException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	}
	
		
}



