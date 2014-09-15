package com.pushtorefresh.bamboostorage;

import android.support.annotation.NonNull;

import java.util.Collection;
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

    private final Queue<ABambooStorageListener> mListeners = new ConcurrentLinkedQueue<ABambooStorageListener>();

    private final Executor mExecutor = Executors.newSingleThreadExecutor(new ThreadFactory() {
        @Override public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setPriority(Thread.MIN_PRIORITY);
            return thread;
        }
    });

    @Override public void addListener(@NonNull ABambooStorageListener listener) {
        mListeners.add(listener);
    }

    @Override public void removeListener(@NonNull ABambooStorageListener listener) {
        mListeners.remove(listener);
    }

    @Override public void notifyAboutAdd(@NonNull final IBambooStorableItem storableItem) {
        if (mListeners.size() != 0) {
            mExecutor.execute(new Runnable() {
                @Override public void run() {
                    final Class<? extends IBambooStorableItem> classOfStorableItem = storableItem.getClass();

                    for (ABambooStorageListener listener : mListeners) {
                        listener.onAdd(storableItem);
                        listener.onAnyCRUDOperation(classOfStorableItem);
                    }
                }
            });
        }
    }

    @Override
    public void notifyAboutUpdate(@NonNull final IBambooStorableItem storableItem, final int count) {
        if (mListeners.size() != 0) {
            mExecutor.execute(new Runnable() {
                @Override public void run() {
                    final Class<? extends IBambooStorableItem> classOfStorableItem = storableItem.getClass();

                    for (ABambooStorageListener listener : mListeners) {
                        listener.onUpdate(storableItem, count);
                        listener.onAnyCRUDOperation(classOfStorableItem);
                    }
                }
            });
        }
    }

    @Override
    public void notifyAboutAddAll(@NonNull final Collection<? extends IBambooStorableItem> storableItems) {
        if (mListeners.size() != 0) {
            mExecutor.execute(new Runnable() {
                @SuppressWarnings("ConstantConditions") @Override public void run() {
                    Class<? extends IBambooStorableItem> classOfStorableItems = null;

                    for (IBambooStorableItem storableItem : storableItems) {
                        if (storableItem != null) {
                            classOfStorableItems = storableItem.getClass();
                            break;
                        }
                    }

                    for (ABambooStorageListener listener : mListeners) {
                        listener.onAddAll(storableItems);
                        listener.onAnyCRUDOperation(classOfStorableItems);
                    }
                }
            });
        }
    }

    @Override
    public void notifyAboutAddOrUpdateAll(@NonNull final Collection<? extends IBambooStorableItem> storableItems) {
        if (mListeners.size() != 0) {
            mExecutor.execute(new Runnable() {
                @SuppressWarnings("ConstantConditions") @Override public void run() {
                    Class<? extends IBambooStorableItem> classOfStorableItems = null;

                    for (IBambooStorableItem storableItem : storableItems) {
                        if (storableItem != null) {
                            classOfStorableItems = storableItem.getClass();
                            break;
                        }
                    }

                    for (ABambooStorageListener listener : mListeners) {
                        listener.onAddOrUpdateAll(storableItems);
                        listener.onAnyCRUDOperation(classOfStorableItems);
                    }
                }
            });
        }
    }

    @Override
    public void notifyAboutRemove(@NonNull final IBambooStorableItem storableItem, final int count) {
        if (mListeners.size() != 0) {
            mExecutor.execute(new Runnable() {
                @Override public void run() {
                    final Class<? extends IBambooStorableItem> classOfStorableItem = storableItem.getClass();

                    for (ABambooStorageListener listener : mListeners) {
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
                    for (ABambooStorageListener listener : mListeners) {
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
                    for (ABambooStorageListener listener : mListeners) {
                        listener.onRemoveAllOfType(classOfStorableItems, count);
                        listener.onAnyCRUDOperation(classOfStorableItems);
                    }
                }
            });
        }
    }
}
