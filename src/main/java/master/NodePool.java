package master;

import java.util.Collection;

import node.NodeService;

import com.google.common.collect.Lists;

public class NodePool {
    
    private final Collection<NodeService> runners;
    
    public NodePool() {
        this.runners = Lists.newArrayList();
    }
    
    public void add(NodeService node) {
        this.runners.add(node);
    }
    
    public synchronized NodeService nextIdleNode() {
        NodeService idleNode = null;
        boolean hasIdleNode = false;
        
        while (!hasIdleNode) {
            for (NodeService node : this.runners) {
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
