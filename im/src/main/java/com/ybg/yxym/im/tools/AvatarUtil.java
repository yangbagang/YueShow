package com.ybg.yxym.im.tools;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ybg on 17-5-11.
 */

public class AvatarUtil {

    private static String SERVER_URL = "http://192.168.12.99:8080/ma/userBase/getAvatarByYmCode";
    //private static String SERVER_URL = "http:///139.224.186.241:8080/ma/userBase/getAvatarByYmCode";

    public static void getAvatarId(final String userId, final GetAvatarCallback callback) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                String avatarId = "c2hvdy9iYXNlL2F2YXRhci9kZWZhdWx0LnBuZw==";
                try {
                    URL url = new URL(SERVER_URL);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");
                    connection.setUseCaches(false);
                    connection.setRequestProperty("Content_Type", "application/x-www-form-urlencoded");
                    connection.setRequestProperty("Charset", "UTF-8");

                    connection.connect();

                    DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                    dos.writeUTF("ymCode=" + userId);
                    dos.flush();
                    dos.close();

                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String result = "";
                    String readLine = "";
                    while ((readLine = bufferedReader.readLine()) != null) {
                        result += readLine;
                    }
                    if (!"".equals(result)) {
                        avatarId = result;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                callback.displayAvatar(avatarId);
            }
        }.start();
    }

    public interface GetAvatarCallback {
        void displayAvatar(String avatarId);
    }
}
