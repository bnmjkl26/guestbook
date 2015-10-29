package com.bit2015.guestbook3.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.bit2015.guestbook3.vo.GuestbookVo;

@Repository
public class GuestbookDao {
	private Connection getConnection() throws SQLException {
		
		Connection connection = null;
		
		try {
			//1.드라이버 로딩
			Class.forName( "oracle.jdbc.driver.OracleDriver" );
		
			//2.커넥션 만들기(ORACLE DB)
			String dbURL  = "jdbc:oracle:thin:@localhost:1521:xe";
			connection = DriverManager.getConnection( dbURL, "webdb", "webdb" );
			
		} catch( ClassNotFoundException ex ){
			System.out.println( "드라이버 로딩 실패-" + ex );
		} 
		
		return connection;
	}
	
	public Boolean delete( GuestbookVo vo ) {
		int countDeleted = 0;
		try {
			//1. Connection 가져오기
			Connection connection = getConnection();
			
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
			connection.close();
			
		} catch( SQLException ex ) {
			System.out.println( "SQL 오류-" + ex );
		}
		
		return ( countDeleted == 1 );
	}
	
	public Long insert( GuestbookVo vo ) {
		Long no = -1L;

		try {
			//1. Connection 가져오기
			Connection connection = getConnection();
			
			//2. Statement 준비
			String sql = 
				" insert" +
				"   into guestbook" + 
				" values( guestbook_seq.nextval, ?, ?, ?, SYSDATE )";
			PreparedStatement pstmt = connection.prepareStatement( sql );
			
			//3. binding
			pstmt.setString( 1, vo.getName() );
			pstmt.setString( 2, vo.getPassword() );
			pstmt.setString( 3, vo.getMessage() );
			
			//4. query 실행
			pstmt.executeUpdate();
			
			//5. 자원정리
			pstmt.close();
			
			//6. sequence 가져오기
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery( "select guestbook_seq.currval from dual" );
			if( rs.next() ) {
				no = rs.getLong( 1 );
			}
			rs.close();
			stmt.close();
			
			connection.close();
			
		} catch( SQLException ex ) {
			System.out.println( "SQL 오류-" + ex );
		}
		
		return no;
	}
	
	public GuestbookVo get( Long gouesbookNo ) {
		GuestbookVo vo = null;
		
		try {
			//1. 커넥션 만들기(ORACLE DB)
			Connection connection = getConnection();
			
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
			connection.close();
			
		} catch( SQLException ex ) {
			System.out.println( "SQL 오류-" + ex );
		}		
		
		return vo;
	}
	
	public List<GuestbookVo> getList( int page ) {
		List<GuestbookVo> list = new ArrayList<GuestbookVo>();
		try {
			//1. 커넥션 만들기(ORACLE DB)
			Connection connection = getConnection();
			
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
	
	public List<GuestbookVo> getList() {
		List<GuestbookVo> list = new ArrayList<GuestbookVo>();
		
		try {
			//1. 커넥션 만들기(ORACLE DB)
			Connection connection = getConnection();
			
			//2. Statement 생성
			Statement stmt = connection.createStatement();
			
			//3. SQL문 실행
			String sql =
				"   select no,"
			  + "          name,"
			  + "          message,"
			  + "          to_char( reg_date, 'yyyy-MM-dd hh:mi:ss' )"
			  + "     from guestbook"
			  + " order by reg_date desc";
			ResultSet rs = stmt.executeQuery( sql );
			
			//4. row 가져오기
			while( rs.next() ) {
				Long no = rs.getLong( 1 );
				String name = rs.getString( 2 );
				String message = rs.getString( 3 );
				String regDate = rs.getString( 4 );
				
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
			stmt.close();
			connection.close();
			
		} catch( SQLException ex ) {
			System.out.println( "SQL 오류-" + ex );
		}
		
		return list;
	}
}
