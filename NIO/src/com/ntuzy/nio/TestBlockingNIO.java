package com.ntuzy.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * NIO 完成网络通信的三个核心
 * 通道
 * SocketChannel
 * ServerSocketChannel
 * DatagramChannel
 * 缓冲区
 * 选择器  SelectorChannel的多路复用器  用于监控SelectableChannel的IO状况
 *
 * @Author IamZY
 * @create 2020/1/21 19:02
 */
public class TestBlockingNIO {

    @Test
    public void client() throws IOException {
        // 获取通道
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

        FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);

        ByteBuffer buf = ByteBuffer.allocate(1024);

        while (inChannel.read(buf) != -1) {
            buf.flip();
            sChannel.write(buf);
            buf.clear();
        }

        inChannel.close();
        sChannel.close();


    }


    @Test
    public void server() throws IOException {

        ServerSocketChannel ssChannel = ServerSocketChannel.open();

        FileChannel outChannel = FileChannel.open(Paths.get("6.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);

        ssChannel.bind(new InetSocketAddress(9898));

        SocketChannel sChannel = ssChannel.accept();

        ByteBuffer buf = ByteBuffer.allocate(1024);

        while (sChannel.read(buf) != -1) {
            buf.flip();
            outChannel.write(buf);
            buf.clear();
        }

        sChannel.close();
        outChannel.close();
        ssChannel.close();

    }

}
