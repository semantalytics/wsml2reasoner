<h1>WSML2Reasoner</h1>
<p>
	WSML2Reasoner is a highly modular framework that combines various validation,
	normalization and transformation algorithms that enable the translation of ontology descriptions
	in WSML to the appropriate syntax of several underlying reasoning engines.
</p>

<h2>WSML</h2>
<p>
	WSML (Web Service Modeling Language) is a family of formal ontology languages that are specifically aimed at describing Semantic Web Services. It's semantics is based on Description Logics, Logic Programming and First-Order Logic, with influences from F-Logic and frame-based representation systems. Conforming to the different influences, there exist five variants of WSML: WSML-Core, WSML-DL, WSML-Flight, WSML-Rule and WSML-Full. Currently, WSML2Reasoner has been implemented and tested using various reasoning engines that support all WSML variants with the exception of WSML-Full. For more infomation on WSML and the related working groups, please refer to the following links:</p>
	<ul>
		<li>Web Service Modeling Language (WSML) - <a href="http://www.wsmo.org/wsml">http://www.wsmo.org/wsml/</a></li>
		<li>WSML Specification - <a href="http://www.wsmo.org/TR/d16/d16.1/">http://www.wsmo.org/TR/d16/d16.1/</a></li>
		<li>Web Service Modeling Ontology (WSMO) - <a href="http://www.wsmo.org/">http://www.wsmo.org/</a></li>
		<li>Web Service Execution Environment (WSMX) - <a href="http://www.wsmx.org/">http://www.wsmx.org/</a></li>
	</ul>
</p>

<p>
	The continued development of WSML2Reasoner has been funded in part by
	<a href="http://www.soa4all.eu/">SOA4All</a>
	a <a href="http://cordis.europa.eu/fp7/home_en.html">European Framework 7</a> research project. 
</p>

<h2>Reasoners</h2>

<p>
	The WSML2Reasoner framework has a flexible architecture that allows the easy integration of
	existing reasoning components.
	It first maps WSML to either generic Datalog or <a href="http://www.w3.org/2004/OWL/">OWL</a> (Web Ontology Language),
	and then uses a plug-in layer that translates to the specific internal representation of a single reasoning engine.
	In the following sections, we briefly discuss a few of the reasoning engines currently
	integrated and tested within the WSML2Reasoner framework.
</p>

<h3>IRIS - (WSML-Core, WSML-Flight, WSML-Rule)</h3>
<p>
	<a href="http://iris-reasoner.org/">IRIS</a>, Integrated Rule Inference System,
	is an extensible reasoning engine for expressive rule-based languages.
	IRIS supports Datalog extended with stratified and well-founded negation, function symbols,
	unsafe-rules, XML schema data-types and an extensible set built-in predicates. 
</p>

<p>
	The IRIS libraries are included in the LGPL <a href="download">release</a>. For further information, please visit the IRIS <a href="http://iris-reasoner.org/">website</a>.
</p>

<h3>KAON2 - (WSML-Core, WSML-Flight, WSML-DL)</h3>
<p>
	<a href="http://kaon2.semanticweb.org/">KAON2</a> is an infrastructure for managing OWL-DL, SWRL, and F-Logic ontologies. It has been integrated into the WSML2Reasoner framework in order to provide reasoning support for the WSML-Core, -Flight, and -DL variants. Contrary to most currently available DL reasoners, such as
	<a href="http://www.cs.man.ac.uk/%7Ehorrocks/FaCT/">FaCT</a>,
	<a href="http://owl.man.ac.uk/factplusplus/">FaCT++</a>,
	<a href="http://www.racer-systems.com/">RACER</a>,
	<a href="http://www.bell-labs.com/user/pfps/dlp/">DLP</a> or
	<a href="http://pellet.owldl.com/">Pellet</a>,
	KAON2 does not implement the tableaux calculus. Rather, reasoning in KAON2 is implemented by
	novel algorithms which reduce a SHIQ(D) knowledge base to a disjunctive datalog program.
	For an overview of these algorithms, please refer to <a href="http://kaon2.semanticweb.org/#kr-paper"><em>Reducing SHIQ- Descrption Logic to Disjunctive Datalog Programs</em></a>.
	A detailed (and quite lengthy) technical presentation of all algorithms is given in <a href="http://kaon2.semanticweb.org/#PhD-thesis"><em>Reasoning in Description Logics using Resolution and Deductive Databases</em></a>.
</p>

<p>
	KAON2 is is free of charge for universities for noncommecial academic usage (national laboratories are not considered universities). For commercial purposes, there is a commercial version of KAON2 called <a href="http://www.ontoprise.de/ontobrokerowl">OntoBroker OWL</a>. By downloading KAON2 (which is included in the WSML2Reasoner noncommercial <a href="download">release</a>), you accept these license agreements.
</p>

<p>
	For developers, specifically those using the WSML2Reasoner SDK release, the necessary KAON2 libs can be retrieved using the <b>get-kaon2-jars-with-confirmation</b> ANT target found in the build.xml included in the release.  For further information, please visit the KAON2 <a href="http://kaon2.semanticweb.org/">website</a>.
</p>


<h3>MINS - (WSML-Core, WSML-Flight, WSML-Rule)</h3>
<p>
	<a href="http://tools.deri.org/mins/">MINS</a> is a reasoner for Datalog programs with negation and function symbols which supports well-founded semantics. MINS stands for Mins Is Not Silri. It is based on <a href="http://ontobroker.semanticweb.org/silri/">SILRI</a>, an inference engine developed by J&uuml;rgen Angele and Stefan Decker (Institute AIFB, University of Karlsruhe). Currently MINS supports the following evaluation methods:
	<ol>
		<li>Naive evaluation (for stratified programs)</li>
		<li>Dynamic Filtering evaluation (for stratified programs)</li>
		<li>Well-founded evaluation with alternating fixed point</li>
		<li>Well-founded evaluation</li>
	</ol>
	All evaluation methods can handle negated rules too. 
</p>

<p>
	The MINS libraries are included in the GPL <a href="download">release</a>. For further information, please visit the MINS <a href="http://tools.deri.org/mins/">website</a>.
</p>

<h3>PELLET - (WSML-Core, WSML-DL)</h3>

<p>
	<a href="http://pellet.owldl.com/">Pellet</a> is an open source,
	<a href="http://www.w3.org/TR/owl-guide/">OWL DL</a> reasoner written in Java that is developed,
	and commercially supported, by <a href="http://clarkparsia.com/">Clark &amp; Parsia LLC</a>.
	Pellet is based on the tableaux algorithms for expressive Description Logics (DL) and
	supports the full expressivity of OWL DL, including reasoning about nominals (enumerated classes).</p>

<p>
	Pellet provides standard and cutting-edge <a href="http://pellet.owldl.com/features/">reasoning services</a>.
	It also incorporates various optimization techniques described in the DL literature and
	contains several novel optimizations for nominals, conjunctive query answering, and incremental reasoning.
	There's more detailed information about the architecture of the system and
	its features in the <a href="http://pellet.owldl.com/docs/">Pellet Documentation</a>.
</p>

<p>
	Pellet is integrated into the WSML2Reasoner framework in order to provide DL reasoning support for WSML-DL ontologies (via translation from WSML-DL to OWL-DL). The Pellet libraries are included in the LGPL <a href="download">release</a>. For further information, please visit the Pellet <a href="http://pellet.owldl.com/">website</a>.
</p>


<h2>News</h2>

<table>
	<tr valign=top>
		<td width=150>
			<p align=left>
				02 Dec 2008
			</p>
		</td>
		<td>
			<p align=left>
				WSML2Reasoner <a href="download#v0_6_4">version 0.6.4</a> released.<br />
				This version includes the latest (v0.6.2) WSMO4J jar files that contain many important bug fixes.
			</p>
		</td>
	</tr>

	<tr valign=top>
		<td width=150>
			<p align=left>
				11 Nov 2008
			</p>
		</td>
		<td>
			<p align=left>
				WSML2Reasoner <a href="download#v0_6_3">version 0.6.3</a> released.<br />
				This version includes refactoring work to simplify the interfaces to the
				different reasoner functionalities, i.e. to properly separate description logic and
				logic programming reasoning activities. <br />
				Numerous bugs have been fixed, including important fixes for date-time arithmetic
				and proper support for floating point seconds. <br />
				Also, there have been a number of fixes related to processing WSML relations and
				their instances. <br />
				This release also includes the latest IRIS v0.58 Datalog reasoner and
				a patched version of the wsmo4j jar files.
			</p>
		</td>
	</tr>
</table>
	