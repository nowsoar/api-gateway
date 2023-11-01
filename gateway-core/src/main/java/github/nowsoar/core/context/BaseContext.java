package github.nowsoar.core.context;

import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * @description:
 * @author: ZKP
 * @time: 2023/11/1
 */
public class BaseContext implements IContext {

    //转发协议
    protected final String protocol;

    //状态，多线程情况下使用volatile
    protected volatile int status = IContext.Running;

    //Netty
    protected final ChannelHandlerContext nettyCtx;

    //上下文参数
    protected final Map<String, Object> attributes = new HashMap<String, Object>();

    //请求过程中的异常
    protected Throwable throwable;

    //是否保持长连接
    protected final boolean keepAlive;

    //存放回调函数集合
    protected List<Consumer<IContext>> completedCallBacks;

    //是否已经释放资源，考虑多线程使用Atomic
    protected final AtomicBoolean requestReleased = new AtomicBoolean(false);

    public BaseContext(String protocol, ChannelHandlerContext nettyCtx, boolean keepAlive) {
        this.protocol = protocol;
        this.nettyCtx = nettyCtx;
        this.keepAlive = keepAlive;
    }

    @Override
    public void running() {
        status = IContext.Running;
    }

    @Override
    public void written() {
        status = IContext.Written;
    }

    @Override
    public void completed() {
        status = IContext.Completed;
    }

    @Override
    public void terminated() {
        status = IContext.Terminated;
    }

    @Override
    public boolean isRunning() {
        return status == IContext.Running;
    }

    @Override
    public boolean isWritten() {
        return status == IContext.Written;
    }

    @Override
    public boolean isCompleted() {
        return status == IContext.Completed;
    }

    @Override
    public boolean isTerminated() {
        return status == IContext.Terminated;
    }

    @Override
    public String getProtocol() {
        return protocol;
    }

    @Override
    public Object getRequest() {
        return null;
    }

    @Override
    public Object getResponse() {
        return null;
    }

    @Override
    public void setResponse(Object response) {

    }

    @Override
    public Throwable getThrowable() {
        return this.throwable;
    }

    @Override
    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    @Override
    public ChannelHandlerContext getNettyCtx() {
        return this.nettyCtx;
    }

    @Override
    public boolean isKeepAlve() {
        return this.keepAlive;
    }

    @Override
    public boolean releaseRequest() {
        return false;
    }

    @Override
    public void setCompletedCallBack(Consumer<IContext> consumer) {
        if (completedCallBacks == null) {
            completedCallBacks = new ArrayList<>();
        }
        completedCallBacks.add(consumer);
    }

    @Override
    public void invokeCompletedCallBack(Consumer<IContext> consumer) {
        if (completedCallBacks != null) {
            completedCallBacks.forEach(call->call.accept(this));
        }
    }
}
