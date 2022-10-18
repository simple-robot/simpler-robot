// /*
//  * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
//  *
//  * This code is free software; you can redistribute it and/or modify it
//  * under the terms of the GNU General Public License version 2 only, as
//  * published by the Free Software Foundation.  Oracle designates this
//  * particular file as subject to the "Classpath" exception as provided
//  * by Oracle in the LICENSE file that accompanied this code.
//  *
//  * This code is distributed in the hope that it will be useful, but WITHOUT
//  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//  * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
//  * version 2 for more details (a copy is included in the LICENSE file that
//  * accompanied this code).
//  *
//  * You should have received a copy of the GNU General Public License version
//  * 2 along with this work; if not, write to the Free Software Foundation,
//  * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
//  *
//  * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
//  * or visit www.oracle.com if you need additional information or have any
//  * questions.
//  */
//
// /*
//  * This file is available under and governed by the GNU General Public
//  * License version 2 only, as published by the Free Software Foundation.
//  * However, the following notice accompanied the original version of this
//  * file:
//  *
//  * Written by Doug Lea and Martin Buchholz with assistance from members of
//  * JCP JSR-166 Expert Group and released to the public domain, as explained
//  * at http://creativecommons.org/publicdomain/zero/1.0/
//  */
// package love.forte.simbot.utils
//
// import kotlinx.atomicfu.AtomicRef
// import kotlinx.atomicfu.atomic
// import kotlin.jvm.Transient
//
// public class ConcurrentLinkedQueue<E> {
//     internal class Node<E> private constructor(
//         val item: AtomicRef<E?>,
//         val next: AtomicRef<Node<E>?>,
//     ) {
//        
//         /**
//          * Constructs a node holding item.  Uses relaxed write because
//          * item can only be seen after piggy-backing publication via CAS.
//          */
//         constructor(item: E): this(atomic(item), atomic(null))
//        
//         /** Constructs a dead dummy node.  */
//         constructor(): this(atomic(null), atomic(null))
//        
//         fun appendRelaxed(next: Node<E>) {
//             // assert next != null;
//             // assert this.next == null;
//             this.next.value = next
//             // NEXT.set(this, next)
//         }
//        
//         fun casItem(cmp: E, `val`: E?): Boolean {
//             // assert item == cmp || item == null;
//             // assert cmp != null;
//             // assert val == null;
//             return this.item.compareAndSet(cmp, `val`)
//             // return ITEM.compareAndSet(this, cmp, `val`)
//         }
//     }
//    
//     /**
//      * A node from which the first live (non-deleted) node (if any)
//      * can be reached in O(1) time.
//      * Invariants:
//      * - all live nodes are reachable from head via succ()
//      * - head != null
//      * - (tmp = head).next != tmp || tmp != head
//      * Non-invariants:
//      * - head.item may or may not be null.
//      * - it is permitted for tail to lag behind head, that is, for tail
//      * to not be reachable from head!
//      */
//     @Transient
//     private val head: AtomicRef<Node<E>?> = atomic(null)
//    
//     /**
//      * A node from which the last node on list (that is, the unique
//      * node with node.next == null) can be reached in O(1) time.
//      * Invariants:
//      * - the last node is always reachable from tail via succ()
//      * - tail != null
//      * Non-invariants:
//      * - tail.item may or may not be null.
//      * - it is permitted for tail to lag behind head, that is, for tail
//      * to not be reachable from head!
//      * - tail.next may or may not be self-linked.
//      */
//     @Transient
//     private val tail: AtomicRef<Node<E>?> = atomic(null)
//    
//     /**
//      * Creates a `ConcurrentLinkedQueue` that is initially empty.
//      */
//     public constructor() {
//         val initValue = Node<E>()
//         tail.value = initValue
//         head.value = initValue
//     }
//    
//     /**
//      * Creates a `ConcurrentLinkedQueue`
//      * initially containing the elements of the given collection,
//      * added in traversal order of the collection's iterator.
//      *
//      * @param c the collection of elements to initially contain
//      * @throws NullPointerException if the specified collection or any
//      * of its elements are null
//      */
//     public constructor(c: Collection<E>) {
//         var h: Node<E>? = null
//         var t: Node<E>? = null
//         for (e in c) {
//             val newNode = Node(e)
//             if (h == null) {
//                 t = newNode
//                 h = t
//             } else t!!.appendRelaxed(newNode.also { t = it })
//         }
//         if (h == null) {
//             t = Node()
//             h = t
//         }
//         head.value = h
//         tail.value = t
//     }
//     // Have to override just to update the javadoc
//     // /**
//     //  * Inserts the specified element at the tail of this queue.
//     //  * As the queue is unbounded, this method will never throw
//     //  * [IllegalStateException] or return `false`.
//     //  *
//     //  * @return `true` (as specified by [Collection.add])
//     //  * @throws NullPointerException if the specified element is null
//     //  */
//     // public fun add(e: E): Boolean {
//     //     return offer(e)
//     // }
//    
//     /**
//      * Tries to CAS head to p. If successful, repoint old head to itself
//      * as sentinel for succ(), below.
//      */
//     internal fun updateHead(h: Node<E>, p: Node<E>) {
//         // assert h != null && p != null && (h == p || h.item == null);
//         if (h != p && head.compareAndSet(h, p)) {
//             //  next.setRelease(h, h)
//             h.next.value = h
//         }
//     }
//    
//     /**
//      * Returns the successor of p, or the head node if p.next has been
//      * linked to self, which will only be true if traversing with a
//      * stale pointer that is now off the list.
//      */
//     internal fun succ(p: Node<E>): Node<E>? {
//         // var p = p
//         val pNext = p.next.value
//         if (p == pNext) {
//             return head.value
//         } else {
//             return pNext
//         }
//         // if (p == p.next.also { p = it }) p = head
//         // return p
//     }
//    
//     /**
//      * Tries to CAS pred.next (or head, if pred is null) from c to p.
//      * Caller must ensure that we're not unlinking the trailing node.
//      */
//     private fun tryCasSuccessor(pred: Node<E>?, c: Node<E>, p: Node<E>): Boolean {
//         // assert p != null;
//         // assert c.item == null;
//         // assert c != p;
//         if (pred != null) return pred.next.compareAndSet(c, p) // NEXT.compareAndSet(pred, c, p)
//         if (head.compareAndSet(c, p)) {
//             c.next.value = c
//             // NEXT.setRelease(c, c)
//             return true
//         }
//         return false
//     }
//    
//     /**
//      * Collapse dead nodes between pred and q.
//      * @param pred the last known live node, or null if none
//      * @param c the first dead node
//      * @param p the last dead node
//      * @param q p.next: the next live node, or null if at end
//      * @return either old pred or p if pred dead or CAS failed
//      */
//     private fun skipDeadNodes(pred: Node<E>?, c: Node<E>, p: Node<E>, q: Node<E>?): Node<E>? {
//         // assert pred != c;
//         // assert p != q;
//         // assert c.item == null;
//         // assert p.item == null;
//         var q1 = q
//         if (q1 == null) {
//             // Never unlink trailing node.
//             if (c == p) return pred
//             q1 = p
//         }
//        
//         return if (tryCasSuccessor(pred, c, q1)
//             && (pred == null || pred.item.value != null)
//         ) pred else p
//     }
//    
//     /**
//      * Inserts the specified element at the tail of this queue.
//      * As the queue is unbounded, this method will never return `false`.
//      *
//      * @return `true` (as specified by [Queue.offer])
//      * @throws NullPointerException if the specified element is null
//      */
//     public fun offer(e: E): Boolean {
//         val newNode = Node(e)
//         var t = tail.value
//         var p = t
//         while (true) {
//             val pNext = p!!.next.value
//             if (pNext == null) {
//                 // p is last node
//                 if (p.next.compareAndSet(null, newNode)) {
//                     // Successful CAS is the linearization point
//                     // for e to become an element of this queue,
//                     // and for newNode to become "live".
//                     if (p != t) // hop two nodes at a time; failure is OK
//                         // TAIL.weakCompareAndSet(this, t, newNode)
//                         tail.compareAndSet(t, newNode)
//                     return true
//                 }
//                 // Lost CAS race to another thread; re-read next
//             } else if (p == pNext) {
//                 // We have fallen off list.  If tail is unchanged, it
//                 // will also be off-list, in which case we need to
//                 // jump to head, from which all live nodes are always
//                 // reachable.  Else the new tail is a better bet.
//                
//                 // p = (t != (t = tail)) ? t : head;
//                 p = if (t != tail.value.also { t = it }) t else head.value
//             } else {
//                 // Check for tail updates after two hops.
//                
//                 // p = (p != t && t != (t = tail)) ? t : q;
//                 p = if (p != t && t != tail.value.also { t = it }) t else pNext
//             }
//         }
//     }
//    
//     public fun poll(): E? {
//         restartFromHead@ while (true) {
//             val h = head.value
//             var p = h
//             var q: Node<E>? = null
//             while (true) {
//                 val item: E? = p!!.item.value
//                 if (item != null && p.casItem(item, null)) {
//                     // Successful CAS is the linearization point
//                     // for item to be removed from this queue.
//                     if (p != h) // hop two nodes at a time
//                         q = p.next.value
//                         updateHead(h!!, if (q != null) q else p)
//                     return item
//                 } else if (p.next.value.also { q = it!! } == null) {
//                     updateHead(h!!, p)
//                     return null
//                 } else if (p == q) {
//                     continue@restartFromHead
//                 }
//                 p = q
//             }
//         }
//     }
//    
//     public fun peek(): E {
//         restartFromHead@ while (true) {
//             val h = head
//             var p = h
//             var q: Node<E>? = null
//             while (true) {
//                 val item: E
//                 if (p.item.also { item = it } != null
//                     || p.next.also { q = it!! } == null) {
//                     updateHead(h, p)
//                     return item
//                 } else if (p == q) {
//                     continue@restartFromHead
//                 }
//                 p = q
//             }
//         }
//     }
//    
//     /**
//      * Returns the first live (non-deleted) node on list, or null if none.
//      * This is yet another variant of poll/peek; here returning the
//      * first node, not element.  We could make peek() a wrapper around
//      * first(), but that would cost an extra volatile read of item,
//      * and the need to add a retry loop to deal with the possibility
//      * of losing a race to a concurrent poll().
//      */
//     fun first(): Node<E?>? {
//         restartFromHead@ while (true) {
//             val h = head
//             var p = h
//             var q: Node<E?>
//             while (true) {
//                 val hasItem = p.item != null
//                 if (hasItem || p.next.also { q = it!! } == null) {
//                     updateHead(h, p)
//                     return if (hasItem) p else null
//                 } else if (p == q) {
//                     continue@restartFromHead
//                 }
//                 p = q
//             }
//         }
//     }
//    
//     /**
//      * Returns `true` if this queue contains no elements.
//      *
//      * @return `true` if this queue contains no elements
//      */
//     val isEmpty: Boolean
//         get() = first() == null
//    
//     /**
//      * Returns the number of elements in this queue.  If this queue
//      * contains more than `Integer.MAX_VALUE` elements, returns
//      * `Integer.MAX_VALUE`.
//      *
//      *
//      * Beware that, unlike in most collections, this method is
//      * *NOT* a constant-time operation. Because of the
//      * asynchronous nature of these queues, determining the current
//      * number of elements requires an O(n) traversal.
//      * Additionally, if elements are added or removed during execution
//      * of this method, the returned result may be inaccurate.  Thus,
//      * this method is typically not very useful in concurrent
//      * applications.
//      *
//      * @return the number of elements in this queue
//      */
//     public override fun size(): Int {
//         restartFromHead@ while (true) {
//             var count = 0
//             var p = first()
//             while (p != null) {
//                 if (p.item != null) if (++count == Int.MAX_VALUE) break // @see Collection.size()
//                 if (p == p.next.also { p = it }) {
//                     continue@restartFromHead
//                 }
//             }
//             return count
//         }
//     }
//    
//     /**
//      * Returns `true` if this queue contains the specified element.
//      * More formally, returns `true` if and only if this queue contains
//      * at least one element `e` such that `o.equals(e)`.
//      *
//      * @param o object to be checked for containment in this queue
//      * @return `true` if this queue contains the specified element
//      */
//     public override operator fun contains(o: Any): Boolean {
//         if (o == null) return false
//         restartFromHead@ while (true) {
//             var p = head
//             var pred: Node<E?>? = null
//             while (p != null) {
//                 var q = p.next
//                 val item: E
//                 if (p.item.also { item = it } != null) {
//                     if (o == item) return true
//                     pred = p
//                     p = q
//                     continue
//                 }
//                 val c: Node<E?> = p
//                 while (true) {
//                     if (q == null || q.item != null) {
//                         pred = skipDeadNodes(pred, c, p, q)
//                         p = q
//                         break
//                     }
//                     if (p == q.also { p = it }) {
//                         continue@restartFromHead
//                     }
//                     q = p.next
//                 }
//             }
//             return false
//         }
//     }
//    
//     /**
//      * Removes a single instance of the specified element from this queue,
//      * if it is present.  More formally, removes an element `e` such
//      * that `o.equals(e)`, if this queue contains one or more such
//      * elements.
//      * Returns `true` if this queue contained the specified element
//      * (or equivalently, if this queue changed as a result of the call).
//      *
//      * @param o element to be removed from this queue, if present
//      * @return `true` if this queue changed as a result of the call
//      */
//     public override fun remove(o: Any): Boolean {
//         if (o == null) return false
//         restartFromHead@ while (true) {
//             var p = head
//             var pred: Node<E?>? = null
//             while (p != null) {
//                 var q = p.next
//                 val item: E
//                 if (p.item.also { item = it } != null) {
//                     if (o == item && p.casItem(item, null)) {
//                         skipDeadNodes(pred, p, p, q)
//                         return true
//                     }
//                     pred = p
//                     p = q
//                     continue
//                 }
//                 val c: Node<E?> = p
//                 while (true) {
//                     if (q == null || q.item != null) {
//                         pred = skipDeadNodes(pred, c, p, q)
//                         p = q
//                         break
//                     }
//                     if (p == q.also { p = it }) {
//                         continue@restartFromHead
//                     }
//                     q = p.next
//                 }
//             }
//             return false
//         }
//     }
//    
//     /**
//      * Appends all of the elements in the specified collection to the end of
//      * this queue, in the order that they are returned by the specified
//      * collection's iterator.  Attempts to `addAll` of a queue to
//      * itself result in `IllegalArgumentException`.
//      *
//      * @param c the elements to be inserted into this queue
//      * @return `true` if this queue changed as a result of the call
//      * @throws NullPointerException if the specified collection or any
//      * of its elements are null
//      * @throws IllegalArgumentException if the collection is this queue
//      */
//     public override fun addAll(c: Collection<E>): Boolean {
//         require(!(c === this))
//        
//         // Copy c into a private chain of Nodes
//         var beginningOfTheEnd: Node<E>? = null
//         var last: Node<E>? = null
//         for (e in c) {
//             val newNode = Node<E>(java.util.Objects.requireNonNull<E>(e))
//             if (beginningOfTheEnd == null) {
//                 last = newNode
//                 beginningOfTheEnd = last
//             } else last!!.appendRelaxed(newNode.also { last = it })
//         }
//         if (beginningOfTheEnd == null) return false
//        
//         // Atomically append the chain at the tail of this collection
//         var t = tail
//         var p = t
//         while (true) {
//             val q = p.next
//             if (q == null) {
//                 // p is last node
//                 if (NEXT.compareAndSet(p, null, beginningOfTheEnd)) {
//                     // Successful CAS is the linearization point
//                     // for all elements to be added to this queue.
//                     if (!TAIL.weakCompareAndSet(this, t, last)) {
//                         // Try a little harder to update tail,
//                         // since we may be adding many elements.
//                         t = tail
//                         if (last!!.next == null) TAIL.weakCompareAndSet(this, t, last)
//                     }
//                     return true
//                 }
//                 // Lost CAS race to another thread; re-read next
//             } else if (p == q) // We have fallen off list.  If tail is unchanged, it
//             // will also be off-list, in which case we need to
//             // jump to head, from which all live nodes are always
//             // reachable.  Else the new tail is a better bet.
//                 p = if (t != tail.also { t = it }) t else head else  // Check for tail updates after two hops.
//                 p = if (p != t && t != tail.also { t = it }) t else q
//         }
//     }
//    
//     override fun toString(): String {
//         var a: Array<String?>? = null
//         restartFromHead@ while (true) {
//             var charLength = 0
//             var size = 0
//             var p = first()
//             while (p != null) {
//                 val item: E
//                 if (p.item.also { item = it } != null) {
//                     if (a == null) a = arrayOfNulls(4) else if (size == a.size) a =
//                         java.util.Arrays.copyOf<String>(a, 2 * size)
//                     val s = item.toString()
//                     a[size++] = s
//                     charLength += s.length
//                 }
//                 if (p == p.next.also { p = it }) {
//                     continue@restartFromHead
//                 }
//             }
//             return if (size == 0) "[]" else Helpers.toString(a, size, charLength)
//         }
//     }
//    
//     private fun toArrayInternal(a: Array<Any?>?): Array<Any?> {
//         var x = a
//         restartFromHead@ while (true) {
//             var size = 0
//             var p = first()
//             while (p != null) {
//                 val item: E
//                 if (p.item.also { item = it } != null) {
//                     if (x == null) x = arrayOfNulls(4) else if (size == x.size) x =
//                         java.util.Arrays.copyOf<Any>(x, 2 * (size + 4))
//                     x[size++] = item
//                 }
//                 if (p == p.next.also { p = it }) {
//                     continue@restartFromHead
//                 }
//             }
//             if (x == null) return arrayOfNulls(0) else if (a != null && size <= a.size) {
//                 if (a != x) java.lang.System.arraycopy(x, 0, a, 0, size)
//                 if (size < a.size) a[size] = null
//                 return a
//             }
//             return if (size == x.size) x else java.util.Arrays.copyOf<Any>(x, size)
//         }
//     }
//    
//     /**
//      * Returns an array containing all of the elements in this queue, in
//      * proper sequence.
//      *
//      *
//      * The returned array will be "safe" in that no references to it are
//      * maintained by this queue.  (In other words, this method must allocate
//      * a new array).  The caller is thus free to modify the returned array.
//      *
//      *
//      * This method acts as bridge between array-based and collection-based
//      * APIs.
//      *
//      * @return an array containing all of the elements in this queue
//      */
//     public override fun toArray(): Array<Any> {
//         return toArrayInternal(null)
//     }
//    
//     /**
//      * Returns an array containing all of the elements in this queue, in
//      * proper sequence; the runtime type of the returned array is that of
//      * the specified array.  If the queue fits in the specified array, it
//      * is returned therein.  Otherwise, a new array is allocated with the
//      * runtime type of the specified array and the size of this queue.
//      *
//      *
//      * If this queue fits in the specified array with room to spare
//      * (i.e., the array has more elements than this queue), the element in
//      * the array immediately following the end of the queue is set to
//      * `null`.
//      *
//      *
//      * Like the [.toArray] method, this method acts as bridge between
//      * array-based and collection-based APIs.  Further, this method allows
//      * precise control over the runtime type of the output array, and may,
//      * under certain circumstances, be used to save allocation costs.
//      *
//      *
//      * Suppose `x` is a queue known to contain only strings.
//      * The following code can be used to dump the queue into a newly
//      * allocated array of `String`:
//      *
//      * <pre> `String[] y = x.toArray(new String[0]);`</pre>
//      *
//      * Note that `toArray(new Object[0])` is identical in function to
//      * `toArray()`.
//      *
//      * @param a the array into which the elements of the queue are to
//      * be stored, if it is big enough; otherwise, a new array of the
//      * same runtime type is allocated for this purpose
//      * @return an array containing all of the elements in this queue
//      * @throws ArrayStoreException if the runtime type of the specified array
//      * is not a supertype of the runtime type of every element in
//      * this queue
//      * @throws NullPointerException if the specified array is null
//      */
//     public override fun <T> toArray(a: Array<T>): Array<T> {
//         java.util.Objects.requireNonNull<Array<T>>(a)
//         return toArrayInternal(a) as Array<T>
//     }
//    
//     /**
//      * Returns an iterator over the elements in this queue in proper sequence.
//      * The elements will be returned in order from first (head) to last (tail).
//      *
//      *
//      * The returned iterator is
//      * [*weakly consistent*](package-summary.html#Weakly).
//      *
//      * @return an iterator over the elements in this queue in proper sequence
//      */
//     public override operator fun iterator(): MutableIterator<E> {
//         return Itr()
//     }
//    
//     private inner class Itr() : MutableIterator<E> {
//         /**
//          * Next node to return item for.
//          */
//         private var nextNode: Node<E?>? = null
//        
//         /**
//          * nextItem holds on to item fields because once we claim
//          * that an element exists in hasNext(), we must return it in
//          * the following next() call even if it was in the process of
//          * being removed when hasNext() was called.
//          */
//         private var nextItem: E? = null
//        
//         /**
//          * Node of the last returned item, to support remove.
//          */
//         private var lastRet: Node<E?>? = null
//        
//         init {
//             restartFromHead@ while (true) {
//                 var h: Node<E?>?
//                 var p: Node<E?>
//                 var q: Node<E?>
//                 p = head.also { h = it }
//                 while (true) {
//                     val item: E
//                     if (p.item.also { item = it } != null) {
//                         nextNode = p
//                         nextItem = item
//                         break
//                     } else if (p.next.also { q = it } == null) break else if (p == q) {
//                         continue@restartFromHead
//                     }
//                     p = q
//                 }
//                 updateHead(h, p)
//                 return
//             }
//         }
//        
//         override fun hasNext(): Boolean {
//             return nextItem != null
//         }
//        
//         override fun next(): E {
//             val pred = nextNode ?: throw NoSuchElementException()
//             // assert nextItem != null;
//             lastRet = pred
//             var item: E? = null
//             var p: Node<E?>? = succ(pred)
//             var q: Node<E?>
//             while (true) {
//                 if (p == null || p.item.also { item = it } != null) {
//                     nextNode = p
//                     val x = nextItem
//                     nextItem = item
//                     return x
//                 }
//                 // unlink deleted nodes
//                 if (succ(p).also { q = it } != null) NEXT.compareAndSet(pred, p, q)
//                 p = q
//             }
//         }
//        
//         // Default implementation of forEachRemaining is "good enough".
//         override fun remove() {
//             val l = lastRet ?: throw IllegalStateException()
//             // rely on a future traversal to relink.
//             l.item = null
//             lastRet = null
//         }
//     }
//    
//     /**
//      * Saves this queue to a stream (that is, serializes it).
//      *
//      * @param s the stream
//      * @throws java.io.IOException if an I/O error occurs
//      * @serialData All of the elements (each an `E`) in
//      * the proper order, followed by a null
//      */
//     @Throws(IOException::class)
//     private fun writeObject(s: ObjectOutputStream) {
//        
//         // Write out any hidden stuff
//         s.defaultWriteObject()
//        
//         // Write out all elements in the proper order.
//         var p = first()
//         while (p != null) {
//             val item: E
//             if (p.item.also { item = it } != null) s.writeObject(item)
//             p = succ(p)
//         }
//        
//         // Use trailing null as sentinel
//         s.writeObject(null)
//     }
//    
//     /**
//      * Reconstitutes this queue from a stream (that is, deserializes it).
//      * @param s the stream
//      * @throws ClassNotFoundException if the class of a serialized object
//      * could not be found
//      * @throws java.io.IOException if an I/O error occurs
//      */
//     @Throws(IOException::class, ClassNotFoundException::class)
//     private fun readObject(s: ObjectInputStream) {
//         s.defaultReadObject()
//        
//         // Read in elements until trailing null sentinel found
//         var h: Node<E?>? = null
//         var t: Node<E?>? = null
//         var item: Any
//         while (s.readObject().also { item = it } != null) {
//             val newNode = Node<E?>(item as E)
//             if (h == null) {
//                 t = newNode
//                 h = t
//             } else t!!.appendRelaxed(newNode.also { t = it })
//         }
//         if (h == null) {
//             t = Node()
//             h = t
//         }
//         head = h
//         tail = t
//     }
//    
//     /** A customized variant of Spliterators.IteratorSpliterator  */
//     internal inner class CLQSpliterator : java.util.Spliterator<E> {
//         var current // current node; null until initialized
//                 : Node<E>? = null
//         var batch // batch size for splits
//                 = 0
//         var exhausted // true when no more nodes
//                 = false
//        
//         override fun trySplit(): java.util.Spliterator<E> {
//             var p: Node<E?>?
//             var q: Node<E>
//             if (current().also { p = it } == null || p!!.next.also { q = it } == null) return null
//             var i = 0
//             val n: Int = java.lang.Math.min(batch + 1, Companion.MAX_BATCH).also { batch = it }
//             var a: Array<Any?>? = null
//             do {
//                 val e: E
//                 if (p!!.item.also { e = it } != null) {
//                     if (a == null) a = arrayOfNulls(n)
//                     a[i++] = e
//                 }
//                 if (p == q.also { p = it }) p = first()
//             } while (p != null && p!!.next.also { q = it } != null && i < n)
//             setCurrent(p)
//             return if (i == 0) null else java.util.Spliterators.spliterator<E>(
//                 a, 0, i, java.util.Spliterator.ORDERED or
//                         java.util.Spliterator.NONNULL or
//                         java.util.Spliterator.CONCURRENT
//             )
//         }
//        
//         override fun forEachRemaining(action: java.util.function.Consumer<in E>) {
//             java.util.Objects.requireNonNull(action)
//             val p: Node<E?>
//             if (current().also { p = it } != null) {
//                 current = null
//                 exhausted = true
//                 forEachFrom(action, p)
//             }
//         }
//        
//         override fun tryAdvance(action: java.util.function.Consumer<in E>): Boolean {
//             java.util.Objects.requireNonNull(action)
//             var p: Node<E?>?
//             if (current().also { p = it } != null) {
//                 var e: E?
//                 do {
//                     e = p!!.item
//                     if (p == p!!.next.also { p = it }) p = first()
//                 } while (e == null && p != null)
//                 setCurrent(p)
//                 if (e != null) {
//                     action.accept(e)
//                     return true
//                 }
//             }
//             return false
//         }
//        
//         private fun setCurrent(p: Node<E?>?) {
//             if (p.also { current = it } == null) exhausted = true
//         }
//        
//         private fun current(): Node<E> {
//             var p: Node<E>
//             if (current.also { p = it!! } == null && !exhausted) setCurrent(first().also { p = it })
//             return p
//         }
//        
//         override fun estimateSize(): Long {
//             return Long.MAX_VALUE
//         }
//        
//         override fun characteristics(): Int {
//             return java.util.Spliterator.ORDERED or
//                     java.util.Spliterator.NONNULL or
//                     java.util.Spliterator.CONCURRENT
//         }
//        
//         companion object {
//             const val MAX_BATCH = 1 shl 25 // max batch array size;
//         }
//     }
//    
//     /**
//      * Returns a [Spliterator] over the elements in this queue.
//      *
//      *
//      * The returned spliterator is
//      * [*weakly consistent*](package-summary.html#Weakly).
//      *
//      *
//      * The `Spliterator` reports [Spliterator.CONCURRENT],
//      * [Spliterator.ORDERED], and [Spliterator.NONNULL].
//      *
//      * @implNote
//      * The `Spliterator` implements `trySplit` to permit limited
//      * parallelism.
//      *
//      * @return a `Spliterator` over the elements in this queue
//      * @since 1.8
//      */
//     public override fun spliterator(): java.util.Spliterator<E> {
//         return CLQSpliterator()
//     }
//    
//     /**
//      * @throws NullPointerException {@inheritDoc}
//      */
//     public override fun removeIf(filter: java.util.function.Predicate<in E>): Boolean {
//         java.util.Objects.requireNonNull(filter)
//         return bulkRemove(filter)
//     }
//    
//     /**
//      * @throws NullPointerException {@inheritDoc}
//      */
//     public override fun removeAll(c: Collection<*>): Boolean {
//         java.util.Objects.requireNonNull(c)
//         return bulkRemove(java.util.function.Predicate<E> { e: E -> c.contains(e) })
//     }
//    
//     /**
//      * @throws NullPointerException {@inheritDoc}
//      */
//     public override fun retainAll(c: Collection<*>): Boolean {
//         java.util.Objects.requireNonNull(c)
//         return bulkRemove(java.util.function.Predicate<E> { e: E -> !c.contains(e) })
//     }
//    
//     public override fun clear() {
//         bulkRemove(java.util.function.Predicate<E> { e: E -> true })
//     }
//    
//     /** Implementation of bulk remove methods.  */
//     private fun bulkRemove(filter: java.util.function.Predicate<in E>): Boolean {
//         var removed = false
//         restartFromHead@ while (true) {
//             var hops = MAX_HOPS
//             // c will be CASed to collapse intervening dead nodes between
//             // pred (or head if null) and p.
//             var p = head
//             var c = p
//             var pred: Node<E?>? = null
//             var q: Node<E?>?
//             while (p != null) {
//                 q = p.next
//                 val item: E
//                 var pAlive: Boolean
//                 if ((p.item.also { item = it } != null).also { pAlive = it }) {
//                     if (filter.test(item)) {
//                         if (p.casItem(item, null)) removed = true
//                         pAlive = false
//                     }
//                 }
//                 if (pAlive || q == null || --hops == 0) {
//                     // p might already be self-linked here, but if so:
//                     // - CASing head will surely fail
//                     // - CASing pred's next will be useless but harmless.
//                     if (c != p && !tryCasSuccessor(pred, c, p.also { c = it })
//                         || pAlive
//                     ) {
//                         // if CAS failed or alive, abandon old pred
//                         hops = MAX_HOPS
//                         pred = p
//                         c = q
//                     }
//                 } else if (p == q) {
//                     continue@restartFromHead
//                 }
//                 p = q
//             }
//             return removed
//         }
//     }
//    
//     /**
//      * Runs action on each element found during a traversal starting at p.
//      * If p is null, the action is not run.
//      */
//     fun forEachFrom(action: java.util.function.Consumer<in E>, p: Node<E?>?) {
//         var p = p
//         var pred: Node<E?>? = null
//         while (p != null) {
//             var q = p.next
//             val item: E
//             if (p.item.also { item = it } != null) {
//                 action.accept(item)
//                 pred = p
//                 p = q
//                 continue
//             }
//             val c: Node<E?> = p
//             while (true) {
//                 if (q == null || q.item != null) {
//                     pred = skipDeadNodes(pred, c, p, q)
//                     p = q
//                     break
//                 }
//                 if (p == q.also { p = it }) {
//                     pred = null
//                     p = head
//                     break
//                 }
//                 q = p!!.next
//             }
//         }
//     }
//    
//     /**
//      * @throws NullPointerException {@inheritDoc}
//      */
//     public override fun forEach(action: java.util.function.Consumer<in E>) {
//         java.util.Objects.requireNonNull(action)
//         forEachFrom(action, head)
//     }
//    
//     public companion object {
//         private const val serialVersionUID = 196745693267521676L
//        
//         /**
//          * Tolerate this many consecutive dead nodes before CAS-collapsing.
//          * Amortized cost of clear() is (1 + 1/MAX_HOPS) CASes per element.
//          */
//         private const val MAX_HOPS = 8
//        
//         // VarHandle mechanics
//         // private val HEAD = atomic
//         // private val TAIL: VarHandle
//         // internal val ITEM: VarHandle
//         // internal val NEXT: VarHandle
//        
//         // init {
//         //
//         //     val l: java.lang.invoke.MethodHandles.Lookup = java.lang.invoke.MethodHandles.lookup()
//         //     HEAD = love.forte.simbot.utils.l.findVarHandle(
//         //         ConcurrentLinkedQueue::class.java, "head",
//         //         Node::class.java
//         //     )
//         //     TAIL = love.forte.simbot.utils.l.findVarHandle(
//         //         ConcurrentLinkedQueue::class.java, "tail",
//         //         Node::class.java
//         //     )
//         //     ITEM = love.forte.simbot.utils.l.findVarHandle(Node::class.java, "item", Any::class.java)
//         //     NEXT = love.forte.simbot.utils.l.findVarHandle(Node::class.java, "next", Node::class.java)
//         // }
//     }
// }