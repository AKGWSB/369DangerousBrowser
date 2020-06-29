package homework;

import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.util.*;
import java.util.regex.*;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.html.*;

public class EmailSender {
	
	JLabel destInputText;
	JTextField destInput;
	JLabel textInputText;
	JTextField textInput;
	JButton sendBtn;
	int errcnt=0;
	
	public EmailSender() {
		
	}
	
	// 252009914@qq.com -> MjUyMDA5OTE0QHFxLmNvbQ==
	// qqrjfuyzywepbigj	-> cXFyamZ1eXp5d2VwYmlnag==
	
	public void send(String host, String dest, String text) throws Exception {
		
		Socket socket = new Socket(host, 25);
		InputStream is = socket.getInputStream();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
		OutputStream os = socket.getOutputStream();
		OutputStreamWriter writer = new OutputStreamWriter(os, "gbk");
		writer.write("HELO a\r\n");
		writer.write("auth login\r\n");
		writer.write("MjUyMDA5OTE0QHFxLmNvbQ==\r\n");
		writer.write("cXFyamZ1eXp5d2VwYmlnag==\r\n");
		writer.write("MAIL FROM: <252009914@qq.com>\r\n");
		writer.write("RCPT TO: <" + dest + ">\r\n");
		writer.write("DATA\r\n");
		
		// 
		writer.write(text+"\r\n");
		System.out.println(text);
		
		writer.write("\r\n.\r\n");
		writer.write("QUIT\r\n");
		writer.flush();
		/*
		String line;
		while((line=reader.readLine()) != null) {
			System.out.println(line);
		}
		writer.close();
		*/
	}
	
	void sendInWindow() {
		try {
			destInputText = new JLabel("Ŀ�������ַ");
			destInput = new JTextField("252009914@qq.com");
			textInputText = new JLabel("����");
			textInput = new JTextField("����һ���Զ����͵��ʼ�--������2018171028");
			sendBtn = new JButton("����");
			sendBtn.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						send("smtp.qq.com", destInput.getText(), textInput.getText());
						sendBtn.setEnabled(false);
						sendBtn.setText("���ͳɹ�");
					} catch (Exception e1) {
						e1.printStackTrace();
						errcnt++;
						sendBtn.setText("����ʧ�� �������·��� "+String.valueOf(errcnt));
					}
				}
			});
			
			JFrame jf = new JFrame("�ʼ�����");
			
			jf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			jf.setSize(768, 221);
			jf.setLayout(new GridLayout(5, 1));
			jf.add(destInputText);
			jf.add(destInput);
			jf.add(textInputText);
			jf.add(textInput);
			jf.add(sendBtn);
			jf.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		EmailSender sender = new EmailSender();
		//sender.send("smtp.qq.com", "252009914@qq.com", "1");
		sender.sendInWindow();
		//System.out.println("���ͳɹ�");
	}

}
