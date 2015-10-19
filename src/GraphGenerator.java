import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.StringTokenizer;


public class GraphGenerator
{
	
	private static Long idxGraphID;
	private static Long idxEdgeID;
	
	
	public GraphGenerator(Long idxGraphID, Long idxEdgeID)
	{
		GraphGenerator.idxGraphID = idxGraphID;
		GraphGenerator.idxEdgeID = idxEdgeID;
		
	}
	/**
	 * Get the current available graphID (automatically increase by 1)
	 * @return the current available graphID
	 */
	private Long getNewGraphID()
	{
		return GraphGenerator.idxGraphID++;
	}
	/**
	 * Get the current available edgeID (automatically increase by 1)
	 * @return the current available edgeID
	 */
	private Long getNewEdgeID()
	{
		return GraphGenerator.idxEdgeID++;
	}
	
	
	/**
	 * 
	 * @param node_path
	 * @param edge_path
	 */
	
	public Graph inputData(String node_path,String edge_path){
		Graph graph = null;		
		FileReader fr;
		BufferedReader br;
		StringTokenizer tokens;
		LinkedList<Node> nodes = new LinkedList<Node>();
		LinkedList<Edge> edges = new LinkedList<Edge>(); 
		
		
		try {
			/*node*/
			fr = new FileReader(node_path);
			br = new BufferedReader(fr);
			
			while (br.ready()) {
				tokens = new StringTokenizer(br.readLine());
				while(tokens.hasMoreTokens()) {
					//documents.get(i).getDocID()
					nodes.add(new Node(Long.parseLong(tokens.nextToken())));
					
				}
			}
			
			
			/*edge*/
			fr = new FileReader(edge_path);
			br = new BufferedReader(fr);
			
			while (br.ready()) {
				String[] split=br.readLine().split("\\s");
				
				//edges.add(new Edge(getNewEdgeID(), documents.get(i).getDocID(), documents.get(j).getDocID(), similarity));
				edges.add(new Edge(Long.parseLong(split[0])
						, Long.parseLong(split[1])
						, Long.parseLong(split[2])
						, Double.parseDouble(split[3])));
			}
			
			graph = new Graph(getNewGraphID(), nodes, edges);
			
			br.close();
	        fr.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return graph;
	}

	/**
	 * 
	 * String selectQueryNode = "SELECT id FROM "+ tableNode;
	String selectQueryEdge = "SELECT id, src, dst, weight FROM "+ tableEdge;
		
		graph.addNode(new Node(resultSet.getLong("id")));
		
		graph.addEdge(new Edge(resultSet.getLong("id"), resultSet.getLong("src"), resultSet.getLong("dst"), resultSet.getDouble("weight")));
		
	 * Load the graph information from file (de-serialization)
	 * @param inputPath - the file path of input graph information
	 * @return the graph that stored in the inputPath
	 */
	public Graph loadFile(String inputPath)
	{
		Graph graph = null;
				
		FileInputStream inFile;
		try
		{
			inFile = new FileInputStream(inputPath);
			ObjectInputStream in = new ObjectInputStream(inFile);
			graph = (Graph) in.readObject();
			
			in.close();
			inFile.close();
		} catch (ClassNotFoundException | IOException e)
		{
			System.err.println("Input graph object failed");
			e.printStackTrace();
		}
		return graph;
	}
	
	/**
	 * node
	 * 
	 * for(Node node : graph.getNodes())
		{
			stubQueryNode += node.getNodeID()+ "),(";
		}
		
		
		String insertSQLNode = "INSERT INTO "+ tableNode+ " (id)"
							 + " VALUES ( "+ stubQueryNode+ " )"
							 + " ON DUPLICATE KEY UPDATE id=values(id)";
		String insertSQLEdge = "INSERT INTO "+ tableEdge+ " (id, src, dst, weight)"
					 		 + " VALUES ( "+ stubQueryEdge+ " )"
					 		 + " ON DUPLICATE KEY UPDATE src=values(src), dst=values(dst), weight=values(weight)";
	 * 
	 * edge
	 * 
	 * preStatement.setLong(idxParameter, edge.getEdgeID());
				preStatement.setLong(idxParameter+1, edge.getSrc());
				preStatement.setLong(idxParameter+2, edge.getDst());
				preStatement.setDouble(idxParameter+3, edge.getWeight());
	 * 
	 * 
	 * Save the graph information into file (serialization)
	 * @param outputPath - the file path of output
	 * @param graph - the graph to be store into the file
	 */
	public void storeFile(String outputPath, Graph graph)
	{
		try
		{
			FileOutputStream outFile = new FileOutputStream(outputPath); 
			ObjectOutputStream out = new ObjectOutputStream(outFile);
			out.writeObject(graph);
			out.flush();
			out.close();
			outFile.flush();
			outFile.close();			
		} catch(IOException e)
		{
			System.err.println("Output graph object failed");
			e.printStackTrace();
		}
	}
	
	
}	
	
