package com.ntuzy.nio;

import org.junit.Test;

import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * @Author IamZY
 * @create 2020/1/21 19:17
 */
public class TestBlockingNIO2 {

    @Test
    public void client() throws IOException {

        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

        FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);

        ByteBuffer buf = ByteBuffer.allocate(1024);


        while (inChannel.read(buf) != -1) {
            buf.flip();
            sChannel.write(buf);
            buf.clear();
        }

        sChannel.shutdownOutput();


        // 接受服务端的反馈
        int len = 0;
        while ((len = sChannel.read(buf)) != -1) {
            buf.flip();
            System.out.println(new String(buf.array(), 0, len));
            buf.clear();
        }

        inChannel.close();
        sChannel.close();

    }

    @Test
    public void server() throws IOException {
        ServerSocketChannel ssChannel = ServerSocketChannel.open();

        ssChannel.bind(new InetSocketAddress(9898));

        FileChannel outChannel = FileChannel.open(Paths.get("7.jpg"), StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);

        SocketChannel sChannel = ssChannel.accept();

        ByteBuffer buf = ByteBuffer.allocate(1024);

        while (sChannel.read(buf) != -1) {
            buf.flip();
            outChannel.write(buf);
            buf.clear();
        }

        buf.put("服务端接受数据成功".getBytes());
        buf.flip();
        sChannel.write(buf);


        sChannel.close();
        outChannel.close();
        ssChannel.close();

    }

}
