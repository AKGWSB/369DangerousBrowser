package homework;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;

class DownloadThread extends Thread {
	
	URLConnection con;
	RandomAccessFile rf;
	
	public static volatile long allcur = 0;
	public static boolean isReady = false;
	
	public DownloadThread() {
		
	}
	
	public DownloadThread(URLConnection con, RandomAccessFile rf) {
		this.con = con;
		this.rf = rf;
	}
	
	public void run() {
		try {
			// ��ȡ���������
			InputStream is = con.getInputStream();
			//System.out.printf("�߳� ��ȡ�ļ��ɹ�\n");
			
			// ����������д�������
			byte[] buf = new byte[1024];
			int len = -1;
			
			while((len=is.read(buf)) != -1) {
				rf.write(buf, 0, len);
				synchronized(new Object()) {
					allcur += (long)len;
				}
				// System.out.printf("%d / %d \n", allcur, 29210163);
			}
			rf.close();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}

public class MultiThreadDownloader {
	
	String url;				// Ŀ��url
	String savePath;		// ���ر���·��
	String fileName;		// �ļ���
	int tnum = 5;			// Ĭ���߳���Ŀ
	long totalLength;		// �ܴ�С
	DownloadThread dth;		// һ�������̣߳����ڲ鿴���ؽ���
	JLabel  urlInputText;	// ���֣�Ŀ�����url
	JTextField urlInput;	// url�����
	JLabel  pathInputText;	// ���֣����ر���·��
	JTextField pathInput;	// ����·�������
	JLabel  nameInputText;	// ���֣��ļ���
	JTextField nameInput;	// �ļ��������
	JLabel  barTex;			// ���֣�������
	JProgressBar bar;		// ������
	JButton startBtn;		// ��ʼ���ذ�ť
	ExecutorService pool = Executors.newCachedThreadPool();	// �̳߳�
	
	MultiThreadDownloader() {
		
	}
	
	MultiThreadDownloader(String url, String savePath, String fileName, int tnum) {
		this.url = url;
		this.tnum = tnum;
		this.savePath = savePath;
		this.fileName = fileName;
	}
	
	public void download() throws Exception {
		
		// ��ʼ�����������Ĭ������
		// url����
		urlInputText = new JLabel ("Ŀ�����URL");
		urlInput = new JTextField(url);
		// ·������
		pathInputText = new JLabel ("���ر���·��");
		pathInput = new JTextField(savePath);
		// �ļ�������
		nameInputText = new JLabel ("�ļ���");
		nameInput = new JTextField(fileName);
		// ������
		JLabel  barText = new JLabel ("������");
		bar = new JProgressBar(0, 100);
		
		// ��ʼ���ذ�ť ע���¼� �����ʱ�򴴽��������߳�
		startBtn = new JButton("��ʼ����");
		startBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startBtn.setEnabled(false);	// ��ť����
				// ����û�ʵ�����������
				savePath = pathInput.getText();
				fileName = nameInput.getText();
				url = urlInput.getText();
				try {
					totalLength = new URL(url).openConnection().getContentLength();
					bar.setMaximum((int)totalLength);
					long eachLength = totalLength / tnum;
					// ��չ��
					String ext = url.substring(url.lastIndexOf("."));
					// ���������߳�
					for(int i=0; i<tnum; i++) {		
						long st = eachLength * i;
						long ed = eachLength * (i+1);
						if(i == tnum-1) {
							ed=Math.max(ed, totalLength);
						}
						// ����URL����
						URLConnection con = new URL(url).openConnection();
						con.setRequestProperty("Range", "bytes="+String.valueOf(st)+"-"+String.valueOf(ed));
						con.connect();
						// ���ļ���
						RandomAccessFile rf = new RandomAccessFile(savePath+fileName+ext, "rw");
						rf.seek(st);	// �ļ�����ת����Ӧλ��
						// ���������߳�
						DownloadThread d =new DownloadThread(con, rf);
						pool.submit(d);
						if(i==tnum-1) {
							dth = d;
						}
					}
					// ����һ��ÿ500ms���½��������߳�
					pool.submit(new Thread() {
						public void run() {
							while(true) {
								//System.out.printf("%d / %d \n", dth.allcur, totalLength);
								bar.setValue((int)dth.allcur);
								if(dth.allcur >= totalLength) {
									startBtn.setText("�������");
									break;
								}
								try {
									this.sleep(500);
								} catch (InterruptedException e) {
									e.printStackTrace();
								}
							}
						}
					});
					// �ر��̳߳�
					pool.shutdown();
				} catch(Exception e1) {
					e1.printStackTrace();
				}
			}
		});
		// ��ʼ���ذ�ť ע���¼� �����ʱ�򴴽��������߳� -- ����
		
		// ������
		JFrame jf = new JFrame("������");
		jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		jf.setSize(512, 321);
		jf.setLayout(new GridLayout(9, 1));
		jf.add(urlInputText);
		jf.add(urlInput);
		jf.add(pathInputText);
		jf.add(pathInput);
		jf.add(nameInputText);
		jf.add(nameInput);
		jf.add(barText);
		jf.add(bar);
		jf.add(startBtn);
		jf.show();
		
	}
	
	public static void main(String[] args) throws Exception {
		MultiThreadDownloader dl = new MultiThreadDownloader(
				"http://www.szulrl.cn/browserTest/PDFFILE.zip", 
				"E:/MyEclipse/WorkSpace/Hello/src/homework/", 
				"����",
				3);
		dl.download();
	}
}
