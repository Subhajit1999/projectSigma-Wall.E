package com.subhajitkar.projectsigma.hydra.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;
import com.subhajitkar.projectsigma.hydra.R;
import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder> {
    private static final String TAG = "RecyclerAdapter";
    private Context mContext;
    private ArrayList<CategoryItem> mCategoryList;
    private ArrayList<ImagesItem> mImagesList;
    private int mRecyclerId;

    public RecyclerAdapter(Context context, ArrayList<CategoryItem> categoryList,ArrayList<ImagesItem> imagesList, int recyclerId){
        Log.d(TAG, "RecyclerAdapter: constructor");
        mContext = context;
        mRecyclerId = recyclerId;
        if (mRecyclerId==0){
            mCategoryList = categoryList;
        }else{
            mImagesList = imagesList;
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
        if (mRecyclerId==0){
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
        if (mRecyclerId==0){
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//                Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(),mCategoryList.get(i).getmImage());
//                Glide.with(mContext).load(new BlurBuilder().blur(mContext,bitmap)).into(holder.mCategoryImage);
//            }
            Glide.with(mContext).load(mCategoryList.get(i).getmImage()).into(holder.mCategoryImage);
            holder.mCategoryText.setText(mCategoryList.get(i).getmTitle());

        }else {
            //images recycler
            Log.d(TAG, "onBindViewHolder: loading images into recycler list");

            Picasso.with(mContext)
                    .load(mImagesList.get(i).getmImagesArray().getmLarge())
                    .placeholder(R.drawable.circle_icon)
                    .centerInside()
                    .resize(500,500)
                    .noFade()
                    .into(holder.mImage);
        }
    }

    @Override
    public int getItemCount() {
        if (mRecyclerId==0){
            return mCategoryList.size();
        }else{
            return mImagesList.size();
        }
    }

    private class BlurBuilder {
        private static final float BITMAP_SCALE = 0.5f;
        private static final float BLUR_RADIUS = 7.5f;

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        private Bitmap blur(Context context, Bitmap image) {
            int width = Math.round(image.getWidth() * BITMAP_SCALE);
            int height = Math.round(image.getHeight() * BITMAP_SCALE);

            Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
            Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

            RenderScript rs = RenderScript.create(context);
            ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
            Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
            theIntrinsic.setRadius(BLUR_RADIUS);
            theIntrinsic.setInput(tmpIn);
            theIntrinsic.forEach(tmpOut);
            tmpOut.copyTo(outputBitmap);

            return outputBitmap;
        }
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder{

        ImageView mCategoryImage;
        RoundedImageView mImage;
        TextView mCategoryText;
        RelativeLayout rootView;

        private RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            //initialize the views

            if (mRecyclerId==0){
                mCategoryImage = itemView.findViewById(R.id.iv_category);
                mCategoryText = itemView.findViewById(R.id.tv_category);
                rootView = itemView.findViewById(R.id.layout_category);
            }else{
                mImage = itemView.findViewById(R.id.iv_wall);
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            mListener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}