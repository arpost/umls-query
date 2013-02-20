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

public final class UMLSNoSuchTermException extends UMLSQueryException {
    
    public UMLSNoSuchTermException() {

    }

    public UMLSNoSuchTermException(String message) {
        super(message);
    }

    public UMLSNoSuchTermException(Throwable cause) {
        super(cause);
    }

    public UMLSNoSuchTermException(String message, Throwable cause) {
        super(message, cause);
    }
}
