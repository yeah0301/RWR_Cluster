import java.io.Serializable;

public class Edge implements Serializable
{
	private Long edgeID;
	private Long src;
	private Long dst;
	private Double weight;
	
	public Edge(long edgeID, long src, long dst, double weight)
	{
		this.edgeID = edgeID;
		this.src = src;
		this.dst = dst;
		this.weight = weight;
	}
	public void setWeight(double weight)
	{
		this.weight = weight;
	}
	public Long getEdgeID()
	{
		return this.edgeID;
	}
	public Double getWeight()
	{
		return this.weight;
	}
	public Long getSrc()
	{
		return this.src;
	}
	public Long getDst()
	{
		return this.dst;
	}
	
	@Override
	public String toString()
	{
		String show = "";
		show += "EdgeID: <"+ this.edgeID+ ">\tsrc: <"+ this.src+ ">\tdst: <"+ this.dst+ ">\tweight: <"+ weight+ ">";
		
		return show;
	}
}
