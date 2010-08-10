package edu.emory.cci.aiw.umls;

public final class CommonParent<T extends ParentsQuerySearchUID> {
	private final AtomUID parent;
	private final T child1;
	private final T child2;
	private final int child1Links;
	private final int child2Links;

	/**
	 * @return the parent
	 */
	public AtomUID getParent() {
		return parent;
	}

	/**
	 * @return the child1
	 */
	public T getChild1() {
		return child1;
	}

	/**
	 * @return the child2
	 */
	public T getChild2() {
		return child2;
	}

	/**
	 * @return the child1Links
	 */
	public int getChild1Links() {
		return child1Links;
	}

	/**
	 * @return the child2Links
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
