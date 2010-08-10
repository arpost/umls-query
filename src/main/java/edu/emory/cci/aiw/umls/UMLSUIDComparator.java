package edu.emory.cci.aiw.umls;

import java.util.Comparator;

final class UMLSUIDComparator<T extends AbstractUMLSSearchUID> implements Comparator<T> {

	@Override
	public int compare(T uid1, T uid2) {
		return uid1.getValue().compareTo(uid2.getValue());
	}
}
