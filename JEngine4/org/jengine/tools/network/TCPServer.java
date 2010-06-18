package org.jengine.tools.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import org.jengine.tools.hl7.HL7Ack;

public class TCPServer {
    
    public static void main(String args[]) {
        
        int port = 1500;
        ServerSocket server_socket;
        BufferedReader input;
        boolean sendAcks = true;
        
        try {
            if (args.length < 1)
            {
                System.out.println("USAGE: TCPServer portNumber [ack | nak]");
                System.exit(-1);
            }
            port = Integer.parseInt(args[0]);
            if (args.length > 1)
            {
                if (args[1] != null)
                {
                    if (args[1].equalsIgnoreCase("NAK") == true)
                    {
                        sendAcks = false;
                        System.out.println("will be sending all NAKs");
                    }
                }
                else
                        System.out.println("will be sending ACKs");
            }
        }
        catch (Exception e) {
            System.out.println("Exception : " + e.getMessage());
            e.printStackTrace();
        }
        
        try {
            server_socket = new ServerSocket(port);
            System.out.println("Server waiting for client on port " +
            server_socket.getLocalPort());
            
            // server infinite loop
            while(true) {
                Socket socket = server_socket.accept();
                System.out.println("New connection accepted "
                    + socket.getInetAddress() + ":" + socket.getPort());
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                LLPReader si = new LLPReader(socket.getInputStream());
                LLPWriter so = new LLPWriter(socket.getOutputStream());
                // print received data
                try {
                    while(true) {
                        //String message = input.readLine();
                        String message = si.readMsg();
                        if (message==null) break;
                        System.out.println("RECV");
                        System.out.println(message);
                        System.out.println("XMIT");
                        HL7Ack ack;
                        if (sendAcks == true)
                            ack = new HL7Ack(true, message);
                        else
                            ack = new HL7Ack(false, message);
                        System.out.println(ack.getMessage());
                        so.writeMsg(ack.getMessage());
                    }
                }
                catch (IOException e) {
                    System.out.println("IOException : " + e.getMessage());
                }
                
                // connection closed by client
                try {
                    socket.close();
                    System.out.println("> > Connection closed by client");
                }
                catch (IOException e) {
                    System.out.println(e);
                }
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }
}




