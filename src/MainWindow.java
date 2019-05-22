import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class MainWindow extends JFrame// ������
{
	/**modified by zhangjian start*/
	private JTextArea jta;
	/**modified by zhangjian end*/
	
	public MainWindow()// ���캯���������д�������
	{
		setTitle("Mesh网络管理系统");// ���ô��ڵı���
		setBounds(0, 0, 800, 600);// ���ô��ڵ�λ��
		setLayout(null);// ��Ϊ����λ�ò���

		JButton b = new JButton();// �ֶ��·����ð�ť
		b.setBounds(10, 10, 300, 100);// ���ô��ڵ�λ��
		b.setText("下发配置");// ��ť����ʾ������
		b.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)// ��ť�����ʱ��Ҫִ�еĲ���
			{
				Main.SendConfiguration();
			}
		});
		add(b);// �������ť��ӵ�������
		
		
		/**modified by zhangjian start*/
		JButton b2 = new JButton();// �ھӷ��ְ�ť
		b2.setBounds(10, 120, 300, 100);// ���ð�ť��λ��
		b2.setText("邻居发现");// ��ť����ʾ������
		b2.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)// ��ť�����ʱ��Ҫִ�еĲ���
			{
				synchronized (Connections.sendCommandListLock)
				{
					for(ConnectionThreadSendCommand ctsc:Connections.sendCommandList)
					{
						ctsc.sendCommand("DISCOVER \r\n");
					}					
				}
				
			}
		});
		add(b2);// �������ť��ӵ�������
		/**modified by zhangjian end*/
		
		
		/**modified by zhangjian start*/
		
		jta=new JTextArea();
		jta.setBounds(10, 230, 300, 30);
		jta.setForeground(Color.BLACK);
		jta.setEditable(false);
		jta.setFont(new Font("宋体",Font.CENTER_BASELINE,16));
		jta.setText("正常");
		add(jta);		
		
		/**modified by zhangjian end*/
		

		setDefaultCloseOperation(EXIT_ON_CLOSE);// �ڵ�����ڵĹرհ�ťʱ�˳�����
		setVisible(true);// ��ʾ����
	}
	
	/**modified by zhangjian start*/
	
	public void showInfo(String s)
	{
		jta.setText(s);
	}
	
	/**modified by zhangjian end*/
}
