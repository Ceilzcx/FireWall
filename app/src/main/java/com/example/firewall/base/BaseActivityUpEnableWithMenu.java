package com.example.firewall.base;

import android.view.Menu;

/**
   *使用标题和显示主页的基本模板活动启用
   *如果扩展此类，则会在onCreate时按顺序调用initView initData initEvent。
   *子类不需要重写onCreate。 只需在initView中调用setContentView即可。
  */
public abstract class BaseActivityUpEnableWithMenu extends BaseActivityUpEnable {
    private int menuId;

    /**
     * 方法构建
     * @param actionBarTitleId 标题的资源ID
     * @param menuId 菜单的资源ID
     */
    public BaseActivityUpEnableWithMenu(int actionBarTitleId, int menuId) {
        super(actionBarTitleId);
        this.menuId = menuId;
    }

    /**
     * 创建菜单使用在构造中输入的menuId
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menuId, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
