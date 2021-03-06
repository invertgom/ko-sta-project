package com.reco.notice.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.reco.exception.AddException;
import com.reco.exception.FindException;
import com.reco.exception.ModifyException;
import com.reco.exception.RemoveException;
import com.reco.notice.service.NoticeService;
import com.reco.notice.vo.Notice;



@Repository
public class NoticeDAOOracle implements NoticeDAOInterface {
	

	@Autowired
	private SqlSessionFactory sqlSessionFactory;
	
	//@Autowired
	private Logger logger = LoggerFactory.getLogger(NoticeService.class.getName());
	
	@Override
	public int findCount() throws FindException{
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			return session.selectOne("com.reco.notice.NoticeMapper.findCount");
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
	public int findCountNickname(String uNickname) throws FindException{
		SqlSession session = null;
		try {
			session = sqlSessionFactory.openSession();
			return session.selectOne("com.reco.notice.NoticeMapper.findCountNickname", uNickname);
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
			return session.selectOne("com.reco.notice.NoticeMapper.findCountWord",word);
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
			return session.selectOne("com.reco.notice.NoticeMapper.findCountTitle",word);
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
	public List<Notice> findNtcAll() throws FindException{
		return findNtcAll(1,5);
	}
	
	@Override
	public List<Notice> findNtcAll(int currentPage, int cntperpage) throws FindException{

		SqlSession session = null;
		try {
			logger.info("dao???????????????"+currentPage);
			session = sqlSessionFactory.openSession();
			Map<String,Integer> map= new HashMap<>();
			map.put("currentPage", currentPage);//???????????????
			map.put("cntperpage", cntperpage);//???????????? ??? ??????
			List<Notice> list = session.selectList("com.reco.notice.NoticeMapper.findNtcAll", map);
			
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
	public Notice addNtc(Notice n) throws AddException,FindException{
		SqlSession session =null;
		try {
			session = sqlSessionFactory.openSession();
			session.insert("com.reco.notice.NoticeMapper.addNtc",n);
			session.commit();
			int ntcIdx = n.getNtcIdx();
			logger.info("dao?????? ????????? ???????????? ????????? : "+ntcIdx);
			Notice notice = findNtcByIdx(ntcIdx);
			return notice;
		} catch (FindException e) {
			e.printStackTrace();
			throw new AddException(e.getMessage());
		}finally {
			if(session != null) {
				session.close();
			}
		}
	}

	
	@Override
	public List<Notice> findNtcByNickname(String uNickname, int currentPage, int cntperpage) throws FindException {
		SqlSession session =null;
		try {
			session = sqlSessionFactory.openSession();
			Map<String,Object> map= new HashMap<>();
			map.put("uNickname", uNickname);
			map.put("currentPage", currentPage);
			map.put("cntperpage", cntperpage);
			List<Notice> list = session.selectList("com.reco.notice.NoticeMapper.findNtcByNickname",map);
//			if(list.size() == 0) {
//				throw new FindException("?????? ????????? ???????????? ?????? ????????????.");
//			}
			return list;
		}catch (Exception e) {
			throw new FindException(e.getMessage());
		}finally {
			if(session != null) {
				session.close();
			}
		}
	}
	@Override
	public Notice findNtcByIdx(int ntcIdx) throws FindException {
		SqlSession session =null;
		try {
			session = sqlSessionFactory.openSession();
			Notice notice= session.selectOne("com.reco.notice.NoticeMapper.findNtcByIdx",ntcIdx);
			plusViewCount(ntcIdx);
			return notice;
		}catch (Exception e) {
			throw new FindException(e.getMessage());
			
		}finally {
			if(session != null) {
				session.close();
			}
		}
	}

	@Override
	public List<Notice> findNtcByTitle(String word, int currentPage, int cntperpage) throws FindException{
		SqlSession session =null;
		try {
			session = sqlSessionFactory.openSession();
			Map<String,Object> map= new HashMap<>();
			map.put("word", word);
			//String cp = Integer.toString(currentPage);
			//String cpp = Integer.toString(cntperpage);
			map.put("currentPage", currentPage);//???????????????
			map.put("cntperpage", cntperpage);//???????????? ?????????
			logger.info("dao????????? word??? "+word+currentPage+cntperpage);
			List<Notice> list = session.selectList("com.reco.notice.NoticeMapper.findNtcByTitle",map);
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
	public List<Notice> findNtcByWord(String word, int currentPage, int cntperpage) throws FindException{
		SqlSession session =null;
		
		try {
			session = sqlSessionFactory.openSession();
			Map<String,Object> map= new HashMap<>();
			map.put("word", word);
			//String cp = Integer.toString(currentPage);
			//String cpp = Integer.toString(cntperpage);
			map.put("currentPage", currentPage);//???????????????
			map.put("cntperpage", cntperpage);//???????????? ?????????
			List<Notice> list = session.selectList("com.reco.notice.NoticeMapper.findNtcByWord",map);
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
	public Notice modifyNtc(Notice n) throws ModifyException,FindException{
		SqlSession session =null;
		try {
			session = sqlSessionFactory.openSession();
			session.update("com.reco.notice.NoticeMapper.modifyNtc",n);
			Notice notice=findNtcByIdx(n.getNtcIdx());
			return notice;
		}catch(FindException e){
			throw new ModifyException(e.getMessage());
		}catch(Exception e) {
			throw new ModifyException(e.getMessage());
		}finally {
			if(session != null) {
				session.close();
			}
		}	
	}

	@Override
	public void removeNtc(int ntcIdx) throws RemoveException {
		SqlSession session =null;
		try {
			session = sqlSessionFactory.openSession();
			int deleterow = session.delete("com.reco.notice.NoticeMapper.removeNtc",ntcIdx);
	
			if(deleterow == 0) {
				System.out.println("?????? ??????????????? ???????????? ????????????.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(session != null) {
				session.close();
			}
		}		
	}

	public void plusViewCount(int ntcIdx) {

		SqlSession session =null;
		session = sqlSessionFactory.openSession();
		session.update("com.reco.notice.NoticeMapper.plusViewCount",ntcIdx);
	}
	
	
	public void removeNtcAllFromDB(String uNickname) throws RemoveException{
		SqlSession session =null;
		try {
			session = sqlSessionFactory.openSession();
			session.delete("com.reco.notice.NoticeMapper.removeNtcAllFromDB", uNickname);
		}finally {
			if(session != null) {
				session.close();
			}
		}	
	}
	
	public static void main(String[] args) {
	System.out.println();
}	

}
