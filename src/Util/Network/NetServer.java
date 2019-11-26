package Util.Network;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.*;

public class NetServer {
    private HttpServer server;

    public NetServer(final int PORT) {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addRoute(String route, NetAction action) {
        if (route.charAt(0) != '/') route = '/' + route;
        server.createContext(route, action.setRoute(route));
    }

    public void start() {
        server.setExecutor(null); //default executor
        server.start();
    }

    public void stop() {
        stop(0);
    }

    public void stop(int delay) {
        server.stop(delay);
    }


    public static class NetAction implements HttpHandler {

        public boolean handled = false; //if the request has been handled yet
        public boolean connected = false;
        protected HttpExchange exchange;
        private OutputStream os;
        private InputStream is;
        private ArrayList<String> toWrite;
        private String route;

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            connected = true;
            toWrite = new ArrayList<>();
            this.exchange = exchange;
            os = exchange.getResponseBody();
            is = exchange.getRequestBody();
            //write("Connection established..");
            customAction(); //includes writes, reads, etc.
            switch (exchange.getRequestMethod()) {
                case "GET":
                    actionGET(getQueryParams(exchange.getRequestURI().getRawQuery()));
                    break;
                case "POST":
                    actionPOST(readRequest());
                    break;
                default:
                    System.out.println("An unsupported connection type was used.");

            }
            processWrites();
            os.close();
            is.close();
            handled = true;
            connected = false;
            onClose();
        }

        //CALL ME FROM OVERRIDDEN customAction() function
        public void write(String write) {
            toWrite.add(write);
        }

        public void writeJSON(String write) {
            JSONObject w = new JSONObject();
            w.put("message", write);
            write(w.toString());
        }

        public void setResponseCode(int code) {
            responseCode = code;
        }

        private int responseCode = 200;

        private void processWrites() {
            String composite = "";
            for (String s : toWrite) {
                composite += s + ' ';
            }
            try {
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseHeaders().set("Access-Control-Allow-Origin","*");
                exchange.sendResponseHeaders(responseCode, composite.length());
                os.write(composite.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        public String readRequest() {
            Headers reqHeader = exchange.getRequestHeaders();
            try {
                byte[] bytes = readAllBytes(is);
                return new String(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        private static final int DEFAULT_BUFFER_SIZE = 8192;
        private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;

        private static byte[] readAllBytes(InputStream is) throws IOException {
            return readNBytes(Integer.MAX_VALUE, is);
        }

        private static byte[] readNBytes(int len, InputStream is) throws IOException {
            if (len < 0) {
                throw new IllegalArgumentException("len < 0");
            }

            List<byte[]> bufs = null;
            byte[] result = null;
            int total = 0;
            int remaining = len;
            int n;
            do {
                byte[] buf = new byte[Math.min(remaining, DEFAULT_BUFFER_SIZE)];
                int nread = 0;

                // read to EOF which may read more or less than buffer size
                while ((n = is.read(buf, nread,
                        Math.min(buf.length - nread, remaining))) > 0) {
                    nread += n;
                    remaining -= n;
                }

                if (nread > 0) {
                    if (MAX_BUFFER_SIZE - total < nread) {
                        throw new OutOfMemoryError("Required array size too large");
                    }
                    total += nread;
                    if (result == null) {
                        result = buf;
                    } else {
                        if (bufs == null) {
                            bufs = new ArrayList<>();
                            bufs.add(result);
                        }
                        bufs.add(buf);
                    }
                }
                // if the last call to read returned -1 or the number of bytes
                // requested have been read then break
            } while (n >= 0 && remaining > 0);

            if (bufs == null) {
                if (result == null) {
                    return new byte[0];
                }
                return result.length == total ?
                        result : Arrays.copyOf(result, total);
            }

            result = new byte[total];
            int offset = 0;
            remaining = total;
            for (byte[] b : bufs) {
                int count = Math.min(b.length, remaining);
                System.arraycopy(b, 0, result, offset, count);
                offset += count;
                remaining -= count;
            }

            return result;
        }


        public void customAction() {
            //OVERRIDE ME
        }

        public void actionPOST(String bodyPOST) {
            //OVERRIDE ME -- POST requests only
        }

        public void actionGET(Map<String, String> requestHeaders) {
            //OVERRIDE ME -- GET requests only
        }

        public void onClose() {
            //OVERRIDE ME, called after streams are closed
            //If you don't know why you're overriding this, you're using the wrong function. Use customAction() instead.
        }


        public Map<String, String> getQueryParams(String qs) {
            Map<String, String> result = new HashMap<>();
            if (qs == null)
                return result;

            int last = 0, next, l = qs.length();
            while (last < l) {
                next = qs.indexOf('&', last);
                if (next == -1)
                    next = l;

                if (next > last) {
                    int eqPos = qs.indexOf('=', last);
                    try {
                        if (eqPos < 0 || eqPos > next)
                            result.put(URLDecoder.decode(qs.substring(last, next), "utf-8"), "");
                        else
                            result.put(URLDecoder.decode(qs.substring(last, eqPos), "utf-8"), URLDecoder.decode(qs.substring(eqPos + 1, next), "utf-8"));
                    } catch (UnsupportedEncodingException e) {
                        throw new RuntimeException(e); // will never happen, utf-8 support is mandatory for java
                    }
                }
                last = next + 1;
            }
            result.put("subroute", getSubroute());
            return result;
        }

        private NetAction setRoute(String route) {
            this.route = route;
            return this;
        }

        public String getSubroute() {
            return exchange.getRequestURI().getRawPath().substring(route.length());
        }


    }


}