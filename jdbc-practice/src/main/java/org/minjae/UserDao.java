package org.minjae;

import java.sql.*;

/**
 * packageName       : org.minjae
 * fileName         : UserDao
 * author           : MinjaeKim
 * date             : 2024-08-14
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-08-14        MinjaeKim       최초 생성
 */
public class UserDao {

    public void create(User user) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;

        //try -withResource 사용시 자동으로 리소스 해제
        try{
            con = ConnectionManager.getConnection();
            String sql = "insert into users (userId, password, name, email) values (?, ?, ? ,?)";
            ps = con.prepareStatement(sql);
            ps.setString(1, user.getUserID());
            ps.setString(2, user.getPassword());
            ps.setString(3, user.getName());
            ps.setString(4, user.getEmail());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally{
            if(ps != null){
                ps.close();
            }

            if(con != null){
                con.close();
            }
        }
    }

    public User findByUserId(String userId) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null; //조회된 값을 가져온다.

        try{
            con = ConnectionManager.getConnection();
            String sql = "select * from users where userId = ?";
            ps = con.prepareStatement(sql);
            ps.setString(1, userId);

            rs = ps.executeQuery();

            User user = null;
            if(rs.next()){
                user = new User(rs.getString("userId"), rs.getString("password"),
                        rs.getString("name"), rs.getString("email"));
            }
            return user;
        }finally {

            if(rs!=null){
                rs.close();
            }
            if(ps != null){
                ps.close();
            }
            if(con != null){
                con.close();
            }
        }
    }
}
