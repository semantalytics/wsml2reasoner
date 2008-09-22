<?php

$selfdir = '/wsml2reasoner/';
$baseURI = 'http://tools.deri.org/';
$location = 'pages/';

$selffilename = str_replace( 'index.php', '', $_SERVER['SCRIPT_FILENAME'] );

if( $_SERVER['REQUEST_URI'] == $selfdir ) // index.php should not be callable to make it independend of how it is implemented (asp/html/php/...): || $_SERVER['REQUEST_URI'] == $selfdir . 'index.php' )
    $_SERVER['REQUEST_URI'] = $selfdir . 'home';
$URL = str_replace("?".$_SERVER['QUERY_STRING'],"",$_SERVER['REQUEST_URI']);
$filelocation = str_replace( $selfdir, $selffilename . $location .'/', $URL ) . '.php';

if( !file_exists( $filelocation ) ) {
    header('HTTP/1.1 404 Not Found');
    header("Status: 404 Not Found");

    echo "<h1>Not Found</h1><p>The requested URL " . $_SERVER['REQUEST_URI'] . " was not found on this server.</p><hr />";
    exit;
}

?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
  <head>
    <meta http-equiv="content-type" content="application/xhtml+xml; charset=utf-8" />
    <link rel="stylesheet" type="text/css" href="css/bluehaze.css" />
    <link rel="stylesheet" type="text/css" href="css/color-scheme.css" />
    <title>WSML2Reasoner</title>
  </head>

  <body>
    <div id="top"></div>
  
    <!-- ###### Header ###### -->

    <div id="header">
      <a href="/wsml2reasoner/" style="text-decoration:none" class="headerTitle"><img src="/wsml2reasoner/images/wsmlThinker.png" alt="wsml-thinker" vspace="5" align="absmiddle" /> WSML2Reasoner</a>
     <!-- <div class="menuBar">
        <a href="download">download</a>|
        <a href="source">source code</a>
      </div>-->
    </div>

    <!-- ###### Side Boxes ###### -->

    <div class="sideBox LHS">
      <div>overview</div>
        <a href="user_guide">documentation</a>
        <a href="download">download</a>
        <a href="contact">contact</a>
        <a href="publications">publications</a>
        <a href="license">license</a>
        <a href="team">team</a>
    </div>

    <div class="sideBox LHS">
      <div>nightly build</div>
		<a href="nightly_build">overview</a>
        <a href="snapshot/javadoc/">java doc</a>
        <a href="snapshot/report/unit_test">unit test report</a>
        <a href="snapshot/report/functional_test">functional test report</a>
		<!--
        <a href="source">source code</a>
		-->
    </div>
    
    <div class="sideBox LHS">
      <div>services</div>
		<a href="http://tools.sti-innsbruck.at/wsml/rule-reasoner/">online demo</a>
		<a href="services">web service</a>
    </div>
    
     <!-- ###### Body Text ###### -->

    <div id="bodyText">
<?php
	include( $filelocation );

?>
  </div>  
    
    <!-- ###### Footer ###### -->
 <!--
    <div id="footer"><center>
    <SCRIPT type='text/javascript' language='JavaScript' src='http://www.ohloh.net/projects/6238;badge_js'></SCRIPT></center>
    </div>
	-->
  </body>
</html> 
