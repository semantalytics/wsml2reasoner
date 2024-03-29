<% 
  boolean inFrame=false;
  if (request.getParameter("inframe")!=null){ 
    inFrame=true;
  }
  String eval=request.getParameter("eval");
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

ontology AboutHumans

concept Human
  hasRelative symmetric ofType Human
  hasChild ofType Child

concept Man subConceptOf Human
  hasWife inverseOf(hasHusband) ofType Woman

concept Woman subConceptOf Human
  hasHusbund ofType Man

concept Child subConceptOf Human

instance Homer memberOf Man
  hasWife hasValue Marge

instance Marge memberOf Woman
  hasChild hasValue Lisa

instance Lisa
  ageInYears hasValue 12

axiom aChild definedBy
  ?child[ageInYears hasValue ?age] and ?age<16 implies ?child memberOf
Child .

axiom aRelative definedBy
  ?x[hasWife hasValue ?y] implies ?x[hasSpouse hasValue ?y].
  ?x[hasHusband hasValue ?y] implies ?x[hasSpouse hasValue ?y].
  ?x[hasSpouse hasValue ?y] implies ?x[hasRelative hasValue ?y].
  ?x[hasChild hasValue ?y] implies ?x[hasRelative hasValue ?y].
        </textarea>
        </td>
      </tr>
     <tr>
          <td>Ontology URL:</td>
          <td><input type="text" size="80" name="url" value="[url]"></td>
     </tr>
       <tr><td colspan="2"></td></tr>
       <%if (eval!=null) {%>
      <tr>
         <td colspan="2">Evaluation Method:
         <input type="text" size="10" name="eval" value="<%=eval %>"/>
         &nbsp; &nbsp; Reasoner: <select name="reasoner">
         <option id="mins" value="mins"/><option id="kaon" value="kaon"/>
         </select></td>
      </tr>
      <%} %>       
      <tr>
         <td>Conjunctive Query:</td>
         <td><input type="text" size="80" name="wsmlQuery" value="?wife[hasHusband hasValue ?husband]"/>
         <input class="button" type="submit" value="go">
         </td>
      </tr>
  </table>
<%if (inFrame){%>
<input type="hidden" name="inframe" value="true"/>
  <% if (eval!=null){ %>
    <input type="hidden" name="eval" value="<%=eval %>"/>
  <%}%>
<%}%>
</form>


<%if (!inFrame) {%>
  <p>&nbsp;</p>
  <div style="font-size:smaller">
  <p>This reasoner is at present able to process WSML-Rule Ontologies, with the following limitations:</p>
  <ul>
  <li> only simple datatypes are supported (int, decimal, string).</li>
  </ul>
  <p> The reasoner is based on the following components:</P>
  <ul>
    <li><a href="http://wsmo4j.sourceforge.net/">WSMO4J</a>, the WSMO API and reference implementation.</li>
    <li><a href="http://dev1.deri.at/wsml2reasoner/">WSML 2 Reasoner</a> framework, for handling the translation to the underlying reasoner.</li>
    <li><a href="http://dev1.deri.at/mins/">MINS</a> as underlying reasoner engine.</li>
  </ul>
  </div>
  
  <p>This service is also accessible as <a href="services/reasoner?wsdl">Web Service</a>. <br/>
  <small>The current interface might be changed without notice, so
  please drop us a <a href="mailto:holger.lausen@deri.org">line</a> if you use it.</small></p>
<%}%>
  </div>
 
<p><small><a href="history.html">Version History</a> | <a href="frame.jsp">Display Using Frames</a> | <a target="_top" href="index.jsp">No Frames</a> </small> </p>
<p><small>$Date: 2006-04-19 08:30:16 $</small>
 
</script> 
</body>
</html>
