package org.wsml.reasoner.builtin.kaon2;

import java.io.*;
import java.util.*;

import org.semanticweb.kaon2.api.*;
import org.semanticweb.kaon2.api.logic.*;
import org.semanticweb.kaon2.api.owl.elements.*;
import org.semanticweb.kaon2.api.reasoner.*;
import org.semanticweb.kaon2.api.formatting.*;

/**
 * This example shows how to create a simple ontology containing rules. Also, it shows how to run sample
 * queries. You should familiarize yourself with examples 1 and 2 before you go through this example.
 */
public class Example4 {
    public static void main(String[] args) throws Exception {
        // To create an ontology, we again start by creating a connection.
        // We again need to register a resolver that will provide a physical URI
        // for the ontology. In this example, the physical URI is relative to the current directory.
        KAON2Connection connection=KAON2Manager.newConnection();
        DefaultOntologyResolver resolver=new DefaultOntologyResolver();
        resolver.registerReplacement("http://kaon2.semanticweb.org/example4","file:example4.xml");
        connection.setOntologyResolver(resolver);

        // We create an ontology by specifying its logical URI. The resolver provides the physical URI.
        // Up until now this example is the same as Example 2.
        Ontology ontology=connection.createOntology("http://kaon2.semanticweb.org/example4",new HashMap<String,Object>());

        // We now create a sample ontology describing relationships among objects in a domain.
        OWLClass person=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example4#person");
        OWLClass student=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example4#student");
        OWLClass professor=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example4#professor");

        OWLClass project=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example4#project");
        OWLClass euProject=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example4#euProject");
        OWLClass dfgProject=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example4#dfgProject");

        OWLClass topic=KAON2Manager.factory().owlClass("http://kaon2.semanticweb.org/example4#topic");

        ObjectProperty worksOn=KAON2Manager.factory().objectProperty("http://kaon2.semanticweb.org/example4#worksOn");
        ObjectProperty projectHasTopic=KAON2Manager.factory().objectProperty("http://kaon2.semanticweb.org/example4#projectHasTopic");
        ObjectProperty personKnowsAboutTopic=KAON2Manager.factory().objectProperty("http://kaon2.semanticweb.org/example4#personKnowsAboutTopic");

        Individual semanticWeb=KAON2Manager.factory().individual("http://kaon2.semanticweb.org/example4#semanticWeb");
        Individual descriptionLogics=KAON2Manager.factory().individual("http://kaon2.semanticweb.org/example4#descriptionLogics");
        Individual owl=KAON2Manager.factory().individual("http://kaon2.semanticweb.org/example4#owl");

        // We perform updates as in Example 2, by adding a sequence of axioms to the ontology.
        List<OntologyChangeEvent> changes=new ArrayList<OntologyChangeEvent>();

        // We now add describe the domain of the ontology.
        // All students are persons.
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().subClassOf(student,person),OntologyChangeEvent.ChangeType.ADD));
        // All professors are persons.
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().subClassOf(professor,person),OntologyChangeEvent.ChangeType.ADD));
        // EU projects are projects
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().subClassOf(euProject,project),OntologyChangeEvent.ChangeType.ADD));
        // DFG projects are projects
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().subClassOf(dfgProject,project),OntologyChangeEvent.ChangeType.ADD));
        // Persons work on projects.
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().objectPropertyDomain(worksOn,person),OntologyChangeEvent.ChangeType.ADD));
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().objectPropertyRange(worksOn,project),OntologyChangeEvent.ChangeType.ADD));
        // Projects have topics.
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().objectPropertyDomain(projectHasTopic,project),OntologyChangeEvent.ChangeType.ADD));
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().objectPropertyRange(projectHasTopic,topic),OntologyChangeEvent.ChangeType.ADD));
        // Persons know about topics.
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().objectPropertyDomain(personKnowsAboutTopic,person),OntologyChangeEvent.ChangeType.ADD));
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().objectPropertyRange(personKnowsAboutTopic,topic),OntologyChangeEvent.ChangeType.ADD));
        // Semantic Web, description logics and OWL are topics.
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().classMember(topic,semanticWeb),OntologyChangeEvent.ChangeType.ADD));
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().classMember(topic,descriptionLogics),OntologyChangeEvent.ChangeType.ADD));
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().classMember(topic,owl),OntologyChangeEvent.ChangeType.ADD));

        // We now create a rule that axiomatizes the following relationship:
        //
        // If
        //    a person X works on a project Y, and
        //    the project Y is about a topic Z,
        // then
        //    the person X knows about topic Z.
        //
        // In Prolog, this rule would be written like this:
        //     personKnowsAboutTopic(X,Z) :- worksOn(X,Y), projectHasTopic(Y,Z).
        //
        // Although the practice often disputes this rule, we shall pretend that we live in a perfect
        // world where only competent people are woking on interesting projects. (sigh!)
        //
        // The above rule is directly converted into an object strucutre. We first create the variables X, Y and Z:
        Variable X=KAON2Manager.factory().variable("X");
        Variable Y=KAON2Manager.factory().variable("Y");
        Variable Z=KAON2Manager.factory().variable("Z");

        // We now create the literals (notice that all of them are positive):
        Literal personKnowsAboutTopic_X_Z=KAON2Manager.factory().literal(true,personKnowsAboutTopic,new Term[] { X,Z });
        Literal worksOn_X_Y=KAON2Manager.factory().literal(true,worksOn,new Term[] { X,Y });
        Literal projectHasTopic_Y_Z=KAON2Manager.factory().literal(true,projectHasTopic,new Term[] { Y,Z });

        // We now create the rule.
        Rule rule=KAON2Manager.factory().rule(
            personKnowsAboutTopic_X_Z,                          // this is the rule head, i.e. the consequent of the rule
            new Literal[] { worksOn_X_Y,projectHasTopic_Y_Z }   // this is the rule body, i.e. the condition of the rule
        );

        // Rule is a kind of axiom, so it can be added to the ontology in the same way as
        // any axiom is added, i.e. by an OntologyChangeEvent.
        changes.add(new OntologyChangeEvent(rule,OntologyChangeEvent.ChangeType.ADD));

        // We shall create another rule which says:
        //
        // If
        //    a project X is about Semantic Web
        // then
        //    the project X is about description logic.
        //
        // In Prolog, this rule would be written like this:
        //     projectHasTopic(X,example4:descriptionLogics) :- projectHasTopic(X,example4:semanticWeb)
        //
        // The above rule is the dream of every logician ("you shall be assimilated, resistence is futile",
        // if you know what I mean :-)
        //
        // We do not to create new variable X; we simply reuse the already created object.
        // We now create the literals inline:
        rule=KAON2Manager.factory().rule(
            KAON2Manager.factory().literal(true,projectHasTopic,new Term[] { X,descriptionLogics }),
            new Literal[] { KAON2Manager.factory().literal(true,projectHasTopic,new Term[] { X,semanticWeb }) }
        );

        // We add the rule to the chande list.
        changes.add(new OntologyChangeEvent(rule,OntologyChangeEvent.ChangeType.ADD));

        // Creating rules in the above way can be tedious. Therefore, the LISP-like syntax
        // can be used to encode rules as well. To use it, we first initialize an instance
        // of the Namespaces class in the same way as in Example 2.
        Namespaces namespaces=new Namespaces();
        namespaces.registerPrefix("example4","http://kaon2.semanticweb.org/example4#");

        // We shall create a yet another rule that is similar to the above rule which says the following:
        //
        // If
        //     a person X knows about OWL
        // then
        //    the person X knowls about description logics as well.
        //
        // In Prolog, this rule would be written like this:
        //     personKnowsAboutTopic(X,example4:descriptionLogics) :- personKnowsAboutTopic(X,example4:owl)
        rule=(Rule)KAON2Manager.factory().axiom(
            "[rule ["+                                                                      // States that the rule follows. The opening brackes introduces a set of head literals.
            "    [[oprop example4:personKnowsAboutTopic] X [example4:descriptionLogics]]"+  // This is the specification of one head literal. The individuals must be enclosed in [].
            "  ] ["+                                                                        // The bracket ] terminated the set of head literals. The bracket [ opens the set of body literals.
            "    [[oprop example4:personKnowsAboutTopic] X [example4:owl]]"+                // This is the one body literal.
            "  ]"+                                                                          // This closes the set of body literals.
            "]"                                                                             // This closes the rule.
        ,namespaces);

        // We add the rule to the chande list.
        changes.add(new OntologyChangeEvent(rule,OntologyChangeEvent.ChangeType.ADD));

        // We now add some facts. We shall use these facts to query answering later.
        Individual boris=KAON2Manager.factory().individual("http://kaon2.semanticweb.org/example4#boris");
        // Boris is a student.
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().classMember(student,boris),OntologyChangeEvent.ChangeType.ADD));
        // DIP is an EU project.
        Individual dip=KAON2Manager.factory().individual("http://kaon2.semanticweb.org/example4#dip");
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().classMember(euProject,dip),OntologyChangeEvent.ChangeType.ADD));
        // DIP is about semantic web.
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().objectPropertyMember(projectHasTopic,dip,semanticWeb),OntologyChangeEvent.ChangeType.ADD));
        // Boris works on DIP.
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().objectPropertyMember(worksOn,boris,dip),OntologyChangeEvent.ChangeType.ADD));

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
        ontology.saveOntology(OntologyFileFormat.OWL_XML,new File("c:\\temp\\example4.xml"),"ISO-8859-1");

        System.out.println("The ontology was saved successfully into 'c:\\temp\\example4.xml'.");

        // We are now ready to ask some questions. Our goal is to obtain the expertise of all persons. In order words,
        // we want to ask the following conjunctive query:
        //
        //   Person(X),personKnowsAboutTopic(X,Y)
        //
        // The first thing we need to do is create an instance of the reasoner. Reasoners consume resources, so you should keep
        // them around only for as long as you need them. However, if you plan to ask several queries, it is *MUCH* more efficient
        // to use one and the same reasoner for all queries. One reasoner can be used to run several concurrent queries.
        Reasoner reasoner=ontology.createReasoner();

        // We now create the query object. A query consists of the following things:
        //
        // - a list of literals definint the query (they are created as usual)
        // - a list of distinguished variables, i.e. the variables that will be returned
        Query whatDoPeopleKnowAbout=reasoner.createQuery(new Literal[] {
            KAON2Manager.factory().literal(true,person,new Term[] { X }),
            KAON2Manager.factory().literal(true,personKnowsAboutTopic,new Term[] { X,Y }),
        },new Variable[] { X,Y});


            Predicate sub = KAON2Manager.factory().predicateSymbol("http://www.w3.org/1999/02/22-rdf-syntax-ns#type",2);

        //sub = KAON2Manager.factory().subClassOf(student,person).;

        whatDoPeopleKnowAbout=reasoner.createQuery(new Literal[] {

                KAON2Manager.factory().literal(true,sub,new Term[] { X,Y }),
            },new Variable[] { X,Y});

        whatDoPeopleKnowAbout=reasoner.createQuery(sub);

        whatDoPeopleKnowAbout=reasoner.createQuery(Namespaces.INSTANCE,"SELECT ?x ?y WHERE { ?x rdfs:subClassOf <http://kaon2.semanticweb.org/example4#person> }");


        // Creating the query has the effect of compiling it. A single query can be executed then several times.
        // A query is executed by invoking the Query.open() method.
        System.out.println();
        System.out.println("The list of people and things that they know about:");
        System.out.println("---------------------------------------------------");

        whatDoPeopleKnowAbout.open();
        // We now iterate over the query results.
        while (!whatDoPeopleKnowAbout.afterLast()) {
            // A query result is a set of tuples. The values in each tuple correspond to the distinguished variables.
            // In the above example, the distinguished variables are [X,Y]; this means that the first object in
            // the tuple is the value for the X variable, and the second one is the value for the Y variable.
            Object[] tupleBuffer=whatDoPeopleKnowAbout.tupleBuffer();
            System.out.println("Person '"+tupleBuffer[0].toString()+"' knows about '"+tupleBuffer[1].toString()+"'.");
            whatDoPeopleKnowAbout.next();
        }

        System.out.println("---------------------------------------------------");

        // If a query has been successfully opened, it should also be closed; otherwise, a resource leak occurs.
        whatDoPeopleKnowAbout.close();

        // After a query is closed, it may be reopened. The results of the query will reflect the changes to the ontology.
        // To see this, we shall add an additional fact that DIP is about OWL.
        changes.clear();
        changes.add(new OntologyChangeEvent(KAON2Manager.factory().objectPropertyMember(projectHasTopic,dip,owl),OntologyChangeEvent.ChangeType.ADD));
        ontology.applyChanges(changes);

        System.out.println();
        System.out.println("New facts have been successfully added to the ontology.");
        System.out.println();

        // We now execute the query again. The results reflect the change in the ontology.
        System.out.println("The same query executed again:");
        System.out.println("---------------------------------------------------");

        whatDoPeopleKnowAbout.open();
        while (!whatDoPeopleKnowAbout.afterLast()) {
            Object[] tupleBuffer=whatDoPeopleKnowAbout.tupleBuffer();
            System.out.println("Person '"+tupleBuffer[0].toString()+"' knows about '"+tupleBuffer[1].toString()+"'.");
            whatDoPeopleKnowAbout.next();
        }
        System.out.println("---------------------------------------------------");
        whatDoPeopleKnowAbout.close();

        // When a query is not needed any more, it should be disposed of. Forgetting to do so will result in
        // a fairly serious memory leak.
        whatDoPeopleKnowAbout.dispose();

        // Reasoners have to be disposed off after they are not used any more. Forgetting to do so will result in
        // a fairly serious memory leak!
        reasoner.dispose();

        // Don't forget to close the connection!
        connection.close();
    }
}

