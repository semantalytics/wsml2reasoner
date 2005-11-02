<% 
  boolean inFrame=false;
  if (request.getParameter("inframe")!=null){ 
    inFrame=true;
  }
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
  <title>WSML Reasoner</title>
  <link rel="shortcut icon" href="favicon.ico"/>
  <link rel='stylesheet' type='text/css' href='wsml.css'/>
</head>
<body>
<div class="box">
<%if (!inFrame){%>
<h1>WSML Reasoner</h1>
  <p>Enter a WSML ontology either by pasting it into the text area below 
  or enter its URL. Pressing the &lt;go&gt; button will then display the variable 
  bindings for the query you entered.
  </p>
<%}%>
<form action="reasoner" 
<%
  if(inFrame) out.println("target=\"answer\"");
  else out.println("target=\"_self\"");
%>
method="post">
  <table border="0">
    <tr>
      <td style="vertical-align: top;">WSML Ontology:</td>
      <td style="vertical-align: top;">
      <textarea  class="c#:nogutter:nocontrols"
      style="font:normal 10pt Arial;" name="wsmlOntology" cols="100" 
      rows="20">namespace {_"http://www.example.org#"}

ontology TheSimpsons

concept Human
  hasRelative transitive symmetric impliesType Human

concept Man subConceptOf Human

concept Woman subConceptOf Human

instance Homer memberOf Man
  hasRelative hasValue Marge
  
instance Marge memberOf Woman
  hasRelative hasValue Lisa
  
        </textarea>
        </td>
      </tr>
     <tr>
          <td>Ontology URL:</td>
          <td><input type="text" size="80" name="url" value="[url]"></td>
     </tr>
       <tr><td colspan="2"></td></tr>
      <tr>
         <td>Conjunctive Query:</td>
         <td><input type="text" size="80" name="wsmlQuery" value="?person [hasRelative hasValue ?relative] memberOf Human"/>
         <input class="button" type="submit" value="go">
         </td>
      </tr>
  </table>

</form>


<%if (!inFrame) {%>
  <p>&nbsp;</p>
  <div style="font-size:smaller">
  <p>This reasoner is at present able to process WSML-Flight Ontologies, with the following limitations:</p>
  <ul>
  <li> constraints are not processed.</li>
  <li> datatype predicates are not processed.</li>
  </ul>
  <p> The reasoner is based on the following components:</P>
  <ul>
    <li><a href="http://wsmo4j.sourceforge.net/">WSMO4J</a>, the WSMO API and reference implementation.</li>
    <li><a href="http://dev1.deri.at/wsml2reasoner/">WSML 2 Reasoner</a> framework, for handling the translation to the underlying reasoner.</li>
    <li><a href="http://cvs.deri.at/cgi-bin/viewcvs.cgi/mins/">MINS</a> as underlying reasoner engine.</li>
  </ul>
  </div>
<%}%>
  </div>
 
<p><small><a href="history.html">Version History</a> | <a href="frame.html">Display Using Frames</a> | <a target="_top" href="index.jsp">No Frames</a> </small> </p>
<p><small>$Date: 2005-11-02 08:54:42 $</small>

</script> 
</body>
</html>
