package com.one.Entities;

import android.os.Handler;
import android.os.Message;

public class Push_Thread extends Thread {


	private Handler handler;
	private String title, content;

	// ������
	public Push_Thread(Handler handler , String title, String content) {
		// TODO Auto-generated constructor stub

		this.handler = handler;
		this.title = title;
		this.content = content;
	}

	// ������ ��ü
	public void run() {
		// �ݺ������� ������ �۾��� �Ѵ�.
			// �ڵ鷯�� ������ �޽������� �ٸ��� ����.
			// �ڵ鷯�� ������ Message ��ü �����ϱ�
			Message m = new Message();
			m.obj = title + "|" + content;
			// �ڵ鷯�� �޼����� ������.
			handler.sendMessage(m);
		}
}
