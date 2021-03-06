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
package org.anurag.file.quest;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class SimpleAdapter extends ArrayAdapter<File>{
	
	static long c;
	public static boolean[] thumbselection;
	public static boolean MULTI_SELECT;
	public static int FOLDER_TYPE;
	public static int[] FOLDERS = {
		R.drawable.ic_launcher_folder_orange , 
		R.drawable.ic_launcher_folder_violet,
		R.drawable.ic_launcher_folder_oxygen ,
		R.drawable.ic_launcher_folder_yellow,
		R.drawable.ic_launcher_folder_ubuntu , 
		R.drawable.ic_launcher_folder_ubuntu_black ,
		R.drawable.ic_launcher_folder_gnome };
	private static ArrayList<File> nList;
	public static ArrayList<File> MULTI_FILES;
	private static PackageManager mPack;
	private static File file;
	private static Context mContext;
	private static LayoutInflater inflater;
	private static PackageInfo pi;
	//private static long size;
	public SimpleAdapter(Context context, int textViewResourceId ,ArrayList<File> objects) {
		super(context,textViewResourceId,objects);
		nList = objects;
		c = 0;
		MULTI_FILES = new ArrayList<File>();
		MULTI_SELECT = false;
		thumbselection = new boolean[nList.size()];
		mContext = context;
		inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPack = mContext.getPackageManager();
	}
	
	private static class FileHolder{
		ImageView FileIcon;
		TextView FileName;
		TextView FileType;
		TextView FileSize;
		CheckBox box;
	}
	
	@Override
	public View getView( int position , View convertView, ViewGroup container){
		file= nList.get(position);
		
		final FileHolder nHolder;
		if( convertView == null){
			convertView = inflater.inflate(R.layout.row_list_1, container , false);
			convertView.setMinimumHeight(5);
			nHolder = new FileHolder();
			nHolder.FileIcon = (ImageView)convertView.findViewById(R.id.fileIcon);
			nHolder.FileName = (TextView)convertView.findViewById(R.id.fileName);
			nHolder.FileSize = (TextView)convertView.findViewById(R.id.fileSize);
			nHolder.FileType = (TextView)convertView.findViewById(R.id.fileType);
			nHolder.box = (CheckBox)convertView.findViewById(R.id.checkbox);
			convertView.setTag(nHolder); 
		}else
			nHolder = (FileHolder)convertView.getTag(); 
		
		
		MULTI_FILES.add(null);	
		if(MULTI_SELECT){
			nHolder.box.setVisibility(View.VISIBLE);
			nHolder.box.setId(position);
			nHolder.box.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					CheckBox ch = (CheckBox) v;
					int id = ch.getId();
					if(thumbselection[id]){
						ch.setChecked(false);
						thumbselection[id] = false;
						MULTI_FILES.remove(id);
						MULTI_FILES.add(id, null);
						c--;
					}else{
						c++;
						MULTI_FILES.remove(id);
						MULTI_FILES.add(id, nList.get(id));
						ch.setChecked(true);
						thumbselection[id] = true;
					}
				}
			});
			nHolder.box.setChecked(thumbselection[position]);
		}else
			nHolder.box.setVisibility(View.GONE);
		
		nHolder.FileName.setText(file.getName());
		
		if(getExt(file).equalsIgnoreCase(".apk")){
			nHolder.FileType.setText("Application");
			nHolder.FileSize.setText(size(file));
			new ApkImage(nHolder.FileIcon).execute(file.getPath());
		}
		else if(getExt(file).equalsIgnoreCase(".png")||
				getExt(file).equalsIgnoreCase(".jpg")||
				getExt(file).equalsIgnoreCase(".jpeg")){
			nHolder.FileSize.setText(size(file));
			nHolder.FileType.setText("Image");
			new Image(nHolder.FileIcon).execute(file.getPath());
		}
		
		
		else{
			if(file.isDirectory()){
				nHolder.FileType.setText("Directory");
				//Drawable draw = mContext.getResources().getDrawable(R.drawable.ic_launcher_folder_orange);
				nHolder.FileIcon.setImageDrawable(mContext.getResources().getDrawable(FOLDERS[FOLDER_TYPE]));
				nHolder.FileName.setText(file.getName());
				if(!file.canRead()){
					nHolder.FileSize.setText("Root Access");
					nHolder.FileSize.setTextColor(Color.RED);
				}else {
					nHolder.FileSize.setText(file.listFiles().length + " Items");
					nHolder.FileSize.setTextColor(Color.WHITE);
				}	
			}else if(file.isFile()){
				nHolder.FileSize.setText(AppAdapter.size(file));
				if( getFileType(file) == "song"){
					nHolder.FileType.setText("Music");
					nHolder.FileIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher_music));
				}else if(file.getName().endsWith(".pdf") || file.getName().endsWith(".PDF")){
					nHolder.FileType.setText("Pdf");
					nHolder.FileIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher_adobe));
				}else if(file.getName().endsWith(".txt") || file.getName().endsWith(".TXT")
						|| file.getName().endsWith(".inf") || file.getName().endsWith(".INF")){
					nHolder.FileType.setText("Text");
					nHolder.FileIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher_text));
				}else if( getFileType(file) == "zip"){
					nHolder.FileType.setText("Zip");
					nHolder.FileIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher_zip_it));
				}else if( getFileType(file) == "video"){
					nHolder.FileType.setText("Video");
					nHolder.FileIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher_video));
				}else if( getFileType(file) == "compressed"){
					nHolder.FileType.setText("Archive");
					nHolder.FileIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher_misce_file_gallery));
				}else if( getFileType(file) == "document"){
					nHolder.FileType.setText("Document");
					nHolder.FileIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher_ppt));
				}else if( getFileType(file) == "web"){
					nHolder.FileType.setText("Saved Web Page");
					nHolder.FileIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher_web_pages));
				}
				else if(getFileType(file)=="sh"){
					nHolder.FileType.setText("Script");
					nHolder.FileIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher_sh));
				}else{
					nHolder.FileType.setText("Unknown");
					nHolder.FileIcon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher_unknown));
				}
			}
		}
		
		
		//new AppImageLoader(nHolder.FileIcon).execute(file.getPath());
		return convertView;
	}
	
	public static String getFileType(File f){
		String Name = f.getName();
		if(Name.endsWith("jpg")||Name.endsWith(".JPG")|| Name.endsWith(".png") || Name.endsWith(".PNG") || Name.endsWith(".gif") || Name.endsWith(".GIF")
				|| Name.endsWith(".JPEG") || Name.endsWith(".jpeg") ||Name.endsWith(".bmp") ||Name.endsWith(".BMP"))
			return "image";
		if(f.isDirectory())
			return "folder";
		else if(Name.endsWith(".zip") || Name.endsWith(".ZIP"))
			return "zip";
		else if( Name.endsWith("mhtml")||Name.endsWith(".MHTML")||  Name.endsWith(".HTM") || Name.endsWith(".htm") 
				||Name.endsWith(".html") || Name.endsWith(".HTML"))
			return "web";
		
		else if(Name.endsWith(".tar") || Name.endsWith(".TAR") || Name.endsWith(".rar") 
				|| Name.endsWith("RAR") || Name.endsWith(".7z") || Name.endsWith(".7Z"))
			return "compressed";
		else if(Name.endsWith(".apk") || Name.endsWith(".APK"))
			return "apk";
		else if(Name.endsWith(".mp3") || Name.endsWith(".MP3") || Name.endsWith(".amr") || Name.endsWith(".AMR")
				|| Name.endsWith(".ogg") || Name.endsWith(".OGG")||Name.endsWith(".m4a")||Name.endsWith(".M4A"))
			return "song";
		else if(Name.endsWith(".doc") ||Name.endsWith(".DOC")
				|| Name.endsWith(".DOCX") || Name.endsWith(".docx") || Name.endsWith(".ppt") || Name.endsWith(".PPT"))
			return "document";
		else if(f.getName().endsWith(".txt") || Name.endsWith(".TXT") || Name.endsWith(".inf") || Name.endsWith(".INF"))
			return "text";
		else if(Name.endsWith(".mp4") || Name.endsWith(".MP4") || Name.endsWith(".avi") ||Name.endsWith(".AVI")
				|| Name.endsWith(".FLV") || Name.endsWith(".flv") || Name.endsWith(".3GP") || Name.endsWith(".3gp"))
			return "video";		
		else if(Name.endsWith(".default")||Name.endsWith(".prop")||Name.endsWith(".rc")||Name.endsWith(".sh")||Name.endsWith("init"))
			return "sh";
		return null;
	}
	
	/**
	 * 
	 * @author Anurag
	 *
	 */
	public static class AppImageLoader extends AsyncTask<String , Void , Drawable>{
		Drawable draw = null;
		ImageView image;
		public AppImageLoader(ImageView view) {
			image = view;
		}
		@Override
		protected void onPostExecute(Drawable result) {
			if(draw != null)
				image.setImageDrawable(draw);
			else
			{
				draw = mContext.getResources().getDrawable(R.drawable.ic_launcher_unknown);
				image.setImageDrawable(draw);
			}
			super.onPostExecute(result);
		}
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		@Override
		protected Drawable doInBackground(String... arg0) {
			File file = new File(arg0[0]);
			if(file.isDirectory()){
				draw = mContext.getResources().getDrawable(FOLDERS[FOLDER_TYPE]);
			}
			else if(file.getName().endsWith(".apk")){
				/**
				 * There is a bug in loading app icon needs to be fixed
				 */
				try{
					pi = mPack.getPackageArchiveInfo(arg0[0], 0 );
					//pi.applicationInfo.sourceDir = arg0[0];
					pi.applicationInfo.publicSourceDir = arg0[0];
					draw = pi.applicationInfo.loadIcon(mPack);	
				}catch(Exception e){
					draw = mContext.getResources().getDrawable(R.drawable.ic_launcher_apk);
				}
				Bitmap bitmap = ((BitmapDrawable) draw).getBitmap();
		        //int dp5 = (int)(75*getResources().getDisplayMetrics().density);
				draw= new BitmapDrawable(Bitmap.createScaledBitmap(bitmap, 65, 65, true));	
			}else if(file.isFile() && getFileType(file)=="song")
				draw = mContext.getResources().getDrawable(R.drawable.ic_launcher_music);
			else if(file.isFile() && getFileType(file)=="zip")
				draw = mContext.getResources().getDrawable(R.drawable.ic_launcher_zip_it);
			else if(file.isFile() && getFileType(file)=="compressed")
				draw = mContext.getResources().getDrawable(R.drawable.ic_launcher_rar);
			else if(file.isFile() && file.getName().endsWith(".pdf"))
				draw = mContext.getResources().getDrawable(R.drawable.ic_launcher_adobe);
			else if(file.isFile() && getFileType(file)=="image"){
				Bitmap b = BitmapFactory.decodeFile(file.getPath());
				draw = new BitmapDrawable(b);
			}else if(file.isFile() && getFileType(file)=="video")
				draw = mContext.getResources().getDrawable(R.drawable.ic_launcher_video );
			else if(file.isFile() && getFileType(file) == "text")
				draw = mContext.getResources().getDrawable(R.drawable.ic_launcher_text);
			else if(file.isFile() && getFileType(file) == "web")
				draw = mContext.getResources().getDrawable(R.drawable.ic_launcher_web_pages);
			else if(file.isFile()&&getFileType(file)=="sh")
				draw = mContext.getResources().getDrawable(R.drawable.ic_launcher_sh);
			return null;
		}
	}
	
	
	/**
	 * THIS FUNCTION RETURN THE SIZE IF THE GIVEN FIZE IN PARAMETER
	 * @param f
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public String size(File f){
		long size = f.length();
		if(size>1024*1024*1024)
			return String.format("%.2f GB", (double)size/(1024*1024*1024));
		
		else if(size > 1024*1024)
			return String.format("%.2f MB", (double)size/(1024*1024));
		
		else if(size>1024)
			return String.format("%.2f KB", (double)size/(1024));
		
		else
			return String.format("%.2f Bytes", (double)size);
	}
	
	
	
	
	
	
	private class Image extends AsyncTask<String, Void, Void>{
		Bitmap draw;
		ImageView view; 
		public Image(ImageView v){
			view = v;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(draw !=null)
				view.setImageBitmap(draw);
			else if(draw == null)
				view.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher_images ));
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			view.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher_images));
		}

		@Override
		protected Void doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			draw = getPreview(arg0[0]);
			return null;
		}
		
	}
	
	
	private  Bitmap getPreview(String url) {
        File image = new File(url);
        int size =72;
        InputStream photoStream = null;
		Bitmap mBitmap = null;
		try {
			photoStream = new FileInputStream(image);
			BitmapFactory.Options opts = new BitmapFactory.Options();
			opts.inJustDecodeBounds = true;
			opts.inSampleSize = 1;

			mBitmap = BitmapFactory.decodeStream(photoStream, null, opts);
			if (opts.outWidth > opts.outHeight && opts.outWidth > size) {
				opts.inSampleSize = opts.outWidth / size;
			} else if (opts.outWidth < opts.outHeight && opts.outHeight > size) {
				opts.inSampleSize = opts.outHeight / size;
			}
			if (opts.inSampleSize < 1) {
				opts.inSampleSize = 1;
			}
			opts.inJustDecodeBounds = false;
			photoStream.close();
			photoStream = new FileInputStream(image);
			mBitmap = BitmapFactory.decodeStream(photoStream, null, opts);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			if (photoStream != null) {
				try {
					photoStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return mBitmap;
      
    }
	
	
	
	/**
	 * THIS FUNCTION RETURNS THE ICON OF THE APP IF GIVEN FILE IS AN APP 
	 * @param f
	 * @return
	 */
	private Drawable getApkIcon(String f){
		Drawable draw;
		PackageInfo info;
		try{
			info = mPack.getPackageArchiveInfo(f, 0);
			info.applicationInfo.publicSourceDir = f;
			draw = info.applicationInfo.loadIcon(mPack);
			return draw;
		}catch(Exception e){
			return mContext.getResources().getDrawable(R.drawable.ic_launcher_apk);
		}
	}
	
	public class ApkImage extends AsyncTask<String , Void, Void>{

		Drawable draw;
		ImageView icon;
		public ApkImage(ImageView iV ){
			icon = iV;
			
		}
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			icon.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_launcher_apk));
			super.onPreExecute();
		}
		@Override
		protected Void doInBackground(String... path) {
			// TODO Auto-generated method stub
			draw = getApkIcon(path[0]);
			return null;
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			icon.setImageDrawable(draw);
			super.onPostExecute(result);
		}
		
	}

	
	
	/**
	 * THIS FUNCTION THE EXTENSION OF THE GIVEN FILE
	 * @param f
	 * @return THE EXTENSION OF A FILE
	 */
	public String getExt(File f){
		String name = f.getName();
		try{
			return name.substring(name.lastIndexOf("."), name.length());
		}catch(IndexOutOfBoundsException e){
			return "";
		}
	}
}
