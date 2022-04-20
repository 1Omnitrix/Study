package cn.os.work3;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRULinkedHashMap<K,V> extends LinkedHashMap<K, V>{
	
	private int maxMemoryBlocksNum;
	private static final float DEFAILT_LOAD_FACTOR = 0.75f;//设置默认负载因子
	public LRULinkedHashMap(int maxCapacity) {
		//设置accessOrder为true，保证LinkedHashMap底层实现的双向链表是按照访问的先后顺序排序
		super(maxCapacity,DEFAILT_LOAD_FACTOR,true);
		this.maxMemoryBlocksNum = maxCapacity;
	}
	//得到最近最少访问度元素
	public V getHead() {
		return (V)this.values().toArray()[0];
	}
	//移除多余度最近最小被访问度元素
	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > maxMemoryBlocksNum;
	}
}

