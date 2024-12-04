package livewallpaper.aod.screenlock.lib.Glide.internal;

import java.util.Queue;

abstract class BaseKeyPool<T extends Poolable> {
    private static final int MAX_SIZE = 20;
    private final Queue<T> keyPool = Util.createQueue(20);

  
    public abstract T create();

    BaseKeyPool() {
    }

  
    public T get() {
        T t = (T) this.keyPool.poll();
        return t == null ? create() : t;
    }

    public void offer(T t) {
        if (this.keyPool.size() < 20) {
            this.keyPool.offer(t);
        }
    }
}
