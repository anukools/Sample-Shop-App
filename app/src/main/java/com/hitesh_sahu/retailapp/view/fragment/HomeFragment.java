package com.hitesh_sahu.retailapp.view.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.Toast;

import com.hitesh_sahu.retailapp.Jiny.AppUtils;
import com.hitesh_sahu.retailapp.Jiny.BusEvents;
import com.hitesh_sahu.retailapp.Jiny.PointerService;
import com.hitesh_sahu.retailapp.Jiny.UIViewsHandler;
import com.hitesh_sahu.retailapp.R;
import com.hitesh_sahu.retailapp.domain.api.ProductCategoryLoaderTask;
import com.hitesh_sahu.retailapp.util.Utils;
import com.hitesh_sahu.retailapp.util.Utils.AnimationType;
import com.hitesh_sahu.retailapp.view.activities.ECartHomeActivity;
import com.twotoasters.jazzylistview.recyclerview.JazzyRecyclerViewScrollListener;

@SuppressLint("ResourceAsColor")
public class HomeFragment extends Fragment implements AppBarLayout.OnOffsetChangedListener {
    CollapsingToolbarLayout collapsingToolbar;
    RecyclerView recyclerView;
    int mutedColor = R.attr.colorPrimary;
    //CategoryListAdapter simpleRecyclerAdapter;

    /**
     * The double back to exit pressed once.
     */
    private boolean doubleBackToExitPressedOnce;

    /**
     * The m handler.
     */
    private Handler mHandler = new Handler();

    /**
     * The m runnable.
     */
    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };

    View homeView;
    FloatingActionButton searchFabButton;
    private AppBarLayout appBarLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        homeView = inflater.inflate(R.layout.frag_product_category, container, false);

        searchFabButton = (FloatingActionButton) homeView.findViewById(R.id.search_item);
        appBarLayout = (AppBarLayout) homeView.findViewById(R.id.appbar);
        appBarLayout.addOnOffsetChangedListener(this);
        
        
        searchFabButton.setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Utils.switchFragmentWithAnimation(R.id.frag_container,
                                new SearchProductFragment(),
                                ((ECartHomeActivity) getActivity()), null,
                                AnimationType.SLIDE_UP);

                    }
                });

        final Toolbar toolbar = (Toolbar) homeView.findViewById(R.id.anim_toolbar);
        ((ECartHomeActivity) getActivity()).setSupportActionBar(toolbar);
        ((ECartHomeActivity) getActivity()).getSupportActionBar()
                .setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationIcon(R.drawable.ic_drawer);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ECartHomeActivity) getActivity()).getmDrawerLayout()
                        .openDrawer(GravityCompat.START);
            }
        });

        collapsingToolbar = (CollapsingToolbarLayout) homeView
                .findViewById(R.id.collapsing_toolbar);

        collapsingToolbar.setTitle("Categories");

        ImageView header = (ImageView) homeView.findViewById(R.id.header);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.header);

        Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
            @SuppressWarnings("ResourceType")
            @Override
            public void onGenerated(Palette palette) {

                mutedColor = palette.getMutedColor(R.color.primary_500);
                collapsingToolbar.setContentScrimColor(mutedColor);
                collapsingToolbar.setStatusBarScrimColor(R.color.black_trans80);
            }
        });

        recyclerView = (RecyclerView) homeView.findViewById(R.id.scrollableview);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        JazzyRecyclerViewScrollListener jazzyScrollListener = new JazzyRecyclerViewScrollListener();
        recyclerView.setOnScrollListener(jazzyScrollListener);

        jazzyScrollListener.setTransitionEffect(4);


        new ProductCategoryLoaderTask(recyclerView, getActivity()).execute();

//
//		if (simpleRecyclerAdapter == null) {
//			simpleRecyclerAdapter = new CategoryListAdapter(getActivity());
//			recyclerView.setAdapter(simpleRecyclerAdapter);
//
//			simpleRecyclerAdapter
//					.SetOnItemClickListener(new OnItemClickListener() {
//
//						@Override
//						public void onItemClick(View homeView, int position) {
//
//							if (position == 0) {
//								GlobaDataHolder.getGlobaDataHolder()
//										.getAllElectronics();
//							} else if (position == 1) {
//								GlobaDataHolder.getGlobaDataHolder()
//										.getAllFurnitures();
//							}
//							Utils.switchFragmentWithAnimation(
//									R.id.frag_container,
//									new ProductOverviewFragment(),
//									((ECartHomeActivity) getActivity()), null,
//									AnimationType.SLIDE_LEFT);
//
//						}
//					});
//		}

        homeView.setFocusableInTouchMode(true);
        homeView.requestFocus();
        homeView.setOnKeyListener(new View.OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP
                        && keyCode == KeyEvent.KEYCODE_BACK) {

                    if (doubleBackToExitPressedOnce) {
                        // super.onBackPressed();

                        if (mHandler != null) {
                            mHandler.removeCallbacks(mRunnable);
                        }

                        getActivity().finish();

                        return true;
                    }

                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(getActivity(),
                            "Please click BACK again to exit",
                            Toast.LENGTH_SHORT).show();

                    mHandler.postDelayed(mRunnable, 2000);

                }
                return true;
            }
        });

        return homeView;

    }

        /*  --------------------  CODE for Jiny --------------------------------------------*/

    ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            // Layout has happened here.
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    UIViewsHandler.sendViewEvent(getActivity(), searchFabButton);
                }
            }, 500);

            // Don't forget to remove your listener when you are done with it.
            searchFabButton.getViewTreeObserver().removeOnGlobalLayoutListener(this);
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        Log.e("Pointer : ", "OnStart");
        searchFabButton.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);

    }

    @Override
    public void onStop() {
        super.onStop();

        // hide when the rootview changed
        PointerService.bus.post(new BusEvents.HideEvent());
    }


    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if(searchFabButton.getVisibility() == View.VISIBLE && searchFabButton.getX() != 0) {
//            UIViewsHandler.sendViewAnimateEvent(getActivity(), searchFabButton);
        }else {
            BusEvents.HideEvent hideEvent = new BusEvents.HideEvent();
            hideEvent.setHideJinyIcon(false);
            PointerService.bus.post(hideEvent);
        }
    }
}
