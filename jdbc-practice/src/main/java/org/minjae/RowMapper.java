package org.minjae;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae
 * @fileName : RowMapper
 * @date : 2024-10-22
 * @description : ===========================================================
 * @DATE @AUTHOR       @NOTE ----------------------------------------------------------- 2024-10-22
 * MinjaeKim       최초 생성
 */
public interface RowMapper {

    Object rowMapper (ResultSet resultSet) throws SQLException;

}
