package com.pushtorefresh.bamboostorage;

import android.support.annotation.NonNull;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Default BambooStorage notifier
 * Has one thread which notifies listeners in background with min priority
 *
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class BambooStorageDefaultNotifier implements IBambooStorageNotifier {

    private final Queue<IBambooStorageListener> mListeners = new ConcurrentLinkedQueue<IBambooStorageListener>();

    private final Executor mExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        }
    });

    @Override public void addListener(@NonNull IBambooStorageListener listener) {
        mListeners.add(listener);
    }

    @Override public void removeListener(@NonNull IBambooStorageListener listener) {
        mListeners.remove(listener);
    }

    @Override public void notifyAboutAdd(@NonNull final IBambooStorableItem storableItem) {
        if (mListeners.size() != 0) {
            mExecutor.execute(new Runnable() {
                @Override public void run() {
                    final Class<? extends IBambooStorableItem> classOfStorableItem = storableItem.getClass();

                    for (IBambooStorageListener listener : mListeners) {
                        listener.onAdd(storableItem);
                        listener.onAnyCRUDOperation(classOfStorableItem);
                    }
                }
            });
        }
    }

    @Override public void notifyAboutUpdate(@NonNull final IBambooStorableItem storableItem, final int count) {
        if (mListeners.size() != 0) {
            mExecutor.execute(new Runnable() {
                @Override public void run() {
                    final Class<? extends IBambooStorableItem> classOfStorableItem = storableItem.getClass();

                    for (IBambooStorageListener listener : mListeners) {
                        listener.onUpdate(storableItem, count);
                        listener.onAnyCRUDOperation(classOfStorableItem);
                    }
                }
            });
        }
    }

    @Override public void notifyAboutRemove(@NonNull final IBambooStorableItem storableItem, final int count) {
        if (mListeners.size() != 0) {
            mExecutor.execute(new Runnable() {
                @Override public void run() {
                    final Class<? extends IBambooStorableItem> classOfStorableItem = storableItem.getClass();

                    for (IBambooStorageListener listener : mListeners) {
                        listener.onRemove(storableItem, count);
                        listener.onAnyCRUDOperation(classOfStorableItem);
                    }
                }
            });
        }
    }

    @Override
    public void notifyAboutRemove(@NonNull final Class<? extends IBambooStorableItem> classOfStorableItems, final String where, final String[] whereArgs, final int count) {
        if (mListeners.size() != 0) {
            mExecutor.execute(new Runnable() {
                @Override public void run() {
                    for (IBambooStorageListener listener : mListeners) {
                        listener.onRemove(classOfStorableItems, where, whereArgs, count);
                        listener.onAnyCRUDOperation(classOfStorableItems);
                    }
                }
            });
        }
    }

    @Override
    public void notifyAboutRemoveAllOfType(@NonNull final Class<? extends IBambooStorableItem> classOfStorableItems, final int count) {
        if (mListeners.size() != 0) {
            mExecutor.execute(new Runnable() {
                @Override public void run() {
                    for (IBambooStorageListener listener : mListeners) {
                        listener.onRemoveAllOfType(classOfStorableItems, count);
                        listener.onAnyCRUDOperation(classOfStorableItems);
                    }
                }
            });
        }
    }
}
