package performance.chart;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.omwg.ontology.*;
import org.wsmo.common.Entity;
import org.wsmo.common.IRI;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.Factory;
import org.wsmo.factory.WsmoFactory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.ParserException;

import performance.BenchmarkOntologyGenerator;

public class Chart {
	
	public static void main(String[] str) throws IOException, ParserException, InvalidModelException {
		Chart c = new Chart();
		
		c.doChartsFromCSV(new File("performance/performance/results/subconcept"));
		
		System.exit(0);
		String base = "performance/performance/results/";
		File f = new File(base);
		File[] dirs = f.listFiles();
		for (File dir:dirs){
			if (dir.isDirectory()){
				c.doChartsFromCSV(dir);
			}
		}
		//make and index
		c.writeGlobalIndex(base);
	}
	
	String replaceLineBreak(String str){
		if (str==null) return "";
		return str.replace("\n", "<br/>");
	}
	
	public void  writeGlobalIndex(String base) throws IOException{
		StringBuffer content = new StringBuffer();
		FileWriter fw = new FileWriter(base+"index.html");
		File[] dirs = new File(base).listFiles();
		fw.append(HEAD.replace("../", ""));
		fw.append("<h1>Single Reports</h1>");
		fw.append("<ul>");
		for (File dir:dirs){
			if (dir.isDirectory()){
				fw.append("<li><a href=\""+dir.getName()+"/index.html\">"+dir.getName()+"</a></li>");
				for(File html:dir.listFiles(indexhtmlfilter)){
					BufferedReader br = new BufferedReader(new FileReader(html));
					boolean inContent=false;
					while (br.ready()){
						String line = br.readLine();
						line = line.replaceAll("<img src=\"", "<img src=\""+dir.getName()+"/");
						if (line.equals(ENDCONTENT)) inContent=false;
						if (inContent) content.append(line);
						if (line.equals(STARTCONTENT)) inContent=true;
					}
				}
			}
		}
		fw.append("</ul>");
		fw.append("<h1>All Reports</h1>");
		fw.append(content.toString());
		fw.append(TAIL);
		fw.close();
	}
	
	
	final static String HEAD="<html><link rel=\"stylesheet\" type=\"text/css\" href=\"../style.css\"><body>";
	final static String TAIL="</body></html>";
	final static String ENDCONTENT="<!--enddata--->";
	final static String STARTCONTENT="<!--data--->";
	
	WsmoFactory wsmoFactory = Factory.createWsmoFactory(null);
	String firstNfpAsString(Entity e,String key){
		IRI i = wsmoFactory.createIRI(key);
		Set<Object> list = e.listNFPValues(i);
		return list.iterator().next().toString();
	}
	
	Parser p = Factory.createParser(null);
	TestInfo getTestInfo(File dir) throws FileNotFoundException, IOException, ParserException, InvalidModelException{
		TestInfo ret = new TestInfo();
		File[] files = dir.listFiles(wsmlfilter);
		for (File file:files){
			Ontology o = (Ontology) p.parse(new FileReader(file))[0];
			if (ret.id==null){
				ret.id = o.getIdentifier().toString();
				ret.title=firstNfpAsString(o, BenchmarkOntologyGenerator.DC_TITLE);
				ret.description=firstNfpAsString(o, BenchmarkOntologyGenerator.DC_DESCRIPTION);
		//		System.out.println(ret.title);
				for (RelationInstance r:o.listRelationInstances()){
					TestQueryInfo i = new TestQueryInfo();
					i.id=((IRI)r.getRelation().getIdentifier()).getLocalName();
					i.title=firstNfpAsString(r, BenchmarkOntologyGenerator.DC_TITLE);
					i.description=firstNfpAsString(r, BenchmarkOntologyGenerator.DC_DESCRIPTION);
					i.query=r.getParameterValue((byte)0).toString();
					ret.query.add(i);
				}
			}
			ret.conceptsAmount.add(firstNfpAsString(o, BenchmarkOntologyGenerator.BM_CONCEPTS));
			ret.instancesAmount.add(firstNfpAsString(o, BenchmarkOntologyGenerator.BM_INSTANCES));
			ret.attributesAmount.add(firstNfpAsString(o, BenchmarkOntologyGenerator.BM_ATTRIBUTES));
			ret.totalTermAmount.add(firstNfpAsString(o, BenchmarkOntologyGenerator.BM_TOTAL));
			ret.replicated.add(firstNfpAsString(o, BenchmarkOntologyGenerator.BM_REPLICATED));
		}
		return ret;
	}
	
	TestQueryInfo findMatchingQueryInfo(TestInfo testInfo, File csv){
		String fileName = csv.getName();
		for (TestQueryInfo i : testInfo.query){
			System.out.println("checking "+fileName+" against id "+i.id);
			if (fileName.contains(i.id)) return i;
		}
		return null;
	}
	
	public void doChartsFromCSV(File dir) throws IOException, ParserException, InvalidModelException{
		FileWriter fw = new FileWriter(dir.getAbsolutePath()+"/index.html");
		
		TestInfo testInfo = getTestInfo(dir);
		fw.append(HEAD);
		fw.append("\n"+STARTCONTENT+"\n");
		fw.append("<h1>"+testInfo.title+"</h1>");
		fw.append("<p>"+replaceLineBreak(testInfo.description)+"</p>");
		fw.append("<p><a href=\"log.txt\">log</a></p>");
		fw.append("<p><a href=\"resultLog.txt\">results</a></p>");
		
		for (File csv : dir.listFiles(csvfilter)){
			CategoryDataset dataset = createDataset(csv,testInfo);
			TestQueryInfo testQueryInfo = findMatchingQueryInfo(testInfo,csv);
			// is either normalization, convertion, consistency check or total registration time
			if (testQueryInfo == null){ 
				if (csv.getName().contains("normalization")) {
					testQueryInfo = new TestQueryInfo();
					testQueryInfo.title="Normalization Times";
					testQueryInfo.description="";
				}
				else if (csv.getName().contains("convertion")) {
					testQueryInfo = new TestQueryInfo();
					testQueryInfo.title="Convertion Times";
					testQueryInfo.description="";
				}
				else if (csv.getName().contains("consistencyCheck")) {
					testQueryInfo = new TestQueryInfo();
					testQueryInfo.title="Consistency Check Times";
					testQueryInfo.description="";
				}
				else {
					testQueryInfo = new TestQueryInfo();
					testQueryInfo.title="Total registration Times";
					testQueryInfo.description="";
				}
			}
			String name = testQueryInfo.title;
			System.out.println("processing: "+ name);
			JFreeChart chart = createChart(name,dataset);
			ChartUtilities.saveChartAsJPEG(
					new File(csv.getAbsolutePath()+"-chart.jpg"), 
					chart, 650, 350);
			fw.append("<h2>"+name+"</h2>\n");
			fw.append("<p>"+testQueryInfo.description+"</p>");
			if (testQueryInfo.query!=null)
				fw.append("<b>"+testQueryInfo.query+"</b>");
			fw.append(createTable(dataset,dir,testInfo)+"\n");
			fw.append("<img src=\""+csv.getName()+"-chart.jpg"+"\"><br>\n");

			
		}
		fw.append("\n"+ENDCONTENT+"\n");
		fw.append(TAIL);
		fw.flush();
		fw.close();
	}
	
	String findMatchingFileName(String col, File dir){
		col = col.substring(2);
		for (File f : dir.listFiles(wsmlfilter)){
			System.out.println("checking "+f.getName()+" against "+col);
			if (f.getName().contains(col)) return f.getName();
		}
		return null;
	}
	
	private String createTable(CategoryDataset data, File dir, TestInfo info){
		StringBuffer buf = new StringBuffer();
		buf.append("<table><thead><tr><td>&nbsp;</td>\n");
		for (Object row : data.getRowKeys()){
			buf.append("<td>"+row+"</td>\n");
		}
		buf.append("<td>&nbps;</td>");
		buf.append("<td>concepts</td>");
		buf.append("<td>instances</td>");
		buf.append("<td>attributes</td>");
		buf.append("<td>terms</td>");
		buf.append("</tr></thead>\n");
		int i=0;
		for (Object col : data.getColumnKeys()){
			String link = findMatchingFileName(col.toString(),dir);
			if (link==null){
				link=col.toString();
			}else{
				link="<a href=\""+link+"\">"+info.replicated.get(i)+"</a>";
			}
			buf.append("<tr><td><b>"+link+"</b></td>\n");
			for (Object row : data.getRowKeys()){
				buf.append("<td>");
				Object value = data.getValue(row.toString(), col.toString());
				if (value!=null) buf.append(((Double)value).intValue());
				buf.append("</td>");
			}
			buf.append("<td>&nbsp;</td>");
			if (info.conceptsAmount.size()>i){
				buf.append("<td>"+info.conceptsAmount.get(i)+"</td>");
				buf.append("<td>"+info.instancesAmount.get(i)+"</td>");
				buf.append("<td>"+info.attributesAmount.get(i)+"</td>");
				buf.append("<td>"+info.totalTermAmount.get(i)+"</td>");
			}
			buf.append("</tr>\n");
			i++;
		}
		buf.append("</table>\n");
		
		return buf.toString();
	}
	
	private JFreeChart createChart(String name, CategoryDataset data) throws IOException {
		JFreeChart chart = ChartFactory
				.createLineChart3D(
						name, // Title
						"", // X-Axis label
						"Time in ms", // Y-Axis label
						data, // Dataset
						PlotOrientation.VERTICAL, // orientation
						true, // Show legend
						true, // tooltips
						false // urls
				);

		BufferedImage image = chart.createBufferedImage(500, 300);
		JLabel lblChart = new JLabel();
		lblChart.setIcon(new ImageIcon(image));
		return chart;
	}
	
	private CategoryDataset createDataset(File csv, TestInfo info)
			throws IOException {
		FileReader fr = new FileReader(csv);
		BufferedReader br = new BufferedReader(fr);
		String[] head = br.readLine().split(",");
		
		
		
		Map<String, String[]> results = new HashMap<String, String[]>();
		while (br.ready()) {
			String[] data = br.readLine().split(",");
			results.put(data[0], data);
		}

		DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		Set<String> reasoners = results.keySet();
		for (String reasoner : reasoners) {
			String[] data = results.get(reasoner);
			for (int i = 1; i < data.length; i++) {
				double time = Double.parseDouble(data[i]);
				int count = Integer.parseInt((head[i].substring(2)));
				if (time>=0){
					dataset.addValue((int)time,reasoner , count+"");
				}
			}
		}
		
		return dataset;
	}
	
	
	FilenameFilter csvfilter = new FilenameFilter(){
		public boolean accept(File dir, String name) {
			return name.endsWith("csv");
		}
	};
	FilenameFilter indexhtmlfilter = new FilenameFilter(){
		public boolean accept(File dir, String name) {
			return name.equals("index.html");
		}
	};
	FilenameFilter wsmlfilter = new FilenameFilter(){
		public boolean accept(File dir, String name) {
			return name.endsWith("wsml");
		}
	};

}

class TestInfo{
	String id;
	String title;
	String description;
	List<TestQueryInfo> query = new ArrayList<TestQueryInfo>();
	List<String> instancesAmount = new ArrayList<String>();
	List<String> attributesAmount = new ArrayList<String>();
	List<String> conceptsAmount = new ArrayList<String>();
	List<String> replicated = new ArrayList<String>();
	List<String> totalTermAmount = new ArrayList<String>();
}

class TestQueryInfo{
	String query;
	String id;
	String title;
	String description;
}
