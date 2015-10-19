

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeCluster
{
	private Long clusterID;
	private List<Node> nodes;
	private Map<Node, Integer> majorNodes;	//	node-degree pairs
	private List<Long> containNodeIDs;
	
	public NodeCluster(long clusterID)
	{
		this.clusterID = clusterID;
		this.nodes = new ArrayList<>();
		this.majorNodes = new HashMap<Node, Integer>();
		this.containNodeIDs = new ArrayList<>();		
	}
	public NodeCluster(NodeCluster oldNodeCluster)
	{
		this.clusterID = oldNodeCluster.getClusterID();
		this.nodes = new ArrayList<>(oldNodeCluster.getNodes());
		this.majorNodes = new HashMap<>(oldNodeCluster.getMajorNodes());
		this.containNodeIDs = new ArrayList<>(oldNodeCluster.getContainNodeIDs());
	}	
	private void addContainNodeIDs(Long newNodeID)
	{
		this.containNodeIDs.add(newNodeID);
	}	
	private void removeContainNodeIDs(Long oldNodeID)
	{
		this.containNodeIDs.remove(oldNodeID);
	}	
	public void addNode(Node newNode)
	{
		this.nodes.add(newNode);
		addContainNodeIDs(newNode.getNodeID());		
	}
	public void removeNode(Node oldNode)
	{
		this.nodes.remove(oldNode);
		removeContainNodeIDs(oldNode.getNodeID());
	}
	
	public void addNodes(List<Node> newNodes)
	{
		for(Node node : newNodes)
		{
			addNode(node);			
		}
	}
	public void removeNodes(List<Node> oldNodes)
	{
		for(Node node : oldNodes)
		{
			removeNode(node);
		}
	}
	/**
	 * Add major node and its degree into this cluster (warning! won't be added into nodeIDs automatically)
	 * @param node - the major node to be added
	 * @param degree - the degree of input major node
	 */
	public void addMajorNode(Node node, Integer degree)
	{
		this.majorNodes.put(node, degree);
	}
	/**
	 * Add major node and its degree into this cluster, multiple version(warning! won't be added into nodeIDs automatically)
	 * @param majorNodes - the major nodes to be added (in Map<Node,Integer> form)
	 */
	public void addMajorNodes(Map<Node,Integer> majorNodes)
	{
		for(Node node : majorNodes.keySet())
		{
			addMajorNode(node, majorNodes.get(node));
		}
	}
	
	public Long getClusterID()
	{
		return this.clusterID;
	}
	public List<Node> getNodes()
	{
		return this.nodes;
	}
	public List<Long> getContainNodeIDs()
	{
		return this.containNodeIDs;
	}
	public Map<Node,Integer> getMajorNodes()
	{
		return this.majorNodes;
	}
	
	@Override
	public boolean equals(Object object)
	{
		if(!(object instanceof NodeCluster))
		{
			return false;
		}
		NodeCluster anotherCluster = (NodeCluster) object;		
		return this.clusterID.equals(anotherCluster.clusterID);
	}
	
	
}
