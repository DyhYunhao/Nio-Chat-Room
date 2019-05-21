package com.daiyh;

import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: daiyunhao
 * Date: 19-5-21
 * Description: 客户端
 */
public class NioClient {

    public void start(String nickName) throws IOException {

        SocketChannel socketChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 8000));

        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        new Thread(new NioClientHandler(selector)).start();


        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNextLine()){
            String request = scanner.nextLine();
            if (request != null && request.length() > 0){
                socketChannel.write(Charset.forName("UTF-8").encode(nickName + ": " + request));
            }
        }

    }

    public static void main(String[] args) throws IOException {
//        new NioClient().start();
    }

}


