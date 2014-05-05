package master;

import java.util.Collection;

import node.NodeService;

import com.google.common.collect.Lists;

public class NodePool {

    private Collection<NodeService> runners;

    public NodePool() {
        runners = Lists.newArrayList();
    }

    public void add(NodeService node) {
        runners.add(node);
    }

    public NodeService nextIdleNode() {
        NodeService idleNode = null;
        boolean hasIdleNode = false;

        while (!hasIdleNode) {
            for (NodeService node : runners) {
                if (node.isIdle()) {
                    hasIdleNode = true;
                    idleNode = node;
                    break;
                }
            }
        }

        return idleNode;
    }

}
