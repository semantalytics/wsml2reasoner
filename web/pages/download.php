<style type="text/css">ul{margin-top:-20px;}</style>


<h1>Releases</h1>
<div class="p">
<p>
The are four basic release variants in accord to included reasoning engine library license agreements. The actual core WSML2Reasoner code base is LGPL.
<ul>
	<li><a href="http://tools.deri.org/wsml2reasoner/releases/v0_6/wsml2reasoner_lgpl-v0_6.zip">LGPL</a> - This release includes the standard LGPL libraries and packages required for WSML2Reasoner, regardless of which underlying reasoning engine is chosen. In addtion to these core libraries, the IRIS and PELLET libraries are included. A single WSML2Reasoner <a href="http://tools.deri.org/wsml2reasoner/releases/v0_6/wsml2reasoner_lgpl-v0_6.jar">jar</a> (without the necessary libraries) is also provided.  
	</li>
	<li><a href="http://tools.deri.org/wsml2reasoner/releases/v0_6/wsml2reasoner_gpl-v0_6.zip">GPL</a>  - In addition to the LGPL libraries and packages, this release includes the MINS jar. A single WSML2Reasoner <a href="http://tools.deri.org/wsml2reasoner/releases/v0_6/wsml2reasoner_gpl-v0_6.jar">jar</a> (without the necessary libraries) is also provided.</li>
	<li><a href="http://tools.deri.org/wsml2reasoner/releases/v0_6/wsml2reasoner_noncommercial-v0_6.zip">Non-commercial</a> - In addition to the GPL libraries and packages, this release includes the KAON2 libraries and packages, which are available free of charge for universities for noncommecial academic usage (national laboratories are not considered universities). For commercial purposes, there is a commercial version of KAON2 called <a href="http://www.ontoprise.de/ontobrokerowl">OntoBroker OWL</a>. By downloading KAON2, you accept these license agreements.
	</li>
	<li><a href="http://tools.deri.org/wsml2reasoner/releases/v0_6/wsml2reasoner_sdk-v0_6.zip">SDK</a> - This release is an uncompiled version of the entire WSML2Reasoner project. It includes all GPL files plus test files, as well as several other additional libraries that are currently being tested, but have yet to reach a mature, stable level necessary for a proper release. The SDK should also be considered a non-commercial release.
	</li>
</ul>
</p>
</div>
<div>
Below are the current <b>0.6</b> release variants, including source code distributions (_src):<br><br>
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
			 echo ereg_replace("href=\"/?([^/][^\"]+)", "href=\"/wsml2reasoner/releases/v0_6/\\1", $line);	
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
<?
//
//
//
///**
//
// * Change the path to your folder.
//
// *
//
// * This must be the full path from the root of your
//
// * web space. If you're not sure what it is, ask your host.
//
// *
//
// * Name this file index.php and place in the directory.
//
// */
//
//
//
//    // Define the full path to your folder from root
//
//    $path = "/var/www/html/tools_deri_org/wsml2reasoner/";
//
//
//
//    // Open the folder
//
//    $dir_handle = @opendir($path) or die("Unable to open $path");
//
//
//
//    // Loop through the files
//
//    while ($file = readdir($dir_handle)) {
//
//
//
//    if($file == "." || $file == ".." || $file == "download.php" )
//
//
//
//        continue;
//
//        echo "<a href=\"$file\">$file</a><br />";
//
//
//
//    }
//
//
//
//    // Close
//
//    closedir($dir_handle);
//
//
//
//?> 
	</div>
</div>

<p>Previous releases can be found <a href="releases/">here</a>.</p>


<p>Source code, trackers, and further technical documentation can be found at <a href="http://sourceforge.net/projects/wsml2reasoner/">sourceforge</a>.</p>

<h1>Daily Snapshot</h1>
<p>We also offer a <a href="nightly_build">daily snapshot</a>.
</p>