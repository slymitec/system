package indi.sly.system.common.supports;

import indi.sly.system.common.lang.ConditionParametersException;
import indi.sly.system.common.lang.StatusUnreadableException;

import java.io.Serializable;
import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class CollectionUtil {
    public static <T> List<T> unmodifiable(List<? extends T> list) {
        return (list instanceof RandomAccess ? new UnmodifiableRandomAccessList<>(list) : new UnmodifiableList<>(list));
    }

    public static <K, V> Map<K, V> unmodifiable(Map<? extends K, ? extends V> map) {
        return new UnmodifiableMap<>(map);
    }

    public static <T> Set<T> unmodifiable(Set<? extends T> set) {
        return new UnmodifiableSet<>(set);
    }

    private static <T> Collection<T> unmodifiable(Collection<? extends T> collection) {
        return new UnmodifiableCollection<>(collection);
    }

    private static class UnmodifiableCollection<E> implements Collection<E>, Serializable {
        private static final long serialVersionUID = 1820017752578914078L;

        final Collection<? extends E> c;

        UnmodifiableCollection(Collection<? extends E> c) {
            if (c == null)
                throw new NullPointerException();
            this.c = c;
        }

        public int size() {
            return c.size();
        }

        public boolean isEmpty() {
            return c.isEmpty();
        }

        public boolean contains(Object o) {
            return c.contains(o);
        }

        public Object[] toArray() {
            return c.toArray();
        }

        public <T> T[] toArray(T[] a) {
            return c.toArray(a);
        }

        public <T> T[] toArray(IntFunction<T[]> f) {
            return c.toArray(f);
        }

        public String toString() {
            return c.toString();
        }

        public Iterator<E> iterator() {
            return new Iterator<E>() {
                private final Iterator<? extends E> i = c.iterator();

                public boolean hasNext() {
                    return i.hasNext();
                }

                public E next() {
                    return i.next();
                }

                public void remove() {
                    throw new StatusUnreadableException();
                }

                @Override
                public void forEachRemaining(Consumer<? super E> action) {
                    // Use backing collection version
                    i.forEachRemaining(action);
                }
            };
        }

        public boolean add(E e) {
            throw new StatusUnreadableException();
        }

        public boolean remove(Object o) {
            throw new StatusUnreadableException();
        }

        public boolean containsAll(Collection<?> coll) {
            return c.containsAll(coll);
        }

        public boolean addAll(Collection<? extends E> coll) {
            throw new StatusUnreadableException();
        }

        public boolean removeAll(Collection<?> coll) {
            throw new StatusUnreadableException();
        }

        public boolean retainAll(Collection<?> coll) {
            throw new StatusUnreadableException();
        }

        public void clear() {
            throw new StatusUnreadableException();
        }

        // Override default methods in Collection
        @Override
        public void forEach(Consumer<? super E> action) {
            c.forEach(action);
        }

        @Override
        public boolean removeIf(Predicate<? super E> filter) {
            throw new StatusUnreadableException();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Spliterator<E> spliterator() {
            return (Spliterator<E>) c.spliterator();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Stream<E> stream() {
            return (Stream<E>) c.stream();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Stream<E> parallelStream() {
            return (Stream<E>) c.parallelStream();
        }
    }

    private static class UnmodifiableRandomAccessList<E> extends UnmodifiableList<E> implements RandomAccess {
        UnmodifiableRandomAccessList(List<? extends E> list) {
            super(list);
        }

        public List<E> subList(int fromIndex, int toIndex) {
            return new UnmodifiableRandomAccessList<>(
                    list.subList(fromIndex, toIndex));
        }

        private static final long serialVersionUID = -2542308836966382001L;

        /**
         * Allows instances to be deserialized in pre-1.4 JREs (which do
         * not have UnmodifiableRandomAccessList).  UnmodifiableList has
         * a readResolve method that inverts this transformation upon
         * deserialization.
         */
        private Object writeReplace() {
            return new UnmodifiableList<>(list);
        }
    }

    private static class UnmodifiableList<E> extends UnmodifiableCollection<E> implements List<E> {
        private static final long serialVersionUID = -283967356065247728L;

        final List<? extends E> list;

        UnmodifiableList(List<? extends E> list) {
            super(list);
            this.list = list;
        }

        public boolean equals(Object o) {
            return o == this || list.equals(o);
        }

        public int hashCode() {
            return list.hashCode();
        }

        public E get(int index) {
            return list.get(index);
        }

        public E set(int index, E element) {
            throw new StatusUnreadableException();
        }

        public void add(int index, E element) {
            throw new StatusUnreadableException();
        }

        public E remove(int index) {
            throw new StatusUnreadableException();
        }

        public int indexOf(Object o) {
            return list.indexOf(o);
        }

        public int lastIndexOf(Object o) {
            return list.lastIndexOf(o);
        }

        public boolean addAll(int index, Collection<? extends E> c) {
            throw new StatusUnreadableException();
        }

        @Override
        public void replaceAll(UnaryOperator<E> operator) {
            throw new StatusUnreadableException();
        }

        @Override
        public void sort(Comparator<? super E> c) {
            throw new StatusUnreadableException();
        }

        public ListIterator<E> listIterator() {
            return listIterator(0);
        }

        public ListIterator<E> listIterator(final int index) {
            return new ListIterator<E>() {
                private final ListIterator<? extends E> i
                        = list.listIterator(index);

                public boolean hasNext() {
                    return i.hasNext();
                }

                public E next() {
                    return i.next();
                }

                public boolean hasPrevious() {
                    return i.hasPrevious();
                }

                public E previous() {
                    return i.previous();
                }

                public int nextIndex() {
                    return i.nextIndex();
                }

                public int previousIndex() {
                    return i.previousIndex();
                }

                public void remove() {
                    throw new StatusUnreadableException();
                }

                public void set(E e) {
                    throw new StatusUnreadableException();
                }

                public void add(E e) {
                    throw new StatusUnreadableException();
                }

                @Override
                public void forEachRemaining(Consumer<? super E> action) {
                    i.forEachRemaining(action);
                }
            };
        }

        public List<E> subList(int fromIndex, int toIndex) {
            return new UnmodifiableList<>(list.subList(fromIndex, toIndex));
        }

        /**
         * UnmodifiableRandomAccessList instances are serialized as
         * UnmodifiableList instances to allow them to be deserialized
         * in pre-1.4 JREs (which do not have UnmodifiableRandomAccessList).
         * This method inverts the transformation.  As a beneficial
         * side-effect, it also grafts the RandomAccess marker onto
         * UnmodifiableList instances that were serialized in pre-1.4 JREs.
         * <p>
         * Note: Unfortunately, UnmodifiableRandomAccessList instances
         * serialized in 1.4.1 and deserialized in 1.4 will become
         * UnmodifiableList instances, as this method was missing in 1.4.
         */
        private Object readResolve() {
            return (list instanceof RandomAccess
                    ? new UnmodifiableRandomAccessList<>(list)
                    : this);
        }
    }

    private static class UnmodifiableSet<E> extends UnmodifiableCollection<E> implements Set<E>, Serializable {
        private static final long serialVersionUID = -9215047833775013803L;

        UnmodifiableSet(Set<? extends E> s) {
            super(s);
        }

        public boolean equals(Object o) {
            return o == this || c.equals(o);
        }

        public int hashCode() {
            return c.hashCode();
        }
    }

    private static class UnmodifiableMap<K, V> implements Map<K, V>, Serializable {
        private static final long serialVersionUID = -1034234728574286014L;

        private final Map<? extends K, ? extends V> m;

        UnmodifiableMap(Map<? extends K, ? extends V> m) {
            if (m == null)
                throw new ConditionParametersException();
            this.m = m;
        }

        public int size() {
            return m.size();
        }

        public boolean isEmpty() {
            return m.isEmpty();
        }

        public boolean containsKey(Object key) {
            return m.containsKey(key);
        }

        public boolean containsValue(Object val) {
            return m.containsValue(val);
        }

        public V get(Object key) {
            return m.get(key);
        }

        public V put(K key, V value) {
            throw new StatusUnreadableException();
        }

        public V remove(Object key) {
            throw new StatusUnreadableException();
        }

        public void putAll(Map<? extends K, ? extends V> m) {
            throw new StatusUnreadableException();
        }

        public void clear() {
            throw new StatusUnreadableException();
        }

        private transient Set<K> keySet;
        private transient Set<Map.Entry<K, V>> entrySet;
        private transient Collection<V> values;

        public Set<K> keySet() {
            if (keySet == null)
                keySet = unmodifiable(m.keySet());
            return keySet;
        }

        public Set<Map.Entry<K, V>> entrySet() {
            if (entrySet == null)
                entrySet = new UnmodifiableMap.UnmodifiableEntrySet<>(m.entrySet());
            return entrySet;
        }

        public Collection<V> values() {
            if (values == null)
                values = unmodifiable(m.values());
            return values;
        }

        public boolean equals(Object o) {
            return o == this || m.equals(o);
        }

        public int hashCode() {
            return m.hashCode();
        }

        public String toString() {
            return m.toString();
        }

        // Override default methods in Map
        @Override
        @SuppressWarnings("unchecked")
        public V getOrDefault(Object k, V defaultValue) {
            // Safe cast as we don't change the value
            return ((Map<K, V>) m).getOrDefault(k, defaultValue);
        }

        @Override
        public void forEach(BiConsumer<? super K, ? super V> action) {
            m.forEach(action);
        }

        @Override
        public void replaceAll(BiFunction<? super K, ? super V, ? extends V> function) {
            throw new StatusUnreadableException();
        }

        @Override
        public V putIfAbsent(K key, V value) {
            throw new StatusUnreadableException();
        }

        @Override
        public boolean remove(Object key, Object value) {
            throw new StatusUnreadableException();
        }

        @Override
        public boolean replace(K key, V oldValue, V newValue) {
            throw new StatusUnreadableException();
        }

        @Override
        public V replace(K key, V value) {
            throw new StatusUnreadableException();
        }

        @Override
        public V computeIfAbsent(K key, Function<? super K, ? extends V> mappingFunction) {
            throw new StatusUnreadableException();
        }

        @Override
        public V computeIfPresent(K key,
                                  BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            throw new StatusUnreadableException();
        }

        @Override
        public V compute(K key,
                         BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
            throw new StatusUnreadableException();
        }

        @Override
        public V merge(K key, V value,
                       BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
            throw new StatusUnreadableException();
        }

        /**
         * We need this class in addition to UnmodifiableSet as
         * Map.Entries themselves permit modification of the backing Map
         * via their setValue operation.  This class is subtle: there are
         * many possible attacks that must be thwarted.
         *
         * @serial include
         */
        static class UnmodifiableEntrySet<K, V>
                extends UnmodifiableSet<Entry<K, V>> {
            private static final long serialVersionUID = 7854390611657943733L;

            @SuppressWarnings({"unchecked", "rawtypes"})
            UnmodifiableEntrySet(Set<? extends Map.Entry<? extends K, ? extends V>> s) {
                // Need to cast to raw in order to work around a limitation in the type system
                super((Set) s);
            }

            static <K, V> Consumer<Map.Entry<? extends K, ? extends V>> entryConsumer(
                    Consumer<? super Entry<K, V>> action) {
                return e -> action.accept(new UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry<>(e));
            }

            public void forEach(Consumer<? super Entry<K, V>> action) {
                Objects.requireNonNull(action);
                c.forEach(entryConsumer(action));
            }

            static final class UnmodifiableEntrySetSpliterator<K, V>
                    implements Spliterator<Entry<K, V>> {
                final Spliterator<Map.Entry<K, V>> s;

                UnmodifiableEntrySetSpliterator(Spliterator<Entry<K, V>> s) {
                    this.s = s;
                }

                @Override
                public boolean tryAdvance(Consumer<? super Entry<K, V>> action) {
                    Objects.requireNonNull(action);
                    return s.tryAdvance(entryConsumer(action));
                }

                @Override
                public void forEachRemaining(Consumer<? super Entry<K, V>> action) {
                    Objects.requireNonNull(action);
                    s.forEachRemaining(entryConsumer(action));
                }

                @Override
                public Spliterator<Entry<K, V>> trySplit() {
                    Spliterator<Entry<K, V>> split = s.trySplit();
                    return split == null
                            ? null
                            : new UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntrySetSpliterator<>(split);
                }

                @Override
                public long estimateSize() {
                    return s.estimateSize();
                }

                @Override
                public long getExactSizeIfKnown() {
                    return s.getExactSizeIfKnown();
                }

                @Override
                public int characteristics() {
                    return s.characteristics();
                }

                @Override
                public boolean hasCharacteristics(int characteristics) {
                    return s.hasCharacteristics(characteristics);
                }

                @Override
                public Comparator<? super Entry<K, V>> getComparator() {
                    return s.getComparator();
                }
            }

            @SuppressWarnings("unchecked")
            public Spliterator<Entry<K, V>> spliterator() {
                return new UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntrySetSpliterator<>(
                        (Spliterator<Map.Entry<K, V>>) c.spliterator());
            }

            @Override
            public Stream<Entry<K, V>> stream() {
                return StreamSupport.stream(spliterator(), false);
            }

            @Override
            public Stream<Entry<K, V>> parallelStream() {
                return StreamSupport.stream(spliterator(), true);
            }

            public Iterator<Map.Entry<K, V>> iterator() {
                return new Iterator<Map.Entry<K, V>>() {
                    private final Iterator<? extends Map.Entry<? extends K, ? extends V>> i = c.iterator();

                    public boolean hasNext() {
                        return i.hasNext();
                    }

                    public Map.Entry<K, V> next() {
                        return new UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry<>(i.next());
                    }

                    public void remove() {
                        throw new StatusUnreadableException();
                    }

                    public void forEachRemaining(Consumer<? super Map.Entry<K, V>> action) {
                        i.forEachRemaining(entryConsumer(action));
                    }
                };
            }

            @SuppressWarnings("unchecked")
            public Object[] toArray() {
                Object[] a = c.toArray();
                for (int i = 0; i < a.length; i++)
                    a[i] = new UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry<>((Map.Entry<? extends K, ? extends V>) a[i]);
                return a;
            }

            @SuppressWarnings("unchecked")
            public <T> T[] toArray(T[] a) {
                // We don't pass a to c.toArray, to avoid window of
                // vulnerability wherein an unscrupulous multithreaded client
                // could get his hands on raw (unwrapped) Entries from c.
                Object[] arr = c.toArray(a.length == 0 ? a : Arrays.copyOf(a, 0));

                for (int i = 0; i < arr.length; i++)
                    arr[i] = new UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry<>((Map.Entry<? extends K, ? extends V>) arr[i]);

                if (arr.length > a.length)
                    return (T[]) arr;

                System.arraycopy(arr, 0, a, 0, arr.length);
                if (a.length > arr.length)
                    a[arr.length] = null;
                return a;
            }

            /**
             * This method is overridden to protect the backing set against
             * an object with a nefarious equals function that senses
             * that the equality-candidate is Map.Entry and calls its
             * setValue method.
             */
            public boolean contains(Object o) {
                if (!(o instanceof Map.Entry))
                    return false;
                return c.contains(
                        new UnmodifiableMap.UnmodifiableEntrySet.UnmodifiableEntry<>((Map.Entry<?, ?>) o));
            }

            /**
             * The next two methods are overridden to protect against
             * an unscrupulous List whose contains(Object o) method senses
             * when o is a Map.Entry, and calls o.setValue.
             */
            public boolean containsAll(Collection<?> coll) {
                for (Object e : coll) {
                    if (!contains(e)) // Invokes safe contains() above
                        return false;
                }
                return true;
            }

            public boolean equals(Object o) {
                if (o == this)
                    return true;

                if (!(o instanceof Set))
                    return false;
                Set<?> s = (Set<?>) o;
                if (s.size() != c.size())
                    return false;
                return containsAll(s); // Invokes safe containsAll() above
            }

            /**
             * This "wrapper class" serves two purposes: it prevents
             * the client from modifying the backing Map, by short-circuiting
             * the setValue method, and it protects the backing Map against
             * an ill-behaved Map.Entry that attempts to modify another
             * Map Entry when asked to perform an equality check.
             */
            private static class UnmodifiableEntry<K, V> implements Map.Entry<K, V> {
                private Map.Entry<? extends K, ? extends V> e;

                UnmodifiableEntry(Map.Entry<? extends K, ? extends V> e) {
                    this.e = Objects.requireNonNull(e);
                }

                public K getKey() {
                    return e.getKey();
                }

                public V getValue() {
                    return e.getValue();
                }

                public V setValue(V value) {
                    throw new StatusUnreadableException();
                }

                public int hashCode() {
                    return e.hashCode();
                }

                public boolean equals(Object o) {
                    if (this == o)
                        return true;
                    if (!(o instanceof Map.Entry))
                        return false;
                    Map.Entry<?, ?> t = (Map.Entry<?, ?>) o;
                    return eq(e.getKey(), t.getKey()) &&
                            eq(e.getValue(), t.getValue());
                }

                public String toString() {
                    return e.toString();
                }
            }
        }

        private static boolean eq(Object o1, Object o2) {
            return o1 == null ? o2 == null : o1.equals(o2);
        }
    }
}
