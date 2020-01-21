# NIO
## Java NIO简介

Java NIO

## Java NIO 与 IO的主要区别

## 缓冲区和通道

```java
package com.ntuzy.nio;

import org.junit.Test;

import javax.xml.soap.Detail;
import java.nio.Buffer;
import java.nio.ByteBuffer;

/**
 * 缓冲区 buffer 存取数据  底层是数组 用于存储不同数据类型的数据
 * 除了Boolean以外 提供了象应类型的缓冲区
 * ByteBuffer
 * CharBuffer
 * ShortBuffer
 * ....
 * 上述缓冲区管理方式几乎一致  通过allocate 获取缓冲区
 * <p>
 * 存取数据的方法
 * put 存入数据到缓冲区
 * get  获取数据到缓冲区
 * <p>
 * <p>
 * // 缓冲区中的四个核心属性
 * mark <= position <= limit <= capacity
 * capacity 容量 表示缓冲区中最大存储数据的容量一旦声明不能改变
 * limit 界限 表示缓冲区周昂可以操作数据的大小 limit后面的数据不能进行读写
 * position 位置 表示缓冲区中正在操作数据的位置
 *
 * mark 标记 表示记录当前position的位置 可以通过reset 恢复恢复到mark的位置
 *
 *
 * 直接缓冲区与非直接缓冲区
 * 非直接缓冲区 allocate 分配到缓冲区 将缓冲区建立在JVM的内存中
 * 直接缓冲区 allocateDirect 分配直接缓冲区 将缓冲区建立在物理内存中
 *
 * @Author IamZY
 * @create 2020/1/21 9:38
 */
public class TestBuffer {

    @Test
    public void test3() {
        // 分配直接缓冲区
        ByteBuffer buf = ByteBuffer.allocateDirect(1024);

        System.out.println(buf.isDirect());
    }


    @Test
    public void test2() {
        ByteBuffer buf = ByteBuffer.allocate(1024);
        String str = "abcde";

        buf.put(str.getBytes());

        buf.flip();
        byte[] dest = new byte[buf.limit()];
        buf.get(dest,0,2);
        System.out.println(new String(dest,0,2));
        System.out.println(buf.position());

        buf.mark();
        buf.get(dest,2,2);
        System.out.println(new String(dest,2,2));
        System.out.println(buf.position());

        buf.reset();  //恢复到mark的位置
        System.out.println(buf.position());

        // 判断缓冲区中可以操作的数量
        if (buf.hasRemaining()) {
            System.out.println(buf.capacity() - buf.limit());
        }

    }


    @Test
    public void test1() {

        String str = "abcde";

        // 分配一个制定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        System.out.println("----------------allocate----------------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

        // 利用put 存入数据到缓冲区
        buf.put(str.getBytes());
        System.out.println("----------------put----------------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());


        // 切换读取模式
        buf.flip();
        System.out.println("----------------flip----------------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());


        // 读取缓冲区中数据
        byte[] dest = new byte[buf.limit()];
        buf.get(dest);
        System.out.println(new String(dest, 0, dest.length));
        System.out.println("----------------get----------------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());


        // rewind  重复读取数据
        buf.rewind();
        System.out.println("----------------rewind----------------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());


        // 清空缓冲区  数据没有被清空  数据处于被遗忘状态
        buf.clear();
        System.out.println("----------------clear----------------");
        System.out.println(buf.position());
        System.out.println(buf.limit());
        System.out.println(buf.capacity());

//        System.out.println((char) buf.get());

    }

}

```



```java
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

```



## 文件通道

```java
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

```

```java
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

```



## NIO 的非阻塞式网络通信

```java
package com.ntuzy.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.time.LocalTime;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @Author IamZY
 * @create 2020/1/21 19:29
 */
public class TestNonBlockingNIO {

    //客户端
    @Test
    public void client() throws IOException {
        //1. 获取通道
        SocketChannel sChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9898));

        //2. 切换非阻塞模式
        sChannel.configureBlocking(false);

        //3. 分配指定大小的缓冲区
        ByteBuffer buf = ByteBuffer.allocate(1024);

        //4. 发送数据给服务端
        Scanner scan = new Scanner(System.in);

        while (scan.hasNext()) {
            String str = scan.next();
            buf.put((new Date().toString() + "\n" + str).getBytes());
            buf.flip();
            sChannel.write(buf);
            buf.clear();
        }

        //5. 关闭通道
        sChannel.close();
    }

    //服务端
    @Test
    public void server() throws IOException {
        //1. 获取通道
        ServerSocketChannel ssChannel = ServerSocketChannel.open();

        //2. 切换非阻塞模式
        ssChannel.configureBlocking(false);

        //3. 绑定连接
        ssChannel.bind(new InetSocketAddress(9898));

        //4. 获取选择器
        Selector selector = Selector.open();

        //5. 将通道注册到选择器上, 并且指定“监听接收事件”
        ssChannel.register(selector, SelectionKey.OP_ACCEPT);

        //6. 轮询式的获取选择器上已经“准备就绪”的事件
        while (selector.select() > 0) {

            //7. 获取当前选择器中所有注册的“选择键(已就绪的监听事件)”
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();

            while (it.hasNext()) {
                //8. 获取准备“就绪”的是事件
                SelectionKey sk = it.next();

                //9. 判断具体是什么事件准备就绪
                if (sk.isAcceptable()) {
                    //10. 若“接收就绪”，获取客户端连接
                    SocketChannel sChannel = ssChannel.accept();

                    //11. 切换非阻塞模式
                    sChannel.configureBlocking(false);

                    //12. 将该通道注册到选择器上
                    sChannel.register(selector, SelectionKey.OP_READ);
                } else if (sk.isReadable()) {
                    //13. 获取当前选择器上“读就绪”状态的通道
                    SocketChannel sChannel = (SocketChannel) sk.channel();

                    //14. 读取数据
                    ByteBuffer buf = ByteBuffer.allocate(1024);

                    int len = 0;
                    while ((len = sChannel.read(buf)) > 0) {
                        buf.flip();
                        System.out.println(new String(buf.array(), 0, len));
                        buf.clear();
                    }
                }

                //15. 取消选择键 SelectionKey
                it.remove();
            }
        }
    }

}

```

```java
package com.ntuzy.nio;

import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Date;
import java.util.Iterator;
import java.util.Scanner;

/**
 * @Author IamZY
 * @create 2020/1/21 19:51
 */
public class TestNonBlockingNIO2 {

    @Test
    public void send() throws IOException{
        DatagramChannel dc = DatagramChannel.open();

        dc.configureBlocking(false);

        ByteBuffer buf = ByteBuffer.allocate(1024);

        Scanner scan = new Scanner(System.in);

        while(scan.hasNext()){
            String str = scan.next();
            buf.put((new Date().toString() + ":\n" + str).getBytes());
            buf.flip();
            dc.send(buf, new InetSocketAddress("127.0.0.1", 9898));
            buf.clear();
        }

        dc.close();
    }

    @Test
    public void receive() throws IOException{
        DatagramChannel dc = DatagramChannel.open();

        dc.configureBlocking(false);

        dc.bind(new InetSocketAddress(9898));

        Selector selector = Selector.open();

        dc.register(selector, SelectionKey.OP_READ);

        while(selector.select() > 0){
            Iterator<SelectionKey> it = selector.selectedKeys().iterator();

            while(it.hasNext()){
                SelectionKey sk = it.next();

                if(sk.isReadable()){
                    ByteBuffer buf = ByteBuffer.allocate(1024);

                    dc.receive(buf);
                    buf.flip();
                    System.out.println(new String(buf.array(), 0, buf.limit()));
                    buf.clear();
                }
            }

            it.remove();
        }
    }

}

```



### 选择器 Selector

### SocketChannel ServerSocketChannel DatagramChannel

## 管道 Pipe

```java
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

```



## Java NIO2（Path、Paths与Files）