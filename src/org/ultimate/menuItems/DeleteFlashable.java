package org.ultimate.menuItems;

import java.io.File;


import org.anurag.file.quest.R;
import org.anurag.file.quest.RFileManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.DialerFilter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Copyright(c) 2013 ANURAG 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * anurag.dev1512@gmail.com
 *
 */
public class DeleteFlashable{

	private File file;
	private TextView popupTitle;
	private TextView popupMessage;
	private Button btn1;
	private Button btn2;
	
	private RFileManager  mManager;
	private Thread thread;
	
	Context mContext;
	Dialog dialog;
	
	public DeleteFlashable(Context context,int width) {
		// TODO Auto-generated constructor stub
		mContext = context;
		dialog = new Dialog(mContext, R.style.custom_dialog_theme);
		dialog.setContentView(R.layout.popup_dialog);
		dialog.getWindow().getAttributes().width = width;
		dialog.setCancelable(false);
		onCreate();
	}
	
	
	
	protected void onCreate() {
		//params.width = size.x*4/5;
		
		String PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
		btn1 = (Button)dialog.findViewById(R.id.popupOk);
		btn2 = (Button)dialog.findViewById(R.id.popupCancel);
		popupTitle = (TextView)dialog.findViewById(R.id.popupTitle);
		final WebView web = (WebView)dialog.findViewById(R.id.popup_Web_View);
		web.loadUrl("file:///android_asset/Progress_Bar_HTML/index.html");
		web.setEnabled(false);
		popupMessage = (TextView)dialog.findViewById(R.id.textMessage);
		ImageView v = (ImageView)dialog.findViewById(R.id.popupImage);
		v.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher_move));
		file = new File(PATH +  "/File Quest");
		if(file.exists()){
			file = new File(PATH + "/File Quest/Flashable.zip");
			if(file.exists()){
				
				popupTitle.setText("Confirm Deletion");
				popupMessage.setText("All Your Flashable Zip Of Installed " +
						"Apps Will Be Deleted,Are You Sure To Delete Them");
				final Handler mHandler = new Handler(){
					public void handleMessage(Message msg){
						switch(msg.what){
							case 0 :
								popupMessage.setText("Please Wait While Deleting File");
								web.setVisibility(View.VISIBLE);
								btn1.setVisibility(View.GONE);
								btn2.setVisibility(View.GONE);
								break;
							case 2 :  	
								btn1.setVisibility(View.VISIBLE);
								btn1.setText("Ok");
								btn2.setVisibility(View.GONE);
								web.setVisibility(View.GONE);
								popupMessage.setText("Earlier Flashable Zip Has Been Deleted");
								
								
						}
					}
				};
				thread = new Thread(new Runnable() {
					@Override
					public void run() {
						mManager.deleteTarget(file);
						mHandler.sendEmptyMessage(2);
					}
				});
				mManager = new RFileManager(mHandler);
				btn1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if(btn2.getVisibility() == View.GONE){
							thread.interrupt();
							dialog.dismiss();
						}else
							thread.start();
					}
				});
				btn2.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						//setResult(-1);
						dialog.dismiss();				
					}
				});
			}
			else{
				popupTitle.setText("Messsge");
				popupMessage.setText("Unable To Locate Earlier Flashable Zip,Probably" +
						" No Zip Was Created");
				btn2.setVisibility(View.GONE);
				btn1.setText("Ok");
				dialog.setCancelable(true);
				btn1.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
			}
		}
		else{
			popupTitle.setText("Messsge");
			popupMessage.setText("Unable To Locate Earlier Backups,Probably" +
					" No Backup Was Taken");
			btn2.setVisibility(View.GONE);
			dialog.setCancelable(true);
			btn1.setText("Ok");
			btn1.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}
		
		dialog.show();
	}
	
	
	
}
