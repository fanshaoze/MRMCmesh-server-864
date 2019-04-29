import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

public class MainWindow extends JFrame// 主窗口
{
	public MainWindow()// 构造函数，在其中创建窗口
	{
		setTitle("Mesh网络管理系统");// 设置窗口的标题
		setBounds(0, 0, 800, 600);// 设置窗口的位置
		setLayout(null);// 改为绝对位置布局

		JButton b = new JButton();// 手动下发配置按钮
		b.setBounds(10, 10, 310, 310);// 设置按钮的位置
		b.setText("下发配置");// 按钮上显示的文字
		b.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)// 按钮被点击时，要执行的操作
			{
				Main.SendConfiguration();
			}
		});
		add(b);// 将这个按钮添加到窗口中

		setDefaultCloseOperation(EXIT_ON_CLOSE);// 在点击窗口的关闭按钮时退出程序
		setVisible(true);// 显示窗口
	}
}
