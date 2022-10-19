package com.example.netty.client;

import com.example.netty.client.handler.EchoClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by sskim
 */
public class EchoClient {

    private final String host;
    private final int port;

    public EchoClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() throws Exception {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();  //부트스트랩 생성
            bootstrap.group(group)  //클라이언트 이벤트를 처리할 EventLoopGroup을 지정함, NIO구현이 이용됨
                    .channel(NioSocketChannel.class)    //채널 유형으로 nio 전송 유형 중 하나를 지정
                    .remoteAddress(new InetSocketAddress(host, port)) //서버의 InetSocketAddress를 설정
                    .handler(new ChannelInitializer<SocketChannel>() {
                        //채널이 생성될 때 파이프라인에 EchoClientHandler하나를 추가
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new EchoClientHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect().sync();   //원격 피어로 연결하고 연결이 완료되기를 기다림
            channelFuture.channel().closeFuture().sync();   //채널이 닫힐 때까지 블로킹함
        } finally {
            group.shutdownGracefully().sync();  //스레드 풀을 종료하고 모든 리소스를 해제함
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println(
                    "Usage: " + EchoClient.class.getSimpleName() +
                            " <host> <port>");
            return;
        }
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        new EchoClient(host, port).start();
    }
}
