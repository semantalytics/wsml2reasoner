package org.wsml.reasoner;

import java.util.LinkedList;
import java.util.List;

import org.omwg.ontology.Variable;

/**
 * Represents a conjunctive query against a knowledgebase which is a datalog
 * program.
 * 
 * @author Uwe Keller, DERI Innsbruck
 * @author Gabor Nagypal, FZI
 */
public class ConjunctiveQuery {

	private List<Literal> literals = null;

	public ConjunctiveQuery(List<Literal> literals) throws DatalogException {
		this.literals = literals;
	}

	public String toString() {
		String result = " ?- ";

		int i = 1;
		for (Literal l : this.literals) {
			result += l.toString();
			if (i < this.literals.size()) {
				result += ", ";
			}
			i++;
		}

		result += ".";
		return result;
	}

	public List<Literal> getLiterals() {
		return literals;
	}

	public List<Variable> getVariables() {
		List<Variable> result = new LinkedList<Variable>();
		for (Literal l : literals) {
			List<Variable> lvars = l.getVariables();
			for (Variable v : lvars) {
				if (!result.contains(v)) {
					result.add(v);
				}
			}
		}
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if ((obj == null) || (obj.getClass() != this.getClass()))
			return false;
		ConjunctiveQuery other = (ConjunctiveQuery) obj;
		return (literals == other.literals || (literals != null && literals
				.equals(other.literals)));
	}

	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + (null == literals ? 0 : literals.hashCode());
		return hash;
	}

}
