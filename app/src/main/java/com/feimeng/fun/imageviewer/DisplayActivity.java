package com.feimeng.fun.imageviewer;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.feimeng.fun.R;
import com.feimeng.imageviewer.ImageViewer;
import com.feimeng.imageviewer.config.ViewerConfig;
import com.feimeng.imageviewer.interfaces.DefaultCircleProgress;
import com.gyf.immersionbar.ImmersionBar;

import org.salient.artplayer.MediaPlayerManager;
import org.salient.artplayer.ScaleType;
import org.salient.artplayer.VideoView;

import me.panpf.sketch.SketchImageView;

public class DisplayActivity extends AppCompatActivity {
    WrapRecyclerView mRecyclerView;
    String[] longImageUrl = new String[]{
            "https://ww4.sinaimg.cn/bmiddle/61e7945bly1fwnpjo7er0j215o6u77o1.jpg",
            "http://wx3.sinaimg.cn/large/9f780829ly1fwvwhq9cg3j2cn40e2npj.jpg",
            "https://wx2.sinaimg.cn/mw600/6d239c49ly1fwsvs7rtocj20k3cmpkjs.jpg",
            "https://wx1.sinaimg.cn/mw600/71038334gy1fwv2i5084aj20b42wigqi.jpg",
            "https://wx3.sinaimg.cn/large/8378206bly1fvf2j96kryj20dc7uhkjq.jpg",
            "https://wx4.sinaimg.cn/large/0075aoetgy1fwkmjmcl67j30b3cmchdw.jpg",
            "https://wx1.sinaimg.cn/mw600/71038334gy1fwv2i5084aj20b42wigqi.jpg",
            "https://wx3.sinaimg.cn/large/8378206bly1fvf2j96kryj20dc7uhkjq.jpg",
            "https://wx4.sinaimg.cn/large/0075aoetgy1fwkmjmcl67j30b3cmchdw.jpg"
    };

    String[] normalImageUlr = new String[]{
            "http://img1.juimg.com/140908/330608-140ZP1531651.jpg",
            "https://img.mozhes.com/images/1586455370426.jpg?x-oss-process=style/thumbnail",
            "https://img.mozhes.com/images/1586455367231.jpg?x-oss-process=style/thumbnail",
            "https://img.mozhes.com/images/1586455366872.jpg?x-oss-process=style/thumbnail",
            "https://img.mozhes.com/images/1586455364922.jpg?x-oss-process=style/thumbnail",
            "https://img.mozhes.com/images/1586455366201.jpg?x-oss-process=style/thumbnail",
            "https://img.mozhes.com/images/1586455371304.jpg?x-oss-process=style/thumbnail",
            "https://img.mozhes.com/images/1586455369355.jpg?x-oss-process=style/thumbnail",
            "https://img.mozhes.com/images/1586455376907.jpg?x-oss-process=style/thumbnail"
    };

    String[] thumbs = new String[]{
            "https://ww4.sinaimg.cn/bmiddle/61e7945bly1fwnpjo7er0j215o6u77o1.jpg",
            "https://img.mozhes.com/images/1586455370426.jpg?x-oss-process=style/thumbnail",
            "https://img.mozhes.com/images/1586455367231.jpg?x-oss-process=style/thumbnail",
            "https://img.mozhes.com/images/1586455366872.jpg?x-oss-process=style/thumbnail",
            "https://img.mozhes.com/images/1586455364922.jpg?x-oss-process=style/thumbnail",
            "https://img.mozhes.com/images/1586455366201.jpg?x-oss-process=style/thumbnail",
            "https://img.mozhes.com/images/1586455371304.jpg?x-oss-process=style/thumbnail",
            "https://img.mozhes.com/images/1586455369355.jpg?x-oss-process=style/thumbnail",
            "https://img.mozhes.com/images/1586455376907.jpg?x-oss-process=style/thumbnail"
    };

    String[] images = new String[]{
            "https://ww4.sinaimg.cn/bmiddle/61e7945bly1fwnpjo7er0j215o6u77o1.jpg",
            "https://img.mozhes.com/images/1586455370426.jpg?Expires=1901815361&OSSAccessKeyId=LTAIn3yAXcNaGyDT&Signature=2IOEjzq1e8iysKiyWU7PYl9%2FqdA%3D&width=1280&height=720",
            "https://img.mozhes.com/images/1586455367231.jpg?Expires=1901815362&OSSAccessKeyId=LTAIn3yAXcNaGyDT&Signature=1jWlrsQn8Hw7aWTSIYFD7qxEYmY%3D&width=1280&height=720",
            "https://img.mozhes.com/images/1586455366872.jpg?Expires=1901815363&OSSAccessKeyId=LTAIn3yAXcNaGyDT&Signature=DqGHI4eM7dB0PhVPP%2FvJ6E9jPiM%3D&width=1280&height=720",
            "https://img.mozhes.com/images/1586455364922.jpg?Expires=1901815364&OSSAccessKeyId=LTAIn3yAXcNaGyDT&Signature=wZ7FxGernex%2B9Zw3qZq6ewrVGJg%3D&width=1280&height=720",
            "https://img.mozhes.com/images/1586455366201.jpg?Expires=1901815365&OSSAccessKeyId=LTAIn3yAXcNaGyDT&Signature=YoZU0jsCjXdeW3PVRdPvzOufLIU%3D&width=1280&height=720",
            "https://img.mozhes.com/images/1586455371304.jpg?Expires=1901815366&OSSAccessKeyId=LTAIn3yAXcNaGyDT&Signature=CKhT0PtYd5tWnlSV6MPQEgWFXRg%3D&width=1280&height=720",
            "https://img.mozhes.com/images/1586455369355.jpg?Expires=1901815367&OSSAccessKeyId=LTAIn3yAXcNaGyDT&Signature=fJwi7bp6ACuuoWim4FvwRYN7%2FHI%3D&width=1280&height=720",
            "https://img.mozhes.com/images/1586455376907.jpg?Expires=1901815368&OSSAccessKeyId=LTAIn3yAXcNaGyDT&Signature=TlVpi%2FcrXeia%2FPnDTBzD8HZdtlo%3D&width=1280&height=720"
    };

    Context context;
    int activityPosition;
    boolean isImmersive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_display);
        ImmersionBar.with(this).statusBarColor("#000000").init();
        activityPosition = getIntent().getIntExtra("position", 0);
        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(new MainAdapter());
        mRecyclerView.addHeaderView(LayoutInflater.from(this).inflate(R.layout.adapter_header, null));
        mRecyclerView.addFooterView(LayoutInflater.from(this).inflate(R.layout.adapter_footer, null));

//        findViewById(R.id.backdrop).setOnClickListener(v -> {
//            Sketch.with(DisplayActivity.this).getConfiguration().getDiskCache().clear();
//            Sketch.with(DisplayActivity.this).getConfiguration().getBitmapPool().clear();
//            Sketch.with(DisplayActivity.this).getConfiguration().getMemoryCache().clear();
//        });
    }


    public static void newIntent(Activity activity, Bundle bundle) {
        Intent intent = new Intent(activity, DisplayActivity.class);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    class MainAdapter extends RecyclerView.Adapter<MainAdapter.MyViewHolder> {

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            MyViewHolder holder = null;
            View view = LayoutInflater.from(
                    DisplayActivity.this).inflate(R.layout.item_grid, parent,
                    false);
            int size = getResources().getDisplayMetrics().widthPixels / 3 - 16;
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(size, size);
            int padding = 16;
            lp.setMargins(padding, padding, padding, padding);
            view.setLayoutParams(lp);
            holder = new MyViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.srcImageView.displayImage(normalImageUlr[position]);
            holder.srcImageView.setShowGifFlagEnabled(R.drawable.ic_gif);
            holder.srcImageView.setOnClickListener(srcView -> {

                int size = mRecyclerView.getChildCount();
                View[] views = new View[size];
                int[] realWidths = new int[size];
                int[] realHeights = new int[size];
                for (int i = 0; i < size; i++) {
                    ImageView recyImageView = mRecyclerView.getChildAt(i).findViewById(R.id.srcImageView);
                    views[i] = recyImageView;
                    realWidths[i] = 1920;
                    realHeights[i] = 720;
                }
                if (activityPosition == 3) {
                    //加载视频
                    ImageViewer imageViewer = new ImageViewer(context)
                            .urls(normalImageUlr[position])
                            .position(holder.getAdapterPosition())
                            .views(holder.srcImageView)
                            .type(ViewerConfig.VIDEO)
                            .immersive(isImmersive)
                            .setProgress(new DefaultCircleProgress())
                            //提供视频View
                            .onProvideVideoView(() -> new VideoView(context))
                            //显示视频加载之前的缩略图
                            .loadPhotoBeforeShowBigImage((sketchImageView, position13) -> sketchImageView.displayImage(normalImageUlr[position]))
                            //动画到最大化时的接口
                            .onVideoLoadEnd((dragDiootoView, sketchImageView, progressView) -> {
                                VideoView videoView = (VideoView) dragDiootoView.getContentView();
                                SimpleControlPanel simpleControlPanel = new SimpleControlPanel(context);
                                simpleControlPanel.setOnClickListener(v -> dragDiootoView.backToMin());
                                simpleControlPanel.setOnVideoPreparedListener(() -> {
                                    sketchImageView.setVisibility(View.GONE);
                                    progressView.setVisibility(View.GONE);
                                });
                                videoView.setControlPanel(simpleControlPanel);
                                videoView.setUp("http://bmob-cdn-982.b0.upaiyun.com/2017/02/23/266454624066f2b680707492a0664a97.mp4");
                                videoView.start();
                                dragDiootoView.notifySize(1920, 1080);
                                MediaPlayerManager.instance().setScreenScale(ScaleType.SCALE_CENTER_CROP);
                            })
                            //到最小状态的接口
                            .onFinish(dragDiootoView -> MediaPlayerManager.instance().releasePlayerAndView(context))
                            .start();
                } else if (activityPosition == 1) {
                    //加载单张图片
                    ImageViewer imageViewer = new ImageViewer(context)
                            .urls(normalImageUlr[position])
                            .type(ViewerConfig.PHOTO)
                            .immersive(isImmersive)
                            .position(0)
                            .views(views[holder.getAdapterPosition()])
                            .loadPhotoBeforeShowBigImage((sketchImageView, position1) -> {
                                sketchImageView.displayImage(normalImageUlr[position]);
                            })
                            .start();
                } else {
                    new ImageViewer(context)
                            .indicatorVisibility(View.GONE)
                            .urls(activityPosition == 2 ? longImageUrl : normalImageUlr)
                            .type(ViewerConfig.PHOTO)
                            .immersive(isImmersive)
                            .position(holder.getAdapterPosition(), 1)
                            .views(mRecyclerView, R.id.srcImageView)
                            .loadPhotoBeforeShowBigImage((sketchImageView, poi) -> {
                                sketchImageView.displayImage(normalImageUlr[poi]);
                                sketchImageView.setOnLongClickListener(v -> {
                                    Toast.makeText(DisplayActivity.this, "Long click", Toast.LENGTH_SHORT).show();
                                    return false;
                                });
                            })
                            .start();
                }
            });
        }

        @Override
        public int getItemCount() {
            return normalImageUlr.length;
        }

        class MyViewHolder extends RecyclerView.ViewHolder {

            SketchImageView srcImageView;

            public MyViewHolder(View view) {
                super(view);
                srcImageView = view.findViewById(R.id.srcImageView);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MediaPlayerManager.instance().pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MediaPlayerManager.instance().releasePlayerAndView(this);
    }

}
