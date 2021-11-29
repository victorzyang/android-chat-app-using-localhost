package com.comp2601.chatclient;

import android.os.Build;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity; //something is wrong with the 'v7'; do I need this line??
import androidx.appcompat.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.net.URI;
import java.net.URISyntaxException;
import tech.gusavila92.websocketclient.WebSocketClient;

//added all these imports below
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;

public class MainActivity extends AppCompatActivity {

    //server url string for AVD accessing server running on local host machine
    public static final String SERVER_URL_STRING = "ws://10.0.2.2:3000";

    //TODO: add IP address and Port for classroom server if you want to talk to it
    public static final String address = "http://134.117.26.92:3001"; //done/ 3001 is the port number

    private static final String TAG = "MainActivity";
    private static final int NORMAL_CLOSURE_STATUS = 1000;


    private TextView mTextViewMsgOutput;
    private EditText mEditViewMsgSend;
    private Button mButtonSendMsg;
    private Button mButtonConnect;
    private Button mButtonDisconnect;
    //TODO: add variables to represent OkHttpClient and WebSocket
    private OkHttpClient client; //both have been created
    private WebSocket ws;

    public void enableButtons(Button mButton1, Button mButton2) {
        mButton1.setEnabled(true);
        mButton2.setEnabled(true);
    }

    public void enableButtons(Button mButton) {
        mButton.setEnabled(true);
    }

    public void disableButtons(Button mButton) {
        mButton.setEnabled(false);
    }

    public void disableButtons(Button mButton1, Button mButton2) {
        mButton1.setEnabled(false);
        mButton2.setEnabled(false);

    }

    //TODO: Create your own subclass of WebSocketListener
    private final class EchoWebSocketListener extends WebSocketListener{ //subclass has been created

        @Override
        public void onOpen(WebSocket webSocket, Response response) { //messages are sent to the server in this method
            webSocket.send("Connected");
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) { //receiving from other clients
            output("Receiving: " + text);
        }

        @Override
        public void onMessage(WebSocket webSocket, ByteString bytes) {
            output("Receiving bytes: " + bytes.hex());
        }

        @Override
        public void onClosing(WebSocket webSocket, int code, String reason) { //when a client leaves
            webSocket.close(NORMAL_CLOSURE_STATUS, null);
            output("Closing: " + code + " / " + reason);
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) { //if there is an error
            output("Error: " + t.getMessage());
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewMsgOutput = (TextView) findViewById(R.id.msgRecievedView); //text view to show messages from other clients
        mEditViewMsgSend = (EditText) findViewById(R.id.editTextMsgSend); //text field to type messages to be send to connected clients
        mButtonSendMsg = (Button) findViewById(R.id.buttonSend);
        mButtonConnect = (Button) findViewById(R.id.buttonConnect);
        mButtonDisconnect = (Button) findViewById(R.id.buttonDisconnect);

        disableButtons(mButtonSendMsg, mButtonDisconnect);

        //TODO: Create an OkHttpClient
        client = new OkHttpClient(); //done

        /*URI uri;
        try{
            //Connect to local host
            uri = new URI(SERVER_URL_STRING);
        }catch (URISyntaxException e){
            e.printStackTrace();
        }*/

        mTextViewMsgOutput.setMovementMethod(new ScrollingMovementMethod());
        mButtonConnect.setOnClickListener(new View.OnClickListener() { //connect button
            @Override
            public void onClick(View v) {

                enableButtons(mButtonSendMsg, mButtonDisconnect);

                disableButtons(mButtonConnect);

                //TODO: Use OkHttpClient to create a Web Socket connected to Chat Server
                //Request request = new Request.Builder().url(address).build(); //done
                Request request = new Request.Builder().url(SERVER_URL_STRING).build();
                EchoWebSocketListener listener = new EchoWebSocketListener();
                ws = client.newWebSocket(request, listener);

                /*ws = new WebSocketClient(uri){
                    @Override
                    public void onOpen(){

                    }

                    @Override
                    public void onTextReceived(String s){
                        runOnUiThread(new Runnable(){
                            @Override
                            public void run(){

                            }
                        });
                    }

                    @Override
                    public void onBinaryReceived(byte[] data){
                    }

                    @Override
                    public void onPingReceived(byte[] data){
                    }

                    @Override
                };*/
            }
        });

        mButtonSendMsg.setOnClickListener(new View.OnClickListener() { //send button
            @Override
            public void onClick(View v) {

                String message = mEditViewMsgSend.getText().toString();
                //TODO: Send Message to Server via Web Socket
                ws.send(message); //is this how I do it?

                mEditViewMsgSend.setText("");
            }
        });

        mButtonDisconnect.setOnClickListener(new View.OnClickListener() { //disconnect button
            @Override
            public void onClick(View v) {

                enableButtons(mButtonConnect);
                disableButtons(mButtonSendMsg, mButtonDisconnect);

                //TODO: Close the Web Socket
                ws.close(NORMAL_CLOSURE_STATUS, null); //is this how I do it?

            }
        });


    }


    private void output(final String txt) {
        /*
          This method will output contents on the mTextViewMsgOutput but run the request
          on the UIThread. This can be called from with a WebSocketClient which runs
          on its own thread.
         */
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mTextViewMsgOutput.setText(mTextViewMsgOutput.getText().toString() + "\n\n" + txt);
            }
        });
    }


}