package com.app.dragableview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.app.dragable_views.DraggableViewMain;
import com.app.dragable_views.OnViewSelectionListener;

public class MainActivity extends AppCompatActivity implements OnViewSelectionListener {

    private ImageView imgSourceOne, imgSourceTwo, imgSourceThree;
    private ViewGroup rlDestination, rlDestination2;
    private DraggableViewMain draggableViewMain;
    private DraggableViewMain draggableViewMain2;
    private ScrollView scrollView;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        draggableViewMain = new DraggableViewMain(this, rlDestination, scrollView);
        draggableViewMain.addView(imgSourceOne);
        draggableViewMain.addView(imgSourceTwo);
        draggableViewMain.addView(imgSourceThree);

        draggableViewMain2 = new DraggableViewMain(this, rlDestination2, scrollView);
        draggableViewMain2.addView(imgSourceOne);
        draggableViewMain2.addView(imgSourceTwo);
        draggableViewMain2.addView(imgSourceThree);
    }

    private void initView() {
        imgSourceOne = (ImageView) findViewById(R.id.imgSourceOne);
        imgSourceTwo = (ImageView) findViewById(R.id.imgSourceTwo);
        imgSourceThree = (ImageView) findViewById(R.id.imgSourceThree);

        scrollView = ((ScrollView) findViewById(R.id.scrollView));

        rlDestination = (FrameLayout) findViewById(R.id.rlDestination);
        rlDestination2 = (FrameLayout) findViewById(R.id.rlDestination2);
    }

    @Override
    public int viewSelectedPosition(int position) {

        if (position == 0) {
            //do after view one dragged
        } else if (position == 1) {
            //do after view two dragged
        } else if (position == 2) {
            //do after view three dragged
        }
        Toast.makeText(this, "dragged view position = " + position, Toast.LENGTH_SHORT).show();
        return position;
    }
}