package com.ntuzy.nio;

import org.junit.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Pipe;

/**
 * @Author IamZY
 * @create 2020/1/21 19:58
 */
public class TestPipe {

    @Test
    public void test1() throws IOException {

        Pipe pipe = Pipe.open();

        Pipe.SinkChannel sinkChannel = pipe.sink();

        ByteBuffer buf = ByteBuffer.allocate(1024);

        buf.put("通过单项管道发送数据".getBytes());
        buf.flip();
        sinkChannel.write(buf);

        Pipe.SourceChannel sourceChannel = pipe.source();
        buf.flip();
        int len = sourceChannel.read(buf);
        System.out.println(new String(buf.array(), 0, len));

        sourceChannel.close();
        sinkChannel.close();

    }

}
