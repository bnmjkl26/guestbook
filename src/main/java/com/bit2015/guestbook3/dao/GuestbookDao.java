package com.bit2015.guestbook3.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.pool.OracleDataSource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.bit2015.guestbook3.vo.GuestbookVo;

@Repository
public class GuestbookDao {
	
	@Autowired
	private OracleDataSource oracleDataSource;
	
	@Autowired
	private SqlSession sqlSession;
	
	public Boolean delete( GuestbookVo vo ) {
		int countDeleted = 0;
		try {
			//1. Connection 가져오기
			Connection connection = oracleDataSource.getConnection();
			
			//2. Statement 준비
			String sql = 
				" delete" +
				"   from guestbook" + 
				"  where no=? and password=?";
			PreparedStatement pstmt = connection.prepareStatement( sql );
			
			//3. binding
			pstmt.setLong( 1, vo.getNo() );
			pstmt.setString( 2, vo.getPassword() );
			
			//4. query 실행
			countDeleted = pstmt.executeUpdate();
			
			//5. 자원정리
			pstmt.close();
			
		} catch( SQLException ex ) {
			System.out.println( "SQL 오류-" + ex );
		}
		
		return ( countDeleted == 1 );
	}
	
	public GuestbookVo get( Long gouesbookNo ) {
		GuestbookVo vo = null;
		
		try {
			//1. 커넥션 만들기(ORACLE DB)
			Connection connection = oracleDataSource.getConnection();
			
			//2. Statement 준비
			String sql =
					"   select no,"
				  + "          name,"
				  + "          message,"
				  + "          to_char( reg_date, 'yyyy-MM-dd hh:mi:ss' )"
				  + "     from guestbook"
				  + "    where no=?";
			PreparedStatement pstmt = connection.prepareStatement( sql );
			
			//3.binding
			pstmt.setLong( 1, gouesbookNo );
			
			// 4 SQL문 실행
			ResultSet rs = pstmt.executeQuery();
			
			//4. row 가져오기
			if( rs.next() ) {
				Long no = rs.getLong( 1 );
				String name = rs.getString( 2 );
				String message = rs.getString( 3 );
				String regDate = rs.getString( 4 );
				
				vo = new GuestbookVo();
				vo.setNo( no );
				vo.setName( name );
				// vo.setPassword(password);
				vo.setMessage( message );
				vo.setRegDate( regDate );
				
			}
			
			//6. 자원 정리
			rs.close();
			pstmt.close();
			
		} catch( SQLException ex ) {
			System.out.println( "SQL 오류-" + ex );
		}		
		
		return vo;
	}
	
	public List<GuestbookVo> getList( int page ) {
		List<GuestbookVo> list = new ArrayList<GuestbookVo>();
		try {
			//1. 커넥션 만들기(ORACLE DB)
			Connection connection = oracleDataSource.getConnection();
			
			//2. Statement 준비
			String sql =
"select * from (select rownum as r, A.* from (select no, name, message, to_char(reg_date, 'yyyy-mm-dd hh:mi:ss') from guestbook order by reg_date desc ) A ) where ? <= r and r <= ?";
			PreparedStatement pstmt = connection.prepareStatement( sql );

			//3. binding
			pstmt.setInt( 1, ( page-1 )*5 + 1 );
			pstmt.setInt( 2, page*5 );
			
			//4. sql문 실행
			ResultSet rs = pstmt.executeQuery();
			
			//4. row 가져오기
			while( rs.next() ) {
				Long no = rs.getLong( 2 );
				String name = rs.getString( 3 );
				String message = rs.getString( 4 );
				String regDate = rs.getString( 5 );
				
				GuestbookVo vo = new GuestbookVo();
				vo.setNo( no );
				vo.setName( name );
				// vo.setPassword(password);
				vo.setMessage( message );
				vo.setRegDate( regDate );
				
				list.add( vo );
			}
			
			//6. 자원 정리
			rs.close();
			pstmt.close();
			connection.close();
			
		} catch( SQLException ex ) {
			System.out.println( "SQL 오류-" + ex );
		}
		
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
}