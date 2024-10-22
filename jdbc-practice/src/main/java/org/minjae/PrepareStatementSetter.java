package org.minjae;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author : MinjaeKim
 * @packageName : org.minjae
 * @fileName : PrepareStatementSetter
 * @date : 2024-10-22
 * @description : ===========================================================
 * @DATE @AUTHOR       @NOTE ----------------------------------------------------------- 2024-10-22
 * MinjaeKim       최초 생성
 */
public interface PrepareStatementSetter {

    void setter(PreparedStatement preparedStatement) throws SQLException;

}
