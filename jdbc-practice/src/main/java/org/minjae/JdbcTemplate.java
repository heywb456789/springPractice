package org.minjae;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae
 * @fileName : JdbcTemplate
 * @date : 2024-10-21
 * @description : ===========================================================
 * @DATE @AUTHOR       @NOTE ----------------------------------------------------------- 2024-10-21
 * MinjaeKim       최초 생성
 */
public class JdbcTemplate {

    //prepareStatement 는 바깥 쪽에서 전달 받으려면 Connection 까지 전달 받아야하므로  Setter의 인자만 전달 받는다.
    //interface로 받는다.
    public void executeUpdate(User user, String sql, PrepareStatementSetter pss) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;

        //try -withResource 사용시 자동으로 리소스 해제
        try{
            con = ConnectionManager.getConnection();
//            String sql = "insert into users (userId, password, name, email) values (?, ?, ? ,?)";
            ps = con.prepareStatement(sql);
//            ps.setString(1, user.getUserID());
//            ps.setString(2, user.getPassword());
//            ps.setString(3, user.getName());
//            ps.setString(4, user.getEmail());
            pss.setter(ps);

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

    public Object excuteQuery(String userId, String sql, PrepareStatementSetter pss,
        RowMapper rowMapper) throws SQLException {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null; //조회된 값을 가져온다.

        try {
            con = ConnectionManager.getConnection();
//            String sql = "select * from users where userId = ?";
            ps = con.prepareStatement(sql);
//            ps.setString(1, userId);
            pss.setter(ps);
            rs = ps.executeQuery();

//            User user = null;
            Object object = null;
            if (rs.next()) {
//                user = new User(rs.getString("userId"),
//                    rs.getString("password"),
//                    rs.getString("name"),
//                    rs.getString("email")
//                );
                return rowMapper.rowMapper(rs);
            }
            return object;
        } finally {

            if (rs != null) {
                rs.close();
            }
            if (ps != null) {
                ps.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }

}
