package github.nowsoar.core.context;

import github.nowsoar.common.rule.Rule;
import github.nowsoar.core.request.GatewayRequest;
import github.nowsoar.core.response.GatewayResponse;
import io.netty.channel.ChannelHandlerContext;

/**
 * @description:
 * @author: ZKP
 * @time: 2023/11/1
 */
public class GatewayContext extends BaseContext{

    public GatewayRequest request;

    public GatewayResponse response;

    public Rule rule;

    public GatewayContext(String protocol, ChannelHandlerContext nettyCtx, boolean keepAlive) {
        super(protocol, nettyCtx, keepAlive);
    }
}
