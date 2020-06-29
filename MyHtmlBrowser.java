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
	JEditorPane jep;	// html��ʾ���
	JPanel menuBox;		// �Ϸ��˵���box װ�кܶఴť�������
	JButton goBtn;		// �����ҷ��ʡ���ť
	JButton backBtn;	// ���˰�ť
	JButton forwBtn;	// ǰ����ť
	JButton refreshBtn;	// ˢ�°�ť
	JButton favoBtn;	// �ղؼа�ť ��������ղؼ�
	JButton emailBtn;	// �����ʼ�
	JTextField jtf;		// �����
	
	// ǰ�����˵�ջ ��˫�˶���ʵ��
	Deque<String> backQueue = new LinkedList<String>();
	Deque<String> forwQueue = new LinkedList<String>();
	
	MyHtmlBrowser() {
		
	}
	
	MyHtmlBrowser(String cp) {
		curPage = cp;
	}
	
	/*
	 * @function updatePage : ��ҳ������Ϊ��ǰcurPageָ���ҳ��
	 */
	public void updatePage() {
		jtf.setText(curPage);
		try {
			jep.setPage(curPage);
		} catch (IOException e) {
			jep.setText("<html><h1>���Ӵ�����߳�ʱ��</h1><br><h1>�����Գ���ˢ��ҳ��</h1></html>");
		}  
	}
	
	/*
	 * @function backwordPage : ����
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
	 * @function forwordPage : ǰ��
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
	 * @param np : ��ҳ���URL
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
	 * @function refreshPage : ˢ�µ�ǰҳ��
	 */
	public void refreshPage() {
		try {
			jep.setPage(curPage);
		} catch (IOException e) {
			jep.setText("<html><h1>���Ӵ�����߳�ʱ��</h1><br><h1>�����Գ���ˢ��ҳ��</h1></html>");
		}
	}
	
	/*
	 * @function deleteFavorite : ɾ��ָ�����ղؼ���Ŀ
	 * @param delLine			: �ղؼ�������Ŀ�� ���� �ո� ֵ�����ַ���
	 */
	public void deleteFavorite(String delLine) {
		try {
			// ��ȡ ���ǲ���ȡҪɾ������
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
			// д���ȡ������
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
	 * @function addCurPageToFavorites : ����ǰҳ����ӵ��ղؼ�
	 */
	public void addCurPageToFavorites() {
		try {
			Pattern r = Pattern.compile("(<title>)(.*?)(<)");
			Matcher m = r.matcher(jep.getText());
			String title = "�±�ǩҳ";
			if(m.find()) {
				// System.out.println(m.group(2));
				title = m.group(2);
			}
			// ��ȡ
			// FileInputStream is = new FileInputStream("E:/MyEclipse/WorkSpace/Hello/src/homework/favorites.txt");
			FileInputStream is = new FileInputStream(System.getProperty("user.dir")+"/favorites.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
			String line;
			LinkedList<String> lines = new LinkedList<String>();
			while((line=reader.readLine())!=null) {
				lines.offer(line);
			}
			is.close();
			// д���ȡ������
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
	 * @function showFavorites : ��ʾ��ǰ�ղؼ�
	 * @param e				   : ���ղؼС���ť�����ʱ���������¼�MouseEvent����
	 */
	public void showFavorites(MouseEvent e) throws Exception {
		JPopupMenu jpm = new JPopupMenu();
		JMenuItem addItem = new JMenuItem("��ӵ�ǰ��ַ���ղؼ�");
		addItem.setForeground(new Color(110,148,252));
		addItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addCurPageToFavorites();
				//System.out.println("12313dadw");
			}
		});
		jpm.add(addItem);
		// ���ļ�����ȡ�ղؼб�����Ϣ
		//FileInputStream is = new FileInputStream("E:/MyEclipse/WorkSpace/Hello/src/homework/favorites.txt");
		FileInputStream is = new FileInputStream(System.getProperty("user.dir")+"/favorites.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
		String line;
		while((line=reader.readLine())!=null) {
			// System.out.println(line);
			final String key = line.split(" ")[0];	// ��
			final String val = line.split(" ")[1];	// ֵ ����ַ
			// ����ɾ���ղذ�ť������¼�
			JMenu item = new JMenu(key);
			JMenuItem delItem = new JMenuItem("ȡ���ղ�");
			delItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					deleteFavorite(key+" "+val);
				}
			});
			// ����������ַ��ť������¼�
			JMenuItem goItem = new JMenuItem("����");
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
	 * @function start : �������ʼ����Ȼ��ʼ����
	 */
	public void start() throws Exception {
		
		jep = new JEditorPane();
		jep.setEditable(false);
		
		// ��ӳ����ӵ���¼��ص����� ����JEditorPane��ҳ���Ϊ�����ӵ�ҳ��
		jep.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent event) {
				if(event.getEventType()==HyperlinkEvent.EventType.ACTIVATED) {
					// ��ȡ������Ŀ�ĵ�ַ
					String hyplink = event.getURL().toString();
					try {
						// �ж�content type �����text/html�ͷ��� ��������
						String ctype = new URL(hyplink).openConnection().getContentType().substring(0,  4);
						if(ctype.equals("text")) {
							newPage(hyplink);
						} else {
							MultiThreadDownloader dl = new MultiThreadDownloader(
									hyplink, 
									System.getProperty("user.dir")+"/", 
									"����",
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
		// ������ҳ
		jep.setContentType("text/html;charset=utf-8");
		try {
			jep.setPage(curPage);
		} catch (IOException e) {
			jep.setText("<html><h1>���Ӵ�����߳�ʱ��</h1><br><h1>�����Գ���ˢ��ҳ��</h1></html>");
		}
		
		// ������������� ���ڴ����ʾhtml��jep���
		JScrollPane scrollpane = new JScrollPane(jep);
		
		// ���ʰ�ť �󶨷��ʰ�ť����¼� ��JTextField������ȡURL���ҷ���
		goBtn = new JButton("���ҷ�����ҳ");
		goBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// ��ȡ����ĵ�ַ���ĵ�ַ
				String input = jtf.getText();
				if(input.equals(curPage)) {
					refreshPage();
				} else if(input.length()>4 && input.substring(0, 4).equals("http")){
					newPage(jtf.getText());
				} else {
					// ��ת����Ӧ����
					newPage("https://cn.bing.com/search?q=" + jtf.getText());
				}
			}
		});
		
		// ����� ����URL
		jtf = new JTextField(40);
		jtf.setText(curPage);
		// �󶨻س������¼�
		jtf.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent event) {
				if(event.getKeyChar()==KeyEvent.VK_ENTER) {
					goBtn.doClick();	// ���»س����ڵ����ť
				}
			}
		});
		
		backBtn = new JButton("<������");
		backBtn.setEnabled(false);
		backBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				backwordPage();
			}
		});
		
		forwBtn = new JButton("ǰ����>");
		forwBtn.setEnabled(false);
		forwBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				forwordPage();
			}
		});
		
		refreshBtn = new JButton("ˢ��ҳ��");
		refreshBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshPage();
			}
		});
		
		favoBtn = new JButton("�ղؼ�");
		favoBtn.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent e) {
				try {
					showFavorites(e);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			// ����ʵ����Щ�ӿ�
			public void mousePressed(MouseEvent e) {}
			public void mouseReleased(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
		});
		
		emailBtn = new JButton("�ʼ�����");
		emailBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EmailSender sender = new EmailSender();
				sender.sendInWindow();
			}
		});
		
		
		// �Ϸ��˵�����
		JPanel menuBox = new JPanel();
		menuBox.add(backBtn);
		menuBox.add(forwBtn);
		menuBox.add(refreshBtn);
		menuBox.add(jtf);
		menuBox.add(goBtn);
		menuBox.add(favoBtn);
		menuBox.add(emailBtn);
		
		// ������JFrame
		JFrame jf = new JFrame("369Σ�������");
		jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		jf.setSize(1024,768);
		// ������
		jf.add(menuBox, BorderLayout.NORTH);
		jf.add(scrollpane, BorderLayout.CENTER);
		// ������
		jf.show();
	}
	
	public static void main(String[] args) throws Exception {
		
		MyHtmlBrowser browser = new MyHtmlBrowser();
		browser.start();
	}
	
}
