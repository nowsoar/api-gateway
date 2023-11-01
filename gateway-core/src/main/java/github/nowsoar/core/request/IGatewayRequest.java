package github.nowsoar.core.request;

import org.asynchttpclient.Request;
import org.asynchttpclient.cookie.Cookie;

/**
 * @description:
 * @author: ZKP
 * @time: 2023/11/1
 */
public interface IGatewayRequest {

    //修改目标服务地址
    void setModifyHost(String host);

    String getModifyHost();

    void setModifyPath(String path);

    String getModifyPath();

    //添加请求头
    void addHeader(CharSequence name, String value);

    //设置请求头
    void setHeader(CharSequence name, String value);

    //添加Get请求参数
    void addQueryParam(String name, String value);

    //添加表单请求参数
    void addFormParam(String name, String value);

    //添加Cookie
    void addOrReplaceCookie(Cookie cookie);

    //设置超时时间
    void setRequestTimeout(int requestTimeout);

    //获取最终请求路径，包含请求参数，类似Http://localhost:8081/api/admin?name=111
    String getFinalUrl();

    Request build();
}
