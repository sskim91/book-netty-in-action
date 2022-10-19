package com.example.netty.server;

import com.example.netty.server.handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

/**
 * Created by sskim
 */
public class EchoServer {
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws Exception{
        if (args.length != 1) {
            System.out.println("Usage: "+ EchoServer.class.getSimpleName() + " <port>");
        }
        int port = Integer.parseInt(args[0]);
        new EchoServer(port).start();
    }

    public void start() throws Exception {
        final EchoServerHandler echoServerHandler = new EchoServerHandler();
        EventLoopGroup group = new NioEventLoopGroup();
        try{
            ServerBootstrap serverBootstrap = new ServerBootstrap();  //부트스트랩을 생성
            serverBootstrap.group(group)
                    .channel(NioServerSocketChannel.class)  //NIO 전송 채널을 이용하도록 지정
                    .localAddress(new InetSocketAddress(port))  //지정된 포트를 이용해 소켓 주소를 설정
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        //EchoServerHandler하나를 채널의 Channel Pipeline으로 추가
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(echoServerHandler);    //EchoServerHandler는 @Sharable이므로 동일한 항목을 이용할 수 있음
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind().sync();  //서버를 비동기식으로 바인딩, sync()는 바인딩이 완료되기를 대기
            channelFuture.channel().closeFuture().sync();   //채널의 CloseFuture를 얻고 완료될 때까지 현재 스레드를 블로킹
        } finally {
            group.shutdownGracefully().sync();  //EventLoopGroup을 종료하고 모든 리소스를 해제
        }
    }

    /**
     * 서버 구현에서 중요한 단계 검토
     * EchoServerHandler는 비즈니스 논리를 구현한다.
     * main() 메서드는 서버를 부트스트랩 한다.
     */
}
