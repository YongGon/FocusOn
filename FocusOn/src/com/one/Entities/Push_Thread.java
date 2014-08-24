package com.one.Entities;

import android.os.Handler;
import android.os.Message;

public class Push_Thread extends Thread {


	private Handler handler;
	private String title, content;

	// 생성자
	public Push_Thread(Handler handler , String title, String content) {
		// TODO Auto-generated constructor stub

		this.handler = handler;
		this.title = title;
		this.content = content;
	}

	// 스레드 본체
	public void run() {
		// 반복적으로 수행할 작업을 한다.
			// 핸들러로 들어오는 메시지별로 다르게 동작.
			// 핸들러에 전달할 Message 객체 생성하기
			Message m = new Message();
			m.obj = title + "|" + content;
			// 핸들러에 메세지를 보낸다.
			handler.sendMessage(m);
		}
}
