import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

class DataOperation {
	static Connection conn = null;
	static String url;
	static Statement stat = null;
	

	// ���ݿ����� //����ͨ��

	public static void connect(String location) {
		try {
			url = location;
			// create a connection to the database
			conn = DriverManager.getConnection(url);
			stat = conn.createStatement();
			System.out.println("Connection to SQLite has been established.");
			//stat.executeUpdate("create table if not exists tbl1(name varchar(20), salary int);");
			createtables();
		} catch (SQLException e) {
			System.out.println("database connect fail");
			System.out.println(e.getMessage());
		}
	}
	public static void createtables() throws SQLException{
//必须在运行时打开, 因为 默认是关闭的
//PRAGMA foreign_keys = ON;
		//System.out.println("Start to create tables");
		try{
			stat.executeUpdate("pragma foreign_keys = on");
			stat.executeUpdate("CREATE TABLE if not exists user(id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "username varchar,password_hash varchar);");
			stat.executeUpdate("CREATE TABLE if not exists node(id INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ "mac varchar,hostname varchar,firmware_ver varchar,lang varchar,kernel_ver varchar,localtime varchar,"
			+"ram_total varchar,ram_available varchar,ram_buffered varchar,rank int,exp_throughput varchar);");
			stat.executeUpdate("CREATE TABLE if not exists node_interface(id INTEGER PRIMARY KEY AUTOINCREMENT,interface_name varchar,"
			+ "status int,protocol varchar,ipv4_address varchar,ipv4_mask varchar,physical_interface varchar,physical_bridging int,dhcp_status int,"
			+"node_id int references node(id) on delete cascade on update cascade);");
			stat.executeUpdate("CREATE TABLE if not exists node_radio(id INTEGER PRIMARY KEY AUTOINCREMENT,radio_name varchar,radio_mac varchar"
			+ "assigned_freq_mode varchar,assigned_freq_channel varchar,assigned_freq_width varchar,assigned_freq_power varchar,ssid varchar,"
			+"mode varchar,assigned_interface_name varchar,status int,direction varchar,wds int"
			+"node_id int references node(id) on delete cascade on update cascade,"
			+"node_interface_id int references node_interface(id) on delete cascade on update cascade);");
			stat.executeUpdate("CREATE TABLE if not exists node_neighbors(id INTEGER PRIMARY KEY AUTOINCREMENT,radio_mac varchar,"
			+ "neighbor_mac varchar,rate varchar,signal varchar,noise varchar,tx_rate varchar,rx_rate varchar,tx_qam varchar,rx_qam varchar,"
			+"node_id int references node(id) on delete cascade on update cascade);");
			stat.executeUpdate("CREATE TABLE if not exists node_test(id INTEGER PRIMARY KEY AUTOINCREMENT,test_type varchar,"
			+ "server_ip varchar,client_ip varchar,test_time_sec int,window_size_M int,two_way varchar,test_result varchar,"
			+"node_id int references node(id) on delete cascade on update cascade);");
		}catch (SQLException e) {
			System.out.println("database connect fail");
			System.out.println(e.getMessage());
		}
	}
	public static void appenduser(String username, String password_hash) {
		connect("jdbc:sqlite:topo3.db");
		try {
			String temp = null;
			temp = "null,'" + username + "','"+ password_hash + "'";
			System.out.println(temp);
			stat.executeUpdate("insert into user values (" + temp + ")");
			//System.out.println(Util.getCurrentTime()+"append to netMonitor success"+"append values:"+temp);//for log
			
		} catch (SQLException e) {
			System.out.println("append fail");
			System.out.println(e.getMessage());
			close();
		}
		close();
	}
	public static void appendnode(NodeInfo node) {
		connect("jdbc:sqlite:topo3.db");
		try {
			String temp = null;
			temp = "null,'" + node.nodeID + "','"+ "',null,null,null,null,null,null,null,null,'" 
			+ node.rank + "','" + node.expthroughput+ "'";
			System.out.println(temp);
			stat.executeUpdate("insert into node_radio values (" + temp + ")");
			//System.out.println(Util.getCurrentTime()+"append to netMonitor success"+"append values:"+temp);//for log
			
		} catch (SQLException e) {
			System.out.println("append fail");
			System.out.println(e.getMessage());
			close();
		}
		close();
	}
	public static void appendradio(int id,String nodeID,RadioInfo radio) {
		connect("jdbc:sqlite:topo3.db");
		try {
			String temp = null;
			temp = "null,null," + radio.radioNumber + "',null,null,'" + radio.assignedChannel+"',null,null,'"+
				radio.assignedssid + "','" +radio.mode + "',null,'" + radio.disabled + "','"
				 + radio.direction + "','"+radio.WDS + "','" + id+ "','" + nodeID+ "','" + "1";
			System.out.println(temp);
			stat.executeUpdate("insert into node_radio values (" + temp + ")");
			//System.out.println(Util.getCurrentTime()+"append to netMonitor success"+"append values:"+temp);//for log
			
		} catch (SQLException e) {
			System.out.println("Radios append fail");
			System.out.println(e.getMessage());
			close();
		}
		close();
	}
	public static void appendneighbor(int id,String nodeID,String radioNumber,NeighborInfo neighbor) {
		connect("jdbc:sqlite:topo3.db");
		try {
			String temp = null;
			temp = "null,'" +neighbor.neighborMac + "','"+neighbor.rate + "','" +neighbor.signal+ "','" +
			neighbor.noise+ "','" +neighbor.tx_rate + "','" +neighbor.rx_rate +"','" +neighbor.tx_QAM + "','" +neighbor.rx_QAM + "','" +id+ "','" + nodeID+ "'";
			System.out.println(temp);
			stat.executeUpdate("insert into node_neighbors values (" + temp + ")");
			//System.out.println(Util.getCurrentTime()+"append to netMonitor success"+"append values:"+temp);//for log
			
		} catch (SQLException e) {
			System.out.println("Radios append fail");
			System.out.println(e.getMessage());
			close();
		}
		close();
	}
	public static void dropNode(String nodeID) throws IOException {
		connect("jdbc:sqlite:topo3.db");
		try {
			stat.executeUpdate("delete from Nodes where nodeID = '" + nodeID + "'");
			System.out.println("delete from Nodes success");//for log
		} catch (SQLException e) {
			close();
			System.out.println(e.getMessage());
		}
		close();
	}
	public static void dropNoderadio(String radioNumber) throws IOException {
		connect("jdbc:sqlite:topo3.db");
		try {
			stat.executeUpdate("delete from Radios where radioNumber = '" + radioNumber + "'");
			System.out.println("delete from Nodes success");//for log
		} catch (SQLException e) {
			close();
			System.out.println(e.getMessage());
		}
		close();
	}
	public static void dropneighbor(String nodeID,String radioNumber,String neighborMac) throws IOException {
		connect("jdbc:sqlite:topo3.db");
		try {
			stat.executeUpdate("delete from Nodes where nodeID = '" + nodeID + "' AND radioNumber = '" + radioNumber+ "' AND neighborMac = '"+ neighborMac+ "'");
			System.out.println("delete from Nodes success");//for log
		} catch (SQLException e) {
			close();
			System.out.println(e.getMessage());
		}
		close();
	}
	
	
	
	public static void setradio(String nodeID,RadioInfo radio) {
		connect("jdbc:sqlite:topo3.db");
		try {
			String temp = null;
			temp = "null,'" + nodeID + "','" + radio.radioNumber + "'," + radio.assignedChannel+","+
				radio.direction + "','" +radio.assignedssid + "','" +radio.mode + "','" +radio.WDS + "','" +radio.disabled;
			System.out.println(temp);
			stat.executeUpdate("insert into Radios values (" + temp + ")");
			//System.out.println(Util.getCurrentTime()+"append to netMonitor success"+"append values:"+temp);//for log
			
		} catch (SQLException e) {
			System.out.println("Radios append fail");
			System.out.println(e.getMessage());
			close();
		}
		close();
	}
	public static void setneighbor(String nodeID,String radioNumber,String neighborMac,NeighborInfo neighbor) throws IOException {
		//未完成
		connect("jdbc:sqlite:topo3.db");
		try {
			stat.executeUpdate("delete from Nodes where nodeID = '" + nodeID + "' AND radioNumber = '" + radioNumber+ "' AND neighborMac = '"+ neighborMac+ "'");
			System.out.println("delete from Nodes success");//for log
		} catch (SQLException e) {
			close();
			System.out.println(e.getMessage());
		}
		close();
	}
	public static void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
