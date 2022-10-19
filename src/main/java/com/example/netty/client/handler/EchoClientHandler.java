package com.example.netty.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;

/**
 * Created by sskim
 */
@ChannelHandler.Sharable
public class EchoClientHandler extends SimpleChannelInboundHandler<ByteBuf> {

    //서버에 대한 연결이 만들어지면 호출된다.
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //채널 활성화 알림을 받으면 메시지를 전송
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
    }

    //서버로부터 메시지를 수신하면 호출된다.
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
        //수신한 메시지의 덤프를 로깅
        System.out.println("Client received: " + msg.toString(CharsetUtil.UTF_8));
    }

    //예외 시 오류를 로깅하고 채널을 닫음
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
