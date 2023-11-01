package github.nowsoar.core.request;

import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import github.nowsoar.common.constants.BasicConst;
import github.nowsoar.common.utils.TimeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;


import java.nio.charset.Charset;
import java.util.*;

/**
 * @description:
 * @author: ZKP
 * @time: 2023/11/1
 */
@Getter
public class GatewayRequest implements IGatewayRequest {

    //服务唯一ID
    private final String uniqueId;

    //进入网关时间
    private final long beginTime;

    //出网关时间
    private final long endTime;

    //字符集
    private final Charset charset;

    //记录客户端ip，做流控，黑白名单等
    private final String clientIp;

    //服务端主机名
    private final String host;

    //服务端请求路径 /xxx/xx/xxx
    private final String path;

    //url是uri子集，/xxx/xx/xxx?atr1=1&attr2=2
    private final String uri;

    //请求方式，Post/Get/Put
    private final HttpMethod method;

    //请求格式
    private final String contentType;

    //请求头
    private final HttpHeaders headers;

    //参数解析器
    private final QueryStringDecoder queryStringDecoder;

    //校验请求
    private final FullHttpRequest fullHttpRequest;

    //请求体
    private String body;

    //不同服务有不同cookie
    private Map<String, Cookie> cookieMap;

    //Post请求参数
    private Map<String, List<String>> postParameters;

    //HTTP修改成HTTPS
    private String modifyScheme;

    private String modifyHost;

    private String modifyPath;

    //构建下游请求时的HTTP构建器
    private final RequestBuilder requestBuilder;

    public GatewayRequest(String uniqueId, long beginTime,
                          long endTime, Charset charset,
                          String clientIp, String host,
                          String path, String uri,
                          HttpMethod method, String contentType,
                          HttpHeaders headers, QueryStringDecoder queryStringDecoder,
                          FullHttpRequest fullHttpRequest, RequestBuilder requestBuilder) {
        this.uniqueId = uniqueId;
        this.beginTime = TimeUtil.currentTimeMillis();
        this.endTime = endTime;
        this.charset = charset;
        this.clientIp = clientIp;
        this.method = method;
        this.contentType = contentType;
        this.headers = headers;
        this.queryStringDecoder = new QueryStringDecoder(uri, charset);
        this.fullHttpRequest = fullHttpRequest;
        this.requestBuilder = new RequestBuilder();
        this.host = host;
        this.path = queryStringDecoder.path();
        this.uri = uri;

        this.modifyHost = host;
        this.modifyPath = path;
        this.modifyScheme = BasicConst.HTTP_PREFIX_SEPARATOR;
        this.requestBuilder.setMethod(getMethod().name());
        this.requestBuilder.setHeaders(getHeaders());
        this.requestBuilder.setQueryParams(queryStringDecoder.parameters());

        ByteBuf contentBuffer = fullHttpRequest.content();
        if (Objects.nonNull(contentBuffer)) {
            this.requestBuilder.setBody(contentBuffer.nioBuffer());
        }
    }

    //获取请求体
    public String getBody() {
        if (StringUtils.isEmpty(body)) {
            body = fullHttpRequest.content().toString();
        }
        return body;
    }

    //获取Cookie
    public Cookie getCookie(String name) {
        if (cookieMap == null) {
            cookieMap = new HashMap<String, Cookie>();
            String cookieStr = getHeaders().get(HttpHeaderNames.COOKIE);
            Set<Cookie> cookies = ServerCookieDecoder.STRICT.decode(cookieStr);
            for (Cookie cookie : cookies) {
                cookieMap.put(name, cookie);
            }
        }
        return cookieMap.get(name);
    }

    //获取指定名称的参数值,Get
    public List<String> getQueryParameters(String name) {
        return queryStringDecoder.parameters().get(name);
    }

    //
    public List<String> getPostParameters(String name) {
        String body = getBody();
        if (isFormPost()) {
            if (postParameters == null) {
                QueryStringDecoder paramDecoder = new QueryStringDecoder(body, false);
                postParameters = paramDecoder.parameters();
            }
            if (postParameters == null || postParameters.isEmpty()) {
                return null;
            } else {

                return postParameters.get(name);
            }
        } else if (isJsonPost()) {
            return Lists.newArrayList(JsonPath.read(body, name).toString());
        }
        return null;
    }

    public boolean isFormPost() {
        return HttpMethod.POST.equals(method)
                && (contentType.startsWith(HttpHeaderValues.FORM_DATA.toString()))
                || contentType.startsWith(HttpHeaderValues.APPLICATION_X_WWW_FORM_URLENCODED.toString());
    }

    public boolean isJsonPost() {
        return HttpMethod.POST.equals(method)
                && contentType.startsWith(HttpHeaderValues.APPLICATION_JSON.toString());
    }

    @Override
    public void setModifyHost(String host) {
        this.modifyHost = host;
    }

    @Override
    public String getModifyHost() {
        return modifyHost;
    }

    @Override
    public void setModifyPath(String path) {
        this.modifyPath = path;
    }

    @Override
    public String getModifyPath() {
        return modifyPath;
    }

    @Override
    public void addHeader(CharSequence name, String value) {
        requestBuilder.addHeader(name, value);
    }

    @Override
    public void setHeader(CharSequence name, String value) {
        requestBuilder.setHeader(name, value);
    }

    @Override
    public void addQueryParam(String name, String value) {
        requestBuilder.addQueryParam(name, value);
    }

    @Override
    public void addFormParam(String name, String value) {
        if (isFormPost()) {
            requestBuilder.addFormParam(name, value);
        }
    }

    @Override
    public void addOrReplaceCookie(org.asynchttpclient.cookie.Cookie cookie) {
        requestBuilder.addOrReplaceCookie(cookie);
    }

    @Override
    public void setRequestTimeout(int requestTimeout) {
        requestBuilder.setRequestTimeout(requestTimeout);
    }

    @Override
    public String getFinalUrl() {
        return modifyScheme + modifyHost + modifyPath;
    }

    @Override
    public Request build() {
        requestBuilder.setUrl(getFinalUrl());
        return requestBuilder.build();
    }
}
