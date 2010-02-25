package performance.chart;

public class IndexEntry {

	private String title;
	private String url;

	public IndexEntry(String theTitle, String theURL) {
		this.title = theTitle;
		this.url = theURL;
	}
	
	public String getTitle() {
		return title;
	}

	public String getUrl() {
		return url;
	}
}