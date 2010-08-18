/**
 * This package consists of the main infrastructure for the UMLS query library.
 * It provides an interface, {@link UMLSQueryExecutor}, for performing common
 * queries. Queries are performed in terms of UMLS unique identifiers. These
 * include: {@link ConceptUID} (CUI), {@link AtomUID} (AUI), {@link LexicalUID}
 * (LUI), {@link StringUID} (SUI), and {@link TermUID} (TUI). In addition, most
 * of the searches can be restricted to a particular terminology, or {@link SAB}
 * .
 */
package edu.emory.cci.aiw.umls;

