package com.smile.socketclientdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    // private properties
    private static final int SERVER_PORT = 6000;
    private String SERVER_IP = "192.168.0.11";
    private EditText messageEditText;
    private EditText serverIpEditText;
    private String messageSend = "";
    private ClientThread clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        messageEditText = findViewById(R.id.messageEditText);
        serverIpEditText = findViewById(R.id.serverIpEditText);
        serverIpEditText.setText(SERVER_IP);

        Button sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageSend = messageEditText.getText().toString();
                SERVER_IP = serverIpEditText.getText().toString();
                clientThread = new ClientThread();
                clientThread.start();
            }
        });
        Button exitButton = findViewById(R.id.exitButton);
        exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (clientThread != null) {
            try {
                clientThread.join();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /*********************************************************/
    private class ClientThread extends Thread {

        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
                Socket socket = new Socket(serverAddr, SERVER_PORT);
                if (socket != null) {
                    // socket has been created
                    System.out.println("Socket has been created.");
                    try {
                        OutputStream outputStream = socket.getOutputStream();
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                        PrintWriter printWriter = new PrintWriter(bufferedWriter, true);

                        printWriter.println(messageSend);
                        System.out.println("Message has been sent out.");

                        socket.close();
                        socket = null;
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            } catch (UnknownHostException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
