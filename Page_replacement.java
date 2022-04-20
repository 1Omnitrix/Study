package cn.os.work3;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
//import java.util.Iterator;
import java.util.Scanner;


public class Page_replacement {
	public static int numPage;//页面大小，每个页面可包含numPage条指令
	public static int[] instructionsSequence = null; //存放该程依次执行的指令的有序地址集合
	public static int[] pagesSequence = null; //存放将有序指令转换成经过合并相邻页号的有序页号序列
	public static int memoryBlocksNum; //指定分配给该程序的内存容量
	public static void main (String[] args) {
		int count = 1;
		Scanner sc = new Scanner(System.in);
		//按要求生成指定的随机地址序列，共400个
		int[] instructionSequence = randomAdr();
		int page_size = 1;
		//页面大小为1k，这里的k只是表示一个单位，不必是1024B
		//每k存放10条指令
		numPage = page_size*10;
		pagesSequence = convertToPagesSequence(instructionSequence,numPage);
		System.out.println("该指令对应的页号序列： ");
		for (int i = 0; i < pagesSequence.length; i++) {
			System.out.print(pagesSequence[i]+" ");
		}
		System.out.println();
		DecimalFormat df = new DecimalFormat("0.0000");
		System.out.println("页框数\tOPT命中数\tFIFO命中数\tLRU命中数");
		//页框从4到40循环
		for (int i = 4; i <= 40; i++) {
			memoryBlocksNum = i;//当前页框数
			double FIFOHitRate = FIFO(pagesSequence,memoryBlocksNum);
			double LRUHitRate = LRU(pagesSequence,memoryBlocksNum);
			double OPTHitRate = OPT(pagesSequence,memoryBlocksNum);
			System.out.print("["+i+"]\tOPT:"+df.format(OPTHitRate)+"\tFIFO:"+df.format(FIFOHitRate)+"\tLRU:"+df.format(LRUHitRate));
			System.out.println();
		}
	}
	
	public static double OPT(int[] pagesSequence, int memoryBlocksNum) {
		
		int curPostion = 0;//该指针指向将要置换的内存块位置（下标位置）
		int[] tempState = new int[memoryBlocksNum];//执行每个页号时内存快序列的状态
		int lacktimes = 0;//缺页次数
		Arrays.fill(tempState, 0,memoryBlocksNum,-1);//开始时，内存块状态都为空闲（-1表示）
		for (int i = 0; i < pagesSequence.length; i++) {
			//如果缺页
			if(findKey(tempState, 0, memoryBlocksNum-1, pagesSequence[i]) == -1) {
				lacktimes = lacks(tempState, lacktimes);
				//如果内存块还有剩余
				if(tempState[memoryBlocksNum -1] == -1) {
					tempState[curPostion] = pagesSequence[i];
					curPostion++;
				}else {//如果内存块都已经使用
					int maxLoc = 0;
					for (int j = 0; j < memoryBlocksNum; j++) {
						//找出当前内存块序列中的内存块tempState[j]在将来会被访问的第一个位置
						int loc = findKey(pagesSequence, i+1, pagesSequence.length -1, tempState[j]);
						if(loc == -1) {
							curPostion = j;
							break;
						}else {//找出当前内存块序列中的所有内存块在将来会被访问到的最远位置，设为maxLoc
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
		//计算出HitRate
		double OPTHitRate = showMemoryBlocksState(pagesSequence, lacktimes);
		return OPTHitRate;
	}

	public static double LRU(int[] pagesSequence, int memoryBlocksNum) {
		//定义一个记录最近使用度内存块集合
		LRULinkedHashMap<String,Integer> recentVisiedBlocks = new LRULinkedHashMap<String, Integer>(memoryBlocksNum);
		int curPostion = 0;//该指针指向将要被置换度内存块位置
		//执行每个页号是内存块序列的状态
		int[] tempState = new int[memoryBlocksNum];
		int lacktimes = 0;//缺页次数
		Arrays.fill(tempState,0,memoryBlocksNum,-1);//初始化为-1
		for (int i = 0; i < pagesSequence.length; i++) {
			if(findKey(tempState, 0, memoryBlocksNum - 1, pagesSequence[i])== -1) {
				lacktimes = lacks(tempState, lacktimes);
				if(tempState[memoryBlocksNum - 1] == -1){
					tempState[curPostion] = pagesSequence[i];
					//内存块集合记录加入页面
					recentVisiedBlocks.put(String.valueOf(pagesSequence[i]), pagesSequence[i]);
					curPostion++;
				}else {
					//找到当前内存块序列中最近最少使用的内存块，并将其置换
					curPostion = findKey(tempState, 0, memoryBlocksNum - 1, recentVisiedBlocks.getHead());
					tempState[curPostion+1] = pagesSequence[i];
					//再次将页面加入至内存块集合记录
					recentVisiedBlocks.put(String.valueOf(pagesSequence[i]), pagesSequence[i]);
				}
			}
			//如果不缺页
			else {
				//将这里被使用的pagesSequence[i]在最近使用的内存块集合中的原先位置调整到最近被访问的位置
				recentVisiedBlocks.get(String.valueOf(pagesSequence[i]));
			}
		}
		//计算HitRate
		double LRUHitRate = showMemoryBlocksState(pagesSequence, lacktimes);
		return LRUHitRate;
	}

	public static double FIFO(int[] pagesSequence, int memoryBlocksNum) {
		
		int curPostion = 0;//该指针指向将要置换的内存块位置（下标位置）
		int[] tempState = new int[memoryBlocksNum];//执行每个页号时内存快序列的状态
		int lacktimes = 0;//缺页次数
		Arrays.fill(tempState, 0,memoryBlocksNum,-1);//开始时，内存块状态都为空闲（-1表示）
		for (int i = 0; i < pagesSequence.length; i++) {
			//如果缺页
			if(findKey(tempState,0,memoryBlocksNum-1,pagesSequence[i])== -1) {
				lacktimes =lacks(tempState,lacktimes);
				tempState[curPostion] = pagesSequence[i];
				//指针向右移动超过memoryBlocksNum时，重置其指向开始的内存块位置0
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
		// 缺页率
		double lackOfPagesRate = lacktimes * 1.0 /pagesSequence.length;
		double hitRate = 1 - lackOfPagesRate; //命中率
		return hitRate;//返回命中率
	}
	//以页框填满之后的总缺页次数计算
	public static int lacks(int[] tempState, int lacktimes) {
		if(tempState[tempState.length-1] != -1) {
			lacktimes++;
		}
		return lacktimes;
	}
	//返回key在arr中第一次出现的位置，start，end为数组下标，找不到则返回-1
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
		int pageIndex;//页号
		for (int i = 0; i < instructionSequence.length; i++) {
			pageIndex = instructionSequence[i]/instructionsNumPerPage;
			if(pageIndex != temp) {
				pagesList.add(pageIndex);
				temp = pageIndex;
			}	
		}
		//有序页号序列经合并之后长度最长不超过指令度有序地址序列长度
		int[] pagesSequence = new int[pagesList.size()];
		for (int i = 0; i < pagesList.size(); i++) {
			pagesSequence[i] = pagesList.get(i);
		}
		return pagesSequence;
	}
	private static int[] randomAdr() {
		int instructionNum = 400; //设置指令为400
		int count = 0;
		int[] instructionSequence = new int[instructionNum];
		while (count < instructionNum) {
			int r1 = 0 + (int)(Math.random()*200);
			instructionSequence[count++] = r1++;
			instructionSequence[count++] = r1;
			int r2 = 200 + (int)(Math.random()*199);//这里生成200~398的随机数，以防止顺序增加是数组越界
			instructionSequence[count++] = r2++;
			instructionSequence[count++] = r2;
		}
		System.out.println("生成的指令地址序列为：");
		for (int i = 0; i < instructionSequence.length;i++) {
			System.out.print(instructionSequence[i]+" ");
			if((i+1)%10 == 0)
				System.out.println();
		}
		return instructionSequence;
	}

}
