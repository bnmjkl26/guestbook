package com.bit2015.guestbook3.dao;


import java.util.List;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bit2015.guestbook3.vo.GuestbookVo;

@Repository
public class GuestbookDao {
	
	@Autowired
	private SqlSession sqlSession;
	
	public GuestbookVo get( Long no ) {
		GuestbookVo vo = sqlSession.selectOne( "guestbook.get",  no );
		return vo;
	}
	
	public List<GuestbookVo> getList( int page ) {
		List<GuestbookVo> list = sqlSession.selectList( "guestbook.listbypage", page );
		return list;
	}
	
	public Long insert( GuestbookVo vo ) {
		sqlSession.insert( "guestbook.insert", vo );
		return vo.getNo();
	}
	
	public List<GuestbookVo> getList() {
		List<GuestbookVo> list = sqlSession.selectList( "guestbook.list" );
		return list;
	}
	
	public Boolean delete( GuestbookVo vo ) {
		int countDeleted = sqlSession.delete( "guestbook.delete", vo );
		return ( countDeleted == 1 );
	}	
}