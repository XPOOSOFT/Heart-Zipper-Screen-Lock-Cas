package livewallpaper.aod.screenlock.lib.Glide.internal;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

@TargetApi(19)
public class SizeConfigStrategy implements LruPoolStrategy {
    private static final Bitmap.Config[] ALPHA_8_IN_CONFIGS = {Bitmap.Config.ALPHA_8};
    private static final Bitmap.Config[] ARGB_4444_IN_CONFIGS = {Bitmap.Config.ARGB_4444};
    private static final Bitmap.Config[] ARGB_8888_IN_CONFIGS = {Bitmap.Config.ARGB_8888, null};
    private static final int MAX_SIZE_MULTIPLE = 8;
    private static final Bitmap.Config[] RGB_565_IN_CONFIGS = {Bitmap.Config.RGB_565};
    private final GroupedLinkedMap<Key, Bitmap> groupedMap = new GroupedLinkedMap<>();
    private final KeyPool keyPool = new KeyPool();
    private final Map<Bitmap.Config, NavigableMap<Integer, Integer>> sortedSizes = new HashMap();

    public void put(Bitmap bitmap) {
        Key key = this.keyPool.get(Util.getBitmapByteSize(bitmap), bitmap.getConfig());
        this.groupedMap.put(key, bitmap);
        NavigableMap<Integer, Integer> sizesForConfig = getSizesForConfig(bitmap.getConfig());
        Integer num = (Integer) sizesForConfig.get(Integer.valueOf(key.size));
        Integer valueOf = Integer.valueOf(key.size);
        int i = 1;
        if (num != null) {
            i = 1 + num.intValue();
        }
        sizesForConfig.put(valueOf, Integer.valueOf(i));
    }

    public Bitmap get(int i, int i2, Bitmap.Config config) {
        Key findBestKey = findBestKey(Util.getBitmapByteSize(i, i2, config), config);
        Bitmap bitmap = this.groupedMap.get(findBestKey);
        if (bitmap != null) {
            decrementBitmapOfSize(Integer.valueOf(findBestKey.size), bitmap);
            bitmap.reconfigure(i, i2, bitmap.getConfig() != null ? bitmap.getConfig() : Bitmap.Config.ARGB_8888);
        }
        return bitmap;
    }

    private Key findBestKey(int i, Bitmap.Config config) {
        Key key = this.keyPool.get(i, config);
        Bitmap.Config[] inConfigs = getInConfigs(config);
        int i2 = 0;
        int length = inConfigs.length;
        while (i2 < length) {
            Bitmap.Config config2 = inConfigs[i2];
            Integer ceilingKey = getSizesForConfig(config2).ceilingKey(Integer.valueOf(i));
            if (ceilingKey == null || ceilingKey.intValue() > i * 8) {
                i2++;
            } else {
                if (ceilingKey.intValue() == i) {
                    if (config2 == null) {
                        if (config == null) {
                            return key;
                        }
                    } else if (config2.equals(config)) {
                        return key;
                    }
                }
                this.keyPool.offer(key);
                return this.keyPool.get(ceilingKey.intValue(), config2);
            }
        }
        return key;
    }

    public Bitmap removeLast() {
        Bitmap removeLast = this.groupedMap.removeLast();
        if (removeLast != null) {
            decrementBitmapOfSize(Integer.valueOf(Util.getBitmapByteSize(removeLast)), removeLast);
        }
        return removeLast;
    }

    private void decrementBitmapOfSize(Integer num, Bitmap bitmap) {
        NavigableMap<Integer, Integer> sizesForConfig = getSizesForConfig(bitmap.getConfig());
        Integer num2 = (Integer) sizesForConfig.get(num);
        if (num2 == null) {
            throw new NullPointerException("Tried to decrement empty size, size: " + num + ", removed: " + logBitmap(bitmap) + ", this: " + this);
        } else if (num2.intValue() == 1) {
            sizesForConfig.remove(num);
        } else {
            sizesForConfig.put(num, Integer.valueOf(num2.intValue() - 1));
        }
    }

    private NavigableMap<Integer, Integer> getSizesForConfig(Bitmap.Config config) {
        NavigableMap<Integer, Integer> navigableMap = this.sortedSizes.get(config);
        if (navigableMap != null) {
            return navigableMap;
        }
        TreeMap treeMap = new TreeMap();
        this.sortedSizes.put(config, treeMap);
        return treeMap;
    }

    public String logBitmap(Bitmap bitmap) {
        return getBitmapString(Util.getBitmapByteSize(bitmap), bitmap.getConfig());
    }

    public String logBitmap(int i, int i2, Bitmap.Config config) {
        return getBitmapString(Util.getBitmapByteSize(i, i2, config), config);
    }

    public int getSize(Bitmap bitmap) {
        return Util.getBitmapByteSize(bitmap);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SizeConfigStrategy{groupedMap=");
        sb.append(this.groupedMap);
        sb.append(", sortedSizes=(");
        for (Map.Entry next : this.sortedSizes.entrySet()) {
            sb.append(next.getKey());
            sb.append('[');
            sb.append(next.getValue());
            sb.append("], ");
        }
        if (!this.sortedSizes.isEmpty()) {
            sb.replace(sb.length() - 2, sb.length(), "");
        }
        sb.append(")}");
        return sb.toString();
    }

    static class KeyPool extends BaseKeyPool<Key> {
        KeyPool() {
        }

        public Key get(int i, Bitmap.Config config) {
            Key key = (Key) get();
            key.init(i, config);
            return key;
        }

      
        public Key create() {
            return new Key(this);
        }
    }

    static final class Key implements Poolable {
        private Bitmap.Config config;
        private final KeyPool pool;
        /* access modifiers changed from: private */
        public int size;

        public Key(KeyPool keyPool) {
            this.pool = keyPool;
        }

        Key(KeyPool keyPool, int i, Bitmap.Config config2) {
            this(keyPool);
            init(i, config2);
        }

        public void init(int i, Bitmap.Config config2) {
            this.size = i;
            this.config = config2;
        }

        public void offer() {
            this.pool.offer(this);
        }

        public String toString() {
            return SizeConfigStrategy.getBitmapString(this.size, this.config);
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof Key)) {
                return false;
            }
            Key key = (Key) obj;
            if (this.size != key.size || !Util.bothNullOrEqual(this.config, key.config)) {
                return false;
            }
            return true;
        }

        public int hashCode() {
            return (31 * this.size) + (this.config != null ? this.config.hashCode() : 0);
        }
    }

    /* access modifiers changed from: private */
    public static String getBitmapString(int i, Bitmap.Config config) {
        return "[" + i + "](" + config + ")";
    }

    /* renamed from: com.glidebitmappool.internal.SizeConfigStrategy$1  reason: invalid class name */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$android$graphics$Bitmap$Config = new int[Bitmap.Config.values().length];

        /* JADX WARNING: Can't wrap try/catch for region: R(10:0|1|2|3|4|5|6|7|8|10) */
        /* JADX WARNING: Can't wrap try/catch for region: R(8:0|1|2|3|4|5|6|(3:7|8|10)) */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Missing exception handler attribute for start block: B:3:0x0014 */
        /* JADX WARNING: Missing exception handler attribute for start block: B:5:0x001f */
        /* JADX WARNING: Missing exception handler attribute for start block: B:7:0x002a */
//        static {
//            /*
//                android.graphics.Bitmap$Config[] r0 = android.graphics.Bitmap.Config.values()
//                int r0 = r0.length
//                int[] r0 = new int[r0]
//                $SwitchMap$android$graphics$Bitmap$Config = r0
//                int[] r0 = $SwitchMap$android$graphics$Bitmap$Config     // Catch:{ NoSuchFieldError -> 0x0014 }
//                android.graphics.Bitmap$Config r1 = android.graphics.Bitmap.Config.ARGB_8888     // Catch:{ NoSuchFieldError -> 0x0014 }
//                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0014 }
//                r2 = 1
//                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0014 }
//            L_0x0014:
//                int[] r0 = $SwitchMap$android$graphics$Bitmap$Config     // Catch:{ NoSuchFieldError -> 0x001f }
//                android.graphics.Bitmap$Config r1 = android.graphics.Bitmap.Config.RGB_565     // Catch:{ NoSuchFieldError -> 0x001f }
//                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x001f }
//                r2 = 2
//                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x001f }
//            L_0x001f:
//                int[] r0 = $SwitchMap$android$graphics$Bitmap$Config     // Catch:{ NoSuchFieldError -> 0x002a }
//                android.graphics.Bitmap$Config r1 = android.graphics.Bitmap.Config.ARGB_4444     // Catch:{ NoSuchFieldError -> 0x002a }
//                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x002a }
//                r2 = 3
//                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x002a }
//            L_0x002a:
//                int[] r0 = $SwitchMap$android$graphics$Bitmap$Config     // Catch:{ NoSuchFieldError -> 0x0035 }
//                android.graphics.Bitmap$Config r1 = android.graphics.Bitmap.Config.ALPHA_8     // Catch:{ NoSuchFieldError -> 0x0035 }
//                int r1 = r1.ordinal()     // Catch:{ NoSuchFieldError -> 0x0035 }
//                r2 = 4
//                r0[r1] = r2     // Catch:{ NoSuchFieldError -> 0x0035 }
//            L_0x0035:
//                return
//            */
//            throw new UnsupportedOperationException("Method not decompiled: com.glidebitmappool.internal.SizeConfigStrategy.AnonymousClass1.<clinit>():void");
//        }
    }

    private static Bitmap.Config[] getInConfigs(Bitmap.Config config) {
        switch (AnonymousClass1.$SwitchMap$android$graphics$Bitmap$Config[config.ordinal()]) {
            case 1:
                return ARGB_8888_IN_CONFIGS;
            case 2:
                return RGB_565_IN_CONFIGS;
            case 3:
                return ARGB_4444_IN_CONFIGS;
            case 4:
                return ALPHA_8_IN_CONFIGS;
            default:
                return new Bitmap.Config[]{config};
        }
    }
}
