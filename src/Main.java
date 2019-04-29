import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Main// ����
{
	private static ServerThreadReceive str; // ���ڽ����ھ���Ϣ�ķ������߳�
	private static ServerThreadSendCommand stsc;// ���ڷ��Ϳ�������ķ������߳�
	
	public static ArrayList<NodeInfo> nodes;// �ڵ���Ϣ���б�
	public static Object nodesLock;// �б�����

	private static MainWindow mainWindow;
	
	public static String rootMacaddr;
	public static double[][][][] NeiInforArrary;
	public static int channel1 = 36;
	public static int channel2 = 149;
	
	public static void main(String[] args)// �������ڵ�
	{
		

		Connections.receiveList = new ArrayList<ConnectionThreadReceive>();// ��ʼ�������б�
		Connections.receiveListLock = new Object();
		Connections.sendCommandList = new ArrayList<ConnectionThreadSendCommand>();
		Connections.sendCommandListLock = new Object();
//�޸ĸ��ڵ��ַ
		rootMacaddr = "04:F0:21:39:C1:91";
		
		nodes = new ArrayList<NodeInfo>();// ��ʼ���ڵ���Ϣ�б�
		nodesLock = new Object();// ��ʼ���б�����
		//BFSofMRMC();

		str = new ServerThreadReceive();// �������ڽ����ھ���Ϣ�ķ������߳�
		str.start();// �����߳�

		stsc = new ServerThreadSendCommand();// �������ڷ��Ϳ�������ķ������߳�
		stsc.start();// �����߳�

		mainWindow = new MainWindow();// ����������

		Scanner scanner = new Scanner(System.in);
		while (true)
		{
			String command = scanner.nextLine();
			System.out.println(command);
			if (command.equals("send"))
			{
				System.out.println("Sending configurations to routers.");
				SendConfiguration();
			}
		}
		
	}

	public static void SendConfiguration()// �·����нڵ������
	{
		
		
		System.out.println("send here");
		//SendToNode("04:F0:21:39:64:26", "SETLINK 04:F0:21:39:64:26#DISABLED 04:F0:21:39:64:25#36#link203");
		//SendToNode("04:F0:21:39:64:20", "SETLINK 04:F0:21:39:64:20#6#link204 04:F0:21:39:64:1F#36#link203");
		//SendToNode("04:F0:21:39:64:06", "SETLINK 04:F0:21:39:64:06#6#link204 04:F0:21:39:64:05#DISABLED");
		
		BFSofMRMC();
		
		//SendToNode("04:F0:21:39:C1:91", "SETLINK 04:F0:21:39:C1:91#36#link201#ap#1 04:F0:21:39:C1:6B#149#link202#ap#0");
		//SendToNode("04:F0:21:39:C1:5F", "SETLINK 04:F0:21:39:C1:5F#36#link203#ap#0 04:F0:21:39:C1:60#149#link204#ap#1");
		//SendToNode("04:F0:21:39:64:06", "SETLINK 04:F0:21:39:64:06#6#link204 04:F0:21:39:64:05#DISABLED");
		//SendToNode("04:F0:21:39:64:26", "SETLINK 04:F0:21:39:64:26#6#link204 04:F0:21:39:64:25#DISABLED");
		//SendToNode("04:F0:21:39:64:1C", "SETLINK 04:F0:21:39:64:1B#36#link204 04:F0:21:39:64:1F#36#link203");
	}
	public static void BFSofMRMC(){// MRMC�ĳ����㷨�������������
		//���ļ��ķ�ʽ
		//BFStestinit("");
		//������־
		int i,j,k,q= 0;
		//����ͷ����β��
		int front = 0;
		int back = 0;
		//radioѭ������
		int elem = 0;
		//���ڻ�ȡÿ���ڵ��ÿ����Ƶ��Ϣ
		int a,b,c,d,e;
		/*��ʶ����
		flag ��ʾ�ýڵ��п������ӵ��ӽڵ�
		flag1 ����flag2
		flag2 ���ڱ�ʶ�ڵ�������Ƶ��û�в��������������������ʱ��
		flag3 ���ڱ�ʶ����һ���ھӽڵ�ɨ���У���һ���ڵ��������Ƶ�Ƿ��Ѿ����ø��ڵ�����
				����ֹͬһ���ڵ�Ķ����Ƶ��ͬһ�����ڵ���Ƶ���ӣ�
		*/
		int flag,flag1,flag2,flag3 = 0;
		//��ȡroot��nodes�е�index
		int rootid = 0;
		int visitednum = 0;
		NodeInfo niTemp;
		NodeInfo niTemp1;
		int radioNo = 0;
		int nodeNum = 0;
		int radioNum = 0;
		RadioInfo radioTemp;
		RadioInfo radioTemp1;
		nodeNum = nodes.size();
		NodeInfo nis = nodes.get(0);
		radioNum = nis.radioInfo.size();
		int [][] visited = new int [nodeNum][radioNum];
		int [][] signaled = new int [nodeNum][radioNum];
		double[][] resultTemp = new double[nodeNum][radioNum];
		String[] results = new String[nodeNum];
		System.out.print(radioNum+" "+nodeNum);
		

		
		compose();
		for(a = 0;a<nodeNum;a++){
			for(b = 0;b<radioNum;b++){
				for(d = 0;d<nodeNum;d++){
					for(e = 0;e<radioNum;e++){
						System.out.print(NeiInforArrary[a][d][b][e]+" ");
					}
					System.out.print("&");
				}
				System.out.println(b+"b");
			}
			System.out.println(a+"a");
		}
		int que[] = new int[1000];
		for(i = 0;i<1000;i++){
			que[i] = -1;
		}
		
		
		for(a = 0;a<nodeNum;a++){
			for(b = 0;b<radioNum;b++){
				resultTemp[a][b] = 0.0;

			}
		}
		for(a = 0;a<nodeNum;a++){
			for(b = 0;b<radioNum;b++){
				visited[a][b] = 0;	
			}
		}
		for(a = 0;a<nodeNum;a++){
			for(b = 0;b<radioNum;b++){
				signaled[a][b] = 0;	
			}
		}
		i = 0;
		j = 0;
		
		//Ĭ�ϸ��ڵ�ֻʹ��һ����Ƶ�����������ʽ�����������Ƶ
		for(NodeInfo ni: Main.nodes){
			if(ni.nodeID.equalsIgnoreCase(rootMacaddr)){
				for(j = 1;j<radioNum;j++){
					visited[i][j] = 1;
				}
			}
			i++;
		}
		for(NodeInfo ni: Main.nodes){
			if(ni.nodeID.equalsIgnoreCase(rootMacaddr)){
				break;
			}
			rootid++;
			
		}
		i = 0;
		j = 0;
		que[0] = rootid;
		back+=1;
		//���У�Ԫ����nodeID��ÿ���ҵ���Ƶ�� t%node-number�������neighbor�µ�
		while(front != back){
			flag1 = 0;
			flag2 = 0;
			visitednum = 0;
			String radioMac = null;
			int channel = 0;
			String ssid;
			//elem��Ӧ���ǽڵ�
			elem = que[front];
			front += 1;
			niTemp = nodes.get(elem);
			for (i = 0;i<radioNum;i++){
				//�жϽڵ�ĵ�i����Ƶ�Ƿ��жϹ������Ҹ���Ƶ���ھ�
				if (visited[elem][i] == 0 && niTemp.radioInfo.get(i).neighborList.size()>0) {
					//�ھ��ж�
					for(a = 0;a<nodeNum;a++){
						for (b = 0;b<radioNum;b++){
							if(NeiInforArrary[elem][a][i][b] != 0){
								visitednum = 0;
								for(j = 0;j<radioNum;j++){
									if(visited[a][j] == 0){
										visitednum++;
									}
								}
							}
							if(visitednum == radioNum){
								flag2 = 1;//���ھӽڵ��������Ƶ��û�б���������ýڵ�ĸ���Ƶ��������
								/*
								 ������ھӽڵ��������Ƶ�������,����γɻ�·
								 ����˵��ĳ�ڵ��ĳ����Ƶ������ʱ������������ھӽڵ�һ������ȫû��������Ľڵ�
								 */
								break;
							}
						}
						if(flag2 == 1) break;
					}
					if(flag2 == 1){
						visited[elem][i] = 1;
						radioMac = niTemp.radioInfo.get(i).radioNumber;
						if(i == 0) channel = channel1;
						else channel = channel2;
						flag1 = 1;
						break;//��ѡû�н�������Ľڵ�
					}
					else{
						visited[elem][i] = 1;
						continue;
					}
				}
				//����Ƶû�����ʹ�����û���ھ�
				else if (visited[elem][i] == 0 && niTemp.radioInfo.get(i).neighborList.size()== 0) {
					
					visited[elem][i] = 1;
					continue;
				}
			}
			if (flag1 == 0){
				continue;
			}
			flag = 0;
			//��ȡmac��ַ�������λ���������ssid
			String[] submac = radioMac.split(":");
			ssid = "Link" + submac[submac.length-1];
			System.out.println(ssid);
			radioTemp = niTemp.radioInfo.get(i);
			/*
			for(a = 0;a<nodeNum;a++){
				for(b = 0;b<radioNum;b++){
					for(d = 0;d<nodeNum;d++){
						for(e = 0;e<radioNum;e++){
							System.out.print(NeiInforArrary[a][d][b][e]+" ");
						}
						System.out.print("&");
					}
					System.out.println(b+"b");
				}
				System.out.println(a+"a");
			}*/
			for(NeighborInfo nei : radioTemp.neighborList){
				for(j = 0;j<nodeNum;j++){
					niTemp1 = nodes.get(j);
					for(k = 0;k<radioNum;k++){
						radioTemp1 = niTemp1.radioInfo.get(k);
						System.out.println(radioTemp1.radioNumber+" "+nei.neighborMac);
						if(radioTemp1.radioNumber.equalsIgnoreCase(nei.neighborMac)){
							//���ڷ�ֹͬһ���ڵ�Ĳ�ͬ��Ƶ������ͬ�ĸ��ڵ���Ƶ
							flag3 = 0;
							for (q = 0;q<radioNum;q++){
								if(q != k){
									if (resultTemp[j][q] == 1){
										flag3 = 1;
									}
								}
							}
							if(flag3 == 1){
								break;
							}
							if(visited[j][k] == 0){
								flag = 1;//��ʾ�ýڵ��п������ӵ��ӽڵ�
								//Ӧ�����ӹ�������ȵ��ж�
								nodes.get(j).radioInfo.get(k).assignedssid = ssid;
								nodes.get(j).radioInfo.get(k).assignedChannel = channel;
								nodes.get(j).radioInfo.get(k).direction = "up";
								if (NeiInforArrary[elem][j][i][k]<=-70.0 || NeiInforArrary[j][elem][k][i]<=-70.0){
									for( a= 0;a<nodeNum;a++){
										for (b = 0;b<radioNum;b++){
											if(a != elem && b != i){
												if(NeiInforArrary[a][j][b][k]>-70){
													signaled[j][k] = 1;
												}
											}
										}
									}
								}
								resultTemp[j][k] = 1;
								
								visited[j][k] = 1;
								que[back] = j;
								back+=1;
								
							}
						}
					}
				}
				
				//��ȡnei���棬����node����Ϣ
			}
			if(flag == 1){
				nodes.get(elem).radioInfo.get(i).assignedssid = ssid;
				nodes.get(elem).radioInfo.get(i).assignedChannel = channel;
				nodes.get(elem).radioInfo.get(i).direction = "down";
				resultTemp[elem][i] = 1;
			}
			else{
				resultTemp[elem][i] = 0;
			}
		}
		for(a = 0;a<nodeNum;a++){
			for(b = 0;b<radioNum;b++){
				for(d = 0;d<nodeNum;d++){
					for(e = 0;e<radioNum;e++){
						System.out.print(NeiInforArrary[a][d][b][e]+" ");
					}
					System.out.print("&");
				}
				System.out.println(b+"b");
			}
			System.out.println(a+"a");
		}
		for(a = 0;a<nodeNum;a++){
			for(b = 0;b<radioNum;b++){
				if(signaled[a][b] ==1){
					for(c = 0;c<nodeNum;c++){
						for(d = 0;d<radioNum;d++){
							if(NeiInforArrary[c][a][d][b]>-70.0 && NeiInforArrary[a][c][b][d]>-70.0 && 
									NeiInforArrary[c][a][d][b]<0.0 && NeiInforArrary[a][c][b][d]<0.0 &&
									nodes.get(c).radioInfo.get(d).direction == "down"){
								System.out.println(NeiInforArrary[c][a][d][b]+" %% "+NeiInforArrary[a][c][b][d]);
								nodes.get(a).radioInfo.get(b).assignedssid = nodes.get(c).radioInfo.get(d).assignedssid;
								nodes.get(a).radioInfo.get(b).assignedChannel = nodes.get(c).radioInfo.get(d).assignedChannel;
								System.out.println(" && ");
								System.out.println(nodes.get(a).radioInfo.get(b).radioNumber+" "+nodes.get(c).radioInfo.get(d).radioNumber);
								//nodes.get(a).radioInfo.get(b).direction = "up";
								//���������ŵģ�

							}
						}
				    }
			    }
			}
		}
		for(a = 0;a<nodeNum;a++){
			results[a] = "SETLINK ";
			for(b = 0;b<radioNum;b++){
				//String subRadioMac = nodes.get(elem).radioInfo.get(i).assignedssid.substring(0, 4);
				if (resultTemp[a][b] == 1){
					radioTemp1 =  nodes.get(a).radioInfo.get(b);
					results[a] += radioTemp1.radioNumber+"#"+radioTemp1.assignedChannel+"#"+radioTemp1.assignedssid+"#";
					/**modified by zhangjian start*/
					if(radioTemp1.direction.equals("up"))
					{
						radioTemp1.mode="sta";
						nodes.get(a).radioInfo.get(b).mode = "ap";
						radioTemp1.WDS = 0;
						nodes.get(a).radioInfo.get(b).WDS = 0;
						
					}
					else if(radioTemp1.direction.equals("down"))
					{
						radioTemp1.mode="ap";
						nodes.get(a).radioInfo.get(b).mode = "ap";
						radioTemp1.WDS = 1;
						nodes.get(a).radioInfo.get(b).WDS = 1;
					}
					//radioTemp1.WDS=1;
					results[a] += radioTemp1.mode + "#" + radioTemp1.WDS + " ";
					/**modified by zhangjian end*/
					
					
					System.out.println(results[a]);
				}
				else{
					radioTemp1 =  nodes.get(a).radioInfo.get(b);
					results[a] += radioTemp1.radioNumber+"#"+"DISABLED"+" ";
					System.out.println(results[a]);
				}
			}
			results[a] = results[a].substring(0, results[a].length()-1);
			System.out.println(results[a]);
		}
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		for(a = 0;a<nodeNum;a++){
			System.out.println(results[a]);
		}
		for(a = 0;a<nodeNum;a++){
			SendToNode(nodes.get(a).nodeID, results[a]);
		}
	}
		//����γ��ַ�����ssid��channel
		//String 
	public static boolean queEmpty(int que[])// ����γ�
	{
		int i = 0; 
		for(i = 0;i<que.length;i++){
			if(que[i] != 0){
				return false;
			}
		}
		return true;
	}
	/*
	public static void rootswitch(){
		int i = 0;
		int radionum = Main.nodes.get(0).radioInfo.size();
		NodeInfo foundni = new NodeInfo();
		for(i = 0;i<radionum;i++){
			foundni.radioInfo.add(Main.nodes.get(0).radioInfo.get(i));
		}
	}*/
	
	public static void compose()// ����γ�
	{
		int a,b,c,d,e;
		int neiNum;
		RadioInfo ra;
		RadioInfo ras;
		System.out.println("send here");
		NodeInfo ni = nodes.get(0);
		NodeInfo nis;
		NeiInforArrary = new double[nodes.size()][nodes.size()][ni.radioInfo.size()][ni.radioInfo.size()];
		for(a = 0;a<nodes.size();a++){
			for(b = 0;b<ni.radioInfo.size();b++){
				for(d = 0;d<nodes.size();d++){
					for(e = 0;e<ni.radioInfo.size();e++){
						NeiInforArrary[a][d][b][e] = 0.0;
					}
				}	
			}
		}
		for(a = 0;a<nodes.size();a++){
			ni = nodes.get(a);
			System.out.println(ni.nodeID);
			for(b = 0;b<ni.radioInfo.size();b++){
				ra = ni.radioInfo.get(b);
				neiNum = ra.neighborList.size(); 
				for(c = 0;c<neiNum;c++){
					NeighborInfo nei = ra.neighborList.get(c);
					for(d = 0;d<nodes.size();d++){
						nis = nodes.get(d);
						for(e = 0;e<nis.radioInfo.size();e++){
							ras = nis.radioInfo.get(e);
							System.out.println(nei.neighborMac+" "+ras.radioNumber);
							System.out.println(nei.neighborMac.equalsIgnoreCase(ras.radioNumber));
							if(nei.neighborMac.equalsIgnoreCase(ras.radioNumber)){
								NeiInforArrary[a][d][b][e] = nei.signal;
							}
						}	
					}
				}
			}
		}
	}
	
	private static void SendToNode(String nodeid, String command)// ��ĳ��ָ���Ľڵ㷢��һ������
	{
		ConnectionThreadSendCommand ctsc = null;
		synchronized (Connections.sendCommandListLock)// ���������������б�����
		{
			for (ConnectionThreadSendCommand c : Connections.sendCommandList)// �ڷ������������б��У��ҵ�nodeid��ͬ������
			{
				System.out.println("nodeID : "+c.nodeID);
				System.out.println("nodeid : "+nodeid);
				if (c.nodeID.equals(nodeid))
				{
					System.out.println("ctsc = c");
					ctsc = c;
					break;
				}
			}
		}
		if (ctsc == null)
		{
			return;
		}
		System.out.println("send here!");
		ctsc.sendCommand(command);// ��������
	}

	
	
	public static void BFStestinit(String neighborinform)  // �߳�����ʱ��ִ�д˺���
	{
		int j = 0;
		String lines[] = new String[8];
		/*
		lines[0] = "NEIGHBOR 04:F0:21:39:64:12 04:F0:21:39:64:11 36 04:f0:21:39:64:1B#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2 04:f0:21:39:64:17#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
		lines[1] = "NEIGHBOR 04:F0:21:39:64:1C 04:F0:21:39:64:1B 36 04:f0:21:39:64:11#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2 04:f0:21:39:64:17#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
		lines[2] = "NEIGHBOR 04:F0:21:39:64:1C 04:F0:21:39:64:1C 11 04:f0:21:39:64:14#-71#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2 04:f0:21:39:64:16#-28#-95#781.000000#VHT-MCS$9$80MHz$VHT-NSS$2#867.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
		lines[3] = "NEIGHBOR 04:F0:21:39:64:18 04:F0:21:39:64:17 36 04:f0:21:39:64:11#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
		lines[4] = "NEIGHBOR 04:F0:21:39:64:18 04:F0:21:39:64:18 11 04:f0:21:39:64:20#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2 04:f0:21:39:64:14#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
		lines[5] = "NEIGHBOR 04:F0:21:39:64:14 04:F0:21:39:64:14 11 04:f0:21:39:64:1C#-71#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2 04:f0:21:39:64:18#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
		lines[6] = "NEIGHBOR 04:F0:21:39:64:16 04:F0:21:39:64:16 11 04:f0:21:39:64:1C#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
		lines[7] = "NEIGHBOR 04:F0:21:39:64:20 04:F0:21:39:64:20 11 04:f0:21:39:64:18#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
		*/
		String str1 = "temp.txt"; 
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader br = new BufferedReader(fr);   
			try {
				neighborinform = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
		
		
			InputStream is;
			Scanner scanner = null;
			String line = neighborinform;
			//neighborinform = lines[0];
		//is = neighborinform;
		//is = connection.getInputStream(); // ��ȡTCP���ӵ�������
		//System.out.print("aaaa\n");
		//System.out.print(is);
		//scanner = new Scanner(is); // ��Scanner��װ������
		//while (true) // һֱѭ����ÿ�ζ�ȡһ��
			while (neighborinform != null) {
			
				line = neighborinform;
				System.out.println(line);
				
				//System.out.print(line);
				line.replace("\r", ""); // ȥ����β�Ļ��з�
				line.replace("\n", "");
				//line = "NEIGHBOR 04:F0:21:39:64:1C 04:F0:21:39:64:1B 36 04:f0:21:39:64:1f"
				//		+ "#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
				String[] parts = line.split(" "); // �Կո�Ϊ�ָ������Խ��յ���һ�н��зָ�
				String command = parts[0];
				System.out.println("command : "+command);
				if (command.equals("NEIGHBOR")) // �����һ�����ھ���Ϣ
				{

					boolean found = false;
					NodeInfo foundni = null;
					System.out.println(Main.nodes.size());
					//��ӡ��Ϣ
					for (NodeInfo ni : Main.nodes)// �ڽڵ��б��в�������ڵ�
					{
						if (ni.nodeID.equals(parts[1]))// ����ҵ���������ѭ��
						{
							found = true;
							foundni = ni;
							break;
						}
					}
					if (found == false) // ���û���ҵ����򴴽�һ���������ӵ��ڵ��б���
					{
						foundni = new NodeInfo();
						foundni.nodeID = parts[1];
						System.out.println("foundni.nodeID : "+foundni.nodeID);
						foundni.radioInfo = new ArrayList<RadioInfo>();
						Main.nodes.add(foundni);
					}

					found = false;
					RadioInfo foundri = null;
					for (RadioInfo ri : foundni.radioInfo)// ����ĳ���ڵ��ĳ��radio
					{
						if ((ri.radioNumber.equals(parts[2]))
								&& (ri.assignedChannel == Integer.decode(parts[3]).intValue()))// ����ҵ�����Ҫradio��mac��ַ���ŵ��Ŷ�ƥ�䣩
						{
							found = true;
							foundri = ri;
						}
					}
					if (found == false)// ���û���ҵ����򴴽�һ���������ӵ�radio�б���
					{
						foundri = new RadioInfo();
						foundri.radioNumber = parts[2];
						System.out.println("foundri.radioNumber : "+foundri.radioNumber);
						foundri.assignedChannel = Integer.decode(parts[3]).intValue();
						foundri.neighborList = new ArrayList<NeighborInfo>();
						foundni.radioInfo.add(foundri);
						//��ӡ��Ϣ
					}

					for (int i = 4; i < parts.length; i++)// �����ھ���Ϣ��ʣ�ಿ��
					{
						String s = parts[i];
						if(s.equals("NONEIGHBOR")){
							break;
						}
						//��ӡ��Ϣ
						String[] parts2 = s.split("#");// ���ھ���Ϣ�Ծ���Ϊ�ָ����ֿ�
						found = false;
						NeighborInfo foundni2 = null;
						//��ӡ��Ϣ
						for (NeighborInfo ni : foundri.neighborList)// �����е��ھ��б��в�������ھ�
						{
							if (ni.neighborMac.equals(parts2[0]))// ����ҵ���������ѭ��
							{
								found = true;
								foundni2 = ni;
								break;
							}
						}
						if (found == false)// ���û���ҵ������´���һ���ھ���Ϣ��Ŀ
						{
							foundni2 = new NeighborInfo();
							foundni2.neighborMac = parts2[0];
							System.out.println("parts2[0] : "+parts2[0]);
							foundri.neighborList.add(foundni2);
						}
						for(int t = 0;t<7;t++){
							System.out.println("t:"+t+" "+parts2[t]);
						}
						
						
						foundni2.signal = Double.parseDouble(parts2[1]);
						foundni2.noise = Double.parseDouble(parts2[2]);
						foundni2.tx_rate = Double.parseDouble(parts2[3]);
						foundni2.tx_QAM = parts2[4];
						foundni2.rx_rate = Double.parseDouble(parts2[5]);
						foundni2.rx_QAM = parts2[6];
						foundni2.rate = 0.5*foundni2.tx_rate;// �洢�ھӵ���Ϣ
						System.out.println("foundni2 : r:"+foundni2.rate+" s:"+foundni2.signal +" n:"+foundni2.noise +" t:"+
						foundni2.tx_rate +" tq:"+foundni2.tx_QAM +" r:"+foundni2.rx_rate +" rq:"+foundni2.rx_QAM );
						//��ӡ��Ϣ
					}
				}
				j++;
				try {
					neighborinform = br.readLine();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
}

class Connections// ���ӵ��б�
{
	public static ArrayList<ConnectionThreadReceive> receiveList;// ���ڽ����ھ���Ϣ�������б�
	public static Object receiveListLock;// �б�����

	public static ArrayList<ConnectionThreadSendCommand> sendCommandList;// ���ڷ��Ϳ�������������б�
	public static Object sendCommandListLock;// �б�����
}

class ServerThreadReceive extends Thread // ���̼߳������ڽ����ھ���Ϣ�Ķ˿ڣ�������������
{
	@Override
	public void run()
	{
		try
		{
			ServerSocket serverSocket = new ServerSocket(10001, 10);
			while (true)
			{
				Socket socket = serverSocket.accept();
				ConnectionThreadReceive ctr = new ConnectionThreadReceive(socket);
				synchronized (Connections.receiveListLock)
				{
					Connections.receiveList.add(ctr);
				}
				ctr.start();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("�ڽ����ھ���Ϣ�������߳��г����쳣");
		}
	}
}

class ConnectionThreadReceive extends Thread
{
	private Socket connection; // ��ͻ��˽���������

	ConnectionThreadReceive(Socket socket) // ���췽������һ����ͻ��˵�������Ϊ����
	{
		connection = socket;
	}

	@Override
	public void run() // �߳�����ʱ��ִ�д˺���
	{
		InputStream is;
		Scanner scanner = null;
		try
		{
			is = connection.getInputStream(); // ��ȡTCP���ӵ�������
			System.out.print("aaaa\n");
			//System.out.print(is);
			scanner = new Scanner(is); // ��Scanner��װ������
			while (true) // һֱѭ����ÿ�ζ�ȡһ��
			{
				String line = scanner.nextLine();
				System.out.print(line);
				line.replace("\r", ""); // ȥ����β�Ļ��з�
				line.replace("\n", "");
				//line = "NEIGHBOR 04:F0:21:39:64:1C 04:F0:21:39:64:1B 36 04:f0:21:39:64:1f"
				//		+ "#-28#-95#780.000000#VHT-MCS$9$80MHz$VHT-NSS$2#866.000000#VHT-MCS$9$80MHz$short$GI$VHT-NSS$2";
				String[] parts = line.split(" "); // �Կո�Ϊ�ָ������Խ��յ���һ�н��зָ�
				String command = parts[0];
				System.out.println("command : "+command);
				if (command.equals("NEIGHBOR")) // �����һ�����ھ���Ϣ
				{
					synchronized (Main.nodesLock)
					{
						boolean found = false;
						NodeInfo foundni = null;
						//��ӡ��Ϣ
						for (NodeInfo ni : Main.nodes)// �ڽڵ��б��в�������ڵ�
						{
							if (ni.nodeID.equals(parts[1]))// ����ҵ���������ѭ��
							{
								found = true;
								foundni = ni;
								break;
							}
						}
						if (found == false) // ���û���ҵ����򴴽�һ���������ӵ��ڵ��б���
						{
							foundni = new NodeInfo();
							foundni.nodeID = parts[1];
							System.out.println("foundni.nodeID : "+foundni.nodeID);
							foundni.radioInfo = new ArrayList<RadioInfo>();
							Main.nodes.add(foundni);
						}

						found = false;
						RadioInfo foundri = null;
						for (RadioInfo ri : foundni.radioInfo)// ����ĳ���ڵ��ĳ��radio
						{
							if ((ri.radioNumber.equals(parts[2]))
									&& (ri.assignedChannel == Integer.decode(parts[3]).intValue()))// ����ҵ�����Ҫradio��mac��ַ���ŵ��Ŷ�ƥ�䣩
							{//???�ŵ�ҲҪһ�£��Ƿ�һ����ͬ
								found = true;
								foundri = ri;
							}
						}
						if (found == false)// ���û���ҵ����򴴽�һ���������ӵ�radio�б���
						{
							foundri = new RadioInfo();
							foundri.radioNumber = parts[2];
							System.out.println("foundri.radioNumber : "+foundri.radioNumber);
							foundri.assignedChannel = Integer.decode(parts[3]).intValue();
							foundri.neighborList = new ArrayList<NeighborInfo>();
							foundni.radioInfo.add(foundri);
							//��ӡ��Ϣ
						}

						for (int i = 4; i < parts.length; i++)// �����ھ���Ϣ��ʣ�ಿ��
						{
							String s = parts[i];
							//��ӡ��Ϣ
							String[] parts2 = s.split("#");// ���ھ���Ϣ�Ծ���Ϊ�ָ����ֿ�
							found = false;
							NeighborInfo foundni2 = null;
							//��ӡ��Ϣ
							for (NeighborInfo ni : foundri.neighborList)// �����е��ھ��б��в�������ھ�
							{
								if (ni.neighborMac.equals(parts2[0]))// ����ҵ���������ѭ��
								{
									found = true;
									foundni2 = ni;
									break;
								}
							}
							if (found == false)// ���û���ҵ������´���һ���ھ���Ϣ��Ŀ
							{
								foundni2 = new NeighborInfo();
								foundni2.neighborMac = parts2[0];
								System.out.println("parts2[0] : "+parts2[0]);
								foundri.neighborList.add(foundni2);
							}
							for(int t = 0;t<7;t++){
								System.out.println("t:"+t+" "+parts2[t]);
							}
							
							
							foundni2.signal = Double.parseDouble(parts2[1]);
							foundni2.noise = Double.parseDouble(parts2[2]);
							foundni2.tx_rate = Double.parseDouble(parts2[3]);
							foundni2.tx_QAM = parts2[4];
							foundni2.rx_rate = Double.parseDouble(parts2[5]);
							foundni2.rx_QAM = parts2[6];
							foundni2.rate = 0.5*foundni2.tx_rate;// �洢�ھӵ���Ϣ
							System.out.println("foundni2 : r:"+foundni2.rate+" s:"+foundni2.signal +" n:"+foundni2.noise +" t:"+
							foundni2.tx_rate +" tq:"+foundni2.tx_QAM +" r:"+foundni2.rx_rate +" rq:"+foundni2.rx_QAM );
							//��ӡ��Ϣ
						}
					}
				}
			}
		}
		catch (IOException e)
		{
			try
			{
				scanner.close();
			}
			catch (Exception e1)
			{

			}
		}
		synchronized (Connections.receiveListLock)// �Խ����ھ���Ϣ�����б�����
		{
			for (int i = 0; i < Connections.receiveList.size(); i++)// �����б�
			{
				if (Connections.receiveList.get(i) == this)// �����ӹر�ʱ������ǰ�����Ӵ��б��г�ȥ
				{
					Connections.receiveList.remove(i);
					i--;
				}
			}
		}
	}
}





class ServerThreadSendCommand extends Thread // ���̼߳��������·��ڵ����õĶ˿ڣ�������������
{
	@Override
	public void run()
	{
		try
		{
			ServerSocket serverSocket = new ServerSocket(10002, 10);// ����10002�˿�
			System.out.println("start connect");
			while (true)// һֱѭ��
			{
				Socket socket = serverSocket.accept();// ��������
				System.out.println("accept");
				ConnectionThreadSendCommand ctsc = new ConnectionThreadSendCommand(socket);
				synchronized (Connections.sendCommandListLock)// �Է������������б�����
				{
					System.out.println("synchronized");
					Connections.sendCommandList.add(ctsc);// ����ǰ���Ӽ��뵽�б���
				}
				ctsc.start();// �����߳�
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("�ڷ�������������߳��з����쳣");
		}
	}
}

class ConnectionThreadSendCommand extends Thread// ���ڷ�������������߳�
{
	private Socket connection; // ��ͻ��˽���������
	public String nodeID;// �ڵ��ID
	private ArrayList<String> commandToSend;// Ҫ���͵�����
	private Object queueLock;// ����������е���

	public ConnectionThreadSendCommand(Socket socket) // ���췽������һ����ͻ��˵�������Ϊ����
	{
		connection = socket;
		commandToSend = new ArrayList<String>();
		queueLock = new Object();
	}

	
	@Override
	public void run() // �߳�����ʱ��ִ�д˺���
	{
		System.out.println("ConnectionThreadSendCommand ");
		InputStream is = null;
		Scanner scanner = null;
		OutputStream os = null;
		PrintWriter printer = null;
		try
		{
			is = connection.getInputStream(); // ��ȡTCP���ӵ�������
			scanner = new Scanner(is); // ��Scanner��װ������

			os = connection.getOutputStream();// ��ȡTCP���ӵ������
			printer = new PrintWriter(os);// ��PrintWriter��װ�����

			String line = scanner.nextLine();// ��ȡһ��
			System.out.println(line);
			line.replace("\r", ""); // ȥ����β�Ļ��з�
			line.replace("\n", "");

			nodeID = line;

			while (true) // һֱѭ��
			{
				synchronized (queueLock)// ��������м���
				{
					while (!commandToSend.isEmpty())// ��������зǿ�
					{
						String command = commandToSend.get(0);// ȡ�����е�ͷ��Ԫ��
						commandToSend.remove(0);

						printer.println(command + "\r\n");// ���ʹ�����
						//os.flush();
						printer.flush();
						System.out.println("printer: "+ command);
					}
				}
				Thread.sleep(100);
			}
		}
		catch (Exception e)// �������쳣
		{
			try
			{
				scanner.close();// �ͷ���Դ
				printer.close();
			}
			catch (Exception e1)
			{

			}
		}
		synchronized (Connections.sendCommandListLock)// �Է������������б�����
		{
			for (int i = 0; i < Connections.sendCommandList.size(); i++)// �����б�
			{
				if (Connections.sendCommandList.get(i) == this)// �����ӹر�ʱ������ǰ�����Ӵ��б��г�ȥ
				{
					Connections.sendCommandList.remove(i);
					i--;
				}
			}
		}
	}

	public void sendCommand(String command)// ʹ�õ�ǰ�����ӷ���һ������
	{
		System.out.println("sendCommand, "+command);
		
		synchronized (queueLock)// ��������м���
		{
			commandToSend.add(command);// ��������뷢�Ͷ�����
		}
	}
}

class NodeInfo // �ڵ���Ϣ
{
	public String nodeID;
	public ArrayList<RadioInfo> radioInfo;
}

class RadioInfo // radio��Ϣ
{
	public String radioNumber;
	public int assignedChannel;
	public String direction;
	public String assignedssid;
	public ArrayList<NeighborInfo> neighborList;
	/**modified by zhangjian start*/
	public String mode;//radio��ģʽ����AP����client��
	public int WDS;
	/**modified by zhangjian end*/
	
}

class NeighborInfo // �ھ���Ϣ
{
	public String neighborMac;
	public double rate;
	public double signal;
	public double noise;
	public double tx_rate;
	public String tx_QAM;
	public double rx_rate;
	public String rx_QAM;
}