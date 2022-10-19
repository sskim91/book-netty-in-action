package com.example.netty.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * Created by sskim
 * Echo Server는 들어오는 메시지에 반응하는 ChannelInboundHandler 인터페이스를 구현해야함.
 * ChannelInboundHandler의 기본 구현체인 ChannelInboundHandlerAdapter
 */
@ChannelHandler.Sharable
public class EchoServerHandler extends ChannelInboundHandlerAdapter {

    //메시지가 들어올 때마다 호출
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        System.out.println("Server received: "+ in.toString(CharsetUtil.UTF_8));
        ctx.write(in);  //아웃바운드 메시지를 플러시하지 않은 채로 받은 메시지를 발신자로 출력함
    }

    //Read의 마지막 호출에서 현재 일괄 처리의 마지막 메시지를 처리했음을 핸들러에 통보함
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER)    //대기중인 메시지를 원격 피어로 플러시하고 채널을 닫음
                .addListener(ChannelFutureListener.CLOSE);
    }

    //읽기 작업 중 예외 발생시 호출
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
