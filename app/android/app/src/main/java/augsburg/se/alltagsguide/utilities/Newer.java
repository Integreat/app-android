package augsburg.se.alltagsguide.utilities;

/**
 * Created by Daniel-L on 19.10.2015.
 */
public interface Newer<E> extends Comparable<E> {
    /* Checks whether or not the item is different (although id might be the same)*/
    long getTimestamp();
}
