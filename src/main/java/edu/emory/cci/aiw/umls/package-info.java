/*
 * #%L
 * UMLSQuery
 * %%
 * Copyright (C) 2012 Emory University
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

