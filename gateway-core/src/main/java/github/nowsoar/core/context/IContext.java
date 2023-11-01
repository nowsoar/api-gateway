package github.nowsoar.core.context;

import io.netty.channel.ChannelHandlerContext;

import java.util.function.Consumer;

/**
 * @description:
 * @author: ZKP
 * @time: 2023/11/1
 */
public interface IContext {

    //上下文生命周期
    int Running = 1;

    //运行发生错误，对其标记，请求结束返回客户端
    int Written = 0;

    //标记写回成功
    int Completed = 1;

    //网关请求结束
    int Terminated = 2;

    //设置上下文状态为运行中
    void running();

    //设置上下文状态为标记写回
    void written();

    //设置上下文为写回成功
    void completed();

    //设置上下文状态为结束
    void terminated();

    //判断是否运行中
    boolean isRunning();

    //判断是否出错
    boolean isWritten();

    //判断是否成功请求
    boolean isCompleted();

    //判断是否结束
    boolean isTerminated();

    //获取协议
    String getProtocol();

    //获取请求对象
    Object getRequest();

    //获取返回对象
    Object getResponse();

    //设置返回对象
    void setResponse(Object response);

    //获取错误
    Throwable getThrowable();

    //设置错误
    void setThrowable(Throwable throwable);

    //获取Netty上下文
    ChannelHandlerContext getNettyCtx();

    boolean isKeepAlve();

    //释放请求资源
    boolean releaseRequest();

    //设置写回接收回调函数
    void setCompletedCallBack(Consumer<IContext> consumer);

    //执行
    void invokeCompletedCallBack(Consumer<IContext> consumer);
}
