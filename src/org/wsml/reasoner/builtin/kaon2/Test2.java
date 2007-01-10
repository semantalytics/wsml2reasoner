package org.wsml.reasoner.builtin.kaon2;

import java.io.*;
import java.util.*;

import org.omwg.ontology.Ontology;
import org.semanticweb.kaon2.api.*;
import org.semanticweb.kaon2.api.logic.*;
import org.semanticweb.kaon2.api.owl.elements.*;
import org.semanticweb.kaon2.api.reasoner.*;
import org.semanticweb.kaon2.api.formatting.*;
import org.wsml.reasoner.serializer.owl.WsmlOwlSerializer;
import org.wsmo.common.TopEntity;
import org.wsmo.factory.Factory;
import org.wsmo.wsml.Parser;
import org.wsmo.wsml.Serializer;

/**
 * This example shows how to create a simple ontology containing rules. Also, it shows how to run sample
 * queries. You should familiarize yourself with examples 1 and 2 before you go through this example.
 */
public class Test2 {
    
	public void fileTest() throws Exception {
		Parser parser = Factory.createParser(null);
		// read test file and parse it 
//        InputStream is = this.getClass().getClassLoader().getResourceAsStream(
//                "reasoner/dl/wsml2owlExample.wsml");
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(
				"org/wsml/reasoner/builtin/kaon2/testFile.wsml");
		
        // assuming first topentity in file is an ontology  
        org.omwg.ontology.Ontology wsmlOntology = (Ontology) parser.parse(new InputStreamReader(is))[0]; 
        
        Serializer serializer = new WsmlOwlSerializer(null);
        StringBuffer buf = new StringBuffer();
        serializer.serialize(new TopEntity[] {wsmlOntology}, buf);

//        System.out.println(buf);
        InputStream in = new ByteArrayInputStream(buf.toString().getBytes());
        
        KAON2Connection connection = KAON2Manager.newConnection();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KAON2Connection.LOAD_FROM_INPUT_STREAM, in);
        
        org.semanticweb.kaon2.api.Ontology ontology = connection.openOntology(
        		"http://www.example.org/ontologies/example", map);
        Reasoner reasoner = ontology.createReasoner();

        ontology.saveOntology(OntologyFileFormat.OWL_RDF, new File("c:\\temp\\OwnTestFromFile.xml"),"ISO-8859-1");
        
//      We are now ready to ask some questions. Our goal is to obtain the expertise of all persons. In order words,
        // we want to ask the following conjunctive query:
        //
        //   Student(X), WorksOn(X,Y)
        //
        // The first thing we need to do is create an instance of the reasoner. Reasoners consume resources, so you should keep
        // them around only for as long as you need them. However, if you plan to ask several queries, it is *MUCH* more efficient
        // to use one and the same reasoner for all queries. One reasoner can be used to run several concurrent queries. 

        Variable X=KAON2Manager.factory().variable("X");
        Variable Y=KAON2Manager.factory().variable("Y");
        ObjectProperty worksOn = KAON2Manager.factory().objectProperty(
        		"http://www.example.org/ontologies/example#worksOn");
        OWLClass student = KAON2Manager.factory().owlClass(
        		"http://www.example.org/ontologies/example#Student");
        
        // We now create the query object. A query consists of the following things:
        //
        // - a list of literals definint the query (they are created as usual)
        // - a list of distinguished variables, i.e. the variables that will be returned
        Query whoWorksOnWhat = reasoner.createQuery(new Literal[] {
            KAON2Manager.factory().literal(true, student, new Term[] { X }),
            KAON2Manager.factory().literal(true, worksOn, new Term[] { X,Y }),
        	}, new Variable[] { X,Y});

        whoWorksOnWhat = reasoner.createQuery(Namespaces.INSTANCE,"SELECT ?x ?y WHERE " +
				"{ ?x <http://www.example.org/ontologies/example#worksOn> ?y }");
        
//        whoWorksOnWhat = reasoner.createQuery(Namespaces.INSTANCE, "SELECT ?x ?y WHERE " +
//        		"{ ?x <http://www.w3.org/2000/01/rdf-schema#subClassOf> ?y }");
        
        // Creating the query has the effect of compiling it. A single query can be 
        // executed then several times. A query is executed by invoking the Query.open() method.
        System.out.println();
        System.out.println("The list of students and topics they work on:");
        System.out.println("---------------------------------------------------");

        whoWorksOnWhat.open();
        System.out.println(whoWorksOnWhat.getNumberOfTuples());
        // We now iterate over the query results.
        while (!whoWorksOnWhat.afterLast()) {
            // A query result is a set of tuples. The values in each tuple correspond to the distinguished variables.
            // In the above example, the distinguished variables are [X,Y]; this means that the first object in
            // the tuple is the value for the X variable, and the second one is the value for the Y variable.
            Object[] tupleBuffer=whoWorksOnWhat.tupleBuffer();
            System.out.println("Student '"+tupleBuffer[0].toString()+"' works on '"+tupleBuffer[1].toString()+"'.");
            whoWorksOnWhat.next();
        }

        System.out.println("---------------------------------------------------");

        // If a query has been successfully opened, it should also be closed; otherwise, a resource leak occurs.
        whoWorksOnWhat.close();

        // When a query is not needed any more, it should be disposed of. Forgetting to do so will result in
        // a fairly serious memory leak.
        whoWorksOnWhat.dispose();
        
        // Reasoners have to be disposed off after they are not used any more. Forgetting to do so will result in
        // a fairly serious memory leak!
        reasoner.dispose();

        // Don't forget to close the connection!
        connection.close();
	}
	
	public void inMemoryTest() throws Exception {
        // To create an ontology, we again start by creating a connection.
        // We again need to register a resolver that will provide a physical URI
        // for the ontology. In this example, the physical URI is relative to the current directory.
        KAON2Connection connection=KAON2Manager.newConnection();
        DefaultOntologyResolver resolver=new DefaultOntologyResolver();
        resolver.registerReplacement("http://kaon2.semanticweb.org/example4","file:example4.xml");
        connection.setOntologyResolver(resolver);

        // We create an ontology by specifying its logical URI. The resolver provides the physical URI.
        // Up until now this example is the same as Example 2.
        org.semanticweb.kaon2.api.Ontology ontology=connection.createOntology("http://kaon2.semanticweb.org/example4",new HashMap<String,Object>());

        // We now create a sample ontology describing relationships among objects in a domain.
        OWLClass person=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example4#person");
        OWLClass student=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example4#student");
        OWLClass topic = KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example4#topic");

//        SubClassOf description = KAON2Manager.factory().subClassOf(student, person);
       
        ObjectProperty worksOn=KAON2Manager.factory().objectProperty("http://kaon2.semanticweb.org/example4#worksOn");

        Individual semanticWeb=KAON2Manager.factory().individual("http://kaon2.semanticweb.org/example4#semanticWeb");
        
        List<OntologyChangeEvent> changes=new ArrayList<OntologyChangeEvent>();
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().classMember(topic,semanticWeb),OntologyChangeEvent.ChangeType.ADD));
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().subClassOf(student, person), OntologyChangeEvent.ChangeType.ADD));

        // We now add some facts. We shall use these facts to query answering later.
        Individual holger = KAON2Manager.factory().individual("http://kaon2.semanticweb.org/example4#holger");
        // Boris is a student.
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().classMember(student, holger),OntologyChangeEvent.ChangeType.ADD));
        // Boris works on DIP.
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().objectPropertyMember(
        		worksOn, holger, semanticWeb),OntologyChangeEvent.ChangeType.ADD));

        // We now apply the changes to the ontology. Only after this action is done,
        // the axioms created above are added to the ontology.
        ontology.applyChanges(changes);

        // We now save the ontology by calling the serializer. Observe that the
        // location where the ontology is stored does not need to be the same
        // as the physical URI. This is deliberate, as this allows you to implement
        // 'Save As' operation. The second parameter defines the character encoding used
        // in the XML file. we save the ontology into 'c:\temp\example4.xml'.
        System.out.println("The ontology will be saved into 'c:\\temp\\example4.xml'.");
        System.out.println("Please ensure that 'c:\\temp' directory exists.");
        ontology.saveOntology(OntologyFileFormat.OWL_RDF, new File("c:\\temp\\OwnTestInMemory.xml"),"ISO-8859-1");

        System.out.println("The ontology was saved successfully into 'c:\\temp\\example4.xml'.");

        // We are now ready to ask some questions. Our goal is to obtain the expertise of all persons. In order words,
        // we want to ask the following conjunctive query:
        //
        //   Student(X), WorksOn(X,Y)
        //
        // The first thing we need to do is create an instance of the reasoner. Reasoners consume resources, so you should keep
        // them around only for as long as you need them. However, if you plan to ask several queries, it is *MUCH* more efficient
        // to use one and the same reasoner for all queries. One reasoner can be used to run several concurrent queries. 
        Reasoner reasoner=ontology.createReasoner();

        Variable X=KAON2Manager.factory().variable("X");
        Variable Y=KAON2Manager.factory().variable("Y");
        
        // We now create the query object. A query consists of the following things:
        //
        // - a list of literals definint the query (they are created as usual)
        // - a list of distinguished variables, i.e. the variables that will be returned
        Query whoWorksOnWhat = reasoner.createQuery(new Literal[] {
            KAON2Manager.factory().literal(true, student, new Term[] { X }),
            KAON2Manager.factory().literal(true, worksOn, new Term[] { X,Y }),
        	}, new Variable[] { X,Y});

        // Creating the query has the effect of compiling it. A single query can be 
        // executed then several times. A query is executed by invoking the Query.open() method.
        System.out.println();
        System.out.println("The list of students and topics they work on:");
        System.out.println("---------------------------------------------------");

        whoWorksOnWhat.open();
        // We now iterate over the query results.
        while (!whoWorksOnWhat.afterLast()) {
            // A query result is a set of tuples. The values in each tuple correspond to the distinguished variables.
            // In the above example, the distinguished variables are [X,Y]; this means that the first object in
            // the tuple is the value for the X variable, and the second one is the value for the Y variable.
            Object[] tupleBuffer=whoWorksOnWhat.tupleBuffer();
            System.out.println("Student '"+tupleBuffer[0].toString()+"' works on '"+tupleBuffer[1].toString()+"'.");
            whoWorksOnWhat.next();
        }

        System.out.println("---------------------------------------------------");

        // If a query has been successfully opened, it should also be closed; otherwise, a resource leak occurs.
        whoWorksOnWhat.close();
        
//        // After a query is closed, it may be reopened. The results of the query will reflect the changes to the ontology.
//        // To see this, we shall add an additional fact that DIP is about OWL.
//        changes.clear();
//        changes.add(new OntologyChangeEvent(KAON2Manager.factory().objectPropertyMember(projectHasTopic,dip,owl),OntologyChangeEvent.ChangeType.ADD));
//        ontology.applyChanges(changes);
//
//        System.out.println();
//        System.out.println("New facts have been successfully added to the ontology.");
//        System.out.println();
//
//        // We now execute the query again. The results reflect the change in the ontology.
//        System.out.println("The same query executed again:");
//        System.out.println("---------------------------------------------------");
//
//        whatDoPeopleKnowAbout.open();
//        while (!whatDoPeopleKnowAbout.afterLast()) {
//            Object[] tupleBuffer=whatDoPeopleKnowAbout.tupleBuffer();
//            System.out.println("Person '"+tupleBuffer[0].toString()+"' knows about '"+tupleBuffer[1].toString()+"'.");
//            whatDoPeopleKnowAbout.next();
//        }
//        System.out.println("---------------------------------------------------");
//        whatDoPeopleKnowAbout.close();

        // When a query is not needed any more, it should be disposed of. Forgetting to do so will result in
        // a fairly serious memory leak.
        whoWorksOnWhat.dispose();
        
        // Reasoners have to be disposed off after they are not used any more. Forgetting to do so will result in
        // a fairly serious memory leak!
        reasoner.dispose();

        // Don't forget to close the connection!
        connection.close();
	}
	
	public static void main(String[] args) throws Exception {
		Test2 test = new Test2();
		
//		test.inMemoryTest();
		
		test.fileTest();
    }
}

