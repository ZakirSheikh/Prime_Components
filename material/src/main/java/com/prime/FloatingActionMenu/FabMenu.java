package com.prime.FloatingActionMenu;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.view.menu.MenuBuilder;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.prime.R;

public class FabMenu extends FloatingActionButton {

    private static final String TAG = "FabMenu";

    private PopupWindow mMenuWindow;
    private ViewGroup mPopupLayout;
    private Rect mArea;
    private Bitmap backgroud;
    private Paint mEraser;


    public FabMenu(@NonNull Context context) {
        this(context, null);
    }

    public FabMenu(@NonNull Context context,
                   @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.floatingActionButtonStyle);
    }

    @SuppressLint("RestrictedApi")
    public FabMenu(@NonNull Context context,
                   @Nullable AttributeSet attrs,
                   int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray
                a =
                getContext().obtainStyledAttributes(attrs, R.styleable.FabMenu);
        mPopupLayout =
                (ViewGroup) LayoutInflater.from(context)
                        .inflate(R.layout.fab_menu_layout, null);
        ListView listView = mPopupLayout.findViewById(R.id.fab_list_view);
        int menuRes = a.getResourceId(R.styleable.FabMenu_menu, -1);
        mMenuWindow =
                new PopupWindow(mPopupLayout,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, false);
        mMenuWindow.setTouchable(true);

        mEraser = new Paint();
        mEraser.setColor(Color.TRANSPARENT);
        mEraser.setAntiAlias(true);
        mEraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mArea = new Rect();
        a.recycle();
        Menu menu = new MenuBuilder(context);
        new MenuInflater(getContext()).inflate(menuRes, menu);
        MenuAdapter adapter = new MenuAdapter(getContext());
        listView.setAdapter(adapter);
        adapter.submitList(menu);
        mMenuWindow.setTouchInterceptor(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (contains(event)) {
                    //FabMenu.this.getMeasuredContentRect(mArea);
                    //event.setLocation(mArea.left, mArea.top);
                    return ((ViewGroup) FabMenu.this.getParent()).dispatchTouchEvent(event);
                }
                return false;
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mMenuWindow.isShowing()) {
                    showPopUp();

                } else
                    mMenuWindow.dismiss();
            }
        });
    }


    private boolean contains(MotionEvent event) {
        final int[] screenLocation = new int[2];
        getLocationOnScreen(screenLocation);
        return event.getX() >= screenLocation[0] &&
                event.getX() < screenLocation[0] + getWidth() &&
                event.getY() >= screenLocation[1] - getHeight() &&
                event.getY() < screenLocation[1];
    }

    private void showPopUp() {
        final int[] screenLocation = new int[2];
        getLocationOnScreen(screenLocation);
      /*  ((AdvancedFrameLayout) mPopupLayout).setReact(screenLocation[0] + getWidth() / 2,
                screenLocation[1], getWidth() / 2);*/
        mArea.set(getLeft(), getTop(), getRight(), getBottom());



        mPopupLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mPopupLayout.getViewTreeObserver().removeOnPreDrawListener(this);
                backgroud =
                        Bitmap.createBitmap(mPopupLayout.getWidth(),
                                mPopupLayout.getHeight(),
                                Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(backgroud);
                canvas.drawColor(Color.WHITE);
                canvas.drawCircle(screenLocation[0] + getWidth() / 2, screenLocation[1], getWidth() / 2,  mEraser);
                mPopupLayout.setBackground(new BitmapDrawable(getResources(), backgroud));
                return false;
            }
        });
        mMenuWindow.showAsDropDown(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    private static class MenuAdapter extends ArrayAdapter<MenuItem> {

        private Menu menu;

        public MenuAdapter(@NonNull Context context) {
            super(context, 0);
        }

        public void submitList(Menu menu) {
            this.menu = menu;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            MenuItem item = getItem(position);
            if (convertView == null)
                convertView =
                        LayoutInflater.from(getContext())
                                .inflate(R.layout.fab_menu_default_item_layout, parent, false);
            TextView title = convertView.findViewById(R.id.menu_item_title);
            //convertView.setLayoutDirection(LAYOUT_DIRECTION_RTL);
            FloatingActionButton fab = convertView.findViewById(R.id.menu_item_icon);
            title.setText(item.getTitle());
            fab.setImageDrawable(item.getIcon());
            return convertView;
        }

        @Override
        public int getCount() {
            return menu != null ? menu.size() : 0;
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getItemId();
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Nullable
        @Override
        public MenuItem getItem(int position) {
            return menu.getItem(position);
        }
    }
}
