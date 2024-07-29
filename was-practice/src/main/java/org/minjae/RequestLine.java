package org.minjae;

import java.util.Objects;

/**
 * packageName       : org.minjae
 * fileName         : RequestLine
 * author           : MinjaeKim
 * date             : 2024-07-24
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-24        MinjaeKim       최초 생성
 */
public class RequestLine {

    /**
     * "GET /calculate?operand1=11&operator=+&operand2=55 HTTP/1.1"
     */

    private final String method;
    private final String urlPath;
    private QueryStrings queryString;

    public RequestLine(String method, String urlPath, String queryString) {
        this.method = method;
        this.urlPath = urlPath;
        this.queryString = new QueryStrings(queryString);
    }

    public boolean isGetRequest() {
        return "GET".equals(this.method);
    }

    public boolean matchPath(String path) {
        return path.equals(this.urlPath);
    }

    public QueryStrings getQueryString() {
        return this.queryString;
    }

    public RequestLine(String requestLine) {
        String[] tokens = requestLine.split("");
        this.method = tokens[0];

        String[] urlPathTokens = tokens[1].split("\\?");
        this.urlPath = urlPathTokens[0];

        if(urlPathTokens.length == 2) {
            this.queryString = new QueryStrings(urlPathTokens[1]);
        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestLine that = (RequestLine) o;
        return Objects.equals(method, that.method) && Objects.equals(urlPath, that.urlPath) && Objects.equals(queryString, that.queryString);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, urlPath, queryString);
    }


}
