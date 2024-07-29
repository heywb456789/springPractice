package org.minjae;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * packageName       : org.minjae
 * fileName         : QueryStrings
 * author           : MinjaeKim
 * date             : 2024-07-24
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-24        MinjaeKim       최초 생성
 */
public class QueryStrings {

    private List<QueryString> queryStringList = new ArrayList<QueryString>();

    public QueryStrings(String queryStringLine) {
        String[] queryStringTokens = queryStringLine.split("&");
        Arrays.stream(queryStringTokens)
                .forEach(queryString -> {
                    String[] values = queryString.split("=");
                    if (values.length != 2) {
                        throw new IllegalArgumentException("잘못된 쿼리 스트린 포맷을 가진 문자열입니다.");
                    }
                    queryStringList.add(new QueryString(values[0], values[1]));
                });
    }

    public String getValue(String key) {
        return (String) this.queryStringList.stream()
                .filter(queryString -> queryString.exists(key))
                .map(QueryString::getValue)
                .findFirst()
                .orElse(null);
    }
}
