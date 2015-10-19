
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Graph implements Serializable
{
	private Long graphID;
	private List<Node> nodes;
	private List<Edge> edges;
	

	public Graph(Long graphID)
	{
		this.graphID = graphID;
		this.nodes = new ArrayList<>();
		this.edges = new ArrayList<>();
	}	
	public Graph(Long graphID, List<Node> nodes, List<Edge> edges)
	{
		this.graphID = graphID;
		this.nodes = nodes;
		this.edges = edges;
	}
	public Long getGraphID()
	{
		return graphID;
	}
	
	public void addNode(Node newNode)
	{
		this.nodes.add(newNode);
	}
	public void removeNode(Node oldNode)
	{
		this.nodes.remove(oldNode);
	}
	public void addEdge(Edge newEdge)
	{
		this.edges.add(newEdge);
	}
	public void removeEdge(Edge oldEdge)
	{
		this.edges.remove(oldEdge);
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
	public void addEdges(List<Edge> newEdges)
	{
		for(Edge edge : newEdges)
		{
			addEdge(edge);
		}
	}
	public void removeEdges(List<Edge> oldEdges)
	{
		for(Edge edge : oldEdges)
		{
			removeEdge(edge);
		}
	}
	public List<Node> getNodes()
	{
		return this.nodes;		
	}
	public List<Edge> getEdges()
	{
		return this.edges;
	}
	
	
	
}
