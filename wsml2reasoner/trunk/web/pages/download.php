<style type="text/css">ul{margin-top:-20px;}</style>


<h1>Download</h1>
<p></p>
<ul>
	<li>Below are the current <b>0.6</b> release variants:<br><br>
	<div style="width:50em; background-color:#eee;border:1px black solid;">
	
	<?
		$lines = file('http://tools.deri.org/wsml2reasoner/releases/v0_6/');
		// Loop through our array, show HTML source as HTML source; and line numbers too.
		$content=false;
		foreach ($lines as $line_num => $line) {
    		//echo "Line #<b>{$line_num}</b> : " . htmlspecialchars($line) . "<br />\n";
			if (str_contains($line,"<table>")) $content=true;
			if (str_contains($line,"<address>")) $content=false;
			if ($content) {
			 	echo ereg_replace("href=\"/?([^/][^\"]+)", "href=\"/wsml2reasoner/releases/v0_6\\1", $line);	
			}
		}
		
		//foreach ($lines as $line_num => $line) {
    	//	echo "Line #<b>{$line_num}</b> : " . htmlspecialchars($line) . "<br />\n";
		//	echo strpos($line,"<table>")."<br/>";
		//}
		
		
function str_contains($haystack, $needle, $ignoreCase = false) {
    if ($ignoreCase) {
        $haystack = strtolower($haystack);
        $needle   = strtolower($needle);
    }
    $needlePos = strpos($haystack, $needle);
    return ($needlePos === false ? false : ($needlePos+1));
}

	?>
	</div>
	</li>
</ul>

<p>The previous release (v0.5) can be found <a href="releases/v0_5/">here</a></p>


<p>Source code, trackers, and further technical documentation can be found at <a href="http://sourceforge.net/projects/wsml2reasoner/">sourceforge</a>.</p>

<h1>Daily Snapshot</h1>
<p>We also offer a <a href="http://tools.deri.org/wsml2reasoner/snapshot">daily snapshot</a>.
</p>