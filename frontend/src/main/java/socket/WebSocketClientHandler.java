package socket;

import javafx.application.Platform;

import java.net.URI;

// TODO SOCKET NOT WORKING
public class WebSocketClientHandler {

//    public static void connectSocket() {
//        System.out.println("bagno");
//        try {
//            // Creating a separate thread for WebSocket connection
//            new Thread(() -> {
//                try {
//                    WebSocketClient webSocketClient = new WebSocketClient(new URI("ws://localhost:8080/image-updates")) {
//                        @Override
//                        public void onOpen(ServerHandshake handshakedata) {
//                            System.out.println("WebSocket connection opened");
//                        }
//
//                        @Override
//                        public void onMessage(String message) {
//                            System.out.println("Received: " + message);
//                        }
//
//                        @Override
//                        public void onClose(int code, String reason, boolean remote) {
//                            System.out.println("WebSocket connection closed with code: " + code + ", reason: " + reason + ", remote: " + remote);
//                        }
//
//                        @Override
//                        public void onError(Exception ex) {
//                            ex.printStackTrace();
//                        }
//                    };
//
//                    webSocketClient.connect();
//
//                    // Thread.sleep(10000);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }).start();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
