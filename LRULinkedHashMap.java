package cn.os.work3;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRULinkedHashMap<K,V> extends LinkedHashMap<K, V>{
	
	private int maxMemoryBlocksNum;
	private static final float DEFAILT_LOAD_FACTOR = 0.75f;//����Ĭ�ϸ�������
	public LRULinkedHashMap(int maxCapacity) {
		//����accessOrderΪtrue����֤LinkedHashMap�ײ�ʵ�ֵ�˫�������ǰ��շ��ʵ��Ⱥ�˳������
		super(maxCapacity,DEFAILT_LOAD_FACTOR,true);
		this.maxMemoryBlocksNum = maxCapacity;
	}
	//�õ�������ٷ��ʶ�Ԫ��
	public V getHead() {
		return (V)this.values().toArray()[0];
	}
	//�Ƴ�����������С�����ʶ�Ԫ��
	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		return size() > maxMemoryBlocksNum;
	}
}

