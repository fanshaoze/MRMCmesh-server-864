import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Scanner;

public class Main// ����
{
	private static ServerThreadReceive str; // ���ڽ����ھ���Ϣ�ķ������߳�
	private static ServerThreadSendCommand stsc;// ���ڷ��Ϳ�������ķ������߳�
	
	public static ArrayList<NodeInfo> nodes;// �ڵ���Ϣ���б�
	public static Object nodesLock;// �б����

	private static MainWindow mainWindow;
	private static DataOperation dataOperation;
	public static double[][][][] NeiInforArrary;
	public static double[][][][] resultgraph_r;
	public static double[][] resultgraph_n;
	public static int maxlevel;
	public static int [][] visited;
	public static double [][] resultTemp;
	public static String[] results;
	
	public static double [] Dxofson;
	public static double [] diffSonProExpthr;
	
	public static double [] Dxofson_pre = new double[Main.maxlevel];
	public static double [] diffSonProExpthr_pre= new double[Main.maxlevel];
	
	public static int radiocount_channel = 0;
	public static int radiocount_load = 0;
	
	public static String rootMacaddr;
	public static int radionum = 2;//改成可配置的
	public static int nodenum = 5;//改成可配置的
	public static int channel1 = 36;
	public static int channel2 = 149;
	public static double orithroughput = 500.0;
	public static double Minthroughput = 100.0;
	public static double throughput66 = 500.0;
	public static double throughput75 = 300.0;
	public static double attenuation = 0.15;
	public static double minSNR = -66.0;
	public static double  bottomSNR= -80.0;
	public static int  enableLB= 0;
	public static int [] nodesignal;
	public static int [][] radiosignal;
	public static long starttime = 0;
	public static long endtime = 0;
	//两个单位是分钟
	public static int channelduration = 1440;
	public static int balanceduration = 5;
	public static int maxloadbalance = 10;
	

	
	
	
	public static void main(String[] args)// �������ڵ�
	{
		int i = 0;
		int j = 0;
		
		System.out.println("start");
		//DataOperation.connect("jdbc:sqlite:MRMCmeshData.db");
		Connections.receiveList = new ArrayList<ConnectionThreadReceive>();// ��ʼ�������б�
		System.out.println("start");
		Connections.receiveListLock = new Object();
		System.out.println("start");
		Connections.sendCommandList = new ArrayList<ConnectionThreadSendCommand>();
		System.out.println("start");
		Connections.sendCommandListLock = new Object();
		System.out.println("start");
//�޸ĸ��ڵ��ַ
		
		
		rootMacaddr = "04:F0:21:3D:B5:AD";
		channel1 = 36;
		channel2 = 149;
		orithroughput = 500.0;
		Minthroughput = 100.0;
		throughput66 = 500.0;
		throughput75 = 300.0;
		attenuation = 0.15;
		minSNR = -66.0;
		bottomSNR= -80.0;
		enableLB = 0;
		channelduration = 1440;
		balanceduration = 5;
		getconfig();
		
		//չʾ���е�������Ϣ
		System.out.println("configs:"+rootMacaddr+" "+radionum+" "+nodenum+" "+channel1+" "+channel2+" "+orithroughput
				+" "+Minthroughput+" "+throughput66+" "+throughput75+" "+attenuation+
				" "+minSNR+" "+bottomSNR+" "+enableLB+" "+channelduration+" "+balanceduration);
		System.out.println(System.currentTimeMillis());
		visited = new int[nodenum][radionum];
		resultTemp = new double[nodenum][radionum];
		nodesignal = new int [nodenum];
		radiosignal = new int [nodenum][Main.radionum];
		results = new String[nodenum];
		for(i = 0;i<nodenum;i++){
			nodesignal[i] = 0;
		}
		for(i = 0;i<nodenum;i++){
			for(j = 0;j<Main.radionum;j++){
				radiosignal[i][j] = 0;
			}
		}
		nodes = new ArrayList<NodeInfo>();// ��ʼ���ڵ���Ϣ�б�
		nodesLock = new Object();// ��ʼ���б����
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
				//dropinform();
				System.out.println("Sending configurations to routers.");
				SendConfiguration();
			}
		}
	}
	/*
	public static void dropinform(){
		int i,j = 0;
		for(i = 0;i<Main.nodes.size();i++){
			if(Main.nodesignal[i] == 0){
				try {
					DataOperation.dropNode(Main.nodes.get(i).nodeID);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		for(i = 0;i<nodenum;i++){
			for(j = 0;j<radionum;j++){
				if(Main.radiosignal[i][j] == 0){
					try {
						DataOperation.dropNoderadio(Main.nodes.get(i).radioInfo.get(j).radioNumber);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	*/
	public static void getconfig()  // �߳�����ʱ��ִ�д˺���
	{

		String neighborinform = " ";
		int j = 0;
		String lines[] = new String[8];
		String str1 = "serverconfig"; 
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
			while (neighborinform != null) {
				line = neighborinform;
				System.out.println(line);
				line.replace("\r", ""); // ȥ����β�Ļ��з�
				line.replace("\n", "");
				String[] parts = line.split(" "); // �Կո�Ϊ�ָ������Խ��յ���һ�н��зָ�
				String configs = parts[0];
				if (configs.equals("rootMacaddr")){
					rootMacaddr = parts[1];
				}
				else if(configs.equals("nodenum")){
					nodenum = Integer.parseInt(parts[1]);
				}
				else if(configs.equals("radionum")){
					radionum = Integer.parseInt(parts[1]);
				}
				else if(configs.equals("channel1")){
					channel1 = Integer.parseInt(parts[1]);
				}
				else if(configs.equals("channel2")){
					channel2 = Integer.parseInt(parts[1]);
				}
				else if(configs.equals("orithroughput")){
					orithroughput = Double.valueOf(parts[1]);
				}
				else if(configs.equals("Minthroughput")){
					Minthroughput = Double.valueOf(parts[1]);
				}
				else if(configs.equals("throughput66")){
					throughput66 = Double.valueOf(parts[1]);
				}
				else if(configs.equals("throughput75")){
					throughput75 = Double.valueOf(parts[1]);
				}
				else if(configs.equals("attenuation")){
					attenuation = Double.valueOf(parts[1]);
				}
				else if(configs.equals("minSNR")){
					minSNR = Double.valueOf(parts[1]);
				}
				else if(configs.equals("bottomSNR")){
					bottomSNR = Double.valueOf(parts[1]);
				}
				else if(configs.equals("enableLB")){
					enableLB = Integer.parseInt(parts[1]);
				}
				else if(configs.equals("channelduration")){
					channelduration = Integer.parseInt(parts[1]);
				}
				else if(configs.equals("balanceduration")){
					balanceduration = Integer.parseInt(parts[1]);
				}
				else continue;
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
	
	

	public static void SendConfiguration()// �·����нڵ������
	{

		//SendToNode("04:F0:21:39:C1:5F", "SETLINK 04:F0:21:39:C1:5F#36#link203#ap#0 04:F0:21:39:C1:60#149#link204#ap#0");
		
		////getNodeLoad();
		////System.out.println("send here");
		
		SendToNode("04:F0:21:36:E5:59", "SETLINK 04:F0:21:36:E5:59#36#Link3C#ap#1 04:F0:21:3B:F4:A9#149#LinkAD#ap#1");
		//SendToNode("04:F0:21:39:C1:60", "SETLINK 04:F0:21:39:C1:5F#36#link3C#sta#1 04:F0:21:39:C1:60#DISABLED");
		//SendToNode("04:F0:21:4B:4C:A4", "SETLINK 04:F0:21:4B:4C:A4#36#link3C#sta#1 04:F0:21:35:27:CF#DISABLED");
		
		//BFSofMRMC();
		////Main.starttime = System.currentTimeMillis();
		
		
		//SendToNode("04:F0:21:39:C1:91", "SETLINK 04:F0:21:39:C1:91##36#Link201#ap#1 04:F0:21:36:21:09#DISABLED");
		//SendToNode("04:F0:21:39:C1:5B", "SETLINK 04:F0:21:39:C1:5B##36#Link201#sta#1 04:F0:21:39:C1:92#DISABLED");
		//SendToNode("04:F0:21:39:C1:5F", "SETLINK 04:F0:21:39:C1:5F#36#link203#ap#0 04:F0:21:39:C1:60#149#link204#ap#1");
		//SendToNode("04:F0:21:39:64:06", "SETLINK 04:F0:21:39:64:06#6#link204 04:F0:21:39:64:05#DISABLED");
		//SendToNode("04:F0:21:39:64:26", "SETLINK 04:F0:21:39:64:26#6#link204 04:F0:21:39:64:25#DISABLED");
		//SendToNode("04:F0:21:39:64:1C", "SETLINK 04:F0:21:39:64:1B#36#link204 04:F0:21:39:64:1F#36#link203");
	}
	public static void getNodeLoad(){//该函数具备树状多射频兼容性
		int i = 0;
		int j = 0;
		int tmpj = 0;
		double maxload = 0;
		for(i = 0;i<nodenum;i++){
			for(j = 0;j<radionum;j++){
				if(nodes.get(i).radioInfo.get(j).load>=maxload){
					maxload = nodes.get(i).radioInfo.get(j).load;
					tmpj = j;
				}
			}
			nodes.get(i).own_load = maxload;
			for(j = 0;j<radionum;j++){
				if(j != tmpj){
					nodes.get(i).own_load -= nodes.get(i).radioInfo.get(j).load;
				}
			}
			tmpj = 0;
			maxload = 0;
		}
	}
	public static void BFSofMRMC(){// MRMC�ĳ����㷨�������������
		//���ļ��ķ�ʽ
		//BFStestinit("");
		//������־
		int i,j,k,q= 0;
		int tmpj = -1,tmpk = -1;//���ڻ���Ԥ��ѡ���ھӽڵ�
		//����ͷ����β��
		int front = 0;
		int back = 0;
		//radioѭ������
		int elem = 0;
		//���ڻ�ȡÿ���ڵ��ÿ����Ƶ��Ϣ
		int a,b,c,d,e;
		int tmpb = -1;
		
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
		maxlevel = 0;
		double maxthroughput = -1;
		double tmpthroughput;
		
		RadioInfo radioTemp;
		RadioInfo radioTemp1;
		nodeNum = nodes.size();
		System.out.println("nodenum "+nodes.size());
		for(i = 0;i<nodeNum;i++){
			System.out.println("node "+nodes.get(i).nodeID+" "+nodes.get(i).radioInfo.size());
			for (j = 0;j<nodes.get(i).radioInfo.size();j++){
				System.out.println("radioinfor "+nodes.get(i).radioInfo.get(j).radioNumber);
			}	
			if(nodes.get(i).radioInfo.size()> radioNum){
				radioNum = nodes.get(i).radioInfo.size();
			}
		}
		radionum = radioNum;
		//int [][] visited = new int [nodeNum][radioNum];
		int [][] signaled = new int [nodeNum][radioNum];
		//double[][] resultTemp = new double[nodeNum][radioNum];
		
		System.out.print( "Radio number:"+radioNum+" node number:"+nodeNum+"\n");
		//չʾ��ǰ̨

		
		compose(nodeNum,radioNum);
		ResultgraphRadioInit(nodeNum, radioNum);
		ResultgraphNodeInit(nodeNum);
		for(a = 0;a<nodeNum;a++){
			for(b = 0;b<radioNum;b++){
				for(d = 0;d<nodeNum;d++){
					for(e = 0;e<radioNum;e++){
						System.out.print(NeiInforArrary[a][d][b][e]+" ");
					}
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
		Main.nodes.get(rootid).rank = 0;
		Main.nodes.get(rootid).expthroughput = 1000;
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
									if (visited[j][q] == 1) flag3 = 1;
								}
							}
							if(flag3 == 1) break;
							if(visited[j][k] == 0 && NeiInforArrary[elem][j][i][k]>minSNR && NeiInforArrary[j][elem][k][i]>minSNR){
								Main.nodes.get(j).rank = Main.nodes.get(elem).rank + 1;
								Main.nodes.get(j).expthroughput = orithroughput*(1-(Main.nodes.get(j).rank-1)*attenuation);
								flag = 1;//��ʾ�ýڵ��п������ӵ��ӽڵ�
								nodes.get(j).radioInfo.get(k).assignedssid = ssid;
								nodes.get(j).radioInfo.get(k).direction = "up";
								resultgraph_r[elem][j][i][k] = 1;
								resultgraph_r[j][elem][k][i] = 1;
								resultgraph_n[elem][j] = 1;
								resultgraph_n[j][elem] = 1;
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
				nodes.get(elem).radioInfo.get(i).direction = "down";
				resultTemp[elem][i] = 1;
				visited[elem][i] = 1;
			}
			else{
				resultTemp[elem][i] = 0;
				visited[elem][i] = 0;
			}
		}
		flag = 0;
		for(a = 0;a<nodeNum;a++){
			visitednum = 0;
			for (b = 0;b<radioNum;b++){
				if(resultTemp[a][b] == 0){
					visitednum++;
				}
			}
			if(visitednum == radioNum){
				flag = 1;
				maxthroughput = -1;
				tmpthroughput = 0;
				tmpj = -1;
				tmpk = -1;
				tmpb = -1;
				//�ýڵ�������Ƶ��û�в������������Ҫ����Ԥ��������
				for(b = 0;b<radioNum;b++){
					for(NeighborInfo nei : Main.nodes.get(a).radioInfo.get(b).neighborList){
						for(j = 0;j<nodeNum;j++){
							niTemp1 = nodes.get(j);
							for(k = 0;k<radioNum;k++){
								radioTemp1 = niTemp1.radioInfo.get(k);
								System.out.println(radioTemp1.radioNumber+" "+nei.neighborMac);
								if(radioTemp1.radioNumber.equalsIgnoreCase(nei.neighborMac)){
									if(NeiInforArrary[a][j][b][k]> bottomSNR && NeiInforArrary[j][a][k][b]> bottomSNR){
										System.out.println(resultTemp[j][k]+" "+resultTemp[j][1-k]+" "+radioTemp1.direction);
										if((resultTemp[j][k] == 1 || resultTemp[j][1-k] == 1)&& 
												(radioTemp1.direction.equalsIgnoreCase("down") || radioTemp1.direction.equalsIgnoreCase("none"))){
											
											if(niTemp1.expthroughput > throughput75){
												//??
												tmpthroughput = throughput75*(1-attenuation);
											}
											else if(niTemp1.expthroughput <= throughput75){
												//??
												tmpthroughput = niTemp1.expthroughput*(1-attenuation);
											}
											if (tmpthroughput > maxthroughput){
												maxthroughput = tmpthroughput;
												tmpb = b;
												tmpj = j;
												tmpk = k;
											}	
										}
										else continue;
									}
								}
									
							}
						}
					}
				}
			}
			if (maxthroughput >= Minthroughput){
				if(nodes.get(tmpj).radioInfo.get(tmpk).direction.equalsIgnoreCase("down")){
					nodes.get(a).radioInfo.get(tmpb).direction = "up";
					nodes.get(a).radioInfo.get(tmpb).assignedssid = nodes.get(tmpj).radioInfo.get(tmpk).assignedssid;
					nodes.get(a).expthroughput = maxthroughput;
					nodes.get(a).rank = nodes.get(tmpj).rank + 1;
					resultgraph_r[tmpj][a][tmpk][tmpb] = 1;
					resultgraph_r[a][tmpj][tmpb][tmpk] = 1;
					resultgraph_n[tmpj][a] = 1;
					resultgraph_n[a][tmpj] = 1;
					resultTemp[a][tmpb] = 1;
					visited[a][tmpb] = 1;
				}
				else if(nodes.get(tmpj).radioInfo.get(tmpk).direction.equalsIgnoreCase("none")){
					nodes.get(tmpj).radioInfo.get(tmpk).direction = "down";
					String[] submac = nodes.get(tmpj).radioInfo.get(tmpk).radioNumber.split(":");
					nodes.get(tmpj).radioInfo.get(tmpk).assignedssid = "Link" + submac[submac.length-1];
					resultgraph_r[tmpj][a][tmpk][tmpb] = 1;
					resultgraph_r[a][tmpj][tmpb][tmpk] = 1;
					resultgraph_n[tmpj][a] = 1;
					resultgraph_n[a][tmpj] = 1;
					resultTemp[tmpj][tmpk] = 1;
					visited[tmpj][tmpk] = 1;
					
					nodes.get(a).radioInfo.get(tmpb).direction = "up";
					nodes.get(a).expthroughput = maxthroughput;
					nodes.get(a).rank = nodes.get(tmpj).rank+1;
					nodes.get(a).radioInfo.get(tmpb).assignedssid = nodes.get(tmpj).radioInfo.get(tmpk).assignedssid;
					resultTemp[a][tmpb] = 1;
					visited[a][tmpb] = 1;
				}
			}
		}
		for(a = 0;a<nodeNum;a++){
			visitednum = 0;
			for (b = 0;b<radioNum;b++){
				if(resultTemp[a][b] == 0){
					visitednum++;
				}
			}
			if(visitednum == radioNum){
				System.out.println("can not network");
				mainWindow.showInfo("can not network");
				return;
			}
			else{
				if(flag == 1){
					System.out.println("some bad links");
					mainWindow.showInfo("some bad links");
				}
				else if(flag == 0){
					System.out.println("good network");
					mainWindow.showInfo("good network");
				}
			}
		}
		for(a = 0;a<nodeNum;a++){
			if(nodes.get(a).rank>maxlevel)
			maxlevel = nodes.get(a).rank;
		}
		maxlevel += 1;
		System.out.println("maxlevel:"+maxlevel);
		
		printresultgraph(nodeNum);
		
		//网络负载均衡性评估
		DxandProInit(maxlevel);
		balanceEstimate(nodeNum,radioNum,maxlevel);
		//展示estimate结果
		//��������û�������Ľڵ㣬
		if(enableLB == 1){
			loadbalance(nodeNum,radioNum,visited,resultTemp);
			DxandProInit(maxlevel);
			balanceEstimate(nodeNum,radioNum,maxlevel);//展示estimate结果
		}
		printresultgraph(nodeNum);
		results = getresult(nodeNum,radioNum,resultTemp,results);
		System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		for(a = 0;a<nodeNum;a++){
			System.out.println("rank "+ Main.nodes.get(a).rank);
			System.out.println("throughput "+ Main.nodes.get(a).expthroughput);
			System.out.println(results[a]);	
		}
		for(a = 0;a<nodeNum;a++){
			SendToNode(nodes.get(a).nodeID, results[a]);
		}
		return;
	}
		//����γ��ַ�����ssid��channel
		//String 
	public static void balanceEstimate(int nodeNum,int radioNum,int maxlevel){
		int a = 0;
		int b = 0;
		int [] levelCount = new int[maxlevel];
		int [] nodeSonCount = new int[nodeNum];
		double [] avgThroughput = new double[maxlevel];
		double [] avgSon = new double[maxlevel];
		for(a = 0;a<maxlevel;a++){
			levelCount[a] = 0;
		}
		for(a = 0;a<nodeNum;a++){
			System.out.println("rank "+nodes.get(a).rank);
			levelCount[nodes.get(a).rank] += 1;
		}
		for(a = 0;a<nodeNum;a++){
			for(b = 0;b<nodeNum;b++){
				if(resultgraph_n[a][b] == 1)
					nodeSonCount[a] += 1;
			}
			nodeSonCount[a] -= 1;
		}
		nodeSonCount[0] += 1;
		for(a = 0;a<nodeNum;a++){
			System.out.println(a+" nodeSonCount "+nodeSonCount[a]);
		}
		for(a = 0;a<nodeNum;a++){
			avgThroughput[nodes.get(a).rank] += nodes.get(a).expthroughput;
		}
		for(a = 1;a<maxlevel;a++){
			if(levelCount[a] !=0 )
				avgThroughput[a] = avgThroughput[a]/levelCount[a];
			else
				avgThroughput[a] = 0;
		}

		for(a = 0;a<nodeNum;a++){
			avgSon[nodes.get(a).rank] += nodeSonCount[a];
		}
		for(a = 1;a<maxlevel;a++){
			if(levelCount[a] !=0 )
				avgSon[a] = avgSon[a]/levelCount[a];
			else
				avgSon[a] = 0;
		}
		for(a = 1;a<maxlevel;a++){	
			System.out.println(a+" avgSon[a] "+avgSon[a]);
		}
		
		for(a = 0;a<nodeNum;a++){
			System.out.println(a+"*****"+nodeSonCount[a]+" "+avgSon[nodes.get(a).rank]);
			Dxofson[nodes.get(a).rank]+=(nodeSonCount[a]-avgSon[nodes.get(a).rank])*(nodeSonCount[a]-avgSon[nodes.get(a).rank]);
		}
		for(a = 1;a<maxlevel;a++){	
			System.out.println(a+" Dxofson "+Dxofson[a]);
		}
		for(a = 1;a<maxlevel;a++){
			if(levelCount[a] !=0 )
				Dxofson[a] = Dxofson[a]/levelCount[a];
			else
				Dxofson[a] = 0;
		}
		
		for(a = 0;a<nodeNum;a++){
			diffSonProExpthr[nodes.get(a).rank]+=(nodeSonCount[a]-avgSon[nodes.get(a).rank])*
					(nodes.get(a).expthroughput-avgThroughput[nodes.get(a).rank]);
		}
		for(a = 1;a<maxlevel;a++){
			System.out.println(a + "\t"+"Dxofson\t"+ Dxofson[a]+"\t"+"diff\t"+ diffSonProExpthr[a]);
		}
	}
	public static void loadbalance(int nodeNum,int radioNum,int [][] visited,double [][] resultTemp){
		int i,j,k = 0;
		int a = 0;
		int b = 0;
		int rootid = 0;
		int back = 0;
		int front = 0;
		int elem = 0;
		int que[] = new int[1000];
		int queTmp[] = new int[1000];
		double remainLoad = 0;
		double overLoad = 0;
		double duration;
		duration = Main.endtime-Main.starttime;
		int flag = 0;
		int flagnoson = 0;
		
		for(i = 0;i<1000;i++){
			que[i] = -1;
		}
		for(i = 0;i<1000;i++){
			queTmp[i] = -1;
		}
		
		for(NodeInfo ni: Main.nodes){
			if(ni.nodeID.equalsIgnoreCase(rootMacaddr)){
				break;
			}
			rootid++;
		}
		i = 0;
		j = 0;
		Main.nodes.get(rootid).rank = 0;
		Main.nodes.get(rootid).expthroughput = 1000;
		queTmp[front] = rootid;
		
		back += 1;
		//���У�Ԫ����nodeID��ÿ���ҵ���Ƶ�� t%node-number�������neighbor�µ�
		int [] banvisited = new int [nodeNum];
		for(i = 0;i<nodeNum;i++){
			banvisited[i] = 0;
		}
		while(front != back){
			elem = queTmp[front]; 
			banvisited[elem] = 1;
			System.out.println("elem " + elem);
			front += 1;
			for (i = 0;i<nodeNum;i++){
				if (resultgraph_n[elem][i] != 0){
					if(banvisited[i] != 1){
						System.out.println("i " + i);
						queTmp[back] = i;
						back+=1;
					}
				}
			}
		}
		for(i = 0;i<nodeNum;i++){
			que[i] = queTmp[nodeNum-i-1];//逆序，获得叶节点向上顺序，用于调整load
		}
		for(i = 0;i<nodeNum;i++){
			elem = que[i];
			System.out.println("bl elem "+elem);
			for(j = 0;j<radioNum;j++){
				if(nodes.get(elem).radioInfo.get(j).direction.equals("up")){
					nodes.get(elem).radioInfo.get(j).load = 0;
					for(k = 0;k<radioNum;k++){
						if(nodes.get(elem).radioInfo.get(k).direction.equals("down")){
							nodes.get(elem).radioInfo.get(j).load+=nodes.get(elem).radioInfo.get(k).load;
						}
					}
					nodes.get(elem).radioInfo.get(j).load += nodes.get(elem).own_load;
					for(k = 0;k<nodeNum;k++){
						if(nodes.get(elem).radioInfo.get(j).load<nodes.get(elem).expthroughput){
							break;
						}
						if (Main.nodes.get(k).rank < Main.nodes.get(elem).rank) continue;
						if(resultgraph_n[elem][k] == 1){
							for(b = 0;b<radioNum;b++){
								if(Main.nodes.get(k).radioInfo.get(b).direction.equals("up")) break;
							}
							if (b == radioNum) continue;
							flag = findAvilableFather(elem,k,b,nodeNum,radioNum,visited,resultTemp);
							if(flag == 1){
								resultgraph_r[elem][k][j][b] = 0;
								resultgraph_r[k][elem][b][j] = 0;
								resultgraph_n[elem][k] = 0;
								resultgraph_n[k][elem] = 0;
								
								nodes.get(elem).radioInfo.get(j).load -= nodes.get(k).radioInfo.get(b).load;//可能是不必要的
								nodes.get(elem).radioInfo.get(1-j).load -= nodes.get(k).radioInfo.get(b).load;//不兼容更多射频	
								if(nodes.get(elem).radioInfo.get(1-j).load<0) nodes.get(elem).radioInfo.get(1-j).load = 0;//防止向下溢出
								for(a = 0;a<nodeNum;a++){
									if(resultgraph_n[elem][a] == 1){
										flagnoson = 1;
										break;
									}
								}
								if(flagnoson != 1){
									nodes.get(elem).radioInfo.get(j).direction = "none";
									nodes.get(elem).radioInfo.get(j).assignedssid = "Link";
									resultTemp[elem][j] = 0;
									visited[elem][j] = 0;
								}
								
							}
							flagnoson = 0;
							flag = 0;
						}
					}
				}
			}
		}
	}
	public static int findAvilableFather(int fatherNode,int sonNode,int sonRadio,int nodeNum,int radioNum,int [][] visited,double [][] resultTemp){
		int a = 0;
		int b = 0;
		int visitednum = 0;
		int flag = 0;
		int flag1 = 0;
		int i,j,k  = 0;
		int tmpj = -1;
		int tmpk = -1;
		int tmpb = -1;
		NodeInfo niTemp1;
		RadioInfo radioTemp1;
		a = sonNode;
		b = sonRadio;
		System.out.println(a+" "+b);
		for(NeighborInfo nei : Main.nodes.get(a).radioInfo.get(b).neighborList){
			for(j = 0;j<nodeNum;j++){
				if(j == fatherNode) continue;
				niTemp1 = nodes.get(j);
				for(k = 0;k<radioNum;k++){
					radioTemp1 = niTemp1.radioInfo.get(k);
					System.out.println(radioTemp1.radioNumber+" "+nei.neighborMac);
					if(radioTemp1.radioNumber.equalsIgnoreCase(nei.neighborMac)){
						if(NeiInforArrary[a][j][b][k]> minSNR && NeiInforArrary[j][a][k][b]> minSNR){
							System.out.println(resultTemp[j][k]+" "+resultTemp[j][1-k]+" "+radioTemp1.direction);
							if((resultTemp[j][k] == 1 || resultTemp[j][1-k] == 1)&& 
									(radioTemp1.direction.equalsIgnoreCase("down") || radioTemp1.direction.equalsIgnoreCase("none"))){
								if (Main.nodes.get(a).radioInfo.get(b).load <= nodes.get(j).expthroughput*(1-attenuation)){
									tmpb = b;
									tmpj = j;
									tmpk = k;
									flag = 1;
									flag1 = 1;
									
								}	
							}
							else continue;
						}
					}
				}
			}
		}
		if (flag1 == 1){
			if(nodes.get(tmpj).radioInfo.get(tmpk).direction.equalsIgnoreCase("down")){
				nodes.get(a).radioInfo.get(tmpb).direction = "up";
				nodes.get(a).radioInfo.get(tmpb).assignedssid = nodes.get(tmpj).radioInfo.get(tmpk).assignedssid;
				nodes.get(a).expthroughput = nodes.get(tmpj).expthroughput*(1-attenuation);
				nodes.get(a).rank = nodes.get(tmpj).rank + 1;
				resultgraph_r[tmpj][a][tmpk][tmpb] = 1;
				resultgraph_r[a][tmpj][tmpb][tmpk] = 1;
				resultgraph_n[tmpj][a] = 1;
				resultgraph_n[a][tmpj] = 1;
				resultTemp[a][tmpb] = 1;
				visited[a][tmpb] = 1;
				
				nodes.get(tmpj).radioInfo.get(tmpk).load+=nodes.get(a).radioInfo.get(tmpb).load;
				nodes.get(tmpj).radioInfo.get(1-tmpk).load+=nodes.get(a).radioInfo.get(tmpb).load;//不兼容
			}
			else if(nodes.get(tmpj).radioInfo.get(tmpk).direction.equalsIgnoreCase("none")){
				nodes.get(tmpj).radioInfo.get(tmpk).direction = "down";
				String[] submac = nodes.get(tmpj).radioInfo.get(tmpk).radioNumber.split(":");
				nodes.get(tmpj).radioInfo.get(tmpk).assignedssid = "Link" + submac[submac.length-1];
				resultgraph_r[tmpj][a][tmpk][tmpb] = 1;
				resultgraph_r[a][tmpj][tmpb][tmpk] = 1;
				resultgraph_n[tmpj][a] = 1;
				resultgraph_n[a][tmpj] = 1;
				resultTemp[tmpj][tmpk] = 1;
				visited[tmpj][tmpk] = 1;
				
				nodes.get(a).radioInfo.get(tmpb).direction = "up";
				nodes.get(a).expthroughput = nodes.get(tmpj).expthroughput*(1-attenuation);
				nodes.get(a).rank = nodes.get(tmpj).rank+1;
				nodes.get(a).radioInfo.get(tmpb).assignedssid = nodes.get(tmpj).radioInfo.get(tmpk).assignedssid;
				resultTemp[a][tmpb] = 1;
				visited[a][tmpb] = 1;
				
				nodes.get(tmpj).radioInfo.get(tmpk).load+=nodes.get(a).radioInfo.get(tmpb).load;
				nodes.get(tmpj).radioInfo.get(1-tmpk).load+=nodes.get(a).radioInfo.get(tmpb).load;//不兼容
			}
		}
		return flag;
	}
	public static String[] getresult(int nodeNum,int radioNum,double[][] resultTemp,String[] results)// ����γ�
	{
		RadioInfo radioTemp1;
		int a,b = 0;
		for(a = 0;a<nodeNum;a++){
			results[a] = "SETLINK ";
			for(b = 0;b<radioNum;b++){
				//String subRadioMac = nodes.get(elem).radioInfo.get(i).assignedssid.substring(0, 4);
				if (resultTemp[a][b] == 1){
					radioTemp1 =  nodes.get(a).radioInfo.get(b);
					results[a] += radioTemp1.radioNumber+"#";
					if(radioTemp1.direction.equals("up"))
					{
						radioTemp1.mode="sta";
						nodes.get(a).radioInfo.get(b).mode = "sta";
						if (nodes.get(a).rank %2 == 0){
							radioTemp1.assignedChannel=149;
							nodes.get(a).radioInfo.get(b).assignedChannel = 149;
						}
						else if (nodes.get(a).rank %2 == 1){
							radioTemp1.assignedChannel=36;
							nodes.get(a).radioInfo.get(b).assignedChannel = 36;
						}
					}
					else if(radioTemp1.direction.equals("down"))
					{
						radioTemp1.mode="ap";
						nodes.get(a).radioInfo.get(b).mode = "ap";
						if (nodes.get(a).rank %2 == 0){
							radioTemp1.assignedChannel=36;
							nodes.get(a).radioInfo.get(b).assignedChannel = 36;
						}
						else if (nodes.get(a).rank %2 == 1){
							radioTemp1.assignedChannel=149;
							nodes.get(a).radioInfo.get(b).assignedChannel = 149;
						}
					}
					radioTemp1.WDS = 1;
					nodes.get(a).radioInfo.get(b).WDS = 1;
					radioTemp1.disabled = 0;
					//DataOperation.setradio(nodes.get(a).nodeID, radioTemp1);
					//radioTemp1.WDS=1;
					results[a] += radioTemp1.assignedChannel+"#"+radioTemp1.assignedssid+"#"+radioTemp1.mode + "#" + radioTemp1.WDS + " ";
					System.out.println(results[a]);
				}
				else{
					radioTemp1 =  nodes.get(a).radioInfo.get(b);
					results[a] += radioTemp1.radioNumber+"#"+"DISABLED"+" ";
					radioTemp1.disabled = 1;
					System.out.println(results[a]);
				}
			}
			results[a] = results[a].substring(0, results[a].length()-1);
			//System.out.println(results[a]);
		}
		return results;
	}
	
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
	
	public static void compose(int nodenumber,int radionumber)// ����γ�
	{
		int a,b,c,d,e;
		int neiNum;
		RadioInfo ra;
		RadioInfo ras;
		System.out.println("send here");
		NodeInfo ni = nodes.get(0);
		NodeInfo nis;
		NeiInforArrary = new double[nodenumber][nodenumber][radionumber][radionumber];
		for(a = 0;a<nodenumber;a++){
			for(b = 0;b<radionumber;b++){
				for(d = 0;d<nodenumber;d++){
					for(e = 0;e<radionumber;e++){
						NeiInforArrary[a][d][b][e] = -95.0;
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
							//System.out.println(nei.neighborMac+" "+ras.radioNumber);
							//System.out.println(nei.neighborMac.equalsIgnoreCase(ras.radioNumber));
							if(nei.neighborMac.equalsIgnoreCase(ras.radioNumber)){
								NeiInforArrary[a][d][b][e] = nei.signal;
							}
						}	
					}
				}
			}
		}
	}
	public static void ResultgraphRadioInit(int nodenumber,int radionumber)// ����γ�
	{
		int a,b,d,e;
		System.out.println("send here");

		resultgraph_r = new double[nodenumber][nodenumber][radionumber][radionumber];
		for(a = 0;a<nodenumber;a++){
			for(b = 0;b<radionumber;b++){
				for(d = 0;d<nodenumber;d++){
					for(e = 0;e<radionumber;e++){
						resultgraph_r[a][d][b][e] = 0;
					}
				}	
			}
		}
	}
	
	
	public static void ResultgraphNodeInit(int nodenumber)// ����γ�
	{
		int a,b,d,e;
		System.out.println("send here");
		resultgraph_n = new double[nodenumber][nodenumber];
		for(a = 0;a<nodenumber;a++){
				for(d = 0;d<nodenumber;d++){
						resultgraph_n[a][d] = 0;	
			}
		}
	}
	public static void printresultgraph(int nodeNum)// ����γ�
	{
		int a,d;

		for(d = 0;d<nodeNum;d++){
			System.out.print("\t"+d);	
		}
		System.out.println();
		for(a = 0;a<nodeNum;a++){
			System.out.print(a+"\t");
			for(d = 0;d<nodeNum;d++){
				System.out.print(resultgraph_n[a][d]+"\t");	
			}
			System.out.println();
		}
	}
	public static void DxandProInit(int maxlevel)// ����γ�
	{
		int a;
		System.out.println("send here");
		Dxofson = new double[maxlevel];
		diffSonProExpthr = new double[maxlevel];
		for(a = 0;a<maxlevel;a++){
			Dxofson[a] = 0;
			diffSonProExpthr[a] = 0;
		}
	}
	public static void SendToNode(String nodeid, String command)// ��ĳ��ָ���Ľڵ㷢��һ������
	{
		ConnectionThreadSendCommand ctsc = null;
		synchronized (Connections.sendCommandListLock)// ���������������б����
		{
			for (ConnectionThreadSendCommand c : Connections.sendCommandList)// �ڷ������������б��У��ҵ�nodeid��ͬ������
			{
				//System.out.println("nodeID : "+c.nodeID);
				//System.out.println("nodeid : "+nodeid);
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
		int i = 0;
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
			i = 0;
			j = 0;
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
					synchronized (Main.nodesLock)
					{
						boolean found = false;
						NodeInfo foundni = null;
						//��ӡ��Ϣ
						i = 0;
						for (NodeInfo ni : Main.nodes)// �ڽڵ��б��в�������ڵ�
						{
							if (ni.nodeID.equals(parts[1]))// ����ҵ���������ѭ��
							{
								Main.nodesignal[i] = 1;
								found = true;
								foundni = ni;
								break;
							}
							i+=1;
						}
						if (found == false) // ���û���ҵ����򴴽�һ��������ӵ��ڵ��б���
						{
							foundni = new NodeInfo();
							foundni.nodeID = parts[1];
							foundni.rank = 0;
							foundni.expthroughput = 0;
							System.out.println("foundni.nodeID : "+foundni.nodeID);
							foundni.radioInfo = new ArrayList<RadioInfo>();
							//DataOperation.appendnode(foundni);
							Main.nodes.add(foundni);
						}

						found = false;
						RadioInfo foundri = null;
						j = 0;
						for (RadioInfo ri : foundni.radioInfo)// ����ĳ���ڵ��ĳ��radio
						{
							if (ri.radioNumber.equals(parts[2]))// ����ҵ�����Ҫradio��mac��ַ���ŵ��Ŷ�ƥ�䣩
							 
							{//???�ŵ�ҲҪһ�£��Ƿ�һ����ͬ
								found = true;
								foundri = ri;
								foundri.disabled = 0;
								if(i != Main.nodes.size()){
									Main.radiosignal[i][j] = 1;
								}
							}
							j++;
						}
						//在这里，旧的radio信息要被替换掉！！！
						if (found == false)// ���û���ҵ����򴴽�һ��������ӵ�radio�б���
						{
							
							foundri = new RadioInfo();
							foundri.radioNumber = parts[2];
							
							//foundri.load = Double.valueOf(parts[3])/((Main.endtime-Main.starttime)/1000);
							
							foundri.load = Double.valueOf(parts[3])/((1)/1000);
							System.out.println("foundri.radioNumber : "+foundri.radioNumber);
							foundri.direction = "none";
							foundri.mode = "none";
							foundri.WDS = 1;
							foundri.disabled = 0;
							foundri.assignedChannel = 36;
							foundri.neighborList = new ArrayList<NeighborInfo>();
							foundni.radioInfo.add(foundri);
							//DataOperation.appendradio(foundni.nodeID, foundri);
							//��ӡ��Ϣ
						}
						foundri.disabled = 0;

						for (i = 4; i < parts.length; i++)// �����ھ���Ϣ��ʣ�ಿ��
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
							/*
							if (found == false)
								DataOperation.appendneighbor(foundni.nodeID, foundri.radioNumber, foundni2);
							else 
								//DataOperation.setneighbor(foundni.nodeID, foundri.radioNumber, foundni2.neighborMac,foundni2);
								 * 
								 */
						}
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
	public static Object receiveListLock;// �б����

	public static ArrayList<ConnectionThreadSendCommand> sendCommandList;// ���ڷ��Ϳ�������������б�
	public static Object sendCommandListLock;// �б����
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

	public static int flag = 0;
	private Socket connection; // ��ͻ��˽���������

	ConnectionThreadReceive(Socket socket) // ���췽������һ����ͻ��˵�������Ϊ����
	{
		connection = socket;
	}

	@Override
	public void run() // �߳�����ʱ��ִ�д˺���
	{
		int i = 0;
		int j = 0;
		int k = 0;
		int l= 0;
		flag = 0;
		//二维数组
		InputStream is;
		Scanner scanner = null;
		try
		{
			i = 0;
			j = 0;
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
						i = 0;
						for (NodeInfo ni : Main.nodes)// �ڽڵ��б��в�������ڵ�
						{
							
							if (ni.nodeID.equals(parts[1]))// ����ҵ���������ѭ��
							{
								Main.nodesignal[i] = 1;
								found = true;
								foundni = ni;
								break;
							}
							i+=1;
						}
						if (found == false) // ���û���ҵ����򴴽�һ��������ӵ��ڵ��б���
						{
							foundni = new NodeInfo();
							foundni.nodeID = parts[1];
							foundni.rank = 0;
							foundni.expthroughput = 0;
							System.out.println("foundni.nodeID : "+foundni.nodeID);
							foundni.radioInfo = new ArrayList<RadioInfo>();
							//DataOperation.appendnode(foundni);
							Main.nodes.add(foundni);
						}

						found = false;
						RadioInfo foundri = null;
						j = 0;
						for (RadioInfo ri : foundni.radioInfo)// ����ĳ���ڵ��ĳ��radio
						{
							if (ri.radioNumber.equals(parts[2]))// ����ҵ�����Ҫradio��mac��ַ���ŵ��Ŷ�ƥ�䣩
							 
							{//???�ŵ�ҲҪһ�£��Ƿ�һ����ͬ
								found = true;
								foundri = ri;
								foundri.disabled = 0;
								if(i != Main.nodes.size()){
									Main.radiosignal[i][j] = 1;
								}
							}
							j++;
						}
						//在这里，旧的radio信息要被替换掉！！！
						if (found == false)// ���û���ҵ����򴴽�һ��������ӵ�radio�б���
						{
							
							foundri = new RadioInfo();
							foundri.radioNumber = parts[2];
							//foundri.load = Double.valueOf(parts[3])/((Main.endtime-Main.starttime)/1000);
							foundri.load = Double.valueOf(parts[3])/((1)/1000);
							System.out.println("foundri.radioNumber : "+foundri.radioNumber);
							foundri.direction = "none";
							foundri.mode = "none";
							foundri.WDS = 1;
							foundri.disabled = 0;
							foundri.assignedChannel = 36;
							foundri.neighborList = new ArrayList<NeighborInfo>();
							foundni.radioInfo.add(foundri);
							//DataOperation.appendradio(foundni.nodeID, foundri);
							//��ӡ��Ϣ
						}
						foundri.disabled = 0;

						for (i = 4; i < parts.length; i++)// �����ھ���Ϣ��ʣ�ಿ��
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
							/*
							if (found == false)
								DataOperation.appendneighbor(foundni.nodeID, foundri.radioNumber, foundni2);
							else 
								//DataOperation.setneighbor(foundni.nodeID, foundri.radioNumber, foundni2.neighborMac,foundni2);
								 * 
								 */
						}
					}
					Main.radiocount_channel++;
					if(Main.radiocount_channel >= Main.radionum*Main.nodenum){
						try {
							//注意两个循环可能会碰在一起，到时候跳过一次balance
							Thread.currentThread().sleep(6*1000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						Main.radiocount_channel = 0;
						Main.SendConfiguration();
					}
				}
				else if(command.equals("LOAD")) // �����һ�����ھ���Ϣ
				{
					synchronized (Main.nodesLock)
					{
						NodeInfo foundni = null;
						//��ӡ��Ϣ
						i = 0;
						for (NodeInfo ni : Main.nodes)// �ڽڵ��б��в�������ڵ�
						{
							if (ni.nodeID.equals(parts[1]))// ����ҵ���������ѭ��
							{
								Main.nodesignal[i] = 1;
								foundni = ni;
								break;
							}
							i+=1;
						}
						RadioInfo foundri = null;
						j = 0;
						for (RadioInfo ri : foundni.radioInfo)// ����ĳ���ڵ��ĳ��radio
						{
							if (ri.radioNumber.equals(parts[2]))// ����ҵ�����Ҫradio��mac��ַ���ŵ��Ŷ�ƥ�䣩
							 
							{//???�ŵ�ҲҪһ�£��Ƿ�һ����ͬ
								foundri = ri;
								foundri.disabled = 0;
								foundri.load = Double.valueOf(parts[3])/(Main.balanceduration*60*1000);
								if(i != Main.nodes.size()){
									Main.radiosignal[i][j] = 1;
								}
							}
							j++;
						}
					}
					Main.radiocount_load++;
					if(Main.radiocount_load >= Main.radionum*Main.nodenum){
						try {
							//注意两个循环可能会碰在一起，到时候跳过一次balance
							Thread.currentThread().sleep(6*1000);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						Main.radiocount_load = 0;
						
						for(k = 0;k<Main.maxloadbalance;k++){
							
							Main.loadbalance(Main.nodenum,Main.radionum,Main.visited,Main.resultTemp);
							for (i = 0;i<Main.maxlevel;i++){
								Main.Dxofson_pre[i] = Main.Dxofson[i];
								Main.diffSonProExpthr_pre[i] = Main.diffSonProExpthr[i];
							}
							Main.DxandProInit(Main.maxlevel);
							Main.balanceEstimate(Main.nodenum,Main.radionum,Main.maxlevel);//展示estimate结果
							for (i = 0;i<Main.maxlevel;i++){
								if(Main.Dxofson_pre[i] != Main.Dxofson[i] || Main.diffSonProExpthr_pre[i] != Main.diffSonProExpthr[i]){
									flag = 1;
									break;
								}
							}
							if(flag == 1) break;
						}
						for (i = 0;i<Main.maxlevel;i++){
							Main.Dxofson_pre[i] = 0;
							Main.diffSonProExpthr_pre[i] = 0;
						}
						flag = 0;
						Main.printresultgraph(Main.nodenum);
						Main.results = Main.getresult(Main.nodenum,Main.radionum,Main.resultTemp,Main.results);
						System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
						for(int a = 0;a<Main.nodenum;a++){
							System.out.println("rank "+ Main.nodes.get(a).rank);
							System.out.println("throughput "+ Main.nodes.get(a).expthroughput);
							System.out.println(Main.results[a]);	
						}
						for(int a = 0;a<Main.nodenum;a++){
							Main.SendToNode(Main.nodes.get(a).nodeID, Main.results[a]);
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
		synchronized (Connections.receiveListLock)// �Խ����ھ���Ϣ�����б����
		{
			for (i = 0; i < Connections.receiveList.size(); i++)// �����б�
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
				synchronized (Connections.sendCommandListLock)// �Է������������б����
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
		synchronized (Connections.sendCommandListLock)// �Է������������б����
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
		int i = 0;
		int j = 0;
		System.out.println("sendCommand, "+command);
		if (command.equals("DISCOVER \r\n")){
			//Main.endtime = System.currentTimeMillis();
			Main.nodesignal = new int [Main.nodes.size()];
			Main.radiosignal = new int [Main.nodes.size()][Main.radionum];
			for(i = 0;i<Main.nodes.size();i++){
				Main.nodesignal[i] = 0;
			}
			for(i = 0;i<Main.nodes.size();i++){
				for(j = 0;j<Main.radionum;j++){
					Main.radiosignal[i][j] = 0;
				}
			}
		}
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
	public int rank;
	public double expthroughput;
	public double own_load;
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
	public int disabled;
	public double load;
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