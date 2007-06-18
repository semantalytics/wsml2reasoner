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

public class Chart {
	public static void main(String[] str) throws IOException {
		String base = "performance/performance/results/";
		File f = new File(base);
		File[] dirs = f.listFiles();
		for (File dir:dirs){
			if (dir.isDirectory()){
				doAllCharts(dir);
			}
		}
		//make and index
		StringBuffer content = new StringBuffer();
		FileWriter fw = new FileWriter(base+"index.html");
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
	
	static void doAllCharts(File dir) throws IOException{
		FileWriter fw = new FileWriter(dir.getAbsolutePath()+"/index.html");
		fw.append(HEAD+"<h1>"+dir+"</h1>");
		fw.append("\n"+STARTCONTENT+"\n");
		for (File csv : dir.listFiles(csvfilter)){
			CategoryDataset dataset = createDataset(csv);
			String name = dir.toString().replaceAll("/[^/]+", "");
			name = name.substring(name.lastIndexOf('\\')+1);
			if (csv.toString().contains("query")){
				name += " Query Times";
			}else{
				name += " Registration Times";
			}
			System.out.println("processing: "+ name);
			JFreeChart chart = createChart(name,dataset);
			ChartUtilities.saveChartAsJPEG(
					new File(csv.getAbsolutePath()+"-chart.jpg"), 
					chart, 500, 300);
			fw.append("<h2>"+name+"</h2>\n");
			fw.append(createTable(dataset)+"\n");
			fw.append("<img src=\""+csv.getName()+"-chart.jpg"+"\"><br>\n");

			
		}
		fw.append("\n"+ENDCONTENT+"\n");
		fw.append(TAIL);
		fw.flush();
		fw.close();
	}
	
	private static String createTable(CategoryDataset data){
		StringBuffer buf = new StringBuffer();
		buf.append("<table><thead><tr><td>&nbsp;</td>\n");
		for (Object row : data.getRowKeys()){
			buf.append("<td>"+row+"</td>\n");
		}
		buf.append("</tr></thead>\n");
		for (Object col : data.getColumnKeys()){
			buf.append("<tr><td><b>"+col+"</b></td>\n");
			for (Object row : data.getRowKeys()){
				buf.append("<td>\n"+data.getValue(row.toString(), col.toString())+"</td>");
			}
			buf.append("</tr>\n");
		}
		buf.append("</table>\n");
		
		return buf.toString();
	}
	
	private static JFreeChart createChart(String name, CategoryDataset data) throws IOException {
		JFreeChart chart = ChartFactory
				.createBarChart3D(
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
	
	private static CategoryDataset createDataset(File csv)
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
				dataset.addValue((int)time, head[i], reasoner);
			}
		}
		
		return dataset;
	}
	
	
	static FilenameFilter csvfilter = new FilenameFilter(){
		public boolean accept(File dir, String name) {
			return name.endsWith("csv");
		}
	};
	static FilenameFilter indexhtmlfilter = new FilenameFilter(){
		public boolean accept(File dir, String name) {
			return name.equals("index.html");
		}
	};

}
