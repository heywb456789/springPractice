package org.minjae;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.http.HttpHeaders;

/**
 * packageName       : org.minjae
 * fileName         : HttpRequest
 * author           : MinjaeKim
 * date             : 2024-07-25
 * description      :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2024-07-25        MinjaeKim       최초 생성
 */
public class HttpRequest {
    private final RequestLine requestLine;
//    private final HttpHeaders headers;
//    private final Body body;

    //BufferedReader 를 통해서 들어온 라인의 첫번째를 전달
    public HttpRequest(BufferedReader br) throws IOException {
        this.requestLine = new RequestLine(br.readLine());
    }

    public QueryStrings getQueryString() {
        return requestLine.getQueryString();
    }

    public boolean isGetRequest() {
        return requestLine.isGetRequest();
    }

    public boolean matchPath(String path) {
        return requestLine.matchPath(path);
    }
}
