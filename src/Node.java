

import java.io.Serializable;

public class Node implements Serializable
{
	private Long nodeID;	//	identical to docID
		
	public Node(Long nodeID)
	{
		this.nodeID = nodeID;		
	}
	
	public Long getNodeID()
	{
		return this.nodeID;
	}
	
	@Override
	public String toString()
	{
		String show = "";
		show += "NodeID: <"+ nodeID+ ">";
				
		return show; 
	}
}
