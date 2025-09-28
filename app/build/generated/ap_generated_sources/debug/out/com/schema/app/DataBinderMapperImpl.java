package com.schema.app;

import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import androidx.databinding.DataBinderMapper;
import androidx.databinding.DataBindingComponent;
import androidx.databinding.ViewDataBinding;
import com.schema.app.databinding.ActivityAddEventBindingImpl;
import com.schema.app.databinding.ActivityEventDetailBindingImpl;
import com.schema.app.databinding.ActivitySettingsBindingImpl;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
import java.lang.Object;
import java.lang.Override;
import java.lang.RuntimeException;
import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataBinderMapperImpl extends DataBinderMapper {
  private static final int LAYOUT_ACTIVITYADDEVENT = 1;

  private static final int LAYOUT_ACTIVITYEVENTDETAIL = 2;

  private static final int LAYOUT_ACTIVITYSETTINGS = 3;

  private static final SparseIntArray INTERNAL_LAYOUT_ID_LOOKUP = new SparseIntArray(3);

  static {
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.schema.app.R.layout.activity_add_event, LAYOUT_ACTIVITYADDEVENT);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.schema.app.R.layout.activity_event_detail, LAYOUT_ACTIVITYEVENTDETAIL);
    INTERNAL_LAYOUT_ID_LOOKUP.put(com.schema.app.R.layout.activity_settings, LAYOUT_ACTIVITYSETTINGS);
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View view, int layoutId) {
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = view.getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
        case  LAYOUT_ACTIVITYADDEVENT: {
          if ("layout/activity_add_event_0".equals(tag)) {
            return new ActivityAddEventBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_add_event is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYEVENTDETAIL: {
          if ("layout/activity_event_detail_0".equals(tag)) {
            return new ActivityEventDetailBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_event_detail is invalid. Received: " + tag);
        }
        case  LAYOUT_ACTIVITYSETTINGS: {
          if ("layout/activity_settings_0".equals(tag)) {
            return new ActivitySettingsBindingImpl(component, view);
          }
          throw new IllegalArgumentException("The tag for activity_settings is invalid. Received: " + tag);
        }
      }
    }
    return null;
  }

  @Override
  public ViewDataBinding getDataBinder(DataBindingComponent component, View[] views, int layoutId) {
    if(views == null || views.length == 0) {
      return null;
    }
    int localizedLayoutId = INTERNAL_LAYOUT_ID_LOOKUP.get(layoutId);
    if(localizedLayoutId > 0) {
      final Object tag = views[0].getTag();
      if(tag == null) {
        throw new RuntimeException("view must have a tag");
      }
      switch(localizedLayoutId) {
      }
    }
    return null;
  }

  @Override
  public int getLayoutId(String tag) {
    if (tag == null) {
      return 0;
    }
    Integer tmpVal = InnerLayoutIdLookup.sKeys.get(tag);
    return tmpVal == null ? 0 : tmpVal;
  }

  @Override
  public String convertBrIdToString(int localId) {
    String tmpVal = InnerBrLookup.sKeys.get(localId);
    return tmpVal;
  }

  @Override
  public List<DataBinderMapper> collectDependencies() {
    ArrayList<DataBinderMapper> result = new ArrayList<DataBinderMapper>(1);
    result.add(new androidx.databinding.library.baseAdapters.DataBinderMapperImpl());
    return result;
  }

  private static class InnerBrLookup {
    static final SparseArray<String> sKeys = new SparseArray<String>(3);

    static {
      sKeys.put(0, "_all");
      sKeys.put(1, "viewModel");
      sKeys.put(2, "viewmodel");
    }
  }

  private static class InnerLayoutIdLookup {
    static final HashMap<String, Integer> sKeys = new HashMap<String, Integer>(3);

    static {
      sKeys.put("layout/activity_add_event_0", com.schema.app.R.layout.activity_add_event);
      sKeys.put("layout/activity_event_detail_0", com.schema.app.R.layout.activity_event_detail);
      sKeys.put("layout/activity_settings_0", com.schema.app.R.layout.activity_settings);
    }
  }
}
