package Common.generator;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import Generator.Util.AbstractGenerator;
import Generator.Util.GeneratorFactory;
import Generator.Util.OutDegreeGenerator;

/**
 * Generate a network according to edge copying model
 * 
 * @author Chengcheng Yu
 */
public class CopyingModelGraphGenerator {
	private double _beta;
	private int _nodeNum;
	private Map<Integer, List<Integer>> _graph;
	OutDegreeGenerator _outDGen;
	BufferedWriter outfile;

	public CopyingModelGraphGenerator(int nodeNum, double beta, String filePath,
			String outPath_patent) throws IOException {
		// TODO Auto-generated constructor stub
		outfile = new BufferedWriter(new FileWriter(outPath_patent));
		this._beta = beta;
		this._nodeNum = nodeNum;
		this._graph = new TreeMap<Integer, List<Integer>>();
		AbstractGenerator genFactory = new GeneratorFactory();
		this._outDGen = genFactory.createOutDegreeGenerator(filePath);
	}

	public void generateGraph() throws IOException {
		for (int n = 0; n < _nodeNum; n++) {
			List<Integer> li = getLinks(n);
			this._graph.put(n, li);
			for (Integer key : li) {
				outfile.write(n + "\t" + key);
				outfile.newLine();
				System.out.println(n + "\t" + key);
			}
		}
		outfile.flush();
		outfile.close();
	}

	private List<Integer> getLinks(int n) {
		List<Integer> result = new ArrayList<Integer>();
		if (n == 0) {
			result.add(0);
		} else {
			int size = _outDGen.nextValue(n + 1);
			if (size > 0) {
				if (Math.random() < _beta) {
					if (size >= n) {
						result.addAll(_graph.keySet());
					} else {
						Random ran = new Random();
						while (result.size() < size) {
							Integer it =ran.nextInt(n);
							if(!result.contains(it))
								result.add(it);
						}
					}
				} else {
					Set<Integer> chosen = new HashSet<Integer>();
					Random ran1 = new Random();
					Integer index;
					while (chosen.size() < this._graph.keySet().size() & result.size() < size) {
						index = ran1.nextInt(this._graph.keySet().size());
						if (!chosen.contains(index)) {
							chosen.add(index);
							List<Integer> candi = _graph.get(index);
							DURCollectionGenerator<Integer> duriGen = new DURCollectionGenerator<Integer>(
									candi);
							while (result.size() < size & duriGen.nextValue() != null) {
								if(!result.contains(duriGen.lastValue()))
									result.add(duriGen.lastValue());
							}
						}
					}
				}
			}
		}

		return result;

	}

	private void addEdge(int id1, int id2) {

		if (_graph.containsKey(id1)) {
			_graph.get(id1).add(id2);
		} else {
			List<Integer> ls = new ArrayList<Integer>();
			ls.add(id2);
			_graph.put(id1, ls);
		}
		System.out.println(id1 + "\t" + id2);
	}

	public static void main(String[] args) throws Exception {

		int num = 180000;

		// double rIN_weibo = 1.4667;
		// double beta_weibo = (rIN_weibo - 1) / rIN_weibo;
		// String outPath_weibo =
		// "/Users/ycc/kuaipan/MyWork/data(weibo&patent)/weibo/weibo_" + num
		// + ".txt";
		// String path_weibo =
		// "/Users/ycc/kuaipan/MyWork/data(weibo&patent)/weibo/IndegreeDist(weibo).txt";

		double rIN_patent = 2.13;
		double beta_patent = (rIN_patent - 1) / rIN_patent;
		String outPath_patent = "/Users/ycc/kuaipan/data/patent/source_data/patent_" + num + ".txt";
		String path_patent = "/Users/ycc/kuaipan/data/patent/source_data/"
				+ "patentProducerNetwork/outDeg.graph.tab.txt";

		CopyingModelGraphGenerator cmgg_patent = new CopyingModelGraphGenerator(num, beta_patent,
				path_patent, outPath_patent);
		cmgg_patent.generateGraph();
		// cmgg_patent.outputGraph(outPath_patent);

	}

}
