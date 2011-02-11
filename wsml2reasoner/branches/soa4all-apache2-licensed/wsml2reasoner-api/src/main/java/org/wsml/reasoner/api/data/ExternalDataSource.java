/*
 * Copyright 2011, Semantic Technology Institute (STI) Innsbruck, 
 * University of Innsbruck, Technikerstrasse 21a, 6020 Innsbruck, Austria.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wsml.reasoner.api.data;

import java.util.Set;

import org.omwg.logicalexpression.terms.Term;
import org.wsmo.common.IRI;

/**
 * <p>
 * Interface for plugable datasources.
 * </p>
 * 
 * @author Richard Pöttler (richard dot poettler at deri dot at)
 */
public interface ExternalDataSource {

    /**
     * Returns all hasValue entries with the given criteria.
     * 
     * @param id
     *            the id you want the entries to restrict to (use
     *            <code>null</code> if you don't want to restrict on the id)
     * @param name
     *            the name you want the entries to restrict to (use
     *            <code>null</code> if you don't want to restrict on the name)
     * @param value
     *            the value you want the entries to restrict to (use
     *            <code>null</code> if you don't want to restrict on the
     *            value)
     * @return all hasValue entries fulfilling the given restrictions
     */
    public Set<HasValue> hasValue(final IRI id, final IRI name, final Term value);

    /**
     * Returns all memberOf entries with the given criteria.
     * 
     * @param id
     *            the id you want the entries to restrict to (use
     *            <code>null</code> if you don't want to restrict on the id)
     * @param concept
     *            the concept you want the entries to restrict to (use
     *            <code>null</code> if you don't want to restrict on the
     *            concept)
     * @return all memberOf entries fulfilling the given restrictions
     */
    public Set<MemberOf> memberOf(final IRI id, final IRI concept);

    /**
     * <p>
     * Represents an entry of the hasValue relation.
     * </p>
     * <p>
     * This implementation is immutable.
     * </p>
     * 
     * @author Richard Pöttler (richard dot poettler at deri dot at)
     */
    public static final class HasValue {

        /** The id of the value. */
        private final IRI id;

        /** The name of the value. */
        private final IRI name;

        /** The value. */
        private final Term value;

        /**
         * Constructs a new hasValue entry.
         * 
         * @param id
         *            the id of the entry
         * @param name
         *            the name of the entry
         * @param value
         *            the value of the entry
         * @throws IllegalArgumentException
         *             if the id is <code>null</code>
         * @throws IllegalArgumentException
         *             if the name is <code>null</code>
         * @throws IllegalArgumentException
         *             if the value is <code>null</code>
         */
        public HasValue(final IRI id, final IRI name, final Term value) {
            if (id == null) {
                throw new IllegalArgumentException("The id must not be null");
            }
            if (name == null) {
                throw new IllegalArgumentException("The name must not be null");
            }
            if (value == null) {
                throw new IllegalArgumentException("The value must not be null");
            }

            this.id = id;
            this.name = name;
            this.value = value;
        }

        /**
         * Returns the id of this value.
         * 
         * @return the id
         */
        public IRI getId() {
            return id;
        }

        /**
         * Returns the name of this value.
         * 
         * @return the value
         */
        public IRI getName() {
            return name;
        }

        /**
         * Returns the value
         * 
         * @return the value
         */
        public Term getValue() {
            return value;
        }

        public int hashCode() {
            int res = 17;
            res = res * 37 + id.hashCode();
            res = res * 37 + name.hashCode();
            res = res * 37 + value.hashCode();
            return res;
        }

        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof HasValue)) {
                return false;
            }
            final HasValue hv = (HasValue) o;
            return id.equals(hv.id) && name.equals(hv.name) && value.equals(hv.name);
        }

        public String toString() {
            return "[" + id + ", " + name + ", " + value + "]";
        }
    }

    /**
     * <p>
     * Represents an entry of the memberOf relation.
     * </p>
     * <p>
     * This implementation is immutable.
     * </p>
     * 
     * @author Richard Pöttler (richard dot poettler at deri dot at)
     */
    public static final class MemberOf {

        /** The id of the member. */
        private final IRI id;

        /** The concept of the member. */
        private final IRI concept;

        /**
         * Constructs a new memberOf entry.
         * 
         * @param id
         *            the id of the member
         * @param concept
         *            the concept of the member
         * @throws IllegalArgumentException
         *             if the id is <code>null</code>
         * @throws IllegalArgumentException
         *             if the concept is <code>null</code>
         */
        public MemberOf(final IRI id, final IRI concept) {
            if (id == null) {
                throw new IllegalArgumentException("The id must not be null");
            }
            if (concept == null) {
                throw new IllegalArgumentException("The concept must not be null");
            }

            this.id = id;
            this.concept = concept;
        }

        /**
         * Returns the id of this member.
         * 
         * @return the id
         */
        public IRI getId() {
            return id;
        }

        /**
         * Returns the name of this member.
         * 
         * @return the concept
         */
        public IRI getConcept() {
            return concept;
        }

        public int hashCode() {
            int res = 17;
            res = res * 37 + id.hashCode();
            res = res * 37 + concept.hashCode();
            return res;
        }

        public boolean equals(final Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof HasValue)) {
                return false;
            }
            final MemberOf mo = (MemberOf) o;
            return id.equals(mo.id) && concept.equals(mo.concept);
        }

        public String toString() {
            return "[" + id + ", " + concept + "]";
        }
    }
}
