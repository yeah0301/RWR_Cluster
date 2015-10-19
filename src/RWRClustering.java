import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class RWRClustering
{
	private double restartProbability; // range from 0.0 to 1.0
	private double overlapThreshold;
	private double convergeThreshold;
	private double preliminaryThreshold;
	private int majorNodeThreshold;
	private List<Node> nodes;
	private List<Edge> edges;
	private static Long idxClusterID;
	
	
	public RWRClustering(Long idxClusterID)
	{
		this.restartProbability = 0.65;
		this.overlapThreshold = 0.6;
		this.convergeThreshold = 1*Math.pow(10, -13);
		this.preliminaryThreshold = 0.015;
		this.majorNodeThreshold = 15;
		RWRClustering.idxClusterID = idxClusterID;
		
	}
	
	/**
	 * Get the current available clusterID (automatically increase by 1)
	 * @return the current available clusterID
	 */
	private Long getNewClusterID()
	{
		return RWRClustering.idxClusterID++;
	}	
	/**
	 * Set the restart probability of random-walk with restart
	 * @param restartProbability - restart probability of random-walk with restart (ranged from 0.0 to 1.0)
	 */
	public void setRestartProbability(double restartProbability)
	{
		this.restartProbability = restartProbability;
	}
	/**
	 * Set the overlap threshold for merging clusters
	 * @param overlapThreshold - overlap threshold of cluster merging
	 */
	public void setOverlapThreadshold(double overlapThreshold)
	{
		this.overlapThreshold = overlapThreshold;
	}
	/**
	 * Set the threshold of preliminary clustering (used to filter out the unnecessary relation)
	 * @param preliminaryThreshold - threshold of preliminary clustering, any stable probability below this value will be ignored.
	 */
	public void setPreliminaryThreshold(double preliminaryThreshold)
	{
		this.preliminaryThreshold = preliminaryThreshold;
	}
	/**
	 * Set the converge threshold of clustering.
	 * @param convergeThreshold - threshold of converge.
	 */
	public void setConvergeThreshold(double convergeThreshold)
	{
		this.convergeThreshold = convergeThreshold;
	}	
	/**
	 * Set the threshold of major node.
	 * @param convergeThreshold - threshold of converge.
	 */
	public void setMajorNodeThreshold(int majorNodeThreshold)
	{
		this.majorNodeThreshold = majorNodeThreshold;
	}	
	/**
	 * Generate adjacency matrix from the input nodes and edges. (limited to Integer addressing)
	 * @param nodes
	 * @param edges
	 * @return the adjacency matrix of input graph (in Matrix form)
	 */
	private Matrix toAdjacencyMatrix(List<Node> nodes, List<Edge> edges)
	{
		List<Long> idxVector = new ArrayList<>(); 	//	store the order of nodeId		
		Matrix adjMatrix = new Matrix(nodes.size(), nodes.size());
		//	initialize adjacencyMatrix by 0
		for(int i=0; i<nodes.size(); i++)
		{
			idxVector.add(nodes.get(i).getNodeID());
			for(int j=0; j<nodes.size(); j++)
			{
				adjMatrix.setValue(i, j, 0);
			}
		}
		for(Edge edge : edges)
		{
			//System.out.println("ori\tsrc: <"+ edge.src+ ">, dst: <"+ edge.dst+ ">");			
			//	transform edge's src and dst into corresponding matrix index
			long srcNodeIdx = idxVector.indexOf(edge.getSrc());
			long dstNodeIdx = idxVector.indexOf(edge.getDst());			

			//System.out.println("trans\tsrc: <"+ srcNodeId+ ">, dst: <"+ dstNodeId+ ">");
			//System.out.println("###\tValue\t###\t<"+ edge.weight+ ">");
			adjMatrix.setValue((int)srcNodeIdx, (int)dstNodeIdx, edge.getWeight());		// precision of indexing is limited to integer (start from 0)	
		}
		return adjMatrix;
	}	
	/**
	 * Check whether the transition vector is converged or not  
	 * @param previousVector - previous transition vector
	 * @param currentVector - current transition vector
	 * @return true if converged, otherwise false
	 */
	private boolean isConverged(Matrix previousVector, Matrix currentVector)
	{
//		System.out.println("prev. <"+ previousVector.getNorm()+ ">, curr.: <"+ currentVector.getNorm()+ ">, diff: <"+ Math.abs(previousVector.getNorm() - currentVector.getNorm())+ ">");
		if(Math.abs(previousVector.getNorm() - currentVector.getNorm()) < convergeThreshold)
		{
			// converged
			return true;
		}
		else
		{
			return false;
		}		
	}
	/**
	 * Calculate the overlapping value of two clusters
	 * @param cluster1
	 * @param cluster2
	 * @return the overlapping value of two clusters
	 */
	private double calculateOverlap(NodeCluster cluster1, NodeCluster cluster2)
	{
		double overlapping = 0.0;
		long intersection = 0;
		
		for(Long nodeId : cluster1.getContainNodeIDs())
		{
			if(cluster2.getContainNodeIDs().contains(nodeId))
			{
				intersection++;
			}
		}
		overlapping = (double) intersection / Math.min(cluster1.getContainNodeIDs().size(), cluster2.getContainNodeIDs().size());	//	 beware using (double)(intersection/Math(...)) will lose the fraction
		//System.out.println("Overlapping status :<"+ intersection+ "/"+ Math.min(cluster1.size(), cluster2.size())+ "="+ overlapping+ ">");
		//System.out.println("Overlapping: <"+ overlapping+ ">");
		return overlapping;
	}
	/**
	 * Check whether all the clusters fit the overlap threshold or not
	 * @param clusters
	 * @return true if all of the overlapping ratio between clusters are under overlap threshold, otherwise false
	 */
	private boolean isWellClustered(List<NodeCluster> clusters)
	{
		for(NodeCluster cluster : clusters)
		{
			for(NodeCluster anotherCluster : clusters)
			{
				if(cluster.equals(anotherCluster))
				{
					//	same cluster, skipped
					continue;
				}
				if(calculateOverlap(cluster, anotherCluster) >= overlapThreshold)
				{
					//	overlap more than threshold
					return false;
				}
			}
		}
		return true;
	}	
	/**
	 * Get the preliminary cluster of specific initial node and its stable transition vector.
	 * @param initialNode - the initial node that trigger the preliminary cluster
	 * @param transitionVector - the transition vector of initial node
	 * @return the preliminary cluster of initial node in NodeCluster form
	 */
	private NodeCluster getPreliminaryCluster(Node initialNode, Matrix transitionVector)
	{
		NodeCluster cluster = new NodeCluster(getNewClusterID());
//		System.err.println("ClusterID: <"+ cluster.getClusterID()+ ">");
		//	add related nodes into initial node's cluster
		for(int i=0; i<nodes.size(); i++)
		{
			if(transitionVector.getValue(i, 0) >= preliminaryThreshold)
			{
				//	treated as a member of initialNodeId's cluster (include initialNode itself)
				cluster.addNode(nodes.get(i));
			}
		}
		//	check the major node qualification of initial node
		if(cluster.getContainNodeIDs().size() >= majorNodeThreshold)
		{
			cluster.addMajorNode(initialNode, cluster.getContainNodeIDs().size());;
		}
		return cluster;
	}
	/**
	 * Merge two clusters into new cluster (preserved the information of larger one)
	 * @param cluster1
	 * @param cluster2
	 * @return the merged cluster (new cluster with all nodes in cluster1 and cluster2)
	 */
	private NodeCluster doMerge(NodeCluster cluster1, NodeCluster cluster2)
	{
		NodeCluster cluster = null;
		if(cluster1.getContainNodeIDs().size() >= cluster2.getContainNodeIDs().size())
		{
			//	case that cluster1 is larger
			cluster = new NodeCluster(cluster1);
			for(Node node : cluster2.getNodes())
			{
				if(!cluster.getContainNodeIDs().contains(node.getNodeID()))
				{
					//	node that only exists in cluster2, added to cluster
					cluster.addNode(node);
				}
			}
			//	update the major node information 
			cluster.addMajorNodes(cluster2.getMajorNodes());
//			return cluster1;
		}
		else
		{
			//	case that cluster2 is larger
			cluster = new NodeCluster(cluster2);
			for(Node node : cluster1.getNodes())
			{
				if(!cluster.getContainNodeIDs().contains(node.getNodeID()))
				{
					//	node that only exists in cluster2, added to cluster
					cluster.addNode(node);
				}
			}
			//	update the major node information 
			cluster.addMajorNodes(cluster1.getMajorNodes());
//			return cluster2;
		}
		
		return cluster;
	}
	/**
	 * Merge the input clusters (every cluster merges with the highest overlapping ratio of existing clusters) 
	 * @param oriClusters - list of original clusters
	 * @return list of merged clusters 
	 */
	private List<NodeCluster> mergePhrase(List<NodeCluster> oriClusters)
	{
//		System.out.println("before mergePhrase totally: <"+ oriClusters.size()+ "> clusters");
		List<Boolean> isMergedVector = new ArrayList<>();
		List<NodeCluster> mergedClusters = new ArrayList<>();
		List<Integer> aloneClusterIdxs = new ArrayList<>(); 
		int topOverlapIdx = -1;
		boolean topOverlapIsMerged = false;	//	currently tend to merge with already merged cluster
		double topOverlapRatio = 0.0;
		double currOverlapRatio = -1;
		
		//	initial all isMerge flags to false (imply not merged)
		for(int i=0; i< oriClusters.size(); i++)
		{
			isMergedVector.add(i, false);
		}
		
		//	begin to merge
		for(int i=0; i<oriClusters.size(); i++)
		{
//			System.out.println("ori/merged: <"+ oriClusters.size()+ "/"+ mergedClusters.size()+ ">");
			//	check whether cluster i is already merged or not
			if(isMergedVector.get(i))
			{
				//	i already merged, skipped
				continue;
			}
			//	initial top overlap information
			topOverlapIdx = -1;
			topOverlapRatio = 0.0;
			topOverlapIsMerged = false;
			//	first, find the top overlapped cluster with respect to cluster i in non-merged clusters in oriClusters (always find the later one (index large than i))
			for(int j=i+1; j<oriClusters.size(); j++)
			{
				//	check whether cluster j is merged or not
				if(isMergedVector.get(j))
				{
					//	cluster j already merged, skipped (will check the merged cluster in the next for loop 
					continue;
				}
				//	check overlap ratio and rank
				currOverlapRatio = calculateOverlap(oriClusters.get(i), oriClusters.get(j));
//				System.out.println("topOverlap/currOverlap: <"+ topOverlapRatio+ "/"+ currOverlapRatio+ ">");
//				if( (currOverlapRatio >= overlapThreshold) && ((-1 == topOverlapIdx) || (currOverlapRatio > topOverlapRatio)) )
				if( (currOverlapRatio > topOverlapRatio) && (currOverlapRatio >= overlapThreshold) )
				{
					//	cluster j is the new top overlapping cluster for cluster i
					isMergedVector.set(i, true);
					isMergedVector.set(j, true);
					if(-1 != topOverlapIdx)
					{
						//	reset previous top overlap cluster's merge status
						isMergedVector.set(topOverlapIdx, false);
					}
					topOverlapIdx = j;	//	index of original cluster
					topOverlapRatio = currOverlapRatio;
				}
			}
//			System.out.println("isMerged: <"+ topOverlapIsMerged+ "> topRatio: <"+ topOverlapRatio+ "> with idx: <"+ topOverlapIdx+ ">");
			//	second, find the top overlapped cluster with respect to cluster i in merged clusters in mergedClusters
			for(int j=0; j< mergedClusters.size(); j++)
			{
				//	check overlap ratio and rank
				currOverlapRatio = calculateOverlap(oriClusters.get(i), mergedClusters.get(j));
//				if( (currOverlapRatio >= overlapThreshold) && ((-1 == topOverlapIdx) || (currOverlapRatio > topOverlapRatio)) )
				if(  (currOverlapRatio > topOverlapRatio) && (currOverlapRatio >= overlapThreshold) )
				{
					//	merged cluster j is the new top overlapping cluster for cluster i
					isMergedVector.set(i, true);
					if(-1 != topOverlapIdx && topOverlapIsMerged == false)
					{
						//	reset previous non-merged top overlap cluster's merge status
						isMergedVector.set(topOverlapIdx, false);
						topOverlapIsMerged = true;
					}
					topOverlapIdx = j;	//	index of merged cluster
					topOverlapRatio = currOverlapRatio;
				}
			}
//			System.out.println("isMerged: <"+ topOverlapIsMerged+ "> topRatio: <"+ topOverlapRatio+ "> with idx: <"+ topOverlapIdx+ ">");
			//	merge cluster i with topOverlap cluster if required and add it into merged clusters list
			//	check whether cluster i is mergeable or not
			if(isMergedVector.get(i))
			{
				//	check whether cluster i will merged with already merged cluster or not
				if(topOverlapIsMerged == false)
				{
//					System.out.print("merged with non-merged case, merged size before/after: <"+ mergedClusters.size()+ "/");					
					//	cluster i could be merged with topOverlap cluster (non-merged case)
					mergedClusters.add(doMerge(oriClusters.get(i), oriClusters.get(topOverlapIdx)));
//					System.out.println(mergedClusters.size()+ ">");
				}
				else
				{
//					System.out.println("top: <"+ topOverlapIdx+ ">");
//					System.out.print("merged with merged case, merged size before/after: <"+ mergedClusters.size()+ "/");
					//	cluster i could be merged with topOverlap cluster (merged case)
					mergedClusters.add(doMerge(oriClusters.get(i), mergedClusters.get(topOverlapIdx)));
					mergedClusters.remove(topOverlapIdx);
//					System.out.println(mergedClusters.size()+ ">");
				}
			}
			else
			{
//				System.out.print("not merge case, merged size before/after: <"+ mergedClusters.size()+ "/");		
				//	cluster i is stand alone with respect to overlap threshold
//				mergedClusters.add(oriClusters.get(i));
				aloneClusterIdxs.add(i);		//	leave the unmergable cluster to the remaining list, not participate with the for loop
				isMergedVector.set(i, true);	//	stick out of the checking list of mergeable cluster
//				System.out.println(mergedClusters.size()+ ">");
			}
		}
		//	add all the stand alone cluster into merged cluster (not consider merged change situation)
		//	unmergeable cluster may be mergeable after some little overlapped clusters' merged, in this situation the mergedPhrase requires to be running multiple times
		for(Integer alone : aloneClusterIdxs)
		{
			mergedClusters.add(oriClusters.get(alone));
		}
		
//		System.out.println("after mergePhrase totally: <"+ mergedClusters.size()+ "> clusters");
		return mergedClusters;
	}	
	/**
	 * Perform clustering by Random-walk-with-restart approach (implementation of paper 2011 9781457706530/11)
	 * @param graph - graph to be clustering (with nodes and edges)
	 * @return list of clusters in NodeCluster form
	 */
	public List<NodeCluster> doClustering(Graph graph)
	{
		List<NodeCluster> clusters = new ArrayList<>();
		Matrix adjacencyMatrix = null;
		this.nodes = graph.getNodes();
		this.edges = graph.getEdges();
		Matrix restartVector = new Matrix(nodes.size(), 1);	//	n by 1 restart vector
		Matrix transitionVector = new Matrix(nodes.size(), 1);	//	n by 1 transition vector
		Matrix preTransitionVector = new Matrix(nodes.size(), 1);	//	previous transition vector
		List<NodeCluster> preliminaryClusters = new ArrayList<>();
		// create adjacency matrix
		adjacencyMatrix = toAdjacencyMatrix(nodes, edges);
		
		//	perform column-normalization to adjacency matrix
		adjacencyMatrix.columnNormalize();
		
		//	start random walks with restart
		for(int i=0; i<nodes.size(); i++)
		{
			//	initialize restart vector
			for(int j=0; j<nodes.size(); j++)
			{
				if(i==j)
				{
					restartVector.setValue(j, 0, 1.0);
				}
				else
				{
					restartVector.setValue(j, 0, 0.0);
				}				
			}
			
			//	initialize transition vector
			transitionVector = restartVector.copyMatrix();
			
			//	begin converging
			do
			{
				//	keep current converge status
				preTransitionVector = transitionVector.copyMatrix();
				
				//	matrix multiplication (RWR score calculation)
				transitionVector = adjacencyMatrix.times(transitionVector)
								 .times(1-restartProbability)
								 .plus(restartVector.times(restartProbability));
//				transitionVector = adjacencyMatrix.transpose()
//								 .times(1-restartProbability)
//								 .times(transitionVector)
//								 .plus(restartVector.times(restartProbability));
			} while (!isConverged(preTransitionVector, transitionVector));
			
			//	create the list of preliminary clusters
			preliminaryClusters.add(getPreliminaryCluster(nodes.get(i), transitionVector));			
		}
		
		//	merge over-overlapped clusters
		clusters = mergePhrase(preliminaryClusters);
		while (!isWellClustered(clusters))
		{
			clusters = mergePhrase(clusters);
		}
		return clusters;
	}
	
	public static void main(String[] args)
	{
		
		Long idxGraphID = 0L;
		Long idxEdgeID = 0L;
		Long idxClusterID = 0L;
		Config config = new Config();
		
		GraphGenerator graphGenerator = new GraphGenerator(idxGraphID, idxEdgeID);
		
		//Graph graph = graphGenerator.loadDB();
		Graph graph = graphGenerator.inputData(config.getNode_path(), config.getEdge_path());
		
		RWRClustering rwrClustering = new RWRClustering(idxClusterID);
		rwrClustering.setMajorNodeThreshold(config.getMajorNodeThreshold());
		rwrClustering.setOverlapThreadshold(config.getOverlapThreadshold());
		rwrClustering.setPreliminaryThreshold(config.getPreliminaryThreshold());
		rwrClustering.setRestartProbability(config.getRestartProbability());
		
		long startTime = System.currentTimeMillis();
		
		List<NodeCluster> nodeClusters = rwrClustering.doClustering(graph);
		
		long endTime = System.currentTimeMillis();
		long count = 0;
		
		Collections.sort(nodeClusters, new Comparator<NodeCluster>()
		{
			@Override
			public int compare(NodeCluster o1, NodeCluster o2)
			{
				return Integer.compare(o1.getContainNodeIDs().size(), o2.getContainNodeIDs().size());
			}
		});
		
		
		for(NodeCluster nodeCluster : nodeClusters)
		{
			System.out.println("\nclusterID: <"+ nodeCluster.getClusterID()+ ">");
			System.out.print("\t");
			List<Long> nodeIDs = nodeCluster.getContainNodeIDs();
			Collections.sort(nodeIDs);
			for(Long nodeID : nodeCluster.getContainNodeIDs())
			{
//				System.out.println("\tnode: <"+ nodeID+ ">");
				System.out.print(nodeID+ ", ");
				count++;
			}
		}
		System.out.println("total number of cluster: <"+ nodeClusters.size()+ ">");
		System.out.println("total number of nodes: <"+ count+ "> with overlapping ratio: <"+ ((float)count/1010)+ ">");
		System.out.println("time cost: <"+ (float)(endTime - startTime)/1000+ "> sec");
	}
	
}

