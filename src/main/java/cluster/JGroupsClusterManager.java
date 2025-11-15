package cluster;

import crdt.StrokeCRDT;
import org.jgroups.JChannel;
import org.jgroups.blocks.cs.ReceiverAdapter;
import org.jgroups.Message;
import org.jgroups.util.Util;
import org.springframework.stereotype.Component;

@Component
public class JGroupsClusterManager extends ReceiverAdapter {
    private final StrokeCRDT crdt;
    private JChannel channel;
    private static final String CLUSTER_NAME = "whiteboard-cluster";


    public JGroupsClusterManager(StrokeCRDT crdt) {
        this.crdt = crdt;
        try {
            channel = new JChannel(); // default config - good for local testing
            channel.setReceiver(this);
            channel.connect(CLUSTER_NAME);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void broadcastState(StrokeCRDT state) {
        try {
            byte[] buf = Util.objectToByteBuffer(state);
            Message msg = new Message(null, null, buf);
            channel.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void receive(Message msg) {
        try {
            Object obj = Util.objectFromByteBuffer(msg.getBuffer());
            if (obj instanceof StrokeCRDT) {
                crdt.merge((StrokeCRDT) obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
