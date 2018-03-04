package android.app.setak;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

public class CustombarAdapter implements View.OnClickListener {

    Context mContext;

    FragmentManager fragmentManager;
    SlideMenuFragment slideMenuFragment;
    FragmentTransaction fragmentTransaction;

    DrawerLayout dl_slidemenu;


    //슬라이드 열기/닫기 플래그
    boolean isPageOpen = false;
    //슬라이드 열기 애니메이션
    Animation translateLeftAnim;
    //슬라이드 닫기 애니메이션
    Animation translateRightAnim;
    //슬라이드 레이아웃
    RelativeLayout lv_slidemenu;

    public CustombarAdapter(Context context){
        mContext = context;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.actionBackBtn:
                this.actionBackBtnClick();
                break;
            case R.id.actionLogoBtn:
                this.actionLogoBtnClick();
                break;
            case R.id.actionMenuBtn:
                this.actionMenuBtnClick();
                break;
        }
    }
    private void actionBackBtnClick(){
        ((Activity)mContext).finish();
    }

    private void actionLogoBtnClick(){
        Intent intent = new Intent(((Activity)mContext), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ((Activity)mContext).startActivity(intent);
        ((Activity)mContext).finish();
    }

    private void actionMenuBtnClick(){
        lv_slidemenu = (RelativeLayout) ((Activity) mContext). findViewById(R.id.lv_slidemenu);
        //애니메이션
        /*translateLeftAnim = AnimationUtils.loadAnimation(this, R.anim.trnaslate_left1);
        translateRightAnim = AnimationUtils.loadAnimation(this, R.anim.translate_right);*/

        //애니메이션 리스너 설정
        SlidingPageAnimationListener animationListener = new SlidingPageAnimationListener();
        translateLeftAnim.setAnimationListener(animationListener);
        translateRightAnim.setAnimationListener(animationListener);



    }

    public void backBtnPressed(){
        fragmentManager = ((Activity) mContext).getFragmentManager();
        dl_slidemenu = (DrawerLayout) ((Activity) mContext).findViewById(R.id.dl_slidemenu);
        lv_slidemenu = (RelativeLayout) ((Activity) mContext). findViewById(R.id.lv_slidemenu);
        if (dl_slidemenu.isDrawerOpen(lv_slidemenu)) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(slideMenuFragment);
            fragmentTransaction.commit();
            dl_slidemenu.closeDrawer(lv_slidemenu);
        } else {
            ((Activity) mContext).finish();
        }
    }

    //애니메이션 리스너
    private class SlidingPageAnimationListener implements Animation.AnimationListener {
        @Override
        public void onAnimationEnd(Animation animation) {
            //슬라이드 열기->닫기
            if(isPageOpen){
                lv_slidemenu.setVisibility(View.INVISIBLE);
                isPageOpen = false;
            }
            //슬라이드 닫기->열기
            else{
                isPageOpen = true;
            }
        }
        @Override
        public void onAnimationRepeat(Animation animation) {

        }
        @Override
        public void onAnimationStart(Animation animation) {

        }
    }

}
