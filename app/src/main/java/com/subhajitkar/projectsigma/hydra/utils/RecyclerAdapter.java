package com.subhajitkar.projectsigma.hydra.utils;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuPopupHelper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.subhajitkar.projectsigma.hydra.R;
import com.subhajitkar.projectsigma.hydra.activities.DetailActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    private static final String TAG = "RecyclerAdapter";
    private Context mContext;
    private ArrayList<CategoryItem> mCategoryList;
    private ArrayList<ImagesItem> mImagesList;
    private int mListId;
    private ArrayList<String> mDownloadedImagesList;
    private File imageDirect;

    public RecyclerAdapter(Context context, ArrayList<CategoryItem> categoryList,ArrayList<ImagesItem> imagesList, int listId) {
        Log.d(TAG, "RecyclerAdapter: constructor");
        mContext = context;
        mListId = listId;
        if (mListId == 0) {
            mCategoryList = categoryList;
        }else if( mListId==1){
            mImagesList = imagesList;
        }else{
            mDownloadedImagesList = new ArrayList<>();
            imageDirect = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "/Wall.e");
            getDownloadedFilePaths();
        }
    }

    private void getDownloadedFilePaths() {
        Log.d(TAG, "getDownloadedFilePaths: getting downloaded images files paths from directory.");

        if (!mDownloadedImagesList.isEmpty()){
            mDownloadedImagesList.clear();
        }
        if (imageDirect.isDirectory()) {
            File[] listFile = imageDirect.listFiles();
            if (listFile.length<=0){
                Toast.makeText(mContext,"No images found. Try to download some.",Toast.LENGTH_SHORT).show();
            }
            for (File value : listFile) {
                mDownloadedImagesList.add(value.getAbsolutePath());
            }
        }else{
            Toast.makeText(mContext,"Nothing found. Seems like file doesn't exist.",Toast.LENGTH_SHORT).show();
        }
    }

    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d(TAG, "onCreateViewHolder: Element view of recycler list");
        View v;
        if (mListId==0){
            v = LayoutInflater.from(mContext).inflate(R.layout.element_category_recycler,viewGroup,false);
        }else{
            v = LayoutInflater.from(mContext).inflate(R.layout.elements_images_recycler,viewGroup,false);
        }
        return new RecyclerViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, final int i) {
        Log.d(TAG, "onBindViewHolder: Setting up the values to the views");

        //category recycler
        if (mListId==0){
            Glide.with(mContext).load(mCategoryList.get(i).getmImage()).into(holder.mCategoryImage);
            holder.mCategoryText.setText(mCategoryList.get(i).getmTitle());

        }else if(mListId==1){
            //images recycler
            Log.d(TAG, "onBindViewHolder: loading images into recycler list");
            loadImage(mImagesList.get(i).getmImagesArray().getmLarge(),holder.mImage);
        }else{
            Log.d(TAG, "onBindViewHolder: File path: "+mDownloadedImagesList.get(i));
            loadImage("file://"+mDownloadedImagesList.get(i),holder.mImage);
        }
    }

    @Override
    public int getItemCount() {
        if (mListId==0){
            return mCategoryList.size();
        }else if (mListId==1){
            return mImagesList.size();
        }else{
            return mDownloadedImagesList.size();
        }
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView mCategoryImage;
        RoundedImageView mImage;
        TextView mCategoryText;

        private RecyclerViewHolder(@NonNull final View itemView) {
            super(itemView);
            //initialize the views

            if (mListId == 0) {
                mCategoryImage = itemView.findViewById(R.id.iv_category);
                mCategoryText = itemView.findViewById(R.id.tv_category);
            } else {
                mImage = itemView.findViewById(R.id.iv_wall);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
            if (mListId == 2) { // if downloads option only
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        PopupMenu popup = new PopupMenu(mContext, itemView);

                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                int position = getAdapterPosition();
                                switch (item.getItemId()) {
                                    case R.id.popup_delete:
                                        //action file delete
                                        deleteItem(position);
                                        return true;
                                    case R.id.popup_send:
                                        sendImage(position);
                                        return true;
                                    case R.id.popup_set_wallpaper:
                                        setWallpaper(position);
                                        return true;
                                    default:
                                        return false;
                                }
                            }
                        });
                        // here you can inflate your menu
                        popup.inflate(R.menu.menu_popup);
                        popup.setGravity(Gravity.CENTER);

                        // setting up icon with menu items.
                        try {
                            Field mFieldPopup = popup.getClass().getDeclaredField("mPopup");
                            mFieldPopup.setAccessible(true);
                            MenuPopupHelper mPopup = (MenuPopupHelper) mFieldPopup.get(popup);
                            mPopup.setForceShowIcon(true);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        popup.show();
                        return true;
                    }
                });
            }
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    private void loadImage(String url, ImageView imageView){
        Log.d(TAG, "loadImage: loading images into view.");
        Picasso.with(mContext)
                .load(url)
                .placeholder(R.drawable.circle_icon)
                .centerInside()
                .resize(500,500)
                .noFade()
                .into(imageView);
    }

    private void deleteItem(int position){
        Log.d(TAG, "deleteItem: deleting item.");

        String fileName = URLUtil.guessFileName("file://"+mDownloadedImagesList.get(position),null,null);
        File file = new File(imageDirect,fileName);
        boolean deleted = file.delete();
        Log.d(TAG, "deleteItem: deleted file: "+deleted);
        getDownloadedFilePaths();
        notifyDataSetChanged();
    }

    private void sendImage(int position) {
        Log.d(TAG, "sendImage: sending image through intent.");

        Intent sendIntent = new Intent();  //preparing intent
        sendIntent.setAction(Intent.ACTION_SEND);

        //getting image file from storage
        String fileName = URLUtil.guessFileName("file://"+mDownloadedImagesList.get(position),null,null);
        File file = new File(imageDirect,fileName);
        Bitmap bmp = BitmapFactory.decodeFile(mDownloadedImagesList.get(position));
        Uri bmpUri = null;
        try {  //for API 24 or higher, using FileProvider to avoid errors
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG,100,out);
            out.close();
            bmpUri = FileProvider.getUriForFile(mContext, mContext.getPackageName(), file);
//            bmpUri = Uri.fromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bmpUri != null){
            Log.d(TAG, "sendImage: bitmap uri not null.");
            sendIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
            sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            sendIntent.setType("image/jpeg");
            mContext.startActivity(Intent.createChooser(sendIntent, "Send image through"));
        }
    }

    private void setWallpaper(int position){
        Log.d(TAG, "setWallpaper: setting wallpaper");
        WallpaperManager manager = WallpaperManager.getInstance(mContext);
        try{
            Bitmap bitmap = BitmapFactory.decodeFile(mDownloadedImagesList.get(position));
            manager.setBitmap(bitmap);
            Toast.makeText(mContext,"Wallpaper set successfully.",Toast.LENGTH_SHORT).show();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}