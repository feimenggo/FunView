
package com.feimeng.fun.replace;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.feimeng.fun.R;
import com.feimeng.view.replace.OnReplaceListener;
import com.feimeng.view.replace.ReplaceView;

/**
 * Author: Feimeng
 * Time:   2020/10/27
 * Description:
 */
public class ReplaceDemoActivity extends AppCompatActivity implements View.OnClickListener {
    private ReplaceView replaceTextView;
    private EditText value2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replace_demo);
        EditText value1 = findViewById(R.id.value1);
        value2 = findViewById(R.id.value2);
        value1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                replaceTextView.search(s.toString());
            }
        });
        Button prevView = findViewById(R.id.prev);
        prevView.setOnClickListener(this);
        Button repealView = findViewById(R.id.repeal);
        repealView.setOnClickListener(this);
        Button replaceView = findViewById(R.id.replace);
        replaceView.setOnClickListener(this);
        Button replaceAllView = findViewById(R.id.replaceAll);
        replaceAllView.setOnClickListener(this);
        Button nextView = findViewById(R.id.next);
        nextView.setOnClickListener(this);
        replaceTextView = findViewById(R.id.replaceView);
        replaceTextView.setContent("测试31度圣斗士星矢推出联名圣衣鞋，已经开始预售啦，双十一到手价超低。还有限量圣衣箱于11月1日 0点正式开始抢购，更多详情上天猫搜索“圣衣鞋”，机会难得！还在等你？ \n" +
                "\n" +
                "　　我认识一个人夫妻俩去厂里打工，一年能存10000万，存了一百万放在银行里。然后他孩子是农村上的小学，要上初中了，他家里人劝他把钱拿出来在城里买套学区房首付，他夫妻说什么都不肯，说一百万放在银行里有几万块钱的利息，还有过年过节送的米和油。然后孩子去城里考试，父母也不来陪他，说我请假会没好多工资。然后小孩心态崩了，在学校前年纪几名也没考上城里的学校，继续农村读初中。WiFi\n" +
                "\n" +
                "　　我觉得那个小孩一辈子要恨死他父母。\n" +
                "\n" +
                "　　不对啊？原先的号呢？从我关注列表直接消失了！怎么回事？开车被封？跳槽？还是这个是假冒得？123456789中国社会体系里，最low最土的群体，逐渐在侵略B站！\n" +
                "\n" +
                "　　我们老师也有腾讯的声音，每次播放都能引起大家的注意\n" +
                "\n" +
                "　　这波闪现a洛问题不大，毕竟最后一发有加速的，问题是，对面站位和自己的站位。苏宁Ez靠下，沙皇靠河道，瞎子猴子在上。自己家杰斯在上，腕豪在往上靠，辅助和中单在下龙坑旁边，闪现A了你凭什么往上走啊？往下走找Ez不是三打一？猴子还下不了，就算下来了也只能大到腕豪\n" +
                "\n" +
                "　　本集补充：\n" +
                "\n" +
                "　　1、卡蕾拉在中计以后想到自己可能要违背莉姆露的任务而死，于是她前所未有地感到了恐惧，唯有这一点他绝对无法接受。\n" +
                "\n" +
                "　　2、卡蕾拉中计之后认为近藤耍这种小手段是看不起她于是感到十分愤怒，再加上第一点提到的恐惧，导致卡蕾拉近乎失去理智，这也是近藤希望看到的。在关键时刻，是阿格拉把卡蕾拉从失控的边缘拉了回来，阿格拉说近藤这种行为是因为他明确地知道自己的弱小，在面对高等种族时唯有用尽一切手段才能活下去，这是尊重卡蕾拉的表现，把她当成强大对手的表现。而在卡蕾拉夺回冷静以后，近藤也收起了小手段，以最警戒的姿态面对卡蕾拉。\n" +
                "\n" +
                "　　3、结束战斗后阿格拉表示如果只用剑决斗自己一定会赢，2333\n" +
                "\n" +
                "　　作为一名社畜的我，\n" +
                "\n" +
                "　　每日通勤时间来回长达3个小时，公交地铁上嘈杂的声音更是成为通勤途中的一大痛点，更大的痛点是因为业务需求，需要时刻保通话畅通，而在车厢这种环境，手机信号且不谈，单单是通话中的噪音就足矣让自己和客户崩溃:喂!你说的啥？我听不到！经常不是自己听不到就是别人听自己的声音模棱两可。因此我还错过了第三季度的晋升机会。\n" +
                "\n" +
                "　　直到有一天我在网上看到了漫步者DreamPods--\n" +
                "\n" +
                "　　DreamPods官方中文名叫追梦宝，\n" +
                "\n" +
                "　　耳机仓设计简洁大方，上下两头采用一刀切设计，可以非常轻松稳固的立在桌子上，中间采用硬直线条加圆弧过渡的组合设计，官方称之为超跑流线设计。 耳机则采用了半入耳式的佩戴方式，更适用于长时间的佩戴。\n" +
                "\n" +
                "　　配件方面有:耳机*2、充电盒*1、绒布袋*1、Type-C充电线*1、说明书&保修卡*1\n" +
                "\n" +
                "　　一款好的产品外观其实只是一方面，更多的是它的内涵，科技研发实力，漫步者作为大厂，研发方面自然也是有自家的看家本领，就比如这次DreamPods上的黑科技———\n" +
                "\n" +
                "　　漫步者EdiCall通话降噪技术");
        replaceTextView.setOnReplaceListener(new OnReplaceListener() {
            @Override
            public void onMoveStatus(boolean prev, boolean next) {
                prevView.setEnabled(prev);
                nextView.setEnabled(next);
            }

            @Override
            public void onActionStatus(boolean repeal, boolean replace) {
                repealView.setEnabled(repeal);
                replaceView.setEnabled(replace);
                replaceAllView.setEnabled(replace);
            }

            @Override
            public void onContentChange(String content) {
                Log.d("nodawang", "内容改变");
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.prev:
                replaceTextView.prev();
                break;
            case R.id.repeal:
                replaceTextView.repeal();
                break;
            case R.id.replace:
                replaceTextView.replace(value2.getText().toString());
                break;
            case R.id.replaceAll:
                replaceTextView.replaceAll(value2.getText().toString());
                break;
            case R.id.next:
                replaceTextView.next();
                break;
        }
    }
}
