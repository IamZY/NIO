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
