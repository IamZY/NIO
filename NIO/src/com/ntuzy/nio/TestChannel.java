package com.ntuzy.nio;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.SortedMap;

/**
 * Channel 用于源节点与目标节点的链接 再Java NIO中负责缓冲区的传输 Channel本身不存储数据 因此需要配合Buffer使用
 * <p>
 * FileChannel
 * SocketChannel
 * ServerSocketChannel
 * DatagramChannel
 * <p>
 * Java 对支持Channel的类 getChannel
 * FileInputStream FileOutputStream
 * <p>
 * Socket ServerSocket DatagramSocket
 * <p>
 * 在JDK 1.7 中的NIO2从针对各个通道提供静态方法 open
 * 在JDK 1.7 中的NIO2 Files 工具类newByteChannel
 * <p>
 * <p>
 * // 通道之家的数据传输
 * transferFrom
 * transferTo
 * <p>
 * 分散与聚集
 * scattering reads 将通道中的数据分散到多个缓冲区中
 * gather writes 将多个缓冲区中的数据聚集到通道中
 *
 * 字符集 charset
 * 编码 字符串->字节数组
 * 解码 字节数组->字符串
 *
 * @Author IamZY
 * @create 2020/1/21 10:46
 */
public class TestChannel {

    @Test
    public void test6() throws CharacterCodingException {
        Charset cs1 = Charset.forName("GBK");

        // 获取编码器
        CharsetEncoder ce = cs1.newEncoder();

        // 获取解码器
        CharsetDecoder cd = cs1.newDecoder();

        CharBuffer cBuf = CharBuffer.allocate(1024);
        cBuf.put("红火火恍恍惚惚");
        cBuf.flip();

        // 编码
        ByteBuffer bBuf = ce.encode(cBuf);

        for(int i = 0;i < 12;i++) {
            System.out.println(bBuf.get());
        }

        // 解码
        bBuf.flip();
        CharBuffer cBuf2 = cd.decode(bBuf);
        System.out.println(cBuf2.toString());

        System.out.println("-----------------------------------");

        Charset cs2 = Charset.forName("UTF-8");
        bBuf.flip();
        CharBuffer cBuf3 = cs2.decode(bBuf);
        System.out.println(cBuf3.toString());

    }


    // 字符集
    @Test
    public void test5() {
        SortedMap<String, Charset> map = Charset.availableCharsets();
        
        map.forEach((k,v)->{
            System.out.println(k + "==" + v);
        });
        
    }
    

    // 分散读取 聚集写入
    @Test
    public void test4() throws IOException {
        RandomAccessFile raf1 = new RandomAccessFile("1.txt", "rw");

        FileChannel channel = raf1.getChannel();

        ByteBuffer buf1 = ByteBuffer.allocate(100);
        ByteBuffer buf2 = ByteBuffer.allocate(1024);

        ByteBuffer[] bufs = {buf1, buf2};
        channel.read(bufs);

        for (ByteBuffer byteBuffer : bufs) {
            byteBuffer.flip();
        }

        System.out.println(new String(bufs[0].array(), 0, bufs[0].limit()));
        System.out.println("--------------------------------------------------------------");
        System.out.println(new String(bufs[1].array(), 0, bufs[1].limit()));

        //
        RandomAccessFile raf2 = new RandomAccessFile("2.txt","rw");
        FileChannel channel2 = raf2.getChannel();

        channel2.write(bufs);

        channel2.close();

    }


    // 直接缓冲区的方式
    @Test
    public void test3() throws IOException {

        FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("5.jpg"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE_NEW);

//        inChannel.transferTo(0, inChannel.size(), outChannel);
        outChannel.transferFrom(inChannel, 0, inChannel.size());


        inChannel.close();
        outChannel.close();

    }


    // 使用直接缓冲区完成文件的复制
    @Test
    public void test2() throws IOException {

        FileChannel inChannel = FileChannel.open(Paths.get("1.jpg"), StandardOpenOption.READ);
        FileChannel outChannel = FileChannel.open(Paths.get("3.jpg"), StandardOpenOption.WRITE, StandardOpenOption.READ, StandardOpenOption.CREATE_NEW);

        // 内存映射文件
        MappedByteBuffer inMappedBuf = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        MappedByteBuffer outMappedBuf = outChannel.map(FileChannel.MapMode.READ_WRITE, 0, inChannel.size());

        // 直接对缓冲区进行读写
        byte[] dst = new byte[inMappedBuf.limit()];
        inMappedBuf.get(dst);
        outMappedBuf.put(dst);

        inChannel.close();
        outChannel.close();

    }


    // 利用通道完成文件的复制
    @Test
    public void test1() throws Exception {
        FileInputStream fis = new FileInputStream("1.jpg");
        FileOutputStream fos = new FileOutputStream("2.jpg");

        // 获取通道
        FileChannel inChannel = fis.getChannel();
        FileChannel outChannel = fos.getChannel();

        // 分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        // 将通道中的数据存入缓冲区中
        while (inChannel.read(buf) != -1) {
            // 将缓冲区的数据再写入通道
            buf.flip();
            outChannel.write(buf);
            buf.clear();
        }

        outChannel.close();
        inChannel.close();
        fos.close();
        fis.close();

    }

}
