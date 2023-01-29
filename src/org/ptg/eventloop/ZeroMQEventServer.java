package org.ptg.eventloop;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ZeroMQEventServer {
	 ObjectMapper mapper = new ObjectMapper();
	public  void startEventServer() throws Exception
    {
        try (ZContext context = new ZContext()) {
            // Socket to talk to clients
            ZMQ.Socket socket = context.createSocket(SocketType.REP);
            socket.bind("tcp://*:5550");

            while (!Thread.currentThread().isInterrupted()) {
                // Block until a message is received
                byte[] reply = socket.recv(0);



                // Send a response
                String response = "Hello, world!";
                socket.send(response.getBytes(ZMQ.CHARSET), 0);
                saveMessage(reply);
            }
        }
    }
	public  void saveMessage(byte[] reply) {
		try {
			Point pt  =  mapper.readValue(reply, Point.class);
			String imgStr = (String) pt.getData().get("img");
			if(imgStr!=null) {
			byte[]data = Base64.getDecoder().decode(imgStr);
			ByteArrayInputStream baos = new ByteArrayInputStream(data);
			BufferedImage img = ImageIO.read(baos);
			ImageIO.write(img, "png", new FileOutputStream("c:\\temp\\"+pt.getId()+".png"));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
