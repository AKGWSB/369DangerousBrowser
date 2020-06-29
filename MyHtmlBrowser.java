package homework;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;

public class MyHtmlBrowser {
	
	String curPage = "http://www.szulrl.cn/browserTest"; 
	JEditorPane jep;	// html显示组件
	JPanel menuBox;		// 上方菜单栏box 装有很多按钮和输入框
	JButton goBtn;		// 【点我访问】按钮
	JButton backBtn;	// 回退按钮
	JButton forwBtn;	// 前进按钮
	JButton refreshBtn;	// 刷新按钮
	JButton favoBtn;	// 收藏夹按钮 点击调出收藏夹
	JButton emailBtn;	// 发送邮件
	JTextField jtf;		// 输入框
	
	// 前进后退的栈 用双端队列实现
	Deque<String> backQueue = new LinkedList<String>();
	Deque<String> forwQueue = new LinkedList<String>();
	
	MyHtmlBrowser() {
		
	}
	
	MyHtmlBrowser(String cp) {
		curPage = cp;
	}
	
	/*
	 * @function updatePage : 将页面设置为当前curPage指向的页面
	 */
	public void updatePage() {
		jtf.setText(curPage);
		try {
			jep.setPage(curPage);
		} catch (IOException e) {
			jep.setText("<html><h1>连接错误或者超时！</h1><br><h1>您可以尝试刷新页面</h1></html>");
		}  
	}
	
	/*
	 * @function backwordPage : 回退
	 */
	public void backwordPage() {
		if(backQueue.size()==0) return;
		forwQueue.offer(new String(curPage));
		curPage = backQueue.pollLast();
		updatePage();
		if(backQueue.size()==0) backBtn.setEnabled(false);
		forwBtn.setEnabled(true);
	}
	
	/*
	 * @function forwordPage : 前进
	 */
	public void forwordPage() {
		if(forwQueue.size()==0) return;
		backQueue.offer(new String(curPage));
		curPage = forwQueue.pollLast();
		updatePage();
		if(forwQueue.size()==0) forwBtn.setEnabled(false);
		backBtn.setEnabled(true);
	}
	
	/*
	 * @function : newPage
	 * @param np : 新页面的URL
	 */
	public void newPage(String np) {
		backQueue.offer(new String(curPage));
		forwQueue.clear();
		curPage = new String(np);
		updatePage();
		backBtn.setEnabled(true);
		forwBtn.setEnabled(false);
	}
	
	/*
	 * @function refreshPage : 刷新当前页面
	 */
	public void refreshPage() {
		try {
			jep.setPage(curPage);
		} catch (IOException e) {
			jep.setText("<html><h1>连接错误或者超时！</h1><br><h1>您可以尝试刷新页面</h1></html>");
		}
	}
	
	/*
	 * @function deleteFavorite : 删除指定的收藏夹条目
	 * @param delLine			: 收藏夹完整条目即 【键 空格 值】的字符串
	 */
	public void deleteFavorite(String delLine) {
		try {
			// 读取 但是不读取要删除的行
			//FileInputStream is = new FileInputStream("E:/MyEclipse/WorkSpace/Hello/src/homework/favorites.txt");
			FileInputStream is = new FileInputStream(System.getProperty("user.dir")+"/favorites.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
			String line;
			LinkedList<String> lines = new LinkedList<String>();
			while((line=reader.readLine())!=null) {
				if(!delLine.equals(line)) {
					lines.offer(line);
				}
			}
			is.close();
			// 写入读取的内容
			//FileOutputStream os = new FileOutputStream("E:/MyEclipse/WorkSpace/Hello/src/homework/favorites.txt");
			FileOutputStream os = new FileOutputStream(System.getProperty("user.dir")+"/favorites.txt");
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
			for(String l : lines) {
				writer.write(l+"\r\n");
			}
			writer.flush();
			writer.close();
			os.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * @function addCurPageToFavorites : 将当前页面添加到收藏夹
	 */
	public void addCurPageToFavorites() {
		try {
			Pattern r = Pattern.compile("(<title>)(.*?)(<)");
			Matcher m = r.matcher(jep.getText());
			String title = "新标签页";
			if(m.find()) {
				// System.out.println(m.group(2));
				title = m.group(2);
			}
			// 读取
			// FileInputStream is = new FileInputStream("E:/MyEclipse/WorkSpace/Hello/src/homework/favorites.txt");
			FileInputStream is = new FileInputStream(System.getProperty("user.dir")+"/favorites.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
			String line;
			LinkedList<String> lines = new LinkedList<String>();
			while((line=reader.readLine())!=null) {
				lines.offer(line);
			}
			is.close();
			// 写入读取的内容
			// FileOutputStream os = new FileOutputStream("E:/MyEclipse/WorkSpace/Hello/src/homework/favorites.txt");
			FileOutputStream os = new FileOutputStream(System.getProperty("user.dir")+"/favorites.txt");
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "utf-8"));
			for(String l : lines) {
				writer.write(l+"\r\n");
			}
			writer.write(title+" "+curPage+"\r\n");
			writer.flush();
			writer.close();
			os.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * @function showFavorites : 显示当前收藏夹
	 * @param e				   : 【收藏夹】按钮被点击时传入的鼠标事件MouseEvent对象
	 */
	public void showFavorites(MouseEvent e) throws Exception {
		JPopupMenu jpm = new JPopupMenu();
		JMenuItem addItem = new JMenuItem("添加当前网址到收藏夹");
		addItem.setForeground(new Color(110,148,252));
		addItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addCurPageToFavorites();
				//System.out.println("12313dadw");
			}
		});
		jpm.add(addItem);
		// 打开文件流读取收藏夹本地信息
		//FileInputStream is = new FileInputStream("E:/MyEclipse/WorkSpace/Hello/src/homework/favorites.txt");
		FileInputStream is = new FileInputStream(System.getProperty("user.dir")+"/favorites.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
		String line;
		while((line=reader.readLine())!=null) {
			// System.out.println(line);
			final String key = line.split(" ")[0];	// 键
			final String val = line.split(" ")[1];	// 值 即网址
			// 创建删除收藏按钮并添加事件
			JMenu item = new JMenu(key);
			JMenuItem delItem = new JMenuItem("取消收藏");
			delItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					deleteFavorite(key+" "+val);
				}
			});
			// 创建访问网址按钮并添加事件
			JMenuItem goItem = new JMenuItem("访问");
			goItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					newPage(val);
				}
			});
			item.add(goItem);
			item.add(delItem);
			jpm.add(item);
		}
		jpm.show(e.getComponent(), e.getX(), e.getY());
	}
	
	/*
	 * @function start : 浏览器初始化，然后开始运作
	 */
	public void start() throws Exception {
		
		jep = new JEditorPane();
		jep.setEditable(false);
		
		// 添加超链接点击事件回调函数 并将JEditorPane的页面改为超链接的页面
		jep.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent event) {
				if(event.getEventType()==HyperlinkEvent.EventType.ACTIVATED) {
					// 获取超链接目的地址
					String hyplink = event.getURL().toString();
					try {
						// 判断content type 如果是text/html就访问 否则下载
						String ctype = new URL(hyplink).openConnection().getContentType().substring(0,  4);
						if(ctype.equals("text")) {
							newPage(hyplink);
						} else {
							MultiThreadDownloader dl = new MultiThreadDownloader(
									hyplink, 
									System.getProperty("user.dir")+"/", 
									"下载",
									1);
							dl.download();
						}
					} catch (Exception e) {
						e.printStackTrace();
						newPage(hyplink);
					} 
				}
			}
		});
		// 设置主页
		jep.setContentType("text/html;charset=utf-8");
		try {
			jep.setPage(curPage);
		} catch (IOException e) {
			jep.setText("<html><h1>连接错误或者超时！</h1><br><h1>您可以尝试刷新页面</h1></html>");
		}
		
		// 带滑动条的组件 用于存放显示html的jep组件
		JScrollPane scrollpane = new JScrollPane(jep);
		
		// 访问按钮 绑定访问按钮点击事件 从JTextField输入框获取URL并且访问
		goBtn = new JButton("点我访问网页");
		goBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 获取输入的地址栏的地址
				String input = jtf.getText();
				if(input.equals(curPage)) {
					refreshPage();
				} else if(input.length()>4 && input.substring(0, 4).equals("http")){
					newPage(jtf.getText());
				} else {
					// 跳转到必应搜索
					newPage("https://cn.bing.com/search?q=" + jtf.getText());
				}
			}
		});
		
		// 输入框 输入URL
		jtf = new JTextField(40);
		jtf.setText(curPage);
		// 绑定回车按键事件
		jtf.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if(event.getKeyChar()==KeyEvent.VK_ENTER) {
					goBtn.doClick();	// 按下回车等于点击按钮
				}
			}
		});
		
		backBtn = new JButton("<—后退");
		backBtn.setEnabled(false);
		backBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backwordPage();
			}
		});
		
		forwBtn = new JButton("前进—>");
		forwBtn.setEnabled(false);
		forwBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				forwordPage();
			}
		});
		
		refreshBtn = new JButton("刷新页面");
		refreshBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshPage();
			}
		});
		
		favoBtn = new JButton("收藏夹");
		favoBtn.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				try {
					showFavorites(e);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			// 必须实现这些接口
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		
		emailBtn = new JButton("邮件发送");
		emailBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EmailSender sender = new EmailSender();
				sender.sendInWindow();
			}
		});
		
		
		// 上方菜单盒子
		JPanel menuBox = new JPanel();
		menuBox.add(backBtn);
		menuBox.add(forwBtn);
		menuBox.add(refreshBtn);
		menuBox.add(jtf);
		menuBox.add(goBtn);
		menuBox.add(favoBtn);
		menuBox.add(emailBtn);
		
		// 主窗体JFrame
		JFrame jf = new JFrame("369危险浏览器");
		jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		jf.setSize(1024,768);
		// 添加组件
		jf.add(menuBox, BorderLayout.NORTH);
		jf.add(scrollpane, BorderLayout.CENTER);
		// 添加组件
		jf.show();
	}
	
	public static void main(String[] args) throws Exception {
		
		MyHtmlBrowser browser = new MyHtmlBrowser();
		browser.start();
	}
	
}
