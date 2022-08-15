/*
Licensed under gpl.
Copyright (c) 2010 sumit singh
http://www.gnu.org/licenses/gpl.html
Use at your own risk
Other licenses may apply please refer to individual source files.
*/
package org.ptg.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.RandomStringUtils;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.stream.ChunkedInput;
import org.ptg.util.events.HeartBeatEvent;
public class ContPublisherWriter implements ChunkedInput, Runnable {

    private static LinkedBlockingQueue q = new LinkedBlockingQueue();
    private static ThreadPoolExecutor executor = new ThreadPoolExecutor(3, 3, 5000, TimeUnit.SECONDS, q);
    private static Map<Long,org.ptg.events.Event> events = new HashMap<Long,org.ptg.events.Event>();
    private static AtomicInteger count = new AtomicInteger();
    private static Map<String, ContPublisherWriter> instances = new HashMap<String, ContPublisherWriter>();
    private static int heartbeat = 10000;
    private static Thread thread = null;
    private static AtomicLong parentSeq = new AtomicLong(-1);
    private static Object nlock = new Object();
    private String uid;
    private long currentSeq;
    private int nid;
    private static Long lastGCSeq = 0L;
    private LinkedBlockingQueue<Long> myLocalEvents = new LinkedBlockingQueue<Long>();

    public ContPublisherWriter(String id, long seq, int intid) {
        uid = id;
        currentSeq = seq;
        nid = intid;
    }

    public Object nextChunk() throws Exception {
                //System.out.println("Backlog: " + (parentSeq.get() - currentSeq));
                currentSeq = myLocalEvents.take();
                org.ptg.events.Event e = events.get(currentSeq);
                StringBuilder temp = new StringBuilder();
                String s = CommonUtil.toJson(e);
                //temp.append("<script type=\"text/javascript\" charset=\"utf-8\">parent.handleDebug(\"");
                temp.append("parent.handleDebug(\"");
                temp.append(e==null?"NPE":e.getEventType());
                temp.append("\",["); //has to go as array
                temp.append(s);
                //temp.append("]);</script>");
                temp.append("]);");
                ChannelBuffer buf2 = ChannelBuffers.copiedBuffer(temp.toString(), "UTF-8");
                return buf2;
    }
    public String nextMsg()  {
        //System.out.println("Backlog: " + (parentSeq.get() - currentSeq));
        StringBuilder temp = new StringBuilder();
        try {
			currentSeq = myLocalEvents.take();
        org.ptg.events.Event e = events.get(currentSeq);
        String s = CommonUtil.toJson(e);
        //temp.append("<script type=\"text/javascript\" charset=\"utf-8\">parent.handleDebug(\"");
        temp.append("parent.handleDebug(\"");
        temp.append(e==null?"NPE":e.getEventType());
        temp.append("\",["); //has to go as array
        temp.append(s);
        //temp.append("]);</script>");
        temp.append("]);");
    	} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
        return temp.toString();
}
    @Override
    public boolean hasNextChunk() {
        return true;
    }

    @Override
    public void close() throws Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public void run() {
        int count = 0;
        for (;;) {
            try {
                Thread.sleep(heartbeat);
                HeartBeatEvent event = new HeartBeatEvent();
                event.setMillis(System.currentTimeMillis());
                loadEvent(event);
                count++;
                if (count == 10) {
                        long seq = minIndex();
                        if(seq==-1)seq=parentSeq.get();
                        count = 0;
                        for (Long i = lastGCSeq; i < seq; i++) {
                            events.remove(i);
                        }
                        lastGCSeq = seq;
                }

            } catch (InterruptedException ex) {
                Logger.getLogger(ContPublisherWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void init() {
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }
    }

    public static ContPublisherWriter getInstance() {
        synchronized (instances) {
            String uid = RandomStringUtils.randomAlphabetic(16);
            int nid = count.incrementAndGet();
            long seq = parentSeq.get();
            ContPublisherWriter writer = new ContPublisherWriter(uid, seq, nid);
            instances.put(uid, writer);
            writer.init();
            return writer;
        }
    }

    public static void removeInstance(ContPublisherWriter w) {
        if (w != null) {
            synchronized (instances) {
                instances.remove(w.uid);
            }
        }

    }

    public static void loadEvent(final org.ptg.events.Event e) {
        executor.submit(new Runnable() {
            public void run() {
                Long l = parentSeq.incrementAndGet();
                e.setSenderSeqNo(l);
                events.put(l,e);
                loadForAll(l);
            }
        });
    }

    private static void loadForAll(Long seq) {
        synchronized (instances) {
            for (ContPublisherWriter w : instances.values()) {
                try {
                    w.myLocalEvents.put(seq);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    private static long minIndex() {
        long seq = 0;
        long seqOld = 0;
        synchronized (instances) {
            if(instances.size()==0)return -1;
            for (ContPublisherWriter w : instances.values()) {
                seq = w.currentSeq-1;
                if(seqOld==0)
                    seqOld=seq;
                else if(seqOld > seq)
                    seqOld = seq;
            }
        }
        return seqOld;
    }

}
