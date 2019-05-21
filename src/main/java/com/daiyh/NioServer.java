package com.daiyh;

import com.sun.org.apache.regexp.internal.RE;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: daiyunhao
 * Date: 19-5-21
 * Description: 服务器端
 */
public class NioServer {

    /**
     * 启动
     */
    public void start() throws IOException {
        //1. 创建Selector
        Selector selector = Selector.open();

        //2. 通过ServerSocketChannel创建Channel通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();

        //3. 为Channel通道绑定监听端口
        serverSocketChannel.bind(new InetSocketAddress(8000));

        //4. 设置Channel为非阻塞模式
        serverSocketChannel.configureBlocking(false);

        //5. 将Channel注册到Selector上,监听连接事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器启动成功!");

        //6. 循环等待新接入的连接
        for (;;){ //在C语言中for(;;)会编译成两条语句, while(true)会被编译成三条
            //可用Channel的数量
            int readyChannels = selector.select();

            if (readyChannels == 0)  continue;

            //获取可用Channel的集合
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            Iterator iterator = selectionKeys.iterator();

            while (iterator.hasNext()){

                //SelectionKey的实例
                SelectionKey selectionKey = (SelectionKey) iterator.next();

                //移除集合中的channel
                iterator.remove();

                //7. 根据就绪状态,调用对应的方法处理业务逻辑

                //如果是接入事件
                if (selectionKey.isAcceptable()){
                    acceptHandler(serverSocketChannel, selector);
                }

                //如果是可读事件
                if (selectionKey.isReadable()){
                    readHandler(selectionKey, selector);
                }
            }
        }
    }

    /**
     * 接入事件处理器
     */
    private void acceptHandler(ServerSocketChannel serverSocketChannel, Selector selector)
                             throws IOException {

        SocketChannel socketChannel = serverSocketChannel.accept();

        socketChannel.configureBlocking(false);

        socketChannel.register(selector, SelectionKey.OP_READ);

        socketChannel.write(Charset.forName("UTF-8").encode("welcome to this chat room!"));
    }

    /**
     * 可读事件处理器
     */
    private void readHandler(SelectionKey selectionKey, Selector selector) throws IOException {

        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

        String request = "";
        while (socketChannel.read(byteBuffer) > 0){
            byteBuffer.flip();
            request += Charset.forName("UTF-8").decode(byteBuffer);
        }

        socketChannel.register(selector, SelectionKey.OP_READ);

        if (request.length() > 0){
            boardCast(selector, socketChannel,request);
        }

    }

    private void boardCast(Selector selector, SocketChannel sourceChannel, String request){

        Set<SelectionKey> selectionKeys = selector.keys();

        selectionKeys.forEach(selectionKey -> {
            Channel targetChannel = selectionKey.channel();

            if (targetChannel instanceof SocketChannel && targetChannel != sourceChannel){
                try {
                    ((SocketChannel) targetChannel).write(Charset.forName("UTF-8").encode(request));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public static void main(String[] args) throws IOException {
        NioServer nioServer = new NioServer();
        nioServer.start();

    }
}
