package core.minecraft.common;

/**
 * This is used to callback some type of data after a task has been completed.
 *
 * @author Preston Brown
 */
public interface Callback<T> {

    public T call(T callback);
}
