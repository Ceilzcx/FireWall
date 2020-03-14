package com.example.firewall.base;

import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firewall.R;
import com.jaeger.library.StatusBarUtil;

/**
   *启动时调用动作标题和显示主页的基本模板
   *如果扩展此类，则会在onCreate时按顺序调用initView initData initEvent。
   *子类需要重写onCreate。 只需在initView中调用setContentView即可。
 */
public abstract class BaseActivityUpEnable extends AppCompatActivity {
    private final int actionBarTitleId;

    /**
     * 构建方法 设置操作栏标题
     * @param actionBarTitleId 标题栏id
     */
    public BaseActivityUpEnable(final int actionBarTitleId) {
        this.actionBarTitleId = actionBarTitleId;
    }

    /**
     * 处理item选定事件
     * 返回时调用super.onOptionsItemSelected（）
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**

     */
    @Override
    protected void onStart() {
        super.onStart();
        ActionBar actionBar = getSupportActionBar();
        if(null != actionBar) {
            actionBar.setTitle(actionBarTitleId);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        StatusBarUtil.setColorNoTranslucent(this, getResources().getColor(R.color.colorPrimary));
    }
}
