package com.iotgatewaybeta;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2016/12/5 0005.
 */
public class mSimpleCache<K,V>{//缓存

    private final Lock lock = new ReentrantLock();
    private final int maxCapacity;
    private final Map<K,V> eden;
    private final Map<K,V> perm;

    public mSimpleCache(int maxCapacity) {
        this.maxCapacity = maxCapacity;
        this.eden = new ConcurrentHashMap<K,V>(maxCapacity);
        this.perm= new WeakHashMap<K,V>(maxCapacity);
    }

    public V get(K k) {
        V v = this.eden.get(k);
        if (v == null) {
            lock.lock();
            try{
                v = this.perm.get(k);
            }finally{
                lock.unlock();
            }
            if (v != null) {
                this.eden.put(k, v);
            }
        }
        this.eden.remove(k);
        return v;
    }

    public void put(K k, V v) {
        if (this.eden.size() >= maxCapacity) {
            lock.lock();
            try{
                this.perm.putAll(this.eden);
            }finally{
                lock.unlock();
            }
            this.eden.clear();
        }
        this.eden.put(k, v);
    }
    public Set<K> getKeySet(){
        return this.eden.keySet();
    }
    public Map<K,V> getCHM(){
        return this.eden;
    }
    public Map<K,V> getWHM(){
        return this.perm;
    }
}
