package org.ptg.eventloop;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class EventSender {
    ZMQ.Socket socket;
    ZContext context;
    ObjectMapper mapper = new ObjectMapper();
    public EventSender() {
        context = new ZContext();
		 socket = context.createSocket(SocketType.REQ);
        socket.connect("tcp://127.0.0.1:5555");

    }

    

    public void sendEvent(IAutomationEvent p) {
        try {
            String s = mapper.writeValueAsString(p);
          //  System.out.println(s);
			sendEventZMQ(s);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void sendEventZMQ(String s) {
        socket.send(s.getBytes(ZMQ.CHARSET), 0);
		byte[] reply = socket.recv(0);
		String ret = new String(reply, ZMQ.CHARSET);
		System.out.println(ret);
	}

}
