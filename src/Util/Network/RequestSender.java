package Util.Network;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public abstract class RequestSender {
    public static String sendPost(String toSend, String route)//defaults to localhost
            throws IOException {
        return sendRequest("http://127.0.0.1", 7070, route, toSend, "POST");
    }


    public static String sendRequest(String IP, final int PORT, String route, String toSend, String reqType) throws IOException {
        if (route.charAt(0) != '/') route = '/' + route;
        URL obj = new URL(IP + ":" + PORT + route);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod(reqType);
        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(toSend);
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode(); //should always be 200
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}
