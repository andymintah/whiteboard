
package cluster;

import crdt.StrokeCRDT;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.BytesMessage;
import org.jgroups.Receiver;
import org.jgroups.util.Util;
import org.springframework.stereotype.Component;

@Component
public class JGroupsClusterManager implements Receiver {
    private final StrokeCRDT crdt;
    private JChannel channel;
    private static final String CLUSTER_NAME = "whiteboard-cluster";

    public JGroupsClusterManager(StrokeCRDT crdt) {
        this.crdt = crdt;
        try {
            channel = new JChannel();
            channel.setReceiver(this);
            channel.connect(CLUSTER_NAME);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void broadcastState(StrokeCRDT state) {
        try {
            byte[] buf = Util.objectToByteBuffer(state);
            Message msg = new BytesMessage(null, buf);
            channel.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void receive(Message msg) {
        try {
            if (msg instanceof BytesMessage bytesMsg) {
                Object obj = Util.objectFromByteBuffer(bytesMsg.getArray());
                if (obj instanceof StrokeCRDT remote) {
                    crdt.merge(remote);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}