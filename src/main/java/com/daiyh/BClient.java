package com.daiyh;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: daiyunhao
 * Date: 19-5-21
 * Description:
 */
public class BClient {
    public static void main(String[] args) throws IOException {
        new NioClient().start("BClient");
    }
}
