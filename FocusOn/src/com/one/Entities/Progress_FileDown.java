package com.one.Entities;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class Progress_FileDown extends AsyncTask< Integer//excute()����� �Ѱ��� ������Ÿ��
, String//�������� ������ Ÿ�� publishProgress(), onProgressUpdate()�� �μ� 
, Integer//doInBackground() ����� ���ϵ� ������ Ÿ�� onPostExecute()�� �μ�
> {
	//ProgressDialog�� ����� �ϳ� �־���
	private ProgressDialog mDlg;
	private Context mContext;

	public Progress_FileDown(Context context) {
		mContext = context;
	}

	//onPreExecute �Լ��� �̸���� excute()�� ���� �� doInBackground() ���� ���� ȣ��Ǵ� �Լ�
	//���⼭ ProgressDialog ���� �� �⺻ �����ϰ� show()
	@Override
	protected void onPreExecute() {
		mDlg = new ProgressDialog(mContext);
		mDlg.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mDlg.setMessage("�۾� ����");
		mDlg.show();

		super.onPreExecute();
	}

	//doInBackground �Լ��� excute() �����  �����
	//���⼭ �μ��δ� �۾������� �Ѱ��־���.
	@Override
	protected Integer doInBackground(Integer... params) {

		final int taskCnt = params[0];
		//�Ѱܹ��� �۾������� ProgressDialog�� �ƽ������� �����ϱ� ���� publishProgress()�� �����͸� �Ѱ��ش�.
		//publishProgress()�� �ѱ�� onProgressUpdate()�Լ��� ����ȴ�.
		publishProgress("max", Integer.toString(taskCnt));

		//�۾� ����, ���⼱ �Ѱ��� �۾����� * 100 ��ŭ sleep() �ɾ���
		for (int i = 0; i < taskCnt; ++i) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//�۾� ���� ���� ������� �����ϱ� ���� ����� ������ ������ publishProgress() �� �Ѱ���.
			publishProgress("progress", Integer.toString(i), "�ٿ�ε� ������...");
		}

		//�۾��� ������ �۾��� ������ ���� . onPostExecute()�Լ��� �μ��� ��
		return taskCnt;
	}

	//onProgressUpdate() �Լ��� publishProgress() �Լ��� �Ѱ��� �����͵��� �޾ƿ�
	@Override
	protected void onProgressUpdate(String... progress) {
		if (progress[0].equals("progress")) {
			mDlg.setProgress(Integer.parseInt(progress[1]));
			mDlg.setMessage(progress[2]);
		}
		else if (progress[0].equals("max")) {
			mDlg.setMax(Integer.parseInt(progress[1]));
		}
	}

	//onPostExecute() �Լ��� doInBackground() �Լ��� ����Ǹ� �����
	@Override
	protected void onPostExecute(Integer result) {
		mDlg.dismiss();
		Toast.makeText(mContext, "�ٿ�ε� �Ϸ�", Toast.LENGTH_SHORT).show();
	}
}
