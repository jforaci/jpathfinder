package org.foraci.math.graph.pathfinder;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class SortedLinkedList
{
	/**
	 * Implements a private Iterator class for the SortedLinkedList
	 */
	private class SortedLinkedListIterator implements Iterator
	{
		private Node cur;
		private int iCur;

		SortedLinkedListIterator()
		{
			cur = head;
			iCur = 0;
		}

		public boolean hasNext()
		{
			return (cur != null);
		}

		public Object next()
		{
			if (cur == null)
				throw new NoSuchElementException();
			Node ret = cur;
			cur = cur.next;
			iCur++;
			return ret.val;
		}

		public void remove()
		{
			//call container remove
			cur = cur.next;
			SortedLinkedList.this.remove(iCur);
		}
	}

	/**
	 * Implements a private node type for SortedLinkedList elements
	 */
	private class Node implements Comparable
	{
		public Node prev;
		public Node next;
		public Comparable val;

		private Node(Comparable val)
		{
			prev = next = null;
			this.val = val;
		}

		private Node(Comparable val, Node prev, Node next)
		{
			this.prev = prev;
			this.next = next;
			this.val = val;
		}

		public final int compareTo(Object node)
		{
			Node other = (Node) node;
			return val.compareTo(other.val);
		}
	}

	//head,tail
	private Node head, tail;
	//size
	private int size;

	public SortedLinkedList()
	{
		size = 0;
		head = tail = null;
	}

	public SortedLinkedList(Collection coll)
	{
		this();
		addAll(coll);
	}

	/**
	 * Returns an <code>Iterator</code> object for this list
	 * 
	 * @return an <code>Iterator</code> object for this list
	 */
	public Iterator iterator()
	{
		return new SortedLinkedListIterator();
	}

	/**
	 * Adds an element to this list
	 * 
	 * @param obj
	 *            must implement Comparable interface.
	 */
	public void add(Comparable obj)
	{
		Node cur = head;
		while (cur != null && cur.val.compareTo(obj) < 0)
		{
			cur = cur.next;
		}
		if (cur == null)
		{
			if (size == 0) //empty
				head = tail = new Node(obj);
			else
			{ //must be at tail
				tail.next = new Node(obj);
				tail.next.prev = tail;
				tail = tail.next;
			}
		}
		else
		{ //cur referencing valid Node to insert before
			Node node = new Node(obj, cur.prev, cur);
			cur.prev = node;
			if (node.prev != null)
				node.prev.next = node;
			else
				head = node;
		}
		size++;
	}

	/**
	 * Adds all elements in <code>coll</code> to this list
	 * 
	 * @param coll
	 *            a Collection to add to this list
	 */
	public void addAll(Collection coll) throws IllegalArgumentException
	{
		Iterator i = coll.iterator();
		Object item = null;
		while (i.hasNext())
		{
			item = i.next();
			if (item instanceof Comparable)
				add((Comparable) item);
			else
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Removes an element at <code>index</code> (zero-based) from this list
	 * 
	 * @param index
	 *            Index to remove
	 * @return removed element
	 * @throws IndexOutOfBoundsException
	 *             if <code>index</code> not within [0..size()-1]
	 */
	public Comparable remove(int index) throws IndexOutOfBoundsException
	{
		if (index < 0 || index >= size)
			throw new IndexOutOfBoundsException("tried to remove index "
				+ index);
		Node prev, next = head;
		for (int i = 0; i < index; i++)
			next = next.next;
		Node removed = next;
		prev = next.prev;
		if (index == 0)
		{ //remove head
			head = next.next;
			if (head != null)
				head.prev = null;
		}
		else if (index == size - 1)
		{ //remove tail
			tail = prev;
			tail.next = null;
		}
		else
		{
			next = next.next;
			next.prev = prev;
			prev.next = next;
		}
		size--;
		return removed.val;
	}

	/**
	 * Removes an element at head of this list
	 * 
	 * @return removed element
	 * @throws IndexOutOfBoundsException
	 *             if list is empty
	 */
	public Comparable removeFirst() throws IndexOutOfBoundsException
	{
		return remove(0);
	}

	/**
	 * Removes an element at tail of this list
	 * 
	 * @return removed element
	 * @throws IndexOutOfBoundsException
	 *             if list is empty
	 */
	public Comparable removeLast() throws IndexOutOfBoundsException
	{
		return remove(size - 1);
	}

	public boolean exists(Comparable o)
	{
		Iterator i = new SortedLinkedListIterator();
		while (i.hasNext())
		{
			if (((Comparable) i.next()).compareTo(o) == 0)
				return true;
		}
		return false;
	}

	/**
	 * Returns a string representation of this SortedLinkedList object
	 * 
	 * @return string representation of this SortedLinkedList object
	 */
	public String toString()
	{
		Iterator i = new SortedLinkedListIterator();
		String str = "{";
		while (i.hasNext())
		{
			str += i.next().toString() + ",";
		}
		str += "}";
		return str;
	}

	/**
	 * Gets size (number of elements) of list
	 * 
	 * @return size of list
	 */
	public int size()
	{
		return size;
	}

	/**
	 * Is this list empty
	 * 
	 * @return true if empty, otherwise returns false
	 */
	public boolean isEmpty()
	{
		return (size == 0);
	}

	public Object[] toArray()
	{
		Object[] oArr = new Object[size];
		Node next = head;
		for (int i = 0; i < size; i++)
		{
			oArr[i] = next.val;
			next = next.next;
		}
		return oArr;
	}
}
