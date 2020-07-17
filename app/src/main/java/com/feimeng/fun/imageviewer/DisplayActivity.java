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

    String[] normalImageUrl = new String[]{
            "http://img1.juimg.com/140908/330608-140ZP1531651.jpg",
            "https://pome.obs.cn-east-3.myhuaweicloud.com/dynamicImage/tiny-41-2020-06-30-23-09-01.jpg?w=198&h=192",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589782304050&di=94f005693f13c5182794db83d16e85d8&imgtype=0&src=http%3A%2F%2F01.minipic.eastday.com%2F20160808%2F20160808005842_8583cad74ac6cf9d6305b323ed6386bc_4.jpeg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589782304049&di=4b23f4053af00b6b22dd717cca92baf6&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fblog%2F201307%2F13%2F20130713175942_8h4Tz.jpeg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589782304049&di=45b9c06dd982e68a63437b13b024de91&imgtype=0&src=http%3A%2F%2Fgss0.baidu.com%2F-vo3dSag_xI4khGko9WTAnF6hhy%2Fzhidao%2Fpic%2Fitem%2Fd4628535e5dde7113dd9a57faeefce1b9d166134.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589782304049&di=927575e8f146af765d3c19d455325071&imgtype=0&src=http%3A%2F%2Fcdn.duitang.com%2Fuploads%2Fitem%2F201407%2F31%2F20140731115020_wV2xL.jpeg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589782304047&di=a2f2d4024723294d5a6b35293d25dc05&imgtype=0&src=http%3A%2F%2Fgss0.baidu.com%2F-Po3dSag_xI4khGko9WTAnF6hhy%2Fzhidao%2Fpic%2Fitem%2F242dd42a2834349ba8b3fa8ec1ea15ce36d3be00.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589782304045&di=83b6950bc8b8b8d55c72c238bff3dae8&imgtype=0&src=http%3A%2F%2Fhiphotos.baidu.com%2F784514578845415478%2Fpic%2Fitem%2F43f581dd064f0c2e485403f8.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589782304045&di=388f86d824091307371c22db20025031&imgtype=0&src=http%3A%2F%2Fgss0.baidu.com%2F-fo3dSag_xI4khGko9WTAnF6hhy%2Fzhidao%2Fpic%2Fitem%2F241f95cad1c8a7869f17219c6109c93d70cf5066.jpg"
    };

    String[] thumbs = new String[]{
            "https://ww4.sinaimg.cn/bmiddle/61e7945bly1fwnpjo7er0j215o6u77o1.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589782304050&di=5df02cff88866929e0cab35d003b5bf0&imgtype=0&src=http%3A%2F%2Fimg.pconline.com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fwallpaper%2F1306%2F17%2Fc1%2F22199754_1371460645988.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589782304050&di=94f005693f13c5182794db83d16e85d8&imgtype=0&src=http%3A%2F%2F01.minipic.eastday.com%2F20160808%2F20160808005842_8583cad74ac6cf9d6305b323ed6386bc_4.jpeg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589782304049&di=4b23f4053af00b6b22dd717cca92baf6&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fblog%2F201307%2F13%2F20130713175942_8h4Tz.jpeg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589782304049&di=45b9c06dd982e68a63437b13b024de91&imgtype=0&src=http%3A%2F%2Fgss0.baidu.com%2F-vo3dSag_xI4khGko9WTAnF6hhy%2Fzhidao%2Fpic%2Fitem%2Fd4628535e5dde7113dd9a57faeefce1b9d166134.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589782304049&di=927575e8f146af765d3c19d455325071&imgtype=0&src=http%3A%2F%2Fcdn.duitang.com%2Fuploads%2Fitem%2F201407%2F31%2F20140731115020_wV2xL.jpeg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589782304047&di=a2f2d4024723294d5a6b35293d25dc05&imgtype=0&src=http%3A%2F%2Fgss0.baidu.com%2F-Po3dSag_xI4khGko9WTAnF6hhy%2Fzhidao%2Fpic%2Fitem%2F242dd42a2834349ba8b3fa8ec1ea15ce36d3be00.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589782304045&di=83b6950bc8b8b8d55c72c238bff3dae8&imgtype=0&src=http%3A%2F%2Fhiphotos.baidu.com%2F784514578845415478%2Fpic%2Fitem%2F43f581dd064f0c2e485403f8.jpg",
            "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1589782304045&di=388f86d824091307371c22db20025031&imgtype=0&src=http%3A%2F%2Fgss0.baidu.com%2F-fo3dSag_xI4khGko9WTAnF6hhy%2Fzhidao%2Fpic%2Fitem%2F241f95cad1c8a7869f17219c6109c93d70cf5066.jpg"
    };

    Context context;
    int activityPosition;
    boolean isImmersive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_display);
        ImmersionBar.with(this).statusBarColor("#00bbff").init();
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
            holder.srcImageView.displayImage(normalImageUrl[position]);
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
                            .urls(normalImageUrl[position])
                            .position(holder.getAdapterPosition())
                            .views(holder.srcImageView)
                            .type(ViewerConfig.VIDEO)
                            .setProgress(new DefaultCircleProgress())
                            //提供视频View
                            .onProvideVideoView(() -> new VideoView(context))
                            //显示视频加载之前的缩略图
                            .loadPhotoBeforeShowBigImage((sketchImageView, position13) -> sketchImageView.displayImage(normalImageUrl[position]))
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
                            .urls(normalImageUrl[position])
                            .type(ViewerConfig.PHOTO)
                            .position(0)
                            .views(views[holder.getAdapterPosition()])
                            .loadPhotoBeforeShowBigImage((sketchImageView, position1) -> {
                                sketchImageView.displayImage(normalImageUrl[position]);
                            })
                            .start();
                } else {
                    new ImageViewer(context)
                            .urls(activityPosition == 2 ? longImageUrl : normalImageUrl)
                            .type(ViewerConfig.PHOTO)
                            .position(holder.getAdapterPosition(), 1)
                            .views(mRecyclerView, R.id.srcImageView)
                            .loadPhotoBeforeShowBigImage((sketchImageView, poi) -> {
                                sketchImageView.displayImage(normalImageUrl[poi]);
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
            return normalImageUrl.length;
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
