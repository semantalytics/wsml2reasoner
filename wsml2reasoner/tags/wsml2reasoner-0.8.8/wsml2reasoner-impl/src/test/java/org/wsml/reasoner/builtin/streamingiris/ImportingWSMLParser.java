package org.wsml.reasoner.builtin.streamingiris;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.omwg.ontology.Ontology;
import org.wsmo.common.IRI;
import org.wsmo.common.TopEntity;
import org.wsmo.common.exception.InvalidModelException;
import org.wsmo.factory.FactoryContainer;
import org.wsmo.wsml.ParserException;

import com.ontotext.wsmo4j.parser.wsml.WsmlParser;

import de.ifgi.envision.CachingRequestService;
import de.ifgi.envision.io.parser.rdf.RDFParser;

public class ImportingWSMLParser extends WsmlParser {

	protected RDFParser.Syntax syntax;

	public static CachingRequestService requestService;

	static {
		requestService = new CachingRequestService();
	}

	private Map<String, List<TopEntity>> res;

	public ImportingWSMLParser() {
		super();
	}

	public ImportingWSMLParser(boolean cleanOnParse, boolean memorizeLELook) {
		super(cleanOnParse, memorizeLELook);
	}

	public ImportingWSMLParser(FactoryContainer factory) {
		super(factory);
	}

	public ImportingWSMLParser(FactoryContainer factory, boolean cleanOnParse,
			boolean memorizeLELook) {
		super(factory, cleanOnParse, memorizeLELook);
	}

	@Override
	public TopEntity[] parse(Reader src) throws ParserException, IOException,
			InvalidModelException {
		res = new HashMap<String, List<TopEntity>>();
		Ontology ontology = null;
		TopEntity[] topEntity;
		topEntity = super.parse(src);

		for (TopEntity entity : topEntity) {
			if (entity instanceof Ontology)
				ontology = (Ontology) entity;
		}
		if (ontology == null) {
			throw new RuntimeException("No ontology found in input.");
		}

		try {
			this.handleImports(ontology);
		} catch (URISyntaxException e) {
			throw new IOException(e);
		} catch (InvalidModelException e) {
			throw new IOException(e);
		}

		ArrayList<TopEntity> teList = new ArrayList<TopEntity>();
		for (Entry<String, List<TopEntity>> resItem : res.entrySet()) {
			for (TopEntity te : resItem.getValue()) {
				teList.add(te);
			}
		}
		return teList.toArray(new TopEntity[teList.size()]);
	}

	private void handleImports(Ontology parsedOntology)
			throws MalformedURLException, IOException, URISyntaxException,
			ParserException, InvalidModelException {
		String namespace = extractNamespace(parsedOntology);
		if (res.containsKey(namespace)) {
			List<TopEntity> tempList = res.get(namespace);
			tempList.add(parsedOntology);
			res.put(namespace, tempList);
		} else {
			ArrayList<TopEntity> tempList = new ArrayList<TopEntity>();
			tempList.add(parsedOntology);
			res.put(namespace, tempList);
		}

		// handle imported ontology
		for (Ontology imported : parsedOntology.getImportedOntologies()
				.listOntologies()) {

			InputStream is = requestService.fetch(new URI(
					extractNamespace(imported)).toURL());

			StringWriter sw = new StringWriter();
			IOUtils.copy(is, sw);
			String fetched = sw.toString();
			syntax = this.identifySyntaxOfImportedOntology(fetched);

			RDFParser rdfParser = new RDFParser(syntax);
			TopEntity[] parsed = rdfParser
					.parse(new StringReader(sw.toString()));

			for (TopEntity te : parsed) {
				if (isAlreadyImported(res, te))
					continue; // to prevent cyclic imports
				if (te instanceof Ontology) {
					handleImports((Ontology) te);
				}
			}

		}

	}

	private boolean isAlreadyImported(Map<String, List<TopEntity>> mapped,
			TopEntity te) {
		if (te instanceof Ontology) {
			Collection<List<TopEntity>> tempList = mapped.values();
			for (List<TopEntity> list : tempList) {
				if (mapped.containsKey(extractNamespace((Ontology) te))
						&& list.contains(te)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Serialization behaviour of imported ontologies is different then just
	 * referenced ontologies imported: namespace#ontology_identifier referenced:
	 * namespace (the problem is, it is unclear if the import should point to
	 * the file or the ontology concept)
	 * 
	 * @param parsedOntology
	 * @return always the ontology namespace
	 */
	private String extractNamespace(Ontology o) {
		if (o.getDefaultNamespace() != null) {
			IRI iri = o.getDefaultNamespace().getIRI();
			if (iri.getNamespace() != null) {
				return iri.getNamespace();
			} else {
				return iri.toString();
			}
		}

		String id = o.toString();
		if (id.contains("#")) {
			return id.substring(0, id.indexOf('#') + 1);
		}

		return id;
	}

	private RDFParser.Syntax identifySyntaxOfImportedOntology(String s)
			throws IOException {
		if (s.startsWith("<?xml"))
			return RDFParser.Syntax.RDF_XML;
		if (s.startsWith("@prefix"))
			return RDFParser.Syntax.N3;

		// default is N3
		throw new IOException(
				"Failed to guess RDF format of import, first 100 chars are: "
						+ s.substring(0, 100));
	}

	public TopEntity[] handleImports(Ontology parsedOntology,
			Set<Ontology> alreadyImported) throws MalformedURLException,
			IOException, URISyntaxException, ParserException,
			InvalidModelException {
		res = new HashMap<String, List<TopEntity>>();
		for (Ontology ontology : alreadyImported) {
			String namespace = extractNamespace(ontology);
			if (res.containsKey(namespace)) {
				List<TopEntity> tempList = res.get(namespace);
				tempList.add(ontology);
				res.put(namespace, tempList);
			} else {
				ArrayList<TopEntity> tempList = new ArrayList<TopEntity>();
				tempList.add(ontology);
				res.put(namespace, tempList);
			}
		}
		handleImports(parsedOntology);

		ArrayList<TopEntity> teList = new ArrayList<TopEntity>();
		for (Entry<String, List<TopEntity>> resItem : res.entrySet()) {
			for (TopEntity te : resItem.getValue()) {
				teList.add(te);
			}
		}
		return teList.toArray(new TopEntity[teList.size()]);
	}

}
