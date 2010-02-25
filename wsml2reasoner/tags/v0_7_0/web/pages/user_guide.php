<h1 align="center">WSML2Reasoner - API and User Guide</h1>
 <h2><a name="tth_sEc1">
1</a>&nbsp;&nbsp;Introduction</h2>

<div class="p">
     <h3><a name="tth_sEc1.1">
1.1</a>&nbsp;&nbsp;Purpose</h3>
This document is intended to give a short introduction to using the
the WSML2Reasoner framework and its application programming interface (API).
</div>

<div class="p">
     <h3><a name="tth_sEc1.2">
1.2</a>&nbsp;&nbsp;Audience</h3>
This guide is not only for software developers who are integrating WSML2Reasoner into their application, but also for higher level business users and professionals that wish to reason over WSML ontologies.
</div>

<div class="p">
     <h3><a name="tth_sEc1.3">
1.3</a>&nbsp;&nbsp;Scope</h3>
This document is intended to be a 'non-technical' guide for the basic usage of the WSML2Reasoner API. For a more technical description of the internal implementations, we refer the user to <a href="publications"><em>A Reasoning Framework for Rule-Based WSML</em></a>.
</div>

<div class="p">
 <h2><a name="tth_sEc2">
2</a>&nbsp;&nbsp;Description</h2>
</div>

<div class="p">
     <h3><a name="tth_sEc2.1">
2.1</a>&nbsp;&nbsp;Function</h3>
The WSML2Reasoner framework performs a variety of functions that ultimately - and optimally - translate a WSML ontology into the appropriate syntax of a specified underlying reasoning engine. Depending on the variant of the WSML ontology, and concurrently the desired reasoning tasks to be performed, the user must specify which reasoning engine he or she wishes to use.
</div>

<div class="p">
     <h3><a name="tth_sEc2.2">
2.2</a>&nbsp;&nbsp;Facades</h3>
Each reasoning engine implemented within the WSML2Reasoner framework requires a specific facade. The facade is essential in order to mediate between the generic Datalog representation and the internal representation specific to the single reasoning engines, or between the resulting OWL representation and an OWL reasoning engine (e.g. <a href="http://pellet.owldl.com/">Pellet</a>). However, aside from simply specifying the reasoning engine of choice (via the API, explained later), the end user mustn't worry about implementing the facades directly.
</div>

<div class="p">
     <h3><a name="tth_sEc2.3">
2.3</a>&nbsp;&nbsp;Extensions</h3>
In addition to the facades which provide an interface for, and implementation of, the underlying reasoning engines, the WSML2Reasoner query extensions (such as the SQL interface for the WSML-Flight-A query language) provide an alternative interface whereby reasoning tasks can be achieved with an alternative query syntax. For more infomation, please refer to section <a href="tth_sEc4">4</a>.
</div>

<div class="p">
 <h2><a name="tth_sEc3">
3</a>&nbsp;&nbsp;Getting Started</h2>
</div>

<div class="p">
     <h3><a name="tth_sEc3.1">
3.1</a>&nbsp;&nbsp;Releases</h3>
The are four basic release variants in accord to included reasoning engine library license agreements. The actual core WSML2Reasoner code base is LGPL.
<ul>
	<li><a href="license">LGPL</a> - This <a href="http://tools.deri.org/wsml2reasoner/releases/v0_6/wsml2reasoner_lgpl-v0_6.zip">release</a> includes the standard LGPL libraries and packages required for WSML2Reasoner, regardless of which underlying reasoning engine is chosen. In addition to these core libraries, the IRIS and PELLET libraries are included.
	</li>
	<li><a href="license">GPL</a>  - In addition to the LGPL libraries and packages, this <a href="http://tools.deri.org/wsml2reasoner/releases/v0_6/wsml2reasoner_gpl-v0_6.zip">release</a> includes the MINS jar.</li>
	<li>Non-commercial - In addition to the GPL libraries and packages, this release includes the KAON2 libraries and packages, which are available free of charge for universities for noncommecial academic usage (national laboratories are not considered universities). For commercial purposes, there is a commercial version of KAON2 called <a href="http://www.ontoprise.de/ontobrokerowl">OntoBroker OWL</a>. By downloading KAON2, you accept these license agreements.
	</li>
	<li>SDK - This release is an uncompiled version of the entire WSML2Reasoner project. It includes all GPL files, as well as several other additional libraries that are currently being tested, but have yet to reach a mature, stable level necessary for a proper release. The SDK should also be considered a non-commercial release.
	</li>
</div>



<h2><a name="tth_sEc4">4</a>&nbsp;&nbsp;WSML2Reasoner Query Extensions</h2>

<h3>Purpose of the SQL Interface Extension/WSML-Flight-A Query Language</h3>

<p>The purpose of the SQL Interface extension required to accomodate the WSML-Flight-A query language is to provide an easy to use syntactic extension for querying ontologies and to facilitate the following main features:</p>

<ul>
<li>A SQL like syntax, which a large number of IT-Professionals are well accustomed
with.</li>

<li>Aggregates, which allow to collect additional useful data from the query result
itself, in a post-processing step.</li>

<li>Further post-processing of query results, e.g. selecting only part of the solution set to a resolved query.</li>
</ul>
<p>This is mainly a use case driven extension of SQL originally required to accomodate the WSML-Flight-A query language. Additional motivations and details are given in <a href="#D1_4">[1]</a>. Furthermore since
this query language is layered on top of WSML-Flight, there are no compatability issues. There is no need to adjust existing ontologies, reasoning components, or other existing dependent software due to the architecture chosen.</p>

<h3>Syntax and Usage</h3>

<p>The allowed syntax for WSML-Flight-A queries closely corresponds to standard SQL
syntax. For an exhaustive, in-depth description we again refer to <a href="#D1_4">[1]</a>, while <a href="#MySQL">[2]</a> and <a href="#HSQLDB">[3]</a> provide a concise overview of the SQL syntax. This section is only meant to serve as a
short introduction.</p>
<p>A WSML-Flight-A query in general is of the following form:</p>
<pre>
	SELECT X<sub>1</sub>, ...,X<sub>n</sub>
	FROM o
	WHERE Q
</pre>

<p>Whereby Q is an ordinary WSML-Flight query, X<sub>1</sub>, ..., X<sub>n</sub> are a
subset of the variables appearing in Q, and o is the IRI of the ontology over which the 
query is placed. The WSML-Flight-A extension allows for SQL-like projection via the select expression in
the usual SQL-like syntax and semantics. It explicitly supports the following
constructs:</p>

<ul>
<li>COUNT(*), COUNT</li>

<li>MIN, MAX, SUM, AVG</li>

<li>SOME, EVERY</li>

<li>VAR_POP, VAR_SAMP, STDDEV_POP, STDDEV_SAMP</li>
</ul>

<p>With the exception of COUNT(*), all aggregate functions exclude NULL values, as in SQL. The type of the returned value for SUM is subject to deterministic widening to ensure lossless results. Numeric aggregates
obviously only return values if and only if all bindings of a variable are also numeric.
The return value type for COUNT is integer, for MIN, MAX and AVG it is the same type as
the column, for SOME and EVERY it is boolean. For VAR_POP, VAR_SAMP, STDDEV_POP and
STDDEV_SAMP statistical functions, the type is always double. Additionally the usual
further SQL constructs are possible:</p>

<ul>
<li>GROUP BY expression</li>

<li>HAVING expression</li>

<li>ORDER BY orderExpression</li>

<li>LIMIT &lt;limit&gt; [OFFSET &lt;offset&gt;]</li>
</ul>

<p>Where orderExpression denotes the usual:</p>

<p>{ column number | column alias | select Expression } [ASC | DESC]</p>

<p>And LIMIT n m creates the result set for the SELECT statement first
and then discards the first n rows (OFFSET) and returns the first m rows of the remaining
result set (LIMIT).</p>

<h3>Examples</h3>

<p>Following are some short examples for the query expressions. The basic usage of the query extension is very
simple. Queries are passed as ordinary <code>String<code> objects, and the
<code>WSMLQuery</code> class is the only entry point. Results are returned in
the expected WSML2Reasoner wrappers.</p>
<pre> 
	String query = "&lt;query&gt;"
	WSMLQuery wqe = new WSMLQuery();
	Set&lt;Map&lt;Variable, Term&gt;&gt; r = wqe.executeQuery(query);
</pre>

<p>Complete usage examples are contained in the WSML2Reasoner <code>org.wsml.reasoner.ext.sql</code> test package. Following are short examples
to illustrate the syntax of queries and same basic combinations. The first query
illustrates one of the simplest use cases of the query extension, in which basically no
features are used.</p>

<pre>
	SELECT ?x
	FROM _http://someontology
	WHERE ?x memberOf ?y"
</pre>

<p>The second example illustrates
the possibility to order query results according to certain variable bindings.</p>

<pre>
	SELECT ?place, ?employee
	FROM _http://someontology
	WHERE ?employee[hasWorkingPlace hasValue ?place]
	ORDER BY 1, 2
</pre>

<p>Furthermore it is possible to use aggregates, group results and
impose further restrictions on the projection, as in standard SQL.</p>

<pre>
	SELECT ?place, COUNT(?place)
	FROM _http://someontology
	WHERE ?employee[hasWorkingPlace hasValue ?place]
	GROUP BY ?place
	HAVING COUNT(?place) &gt; 4
</pre>

<p>Also the order of sorting can be specified, as usual, and the
number of results can then be limited to a certain desired amount.</p>

<pre>
	SELECT ?place, COUNT(?place)
	FROM _http://someontology<br />
	WHERE ?employee[hasWorkingPlace hasValue ?place]
	GROUP BY ?place
	HAVING COUNT(?place) &gt; 4
	ORDER BY ?place DESC
	LIMIT 2
</pre>

<h3>References</h3>

<p><a name="D1_4"></a>[1] D1.4: Process Ontology Query Language, Technical report, SUPER
IST Project 026850. Stijn Heymans, Cristina Feier, Jos de Bruijn, Stefan Z&uuml;ller, Emilia
Cimpian<br />
<a name="MySQL"></a>[2] MySQL 3.23, 4.0, 4.1 Reference Manual - <a href="http://dev.mysql.com/doc/refman/4.1/en">http://dev.mysql.com/doc/refman/4.1/en/</a><br />
<a name="HSQLDB"></a>[3] HSQLDB User Guide, Chapter 9. SQL Syntax - <a href="http://hsqldb.org/doc/guide/ch09.html">http://hsqldb.org/doc/guide/ch09.html</a></p>
