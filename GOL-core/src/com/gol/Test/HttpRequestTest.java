package com.gol.Test;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpRequestTest {

    public static void main(String[] args) throws IOException {

        URL url = new URL("google.com");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        System.out.println(new DataOutputStream(con.getOutputStream()));

    }

}
