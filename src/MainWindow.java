import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class MainWindow extends JFrame// ������
{
	public MainWindow()// ���캯���������д�������
	{
		setTitle("Mesh�������ϵͳ");// ���ô��ڵı���
		setBounds(0, 0, 800, 600);// ���ô��ڵ�λ��
		setLayout(null);// ��Ϊ����λ�ò���

		JButton b = new JButton();// �ֶ��·����ð�ť
		b.setBounds(10, 10, 310, 310);// ���ð�ť��λ��
		b.setText("�·�����");// ��ť����ʾ������
		b.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)// ��ť�����ʱ��Ҫִ�еĲ���
			{
				Main.SendConfiguration();
			}
		});
		add(b);// �������ť��ӵ�������

		setDefaultCloseOperation(EXIT_ON_CLOSE);// �ڵ�����ڵĹرհ�ťʱ�˳�����
		setVisible(true);// ��ʾ����
	}
}
