package cn.os.work3;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Iterator;
import java.util.Scanner;


public class Page_replacement {
	public static int numPage;//ҳ���С��ÿ��ҳ��ɰ���numPage��ָ��
	public static int[] instructionsSequence = null; //��Ÿó�����ִ�е�ָ��������ַ����
	public static int[] pagesSequence = null; //��Ž�����ָ��ת���ɾ����ϲ�����ҳ�ŵ�����ҳ������
	public static int memoryBlocksNum; //ָ��������ó�����ڴ�����
	public static void main (String[] args) {
		int count = 1;
		Scanner sc = new Scanner(System.in);
		//��Ҫ������ָ���������ַ���У���400��
		int[] instructionSequence = randomAdr();
		int page_size = 1;
		//ҳ���СΪ1k�������kֻ�Ǳ�ʾһ����λ��������1024B
		//ÿk���10��ָ��
		numPage = page_size*10;
		pagesSequence = convertToPagesSequence(instructionSequence,numPage);
		System.out.println("��ָ���Ӧ��ҳ�����У� ");
		for (int i = 0; i < pagesSequence.length; i++) {
			System.out.print(pagesSequence[i]+" ");
		}
		System.out.println();
		DecimalFormat df = new DecimalFormat("0.0000");
		System.out.println("ҳ����\tOPT������\tFIFO������\tLRU������");
		//ҳ���4��40ѭ��
		for (int i = 4; i <= 40; i++) {
			memoryBlocksNum = i;//��ǰҳ����
			double FIFOHitRate = FIFO(pagesSequence,memoryBlocksNum);
			double LRUHitRate = LRU(pagesSequence,memoryBlocksNum);
			double OPTHitRate = OPT(pagesSequence,memoryBlocksNum);
			System.out.print("["+i+"]\tOPT:"+df.format(OPTHitRate)+"\tFIFO:"+df.format(FIFOHitRate)+"\tLRU:"+df.format(LRUHitRate));
			System.out.println();
		}
	}
	
	public static double OPT(int[] pagesSequence, int memoryBlocksNum) {
		
		int curPostion = 0;//��ָ��ָ��Ҫ�û����ڴ��λ�ã��±�λ�ã�
		int[] tempState = new int[memoryBlocksNum];//ִ��ÿ��ҳ��ʱ�ڴ�����е�״̬
		int lacktimes = 0;//ȱҳ����
		Arrays.fill(tempState, 0,memoryBlocksNum,-1);//��ʼʱ���ڴ��״̬��Ϊ���У�-1��ʾ��
		for (int i = 0; i < pagesSequence.length; i++) {
			//���ȱҳ
			if(findKey(tempState, 0, memoryBlocksNum-1, pagesSequence[i]) == -1) {
				lacktimes = lacks(tempState, lacktimes);
				//����ڴ�黹��ʣ��
				if(tempState[memoryBlocksNum -1] == -1) {
					tempState[curPostion] = pagesSequence[i];
					curPostion++;
				}else {//����ڴ�鶼�Ѿ�ʹ��
					int maxLoc = 0;
					for (int j = 0; j < memoryBlocksNum; j++) {
						//�ҳ���ǰ�ڴ�������е��ڴ��tempState[j]�ڽ����ᱻ���ʵĵ�һ��λ��
						int loc = findKey(pagesSequence, i+1, pagesSequence.length -1, tempState[j]);
						if(loc == -1) {
							curPostion = j;
							break;
						}else {//�ҳ���ǰ�ڴ�������е������ڴ���ڽ����ᱻ���ʵ�����Զλ�ã���ΪmaxLoc
							if(maxLoc <loc) {
								maxLoc = loc;
								curPostion = j;
							}
						}
					}
					tempState[curPostion] = pagesSequence[i];
				}
			}
		}
		//�����HitRate
		double OPTHitRate = showMemoryBlocksState(pagesSequence, lacktimes);
		return OPTHitRate;
	}

	public static double LRU(int[] pagesSequence, int memoryBlocksNum) {
		//����һ����¼���ʹ�ö��ڴ�鼯��
		LRULinkedHashMap<String,Integer> recentVisiedBlocks = new LRULinkedHashMap<String, Integer>(memoryBlocksNum);
		int curPostion = 0;//��ָ��ָ��Ҫ���û����ڴ��λ��
		//ִ��ÿ��ҳ�����ڴ�����е�״̬
		int[] tempState = new int[memoryBlocksNum];
		int lacktimes = 0;//ȱҳ����
		Arrays.fill(tempState,0,memoryBlocksNum,-1);//��ʼ��Ϊ-1
		for (int i = 0; i < pagesSequence.length; i++) {
			if(findKey(tempState, 0, memoryBlocksNum - 1, pagesSequence[i])== -1) {
				lacktimes = lacks(tempState, lacktimes);
				if(tempState[memoryBlocksNum - 1] == -1){
					tempState[curPostion] = pagesSequence[i];
					//�ڴ�鼯�ϼ�¼����ҳ��
					recentVisiedBlocks.put(String.valueOf(pagesSequence[i]), pagesSequence[i]);
					curPostion++;
				}else {
					//�ҵ���ǰ�ڴ���������������ʹ�õ��ڴ�飬�������û�
					curPostion = findKey(tempState, 0, memoryBlocksNum - 1, recentVisiedBlocks.getHead());
					tempState[curPostion+1] = pagesSequence[i];
					//�ٴν�ҳ��������ڴ�鼯�ϼ�¼
					recentVisiedBlocks.put(String.valueOf(pagesSequence[i]), pagesSequence[i]);
				}
			}
			//�����ȱҳ
			else {
				//�����ﱻʹ�õ�pagesSequence[i]�����ʹ�õ��ڴ�鼯���е�ԭ��λ�õ�������������ʵ�λ��
				recentVisiedBlocks.get(String.valueOf(pagesSequence[i]));
			}
		}
		//����HitRate
		double LRUHitRate = showMemoryBlocksState(pagesSequence, lacktimes);
		return LRUHitRate;
	}

	public static double FIFO(int[] pagesSequence, int memoryBlocksNum) {
		
		int curPostion = 0;//��ָ��ָ��Ҫ�û����ڴ��λ�ã��±�λ�ã�
		int[] tempState = new int[memoryBlocksNum];//ִ��ÿ��ҳ��ʱ�ڴ�����е�״̬
		int lacktimes = 0;//ȱҳ����
		Arrays.fill(tempState, 0,memoryBlocksNum,-1);//��ʼʱ���ڴ��״̬��Ϊ���У�-1��ʾ��
		for (int i = 0; i < pagesSequence.length; i++) {
			//���ȱҳ
			if(findKey(tempState,0,memoryBlocksNum-1,pagesSequence[i])== -1) {
				lacktimes =lacks(tempState,lacktimes);
				tempState[curPostion] = pagesSequence[i];
				//ָ�������ƶ�����memoryBlocksNumʱ��������ָ��ʼ���ڴ��λ��0
				if(curPostion +1 > memoryBlocksNum -1) {
					curPostion = 0;
				}else {
					curPostion++;
				}
			}
		}
		double FIFOHitRate  = showMemoryBlocksState(pagesSequence,lacktimes);
		return FIFOHitRate;
	}
	public static double showMemoryBlocksState(int[] pagesSequence, int lacktimes) {
		// ȱҳ��
		double lackOfPagesRate = lacktimes * 1.0 /pagesSequence.length;
		double hitRate = 1 - lackOfPagesRate; //������
		return hitRate;//����������
	}
	//��ҳ������֮�����ȱҳ��������
	public static int lacks(int[] tempState, int lacktimes) {
		if(tempState[tempState.length-1] != -1) {
			lacktimes++;
		}
		return lacktimes;
	}
	//����key��arr�е�һ�γ��ֵ�λ�ã�start��endΪ�����±꣬�Ҳ����򷵻�-1
	public static int findKey(int[] arr, int start, int end, int key) {
		for(int i = start;i <= end; i++) {
			if(arr[i] == key) {
				return 1;
			}
		}
		return -1;
	}
	public static int[] convertToPagesSequence(int[] instructionSequence, int instructionsNumPerPage) {
		ArrayList<Integer> pagesList = new ArrayList<Integer>();
		int temp = -1;
		int pageIndex;//ҳ��
		for (int i = 0; i < instructionSequence.length; i++) {
			pageIndex = instructionSequence[i]/instructionsNumPerPage;
			if(pageIndex != temp) {
				pagesList.add(pageIndex);
				temp = pageIndex;
			}	
		}
		//����ҳ�����о��ϲ�֮�󳤶��������ָ��������ַ���г���
		int[] pagesSequence = new int[pagesList.size()];
		for (int i = 0; i < pagesList.size(); i++) {
			pagesSequence[i] = pagesList.get(i);
		}
		return pagesSequence;
	}
	private static int[] randomAdr() {
		int instructionNum = 400; //����ָ��Ϊ400
		int count = 0;
		int[] instructionSequence = new int[instructionNum];
		while (count < instructionNum) {
			int r1 = 0 + (int)(Math.random()*200);
			instructionSequence[count++] = r1++;
			instructionSequence[count++] = r1;
			int r2 = 200 + (int)(Math.random()*199);//��������200~398����������Է�ֹ˳������������Խ��
			instructionSequence[count++] = r2++;
			instructionSequence[count++] = r2;
		}
		System.out.println("���ɵ�ָ���ַ����Ϊ��");
		for (int i = 0; i < instructionSequence.length;i++) {
			System.out.print(instructionSequence[i]+" ");
			if((i+1)%10 == 0)
				System.out.println();
		}
		return instructionSequence;
	}

}
