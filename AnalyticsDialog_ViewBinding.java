// Generated code from Butter Knife. Do not modify!
package com.example.puissance4;

import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.view.View;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class AnalyticsDialog_ViewBinding implements Unbinder {
  private AnalyticsDialog target;

  @UiThread
  public AnalyticsDialog_ViewBinding(AnalyticsDialog target, View source) {
    this.target = target;

    target.winGames = Utils.findRequiredViewAsType(source, R.id.winGames, "field 'winGames'", TextView.class);
    target.looseGames = Utils.findRequiredViewAsType(source, R.id.looseGames, "field 'looseGames'", TextView.class);
    target.equalGames = Utils.findRequiredViewAsType(source, R.id.equalGames, "field 'equalGames'", TextView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    AnalyticsDialog target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.winGames = null;
    target.looseGames = null;
    target.equalGames = null;
  }
}
