package org.wsml.reasoner;

public class TransformerPredicates {
	// Some predicates that are used to represent the oo-based molecules
	// of WSML in datalog.
	public final static String PRED_SUB_CONCEPT_OF = "wsml_subconcept_of";

	public final static String PRED_OF_TYPE = "wsml_of_type";

	public final static String PRED_IMPLIES_TYPE = "wsml_implies_type";

	public final static String PRED_MEMBER_OF = "wsml_member_of";

	public final static String PRED_HAS_VALUE = "wsml_has_value";

	// public final static String PRED_DECLARED_IRI =
	// "http://www.wsmo.org/wsml/wsml-syntax/extensions#wsml_is_declared_iri";

	public final static String PRED_DIRECT_SUBCONCEPT = "http://temp/direct/subConceptOf";

	public final static String PRED_INDIRECT_SUBCONCEPT = "http://temp/indirect/subConceptOf";

	public final static String PRED_DIRECT_CONCEPT = "http://temp/direct/memberOf";

	public final static String PRED_INDIRECT_CONCEPT = "http://temp/indirect/memberOf";

	public final static String PRED_KNOWN_CONCEPT = "http://temp/knownConcept";
}
