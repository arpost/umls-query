/*
 * #%L
 * UMLSQuery
 * %%
 * Copyright (C) 2012 - 2013 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package edu.emory.cci.aiw.umls;

/**
 * Contains all relevant information retrieved from a
 * {@link UMLSQueryExecutor#getCommonParent} query. This includes the parent
 * AUI, the two children, and the number of links from the parent to each child.
 * The generic parameter is the type of the children.
 * 
 * @author Michel Mansour
 * 
 * @param <T>
 *            the type of the children, which must implement the
 *            {@link ParentsQuerySearchUID} marker interface.
 */
public final class CommonParent<T extends ParentsQuerySearchUID> {
    private final AtomUID parent;
    private final T child1;
    private final T child2;
    private final int child1Links;
    private final int child2Links;

    /**
     * @return the parent {@link AtomUID} of the 2 child UIDs involved
     */
    public AtomUID getParent() {
        return parent;
    }

    /**
     * @return one of the child UIDs
     */
    public T getChild1() {
        return child1;
    }

    /**
     * @return the other of the child UIDs
     */
    public T getChild2() {
        return child2;
    }

    /**
     * @return the number of links from the first child to the parent
     */
    public int getChild1Links() {
        return child1Links;
    }

    /**
     * @return the number of links from the second child to the parent
     */
    public int getChild2Links() {
        return child2Links;
    }

    CommonParent(AtomUID parent, T child1, T child2, int child1Links,
            int child2Links) {
        this.parent = parent;
        this.child1 = child1;
        this.child2 = child2;
        this.child1Links = child1Links;
        this.child2Links = child2Links;
    }
}
