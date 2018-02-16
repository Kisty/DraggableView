package com.app.dragableview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.app.dragable_views.AutoScrollDragHelper;
import com.app.dragable_views.DraggableViewMain;
import com.app.dragable_views.OnViewSelectionListener;

import java.util.Arrays;
import java.util.List;

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
        List<ViewGroup> targets = Arrays.asList(
                rlDestination,
                rlDestination2
        );
        draggableViewMain = new DraggableViewMain(this, targets);
        draggableViewMain.addView(imgSourceOne);
        draggableViewMain.addView(imgSourceTwo);
        draggableViewMain.addView(imgSourceThree);

        AutoScrollDragHelper autoScrollDragHelper = new AutoScrollDragHelper(scrollView);
        autoScrollDragHelper.start();
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