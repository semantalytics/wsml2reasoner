/**
 * 
 */
package org.wsml.reasoner.builtin.streamingiris;

import org.sti2.wsmo4j.factory.WsmlFactoryContainer;
import org.wsmo.common.Namespace;
import org.wsmo.factory.WsmoFactory;

import com.ontotext.wsmo4j.common.NamespaceImpl;

/**
 * @author Henry
 * 
 */
public class WSMLQueryStore {

	public enum Namespaces {
		dublin_core {
			@Override
			public Namespace getNamespace() {
				return new NamespaceImpl("dc", initWsmoFactory().createIRI(
						"http://purl.org/dc/elements/1.1#"));
			}
		},
		rdf_schema {
			@Override
			public Namespace getNamespace() {
				return new NamespaceImpl("rdfs", initWsmoFactory().createIRI(
						"http://www.w3.org/2000/01/rdf-schema#"));
			}
		},
		owl {
			@Override
			public Namespace getNamespace() {
				return new NamespaceImpl("owl", initWsmoFactory().createIRI(
						"http://www.w3.org/2002/07/owl#"));
			}
		},
		affecting_environment {
			@Override
			public Namespace getNamespace() {
				return new NamespaceImpl("ae", initWsmoFactory().createIRI(
						"http://purl.org/ifgi/affenv#"));
			}
		},
		notification {
			@Override
			public Namespace getNamespace() {
				return new NamespaceImpl("no", initWsmoFactory().createIRI(
						"http://purl.org/ifgi/notification#"));
			}
		},
		geographic_space {
			@Override
			public Namespace getNamespace() {
				return new NamespaceImpl("gspace", initWsmoFactory().createIRI(
						"http://purl.org/ifgi/geospace#"));
			}
		},
		flood {
			@Override
			public Namespace getNamespace() {
				return new NamespaceImpl("flood", initWsmoFactory().createIRI(
						"http://purl.org/ifgi/water/flood#"));
			}
		},
		event_observation {
			@Override
			public Namespace getNamespace() {
				return new NamespaceImpl("eo", initWsmoFactory().createIRI(
						"http://purl.org/ifgi/event-observation#"));
			}
		},
		geographic_descriptions {
			@Override
			public Namespace getNamespace() {
				return new NamespaceImpl("gDesc", initWsmoFactory().createIRI(
						"http://purl.org/ifgi/geodesc#"));
			}
		},
		semantic_sensor_network {
			@Override
			public Namespace getNamespace() {
				return new NamespaceImpl("ssn", initWsmoFactory().createIRI(
						"http://purl.org/ifgi/ssn#"));
			}
		},
		dolce_ultralite {
			@Override
			public Namespace getNamespace() {
				return new NamespaceImpl("dul", initWsmoFactory().createIRI(
						"http://purl.org/ifgi/dul#"));
			}
		},
		geographic_models {
			@Override
			public Namespace getNamespace() {
				return new NamespaceImpl("geomod", initWsmoFactory().createIRI(
						"http://purl.org/ifgi/geomodels#"));
			}
		},
		dolce {
			@Override
			public Namespace getNamespace() {
				return new NamespaceImpl("dolce", initWsmoFactory().createIRI(
						"http://purl.org/ifgi/dolce#"));
			}
		},
		geographic_qualities {
			@Override
			public Namespace getNamespace() {
				return new NamespaceImpl("gq", initWsmoFactory().createIRI(
						"http://purl.org/ifgi/geoqualities#"));
			}
		},
		xml_schema_description {
			@Override
			public Namespace getNamespace() {
				return new NamespaceImpl("xsd", initWsmoFactory().createIRI(
						"http://www.w3.org/2001/XMLSchema#"));
			}
		},
		geographic_events {
			@Override
			public Namespace getNamespace() {
				return new NamespaceImpl("ge", initWsmoFactory().createIRI(
						"http://purl.org/ifgi/geoevents"));
			}
		},
		oil_spill {
			@Override
			public Namespace getNamespace() {
				return new NamespaceImpl("os", initWsmoFactory().createIRI(
						"http://purl.org/ifgi/oilspill#"));
			}
		},
		time {
			@Override
			public Namespace getNamespace() {
				return new NamespaceImpl("time", initWsmoFactory().createIRI(
						"http://purl.org/ifgi/time#"));
			}
		},
		geosparql {
			@Override
			public Namespace getNamespace() {
				return new NamespaceImpl("gsparql", initWsmoFactory()
						.createIRI("http://purl.org/ifgi/geosparql#"));
			}
		};

		public WsmoFactory initWsmoFactory() {
			if (wsmlFactoryContainer == null) {
				wsmlFactoryContainer = new WsmlFactoryContainer();
			}
			return wsmlFactoryContainer.getWsmoFactory();
		};

		public abstract Namespace getNamespace();

		private static WsmlFactoryContainer wsmlFactoryContainer = null;
	};

	public enum Query {
		get_event_observation, get_subconcepts_of_geographical_event, get_instances_of_subscriber, get_OneD_instances_of_geographical_unit, get_hazard_level_description, get_affected_subscribers_by_flood, get_geographical_units_became_affected, get_predicted_event_observation;
	}

	public static String selectPredefinedQuery(Query query) {
		String queryString = "";
		switch (query) {
		// case test1:
		// // get all roads being FLOODED
		// queryString =
		// "?subscriber[participantIn hasValue ?activity, hasContact hasValue ?contact, hasKindOfContact hasValue ?kindofcontact] memberOf Subscriber and "+
		// "?contact memberOf _string and " +
		// "?kindofcontact memberOf _string and " +
		// "?activity[define hasValue ?affenv] memberOf EnvisionActivity and " +
		// "?affenv[hasPart hasValue ?object] memberOf EnvisionActivityEnvironment and "
		// +
		// "?object[hasState hasValue ?state] memberOf EnvisionObject and " +
		// "?state memberOf Affected";
		// break;
		case get_subconcepts_of_geographical_event:
			queryString = "?event subConceptOf _\""
					+ Namespaces.geographic_events.getNamespace().getIRI()
							.toString() + "geographical_event\"";
			break;
		case get_instances_of_subscriber:
			queryString = "?subscriber[_\""
					+ Namespaces.notification.getNamespace().getIRI()
							.toString()
					+ "has_contact\" hasValue ?contact] memberOf _\""
					+ Namespaces.notification.getNamespace().getIRI()
							.toString() + "subscriber\"";
			break;
		case get_event_observation:
			queryString = "?eventObservation memberOf _\""
					+ Namespaces.event_observation.getNamespace().getIRI()
							.toString() + "EventObservation\"";
			break;
		case get_OneD_instances_of_geographical_unit:
			queryString = "?geounit memberOf _\""
					+ Namespaces.geographic_space.getNamespace().getIRI()
							.toString()
					+ "geographical_unit\" and "
					+ "?geodesc[_\""
					+ Namespaces.dolce_ultralite.getNamespace().getIRI()
							.toString()
					+ "describes\" hasValue ?geounit, _\""
					+ Namespaces.geographic_descriptions.getNamespace()
							.getIRI().toString()
					+ "describedLocation\" hasValue ?geoloc] memberOf _\""
					+ Namespaces.geographic_descriptions.getNamespace()
							.getIRI().toString()
					+ "GeographicalDescription\" and "
					+ "?geoloc[_\""
					+ Namespaces.geosparql.getNamespace().getIRI().toString()
					+ "spatialDimension\" hasValue ?value, _\""
					+ Namespaces.geosparql.getNamespace().getIRI().toString()
					+ "asWKT\" hasValue ?coord] memberOf _\""
					+ Namespaces.geographic_descriptions.getNamespace()
							.getIRI().toString() + "LocationDescription\" and "
					+ "?value = 1";
			break;
		case get_hazard_level_description:
			queryString = "?hazLevDesc[_\""
					+ Namespaces.geographic_models.getNamespace().getIRI()
							.toString()
					+ "hasHazardLevel\" hasValue ?hazLev] memberOf _\""
					+ Namespaces.geographic_models.getNamespace().getIRI()
							.toString() + "HazardLevelDescription\"";
			break;
		case get_affected_subscribers_by_flood:
			queryString = "?subscriber[_\""
					+ Namespaces.dolce.getNamespace().getIRI().toString()
					+ "participant_in\" hasValue ?activity, _\""
					+ Namespaces.notification.getNamespace().getIRI()
							.toString()
					+ "has_contact\" hasValue ?contact, _\""
					+ Namespaces.notification.getNamespace().getIRI()
							.toString()
					+ "subscribed_to\" hasValue ?eventsub] memberOf _\""
					+ Namespaces.notification.getNamespace().getIRI()
							.toString()
					+ "subscriber\" and "
					+ "?activity[_\""
					+ Namespaces.affecting_environment.getNamespace().getIRI()
							.toString()
					+ "define\" hasValue ?affenv] memberOf _\""
					+ Namespaces.notification.getNamespace().getIRI()
							.toString()
					+ "stay_at_place\" and "
					+ "?affenv[_\""
					+ Namespaces.affecting_environment.getNamespace().getIRI()
							.toString()
					+ "contains\" hasValue ?region] memberOf _\""
					+ Namespaces.notification.getNamespace().getIRI()
							.toString()
					+ "stay_at_place_environment\" and "
					+ "?geounit[_\""
					+ Namespaces.geographic_space.getNamespace().getIRI()
							.toString()
					+ "situated_in\" hasValue ?region, _\""
					+ Namespaces.affecting_environment.getNamespace().getIRI()
							.toString()
					+ "has_state\" hasValue ?state] memberOf _\""
					+ Namespaces.geographic_space.getNamespace().getIRI()
							.toString()
					+ "geographical_unit\" and "
					+ "?state memberOf _\""
					+ Namespaces.notification.getNamespace().getIRI()
							.toString()
					+ "flooded\" and "
					+ "?predeventobsdesc[_\""
					+ Namespaces.dolce_ultralite.getNamespace().getIRI()
							.toString()
					+ "describes\" hasValue ?predeventobs] memberOf _\""
					+ Namespaces.geographic_models.getNamespace().getIRI()
							.toString()
					+ "PredictedEventObservationDescription\" and "
					+ "?predeventobs[_\""
					+ Namespaces.geographic_models.getNamespace().getIRI()
							.toString()
					+ "predicts\" hasValue ?event] memberOf _\""
					+ Namespaces.geographic_models.getNamespace().getIRI()
							.toString()
					+ "PredictedEventObservation\" and "
					+ "?eventsub memberOf _\""
					+ Namespaces.flood.getNamespace().getIRI().toString()
					+ "Flood\" and "
					+ "?event memberOf ?eventconcept and "
					+ "?eventconcept subConceptOf _\""
					+ Namespaces.flood.getNamespace().getIRI().toString()
					+ "Flood\" and "
					+ "?dam[_\""
					+ Namespaces.affecting_environment.getNamespace().getIRI()
							.toString()
					+ "has_state\" hasValue ?statedam] memberOf _\""
					+ Namespaces.flood.getNamespace().getIRI().toString()
					+ "Dam\" and "
					+ "?statedam memberOf _\""
					+ Namespaces.notification.getNamespace().getIRI()
							.toString() + "flooded\" and " + "naf ?dam[_\""
					+ Namespaces.flood.getNamespace().getIRI().toString()
					+ "downstream\" hasValue ?geounit]";
			break;
		case get_geographical_units_became_affected:
			queryString = "?geounit[_\""
					+ Namespaces.affecting_environment.getNamespace().getIRI()
							.toString()
					+ "has_state\" hasValue ?state] memberOf _\""
					+ Namespaces.geographic_space.getNamespace().getIRI()
							.toString()
					+ "geographical_unit\" and "
					+ "?state memberOf _\""
					+ Namespaces.notification.getNamespace().getIRI()
							.toString() + "affected\"";
			break;
		case get_predicted_event_observation:
			queryString = "?predeventobsdesc[_\""
					+ Namespaces.dolce_ultralite.getNamespace().getIRI()
							.toString()
					+ "describes\" hasValue ?predeventobs, "
					+ "_\""
					+ Namespaces.geographic_descriptions.getNamespace()
							.getIRI().toString()
					+ "describedLocation\" hasValue ?descloc, "
					+ "_\""
					+ Namespaces.geographic_models.getNamespace().getIRI()
							.toString()
					+ "associatedHazardLevel\" hasValue ?hazlevdesc, "
					+ "_\""
					+ Namespaces.geographic_models.getNamespace().getIRI()
							.toString()
					+ "hasSource\" hasValue ?eventobsdesc] memberOf "
					+ "_\""
					+ Namespaces.geographic_models.getNamespace().getIRI()
							.toString()
					+ "PredictedEventObservationDescription\" and "
					+ "?eventobsdesc memberOf ?eventobsdescconcept and "
					+ "?eventobsdescconcept subConceptOf ?anyconcept and "
					+ "?predeventobs[_\""
					+ Namespaces.geographic_models.getNamespace().getIRI()
							.toString()
					+ "predicts\" hasValue ?event] memberOf "
					+ "_\""
					+ Namespaces.geographic_models.getNamespace().getIRI()
							.toString()
					+ "PredictedEventObservation\" and "
					+ "?event memberOf ?eventconcept and "
					+ "?eventconcept subConceptOf ?anyconcept2 and "
					+ "?descloc[_\""
					+ Namespaces.geosparql.getNamespace().getIRI().toString()
					+ "asWKT\" hasValue ?locvalue] memberOf "
					+ "_\""
					+ Namespaces.geographic_descriptions.getNamespace()
							.getIRI().toString()
					+ "LocationDescription\" and "
					+ "?hazlevdesc[_\""
					+ Namespaces.geographic_models.getNamespace().getIRI()
							.toString()
					+ "hasHazardLevel\" hasValue ?hazlev] memberOf "
					+ "_\""
					+ Namespaces.geographic_models.getNamespace().getIRI()
							.toString() + "HazardLevelDescription\"";
			break;
		// case 3:
		// //get all cities
		// queryString =
		// "?city["+geosparqlNS+"asWKT hasValue ?geom] memberOf "+affenvNS+"City";
		// break;
		default:
			throw new IllegalArgumentException(
					"Please select a valid query from the enumeration \"Query\"!");
		}
		return queryString;
	}

}
