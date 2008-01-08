<h1>Nightly Build</h1>

<p>We are running an automated build process once a day. All generated
resources are available in the <a href="snapshot">snapshot</a> directory.</p>

<p>For your convenience the build process includes:</p>
<ul>
<!--
	<li><a href="/snapshot/javadoc">java doc</a></li>
	<li><a href="/snapshot/junit_report">test results</a></li>
-->
	<li>The files available in the snapshot are:<br><br>
	<div style="width:50em; background-color:#eee;border:1px black solid;">
	
	<?
		$lines = file('http://tools.deri.org/wsml2reasoner/snapshot/');
		// Loop through our array, show HTML source as HTML source; and line numbers too.
		$content=false;
		foreach ($lines as $line_num => $line) {
    		//echo "Line #<b>{$line_num}</b> : " . htmlspecialchars($line) . "<br />\n";
			if (str_contains($line,"<table>")) $content=true;
			if (str_contains($line,"<address>")) $content=false;
			if ($content) {
			 	echo ereg_replace("href=\"/?([^/][^\"]+)", "href=\"/snapshot/\\1", $line);	
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
