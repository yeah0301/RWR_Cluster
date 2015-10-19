import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class Config {
	
	Properties properties = new Properties();
	String configFile = "config.properties";
	
	int MajorNodeThreshold;
	double OverlapThreadshold;
	double PreliminaryThreshold;
	double RestartProbability;
	String node_path;
	String edge_path;
	
	public Config(){
		
		try {
		    properties.load(new InputStreamReader(new FileInputStream(configFile), "UTF-8"));
		    
		} catch (FileNotFoundException ex) {
		    ex.printStackTrace();
		    return;
		} catch (IOException ex) {
		    ex.printStackTrace();
		    return;
		}
		
		// 第二個參數為預設值，如果沒取到值的時候回傳預設值
		MajorNodeThreshold = Integer.parseInt(properties.getProperty("MajorNodeThreshold", "15"));
		OverlapThreadshold = Double.parseDouble(properties.getProperty("OverlapThreadshold", "0.4"));
		PreliminaryThreshold = Double.parseDouble(properties.getProperty("PreliminaryThreshold", "0.014"));
		RestartProbability = Double.parseDouble(properties.getProperty("RestartProbability", "0.65"));
		node_path = properties.getProperty("node_path","");
		edge_path = properties.getProperty("edge_path","");
		//System.out.println(edge_path+MajorNodeThreshold);
		
	}
	
	public int getMajorNodeThreshold(){
		return MajorNodeThreshold;
	}
	
	public double getOverlapThreadshold(){
		return OverlapThreadshold;
	}
	
	public double getPreliminaryThreshold(){
		return PreliminaryThreshold;
	}
	
	public double getRestartProbability(){
		return RestartProbability;
	}
	
	public String getNode_path(){
		return node_path;
	}
	
	public String getEdge_path(){
		return edge_path;
	}
	
}
