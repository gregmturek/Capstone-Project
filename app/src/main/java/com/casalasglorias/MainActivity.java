package com.casalasglorias;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FloatingActionButton mFab;

    NavigationView mNavigationView;
    ImageView mNavHeaderImageImageView;
    TextView mNavHeaderNameTextView, mNavHeaderEmailTextView, mMainContentTextView;

    CardView mSaladsCardView, mEnchiladasCardView, mBurritosAndWrapsCardView, mFajitasAndSteaksCardView,
            mSpecialtiesCardView, mMariscosCardView, mCombinationsCardView, mChimichangasCardView,
            mQuesadillasCardView, mPepesPlattersCardView, mBurgersAndSandwichesCardView, mLunchCardView,
            mAppetizersCardView, mDipsForYourChipsCardView, mExtrasCardView, mBeveragesCardView,
            mDessertsCardView;
    TextView mSaladsTextView, mEnchiladasTextView, mBurritosAndWrapsTextView, mFajitasAndSteaksTextView,
            mSpecialtiesTextView, mMariscosTextView, mCombinationsTextView, mChimichangasTextView,
            mQuesadillasTextView, mPepesPlattersTextView, mBurgersAndSandwichesTextView, mLunchTextView,
            mAppetizersTextView, mDipsForYourChipsTextView, mExtrasTextView, mBeveragesTextView,
            mDessertsTextView;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 123;

    private static String mModifiedHomeWebView;
    private static boolean mCateringWebViewLoaded;

    RestaurantMenuData mRestaurantMenuData;

    private NetworkChangeReceiver mNetworkChangeReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mModifiedHomeWebView = null;
        mCateringWebViewLoaded = false;
        mRestaurantMenuData = null;

        mFab = findViewById(R.id.fab);
        mFab.setTag(true);
        mFab.setOnClickListener(view -> {
            if (mFab.getTag().equals(true)) {
                new SearchDialogFragment().show(getSupportFragmentManager(), "SearchDialog");
            } else {
                updateFab();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = findViewById(R.id.nav_view);
        View headerView = mNavigationView.getHeaderView(0);
        mNavHeaderImageImageView = headerView.findViewById(R.id.nav_header_image);
        mNavHeaderNameTextView = headerView.findViewById(R.id.nav_header_name);
        mNavHeaderEmailTextView = headerView.findViewById(R.id.nav_header_email);
        mMainContentTextView = findViewById(R.id.main_content_text_view);

        mSaladsCardView = findViewById(R.id.salads);
        mEnchiladasCardView = findViewById(R.id.enchiladas);
        mBurritosAndWrapsCardView = findViewById(R.id.burritos_and_wraps);
        mFajitasAndSteaksCardView = findViewById(R.id.fajitas_and_steaks);
        mSpecialtiesCardView = findViewById(R.id.specialties);
        mMariscosCardView = findViewById(R.id.mariscos);
        mCombinationsCardView = findViewById(R.id.combinations);
        mChimichangasCardView = findViewById(R.id.chimichangas);
        mQuesadillasCardView = findViewById(R.id.quesadillas);
        mPepesPlattersCardView = findViewById(R.id.pepes_platters);
        mBurgersAndSandwichesCardView = findViewById(R.id.burgers_and_sandwiches);
        mLunchCardView = findViewById(R.id.lunch);
        mAppetizersCardView = findViewById(R.id.appetizers);
        mDipsForYourChipsCardView = findViewById(R.id.dips_for_your_chips);
        mExtrasCardView = findViewById(R.id.extras);
        mBeveragesCardView = findViewById(R.id.beverages);
        mDessertsCardView = findViewById(R.id.desserts);

        mSaladsTextView = findViewById(R.id.salads_results);
        mEnchiladasTextView = findViewById(R.id.enchiladas_results);
        mBurritosAndWrapsTextView = findViewById(R.id.burritos_and_wraps_results);
        mFajitasAndSteaksTextView = findViewById(R.id.fajitas_and_steaks_results);
        mSpecialtiesTextView = findViewById(R.id.specialties_results);
        mMariscosTextView = findViewById(R.id.mariscos_results);
        mCombinationsTextView = findViewById(R.id.combinations_results);
        mChimichangasTextView = findViewById(R.id.chimichangas_results);
        mQuesadillasTextView = findViewById(R.id.quesadillas_results);
        mPepesPlattersTextView = findViewById(R.id.pepes_platters_results);
        mBurgersAndSandwichesTextView = findViewById(R.id.burgers_and_sandwiches_results);
        mLunchTextView = findViewById(R.id.lunch_results);
        mAppetizersTextView = findViewById(R.id.appetizers_results);
        mDipsForYourChipsTextView = findViewById(R.id.dips_for_your_chips_results);
        mExtrasTextView = findViewById(R.id.extras_results);
        mBeveragesTextView = findViewById(R.id.beverages_results);
        mDessertsTextView = findViewById(R.id.desserts_results);

        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.nav_home);
        mNavigationView.getMenu().performIdentifierAction(R.id.nav_home, 0);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            afterSignedIn(user);
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 mRestaurantMenuData = dataSnapshot.child("public").getValue(RestaurantMenuData.class);

                 if (getSelectedItemId() == R.id.nav_menu) {
                     mNavigationView.getMenu().performIdentifierAction(R.id.nav_menu, 0);
                 }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(LOG_TAG, "loadPost:onCancelled", databaseError.toException());
                mRestaurantMenuData = null;
            }
        });
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            isNetworkAvailable(context);
        }

        private void isNetworkAvailable(Context context) {
            ConnectivityManager connectivity = (ConnectivityManager)
                    context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo networkInfo = connectivity.getActiveNetworkInfo();
                if (networkInfo != null) {
                    if (networkInfo.isConnected()) {
                        mNavigationView.getMenu().performIdentifierAction(getSelectedItemId(), 0);
                    }
                }
            }
        }
    }

    private int getSelectedItemId() {
        Menu menu = mNavigationView.getMenu();

        for (int i = 0; i < menu.size(); i++) {
            MenuItem menuItem = menu.getItem(i);
            if (menuItem.isChecked()) {
                return menuItem.getItemId();
            }
        }

        return -1;
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        mNetworkChangeReceiver = new NetworkChangeReceiver();
        registerReceiver(mNetworkChangeReceiver, filter);
        mNetworkChangeReceiver.isNetworkAvailable(getApplicationContext());
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mNetworkChangeReceiver);
    }

    private void updateFab() {
        if (mFab.getTag().equals(true)) {
            mFab.setImageDrawable(getDrawable(R.drawable.ic_undo_white_24dp));
            mFab.setTag(false);

            mSaladsCardView.setVisibility(View.GONE);
            mEnchiladasCardView.setVisibility(View.GONE);
            mBurritosAndWrapsCardView.setVisibility(View.GONE);
            mFajitasAndSteaksCardView.setVisibility(View.GONE);
            mSpecialtiesCardView.setVisibility(View.GONE);
            mMariscosCardView.setVisibility(View.GONE);
            mCombinationsCardView.setVisibility(View.GONE);
            mChimichangasCardView.setVisibility(View.GONE);
            mQuesadillasCardView.setVisibility(View.GONE);
            mPepesPlattersCardView.setVisibility(View.GONE);
            mBurgersAndSandwichesCardView.setVisibility(View.GONE);
            mLunchCardView.setVisibility(View.GONE);
            mAppetizersCardView.setVisibility(View.GONE);
            mDipsForYourChipsCardView.setVisibility(View.GONE);
            mExtrasCardView.setVisibility(View.GONE);
            mBeveragesCardView.setVisibility(View.GONE);
            mDessertsCardView.setVisibility(View.GONE);
        } else {
            mFab.setImageDrawable(getDrawable(R.drawable.ic_search_white_24dp));
            mFab.setTag(true);

            mSaladsCardView.setVisibility(View.VISIBLE);
            mEnchiladasCardView.setVisibility(View.VISIBLE);
            mBurritosAndWrapsCardView.setVisibility(View.VISIBLE);
            mFajitasAndSteaksCardView.setVisibility(View.VISIBLE);
            mSpecialtiesCardView.setVisibility(View.VISIBLE);
            mMariscosCardView.setVisibility(View.VISIBLE);
            mCombinationsCardView.setVisibility(View.VISIBLE);
            mChimichangasCardView.setVisibility(View.VISIBLE);
            mQuesadillasCardView.setVisibility(View.VISIBLE);
            mPepesPlattersCardView.setVisibility(View.VISIBLE);
            mBurgersAndSandwichesCardView.setVisibility(View.VISIBLE);
            mLunchCardView.setVisibility(View.VISIBLE);
            mAppetizersCardView.setVisibility(View.VISIBLE);
            mDipsForYourChipsCardView.setVisibility(View.VISIBLE);
            mExtrasCardView.setVisibility(View.VISIBLE);
            mBeveragesCardView.setVisibility(View.VISIBLE);
            mDessertsCardView.setVisibility(View.VISIBLE);

            mSaladsTextView.setText(null);
            mSaladsTextView.setVisibility(View.GONE);
            mEnchiladasTextView.setText(null);
            mEnchiladasTextView.setVisibility(View.GONE);
            mBurritosAndWrapsTextView.setText(null);
            mBurritosAndWrapsTextView.setVisibility(View.GONE);
            mFajitasAndSteaksTextView.setText(null);
            mFajitasAndSteaksTextView.setVisibility(View.GONE);
            mSpecialtiesTextView.setText(null);
            mSpecialtiesTextView.setVisibility(View.GONE);
            mMariscosTextView.setText(null);
            mMariscosTextView.setVisibility(View.GONE);
            mCombinationsTextView.setText(null);
            mCombinationsTextView.setVisibility(View.GONE);
            mChimichangasTextView.setText(null);
            mChimichangasTextView.setVisibility(View.GONE);
            mQuesadillasTextView.setText(null);
            mQuesadillasTextView.setVisibility(View.GONE);
            mPepesPlattersTextView.setText(null);
            mPepesPlattersTextView.setVisibility(View.GONE);
            mBurgersAndSandwichesTextView.setText(null);
            mBurgersAndSandwichesTextView.setVisibility(View.GONE);
            mLunchTextView.setText(null);
            mLunchTextView.setVisibility(View.GONE);
            mAppetizersTextView.setText(null);
            mAppetizersTextView.setVisibility(View.GONE);
            mDipsForYourChipsTextView.setText(null);
            mDipsForYourChipsTextView.setVisibility(View.GONE);
            mExtrasTextView.setText(null);
            mExtrasTextView.setVisibility(View.GONE);
            mBeveragesTextView.setText(null);
            mBeveragesTextView.setVisibility(View.GONE);
            mDessertsTextView.setText(null);
            mDessertsTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            case R.id.action_dial:
                Intent dialIntent = new Intent(Intent.ACTION_DIAL);
                dialIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                dialIntent.setData(Uri.parse(getString(R.string.phone_number)));
                if (dialIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(dialIntent);
                }
                return true;
            case R.id.action_place:
                Uri mapUri = Uri.parse(getString(R.string.map_uri));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        if (id != R.id.nav_sign) {
            RelativeLayout mainContent = findViewById(R.id.main_content);
            NestedScrollView menuContent = findViewById(R.id.menu_content);
            FrameLayout cateringContent = findViewById(R.id.catering_content);
            ConstraintLayout findContent = findViewById(R.id.find_content);

            mainContent.setVisibility(View.INVISIBLE);
            menuContent.setVisibility(View.INVISIBLE);
            cateringContent.setVisibility(View.INVISIBLE);
            findContent.setVisibility(View.INVISIBLE);
            mFab.setVisibility(View.INVISIBLE);

            if (id == R.id.nav_home) {
                mainContent.setVisibility(View.VISIBLE);
                loadHomeWebView();
            } else if (id == R.id.nav_menu) {
                menuContent.setVisibility(View.VISIBLE);
                if (mRestaurantMenuData != null) {
                    mFab.setVisibility(View.VISIBLE);
                }
                loadMenuImages();
            } else if (id == R.id.nav_catering) {
                cateringContent.setVisibility(View.VISIBLE);
                loadCateringWebView();
            } else if (id == R.id.nav_find) {
                findContent.setVisibility(View.VISIBLE);
                tryToShowMap();
            }
        } else {
            if (mNavigationView.getMenu().findItem(R.id.nav_sign).getTitle()
                    .equals(getString(R.string.navigation_menu_sign_in))) {
                signIn();
            } else {
                signOut();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadHomeWebView(){
        WebView webView = findViewById(R.id.main_content_web_view);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.equals(getString(R.string.main_content_web_view_url))) {
                    view.setVisibility(View.INVISIBLE);
                    view.getSettings().setJavaScriptEnabled(true);
                    view.setWebChromeClient(new WebChromeClient() {
                        @Override
                        public void onProgressChanged(WebView view, int newProgress) {
                            ProgressBar progressBar = findViewById(R.id.main_content_progress_bar);
                            if (newProgress < 100) {
                                progressBar.setVisibility(View.VISIBLE);
                            } else {
                                progressBar.postDelayed(() -> {
                                    if (progressBar != null) {
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }, 500);
                            }
                        }
                    });
                }
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                if (url.equals(getString(R.string.main_content_web_view_url))) {
                    view.evaluateJavascript("javascript:(function() { " +
                            "document.getElementById('mobile-site-view')" +
                            ".style.paddingTop=\"0px\"; " +
                            "})()", value -> {});
                    view.evaluateJavascript("javascript:(function() { " +
                            "document.getElementsByClassName('clearfix mobile-menu')[0]" +
                            ".style.display=\"none\"; " +
                            "})()", value -> {});
                    view.evaluateJavascript("javascript:(function() { " +
                            "document.getElementById('wsb-element-53eb9b54-59e9-48af-94bc-1556b6a31c9f')" +
                            ".style.display=\"none\"; " +
                            "})()", value -> {});
                    view.evaluateJavascript("javascript:(function() { " +
                            "document.getElementsByClassName('view-as-desktop')[0]" +
                            ".style.display=\"none\"; " +
                            "})()", value -> {});

                    view.saveWebArchive(getBaseContext().getCacheDir().getPath() +
                                    File.separator +
                                    "home.mht",
                            false,
                            value -> mModifiedHomeWebView = value);

                    view.setWebChromeClient(null);
                    view.getSettings().setJavaScriptEnabled(false);
                    view.setVisibility(View.VISIBLE);
                } else if (url.endsWith(getString(R.string.html_content_unavailable))) {
                    mModifiedHomeWebView = null;
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                view.loadData(getString(R.string.html_content_unavailable), "text/html", null);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.equals(getString(R.string.main_content_web_view_url)) ||
                        url.equals("file://" + mModifiedHomeWebView)) {
                    view.loadUrl(request.getUrl().toString());
                }
                return true;
            }
        });

        if (mModifiedHomeWebView == null) {
            webView.loadUrl(getString(R.string.main_content_web_view_url));
        } else {
            String url = "file://" + mModifiedHomeWebView;
            webView.loadUrl(url);
        }
    }

    private void loadMenuImages() {
        Uri saladsUri = Uri.parse(getString(R.string.menu_content_salads_url));
        ImageView saladsImageView = findViewById(R.id.salads_image);
        GlideApp.with(this)
                .load(saladsUri)
                .into(saladsImageView);

        Uri enchiladasUri = Uri.parse(getString(R.string.menu_content_enchiladas_url));
        ImageView enchiladasImageView = findViewById(R.id.enchiladas_image);
        GlideApp.with(this)
                .load(enchiladasUri)
                .into(enchiladasImageView);

        Uri burritosAndWrapsUri = Uri.parse(getString(R.string.menu_content_burritos_and_wraps_url));
        ImageView burritosAndWrapsImageView = findViewById(R.id.burritos_and_wraps_image);
        GlideApp.with(this)
                .load(burritosAndWrapsUri)
                .into(burritosAndWrapsImageView);

        Uri fajitasAndSteaksUri = Uri.parse(getString(R.string.menu_content_fajitas_and_steaks_url));
        ImageView fajitasAndSteaksImageView = findViewById(R.id.fajitas_and_steaks_image);
        GlideApp.with(this)
                .load(fajitasAndSteaksUri)
                .into(fajitasAndSteaksImageView);

        Uri specialtiesUri = Uri.parse(getString(R.string.menu_content_specialties_url));
        ImageView specialtiesImageView = findViewById(R.id.specialties_image);
        GlideApp.with(this)
                .load(specialtiesUri)
                .into(specialtiesImageView);

        Uri mariscosUri = Uri.parse(getString(R.string.menu_content_mariscos_url));
        ImageView mariscosImageView = findViewById(R.id.mariscos_image);
        GlideApp.with(this)
                .load(mariscosUri)
                .into(mariscosImageView);

        Uri combinationsUri = Uri.parse(getString(R.string.menu_content_combinations_url));
        ImageView combinationsImageView = findViewById(R.id.combinations_image);
        GlideApp.with(this)
                .load(combinationsUri)
                .into(combinationsImageView);

        Uri chimichangasUri = Uri.parse(getString(R.string.menu_content_chimichangas_url));
        ImageView chimichangasImageView = findViewById(R.id.chimichangas_image);
        GlideApp.with(this)
                .load(chimichangasUri)
                .into(chimichangasImageView);

        Uri quesadillasUri = Uri.parse(getString(R.string.menu_content_quesadillas_url));
        ImageView quesadillasImageView = findViewById(R.id.quesadillas_image);
        GlideApp.with(this)
                .load(quesadillasUri)
                .into(quesadillasImageView);

        Uri pepesPlattersUri = Uri.parse(getString(R.string.menu_content_pepes_platters_url));
        ImageView pepesPlattersImageView = findViewById(R.id.pepes_platters_image);
        GlideApp.with(this)
                .load(pepesPlattersUri)
                .into(pepesPlattersImageView);

        Uri burgersAndSandwichesUri = Uri.parse(getString(R.string.menu_content_burgers_and_sandwiches_url));
        ImageView burgersAndSandwichesImageView = findViewById(R.id.burgers_and_sandwiches_image);
        GlideApp.with(this)
                .load(burgersAndSandwichesUri)
                .into(burgersAndSandwichesImageView);

        Uri lunchUri = Uri.parse(getString(R.string.menu_content_lunch_url));
        ImageView lunchImageView = findViewById(R.id.lunch_image);
        GlideApp.with(this)
                .load(lunchUri)
                .into(lunchImageView);

        Uri appetizersUri = Uri.parse(getString(R.string.menu_content_appetizers_url));
        ImageView appetizersImageView = findViewById(R.id.appetizers_image);
        GlideApp.with(this)
                .load(appetizersUri)
                .into(appetizersImageView);

        Uri dipsForYourChipsUri = Uri.parse(getString(R.string.menu_content_dips_for_your_chips_url));
        ImageView dipsForYourChipsImageView = findViewById(R.id.dips_for_your_chips_image);
        GlideApp.with(this)
                .load(dipsForYourChipsUri)
                .into(dipsForYourChipsImageView);

        Uri extrasUri = Uri.parse(getString(R.string.menu_content_extras_url));
        ImageView extrasImageView = findViewById(R.id.extras_image);
        GlideApp.with(this)
                .load(extrasUri)
                .into(extrasImageView);

        Uri beveragesUri = Uri.parse(getString(R.string.menu_content_beverages_url));
        ImageView beveragesImageView = findViewById(R.id.beverages_image);
        GlideApp.with(this)
                .load(beveragesUri)
                .into(beveragesImageView);

        Uri dessertsUri = Uri.parse(getString(R.string.menu_content_desserts_url));
        ImageView dessertsImageView = findViewById(R.id.desserts_image);
        GlideApp.with(this)
                .load(dessertsUri)
                .into(dessertsImageView);
    }

    private void loadCateringWebView() {
        if (mCateringWebViewLoaded) {
            return;
        }

        WebView webView = findViewById(R.id.catering_content_web_view);
        WebSettings webSettings = webView.getSettings();

        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setAppCachePath(getBaseContext().getCacheDir().getPath());
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setAppCacheEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (url.equals(getString(R.string.catering_content_web_view_url))) {
                    view.setWebChromeClient(new WebChromeClient() {
                        @Override
                        public void onProgressChanged(WebView view, int newProgress) {
                            ProgressBar progressBar = findViewById(R.id.catering_content_progress_bar);
                            if (newProgress < 100) {
                                progressBar.setVisibility(View.VISIBLE);

                            } else {
                                progressBar.postDelayed(() -> progressBar.setVisibility(View.GONE), 500);
                            }
                        }
                    });
                }
            }

            @Override
            public void onPageFinished(WebView view, String url)
            {
                if (url.equals(getString(R.string.catering_content_web_view_url))) {
                    mCateringWebViewLoaded = true;
                    view.setWebChromeClient(null);
                } else if (url.endsWith(getString(R.string.html_content_unavailable))) {
                    mCateringWebViewLoaded = false;
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                WebSettings webSettings1 = view.getSettings();
                webSettings1.setSupportZoom(false);
                webSettings1.setBuiltInZoomControls(false);
                webSettings1.setUseWideViewPort(false);
                webSettings1.setLoadWithOverviewMode(false);

                view.setInitialScale(0);
                view.loadData(getString(R.string.html_content_unavailable), "text/html", null);
            }
        });

        webView.setInitialScale(1);
        webView.loadUrl(getString(R.string.catering_content_web_view_url));
    }

    public void tryToShowMap(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    showRationaleAndRequestPermission();
                } else {
                    requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            123);
                }
            } else {
                startMap(true);
            }
        } else {
            startMap(true);
        }
    }

    @SuppressLint("NewApi")
    private void showRationaleAndRequestPermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.dialog_permission_title)
                .setMessage(R.string.dialog_permission_message)
                .setPositiveButton(R.string.button_ok, (dialog, id) -> requestPermissions(
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 123))
                .create()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 123) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startMap(true);
            } else {
                startMap(false);
            }
        }
    }

    private void startMap(boolean locationPermission) {
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        FusedLocationProviderClient fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(this);
        mapFragment.getMapAsync(googleMap -> {
            googleMap.clear();
            if (locationPermission) {
                @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.
                        getLastLocation();
                task.addOnCompleteListener(task1 -> {
                    if (task.isSuccessful()) {
                        Location location = task.getResult();
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        Marker currentMarker = googleMap.addMarker(new MarkerOptions().position(latLng));
                        currentMarker.setTitle(getString(R.string.marker_current_title));
                        finishMap(googleMap, currentMarker);
                    }
                });
            } else {
                finishMap(googleMap, null);
            }
        });
    }

    private void finishMap(GoogleMap googleMap, Marker currentMarker) {
        Bitmap bitmap = BitmapFactory.decodeResource(MainActivity.this.getResources(),
                R.drawable.casalasglorias_pin);
        bitmap.setHasAlpha(true);
        bitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, true);
        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);

        LatLng latLng = new LatLng(42.0342642, -91.6740979);
        Marker restaurantMarker = googleMap.addMarker(new MarkerOptions().position(latLng)
                .icon(bitmapDescriptor));
        restaurantMarker.setAnchor(0.5f, 1f);

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(restaurantMarker.getPosition());
        if (currentMarker != null) {
            builder.include(currentMarker.getPosition());
        }
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(builder.build(), 192);
        googleMap.moveCamera(cameraUpdate);
    }

    private void signIn() {
        List<AuthUI.IdpConfig> providers = Collections.singletonList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    private void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(task -> afterSignedOut());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    afterSignedIn(user);
                } else {
                    // Proceed as if sign-in failed
                    signOut();
                    showSnackbar(R.string.sign_in_failure);
                }
            } else {
                // Sign-in failed

                Log.e(LOG_TAG, "Sign-in error: ", response != null ? response.getError() : null);

                if (response != null) {
                    if (response.getError() != null) {
                        if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                            showSnackbar(R.string.no_internet_connection);
                            return;
                        }
                    }
                }

                showSnackbar(R.string.sign_in_failure);
            }
        }
    }

    private void showSnackbar(int resId) {
        Snackbar.make(getWindow().getDecorView().getRootView(), resId, Snackbar.LENGTH_LONG).show();
    }

    private void showSnackbar(View view, int resId) {
        Snackbar.make(view, resId, Snackbar.LENGTH_LONG).show();
    }

    private void afterSignedIn(FirebaseUser user) {
        mNavigationView.getMenu().findItem(R.id.nav_sign).setTitle(R.string.navigation_menu_sign_out);

        Uri uri = user.getPhotoUrl();
        GlideApp.with(this)
                .load(uri)
                .error(R.drawable.ic_account_circle_black_24dp)
                .into(mNavHeaderImageImageView);

        mNavHeaderNameTextView.setText(user.getDisplayName());
        mNavHeaderEmailTextView.setText(user.getEmail());

        String formattedString = String.format(getString(R.string.main_content_text_view_format),
                user.getDisplayName());
        mMainContentTextView.setText(formattedString);
    }

    private void afterSignedOut() {
        mNavigationView.getMenu().findItem(R.id.nav_sign).setTitle(R.string.navigation_menu_sign_in);

        mNavHeaderImageImageView.setImageDrawable(getDrawable(R.drawable.ic_account_circle_black_24dp));

        mNavHeaderNameTextView.setText(getString(R.string.nav_header_name));
        mNavHeaderEmailTextView.setText(getString(R.string.nav_header_email));

        mMainContentTextView.setText(getString(R.string.main_content_text_view));
    }

    public static class SearchDialogFragment extends DialogFragment {
        @SuppressLint("InflateParams")
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Activity activity = requireActivity();
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);

            LayoutInflater layoutInflater = activity.getLayoutInflater();
            View view = layoutInflater.inflate(R.layout.dialog_search, null);

            CheckBox box0 = view.findViewById(R.id.box_0);
            CheckBox box1 = view.findViewById(R.id.box_1);
            CheckBox box2 = view.findViewById(R.id.box_2);
            CheckBox box3 = view.findViewById(R.id.box_3);
            CheckBox box4 = view.findViewById(R.id.box_4);
            CheckBox box5 = view.findViewById(R.id.box_5);
            CheckBox box6 = view.findViewById(R.id.box_6);
            CheckBox box7 = view.findViewById(R.id.box_7);
            CheckBox box8 = view.findViewById(R.id.box_8);
            CheckBox box9 = view.findViewById(R.id.box_9);
            CheckBox box10 = view.findViewById(R.id.box_10);
            CheckBox box11 = view.findViewById(R.id.box_11);
            CheckBox box12 = view.findViewById(R.id.box_12);
            CheckBox box13 = view.findViewById(R.id.box_13);
            CheckBox box14 = view.findViewById(R.id.box_14);
            CheckBox box15 = view.findViewById(R.id.box_15);
            CheckBox box16 = view.findViewById(R.id.box_16);
            CheckBox box17 = view.findViewById(R.id.box_17);

            box0.setOnCheckedChangeListener((buttonView, isChecked) -> {
                box1.setChecked(isChecked);
                box2.setChecked(isChecked);
                box3.setChecked(isChecked);
                box4.setChecked(isChecked);
                box5.setChecked(isChecked);
                box6.setChecked(isChecked);
                box7.setChecked(isChecked);
                box8.setChecked(isChecked);
                box9.setChecked(isChecked);
                box10.setChecked(isChecked);
                box11.setChecked(isChecked);
                box12.setChecked(isChecked);
                box13.setChecked(isChecked);
                box14.setChecked(isChecked);
                box15.setChecked(isChecked);
                box16.setChecked(isChecked);
                box17.setChecked(isChecked);
            });

            box0.setText(((MainActivity)activity).mRestaurantMenuData.boxLabels.get(0));
            box1.setText(((MainActivity)activity).mRestaurantMenuData.boxLabels.get(1));
            box2.setText(((MainActivity)activity).mRestaurantMenuData.boxLabels.get(2));
            box3.setText(((MainActivity)activity).mRestaurantMenuData.boxLabels.get(3));
            box4.setText(((MainActivity)activity).mRestaurantMenuData.boxLabels.get(4));
            box5.setText(((MainActivity)activity).mRestaurantMenuData.boxLabels.get(5));
            box6.setText(((MainActivity)activity).mRestaurantMenuData.boxLabels.get(6));
            box7.setText(((MainActivity)activity).mRestaurantMenuData.boxLabels.get(7));
            box8.setText(((MainActivity)activity).mRestaurantMenuData.boxLabels.get(8));
            box9.setText(((MainActivity)activity).mRestaurantMenuData.boxLabels.get(9));
            box10.setText(((MainActivity)activity).mRestaurantMenuData.boxLabels.get(10));
            box11.setText(((MainActivity)activity).mRestaurantMenuData.boxLabels.get(11));
            box12.setText(((MainActivity)activity).mRestaurantMenuData.boxLabels.get(12));
            box13.setText(((MainActivity)activity).mRestaurantMenuData.boxLabels.get(13));
            box14.setText(((MainActivity)activity).mRestaurantMenuData.boxLabels.get(14));
            box15.setText(((MainActivity)activity).mRestaurantMenuData.boxLabels.get(15));
            box16.setText(((MainActivity)activity).mRestaurantMenuData.boxLabels.get(16));
            box17.setText(((MainActivity)activity).mRestaurantMenuData.boxLabels.get(17));

            builder.setView(view)
                    .setTitle(getResources().getString(R.string.dialog_search_title))
                    .setPositiveButton(R.string.button_search, null)
                    .setNegativeButton(R.string.button_cancel, null);

            final AlertDialog alertDialog = builder.create();

            alertDialog.setOnShowListener(dialog -> {
                Button positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(v -> {
                    String keyword = ((EditText) SearchDialogFragment.this.getDialog()
                            .findViewById(R.id.edit_text_keyword))
                            .getText().toString();

                    boolean[] boxes = new boolean[18];
                    boxes[0] = ((CheckBox) SearchDialogFragment.this.getDialog().findViewById(R.id.box_0)).isChecked();
                    boxes[1] = ((CheckBox) SearchDialogFragment.this.getDialog().findViewById(R.id.box_1)).isChecked();
                    boxes[2] = ((CheckBox) SearchDialogFragment.this.getDialog().findViewById(R.id.box_2)).isChecked();
                    boxes[3] = ((CheckBox) SearchDialogFragment.this.getDialog().findViewById(R.id.box_3)).isChecked();
                    boxes[4] = ((CheckBox) SearchDialogFragment.this.getDialog().findViewById(R.id.box_4)).isChecked();
                    boxes[5] = ((CheckBox) SearchDialogFragment.this.getDialog().findViewById(R.id.box_5)).isChecked();
                    boxes[6] = ((CheckBox) SearchDialogFragment.this.getDialog().findViewById(R.id.box_6)).isChecked();
                    boxes[7] = ((CheckBox) SearchDialogFragment.this.getDialog().findViewById(R.id.box_7)).isChecked();
                    boxes[8] = ((CheckBox) SearchDialogFragment.this.getDialog().findViewById(R.id.box_8)).isChecked();
                    boxes[9] = ((CheckBox) SearchDialogFragment.this.getDialog().findViewById(R.id.box_9)).isChecked();
                    boxes[10] = ((CheckBox) SearchDialogFragment.this.getDialog().findViewById(R.id.box_10)).isChecked();
                    boxes[11] = ((CheckBox) SearchDialogFragment.this.getDialog().findViewById(R.id.box_11)).isChecked();
                    boxes[12] = ((CheckBox) SearchDialogFragment.this.getDialog().findViewById(R.id.box_12)).isChecked();
                    boxes[13] = ((CheckBox) SearchDialogFragment.this.getDialog().findViewById(R.id.box_13)).isChecked();
                    boxes[14] = ((CheckBox) SearchDialogFragment.this.getDialog().findViewById(R.id.box_14)).isChecked();
                    boxes[15] = ((CheckBox) SearchDialogFragment.this.getDialog().findViewById(R.id.box_15)).isChecked();
                    boxes[16] = ((CheckBox) SearchDialogFragment.this.getDialog().findViewById(R.id.box_16)).isChecked();
                    boxes[17] = ((CheckBox) SearchDialogFragment.this.getDialog().findViewById(R.id.box_17)).isChecked();

                    keyword = keyword.trim();

                    if (keyword.isEmpty()) {
                        ((MainActivity) activity).showSnackbar(v, R.string.dialog_search_error_no_keyword);
                    } else if (!keyword.matches("^[a-zA-Z0-9áéíóúÁÉÍÓÚüÜñÑ'~.,$&!/#\\-\\s]+$")) {
                        ((MainActivity) activity).showSnackbar(v, R.string.dialog_search_error_invalid_keyword);
                    } else if (!Arrays.toString(boxes).contains("t")) {
                        ((MainActivity) activity).showSnackbar(v, R.string.dialog_search_error_no_category);
                    } else {
                        ((MainActivity) activity).updateFab();
                        new MenuDataSearch(activity, keyword, boxes).execute();
                        dialog.dismiss();
                    }

                });
                Button negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                negativeButton.setOnClickListener(v -> dialog.dismiss());
            });

            return alertDialog;
        }
    }

    private static class MenuDataSearch extends AsyncTask<Void, Void, Void> {
        private final WeakReference<Activity> mActivityReference;
        String mKeyword;
        boolean[] mBoxes;

        String mSaladsText = "";
        String mEnchiladasText = "";
        String mBurritosAndWrapsText = "";
        String mFajitasAndSteaksText = "";
        String mSpecialtiesText = "";
        String mMariscosText = "";
        String mCombinationsText = "";
        String mChimichangasText = "";
        String mQuesadillasText = "";
        String mPepesPlattersText = "";
        String mBurgersAndSandwichesText = "";
        String mLunchText = "";
        String mAppetizersText = "";
        String mDipsForYourChipsText = "";
        String mExtrasText = "";
        String mBeveragesText = "";
        String mDessertsText = "";

        MenuDataSearch(Activity activity, String keyword, boolean[] boxes){
            mActivityReference = new WeakReference<>(activity);
            mKeyword = keyword;
            mBoxes = boxes;
        }

        @Override
        protected Void doInBackground(Void... Void) {
            Map<String, String> categories = ((MainActivity)mActivityReference.get()).mRestaurantMenuData.categories;

            mKeyword = prepareToCompare(mKeyword);

            for (int i = 1; i <= 17; i++) {
                if (!mBoxes[i]) {
                    continue;
                }

                StringBuilder tempText = new StringBuilder();

                String categoryTitle = ((MainActivity)mActivityReference.get())
                        .mRestaurantMenuData.boxLabels.get(i);
                String categoryDescription = Objects.toString(categories.get(categoryTitle), "");
                if (prepareToCompare(categoryTitle).contains(mKeyword) ||
                        prepareToCompare(categoryDescription).contains(mKeyword)) {
                    tempText.append("\n").append(mActivityReference.get()
                            .getString(R.string.menu_content_all_of_the_above));
                }

                if (tempText.length() == 0) {
                    Map<String, String> tempMap = Collections.emptyMap();
                    switch (i) {
                        case 1:
                            tempMap = ((MainActivity)mActivityReference
                                    .get()).mRestaurantMenuData.salads;
                            break;
                        case 2:
                            tempMap = ((MainActivity)mActivityReference
                                    .get()).mRestaurantMenuData.enchiladas;
                            break;
                        case 3:
                            tempMap = ((MainActivity)mActivityReference
                                    .get()).mRestaurantMenuData.burritosAndWraps;
                            break;
                        case 4:
                            tempMap = ((MainActivity)mActivityReference
                                    .get()).mRestaurantMenuData.fajitasAndSteaks;
                            break;
                        case 5:
                            tempMap = ((MainActivity)mActivityReference
                                    .get()).mRestaurantMenuData.specialties;
                            break;
                        case 6:
                            tempMap = ((MainActivity)mActivityReference
                                    .get()).mRestaurantMenuData.mariscos;
                            break;
                        case 7:
                            tempMap = ((MainActivity)mActivityReference
                                    .get()).mRestaurantMenuData.combinations;
                            break;
                        case 8:
                            tempMap = ((MainActivity)mActivityReference
                                    .get()).mRestaurantMenuData.chimichangas;
                            break;
                        case 9:
                            tempMap = ((MainActivity)mActivityReference
                                    .get()).mRestaurantMenuData.quesadillas;
                            break;
                        case 10:
                            tempMap = ((MainActivity)mActivityReference
                                    .get()).mRestaurantMenuData.pepesPlatters;
                            break;
                        case 11:
                            tempMap = ((MainActivity)mActivityReference
                                    .get()).mRestaurantMenuData.burgersAndSandwiches;
                            break;
                        case 12:
                            tempMap = ((MainActivity)mActivityReference
                                    .get()).mRestaurantMenuData.lunch;
                            break;
                        case 13:
                            tempMap = ((MainActivity)mActivityReference
                                    .get()).mRestaurantMenuData.appetizers;
                            break;
                        case 14:
                            tempMap = ((MainActivity)mActivityReference
                                    .get()).mRestaurantMenuData.dipsForYourChips;
                            break;
                        case 15:
                            tempMap = ((MainActivity)mActivityReference
                                    .get()).mRestaurantMenuData.extras;
                            break;
                        case 16:
                            tempMap = ((MainActivity)mActivityReference
                                    .get()).mRestaurantMenuData.beverages;
                            break;
                        case 17:
                            tempMap = ((MainActivity)mActivityReference
                                    .get()).mRestaurantMenuData.desserts;
                            break;
                    }

                    for (Map.Entry<String, String> entry : tempMap.entrySet()) {
                        boolean match = false;
                        if (prepareToCompare(entry.getKey()).contains(mKeyword)) {
                            match = true;
                        } else if (prepareToCompare(entry.getValue()).contains(mKeyword)) {
                            match = true;
                        }
                        if (match) {
                            tempText.append("\n").append(entry.getKey());
                        }
                    }
                }

                if (tempText.length() != 0) {
                    switch (i) {
                        case 1:
                            mSaladsText = convertToDisplay(tempText.toString());
                            break;
                        case 2:
                            mEnchiladasText = convertToDisplay(tempText.toString());
                            break;
                        case 3:
                            mBurritosAndWrapsText = convertToDisplay(tempText.toString());
                            break;
                        case 4:
                            mFajitasAndSteaksText = convertToDisplay(tempText.toString());
                            break;
                        case 5:
                            mSpecialtiesText = convertToDisplay(tempText.toString());
                            break;
                        case 6:
                            mMariscosText = convertToDisplay(tempText.toString());
                            break;
                        case 7:
                            mCombinationsText = convertToDisplay(tempText.toString());
                            break;
                        case 8:
                            mChimichangasText = convertToDisplay(tempText.toString());
                            break;
                        case 9:
                            mQuesadillasText = convertToDisplay(tempText.toString());
                            break;
                        case 10:
                            mPepesPlattersText = convertToDisplay(tempText.toString());
                            break;
                        case 11:
                            mBurgersAndSandwichesText = convertToDisplay(tempText.toString());
                            break;
                        case 12:
                            mLunchText = convertToDisplay(tempText.toString());
                            break;
                        case 13:
                            mAppetizersText = convertToDisplay(tempText.toString());
                            break;
                        case 14:
                            mDipsForYourChipsText = convertToDisplay(tempText.toString());
                            break;
                        case 15:
                            mExtrasText = convertToDisplay(tempText.toString());
                            break;
                        case 16:
                            mBeveragesText = convertToDisplay(tempText.toString());
                            break;
                        case 17:
                            mDessertsText = convertToDisplay(tempText.toString());
                            break;
                    }
                }
            }

            return null;
        }

        private String prepareToCompare(String string) {
            return Normalizer
                    .normalize(convertToDisplay(string), Normalizer.Form.NFD)
                    .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                    .toLowerCase();
        }

        private String convertToDisplay(String string) {
            return string
                    .replaceAll("__", "/")
                    .replaceAll("_", "#")
                    .toUpperCase();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (!mSaladsText.isEmpty()) {
                ((MainActivity)mActivityReference.get()).mSaladsCardView.setVisibility(View.VISIBLE);
                ((MainActivity)mActivityReference.get()).mSaladsTextView.setText(mSaladsText);
                ((MainActivity)mActivityReference.get()).mSaladsTextView.setVisibility(View.VISIBLE);
            }

            if (!mEnchiladasText.isEmpty()) {
                ((MainActivity)mActivityReference.get()).mEnchiladasCardView.setVisibility(View.VISIBLE);
                ((MainActivity)mActivityReference.get()).mEnchiladasTextView.setText(mEnchiladasText);
                ((MainActivity)mActivityReference.get()).mEnchiladasTextView.setVisibility(View.VISIBLE);
            }

            if (!mBurritosAndWrapsText.isEmpty()) {
                ((MainActivity)mActivityReference.get()).mBurritosAndWrapsCardView.setVisibility(View.VISIBLE);
                ((MainActivity)mActivityReference.get()).mBurritosAndWrapsTextView.setText(mBurritosAndWrapsText);
                ((MainActivity)mActivityReference.get()).mBurritosAndWrapsTextView.setVisibility(View.VISIBLE);
            }

            if (!mFajitasAndSteaksText.isEmpty()) {
                ((MainActivity)mActivityReference.get()).mFajitasAndSteaksCardView.setVisibility(View.VISIBLE);
                ((MainActivity)mActivityReference.get()).mFajitasAndSteaksTextView.setText(mFajitasAndSteaksText);
                ((MainActivity)mActivityReference.get()).mFajitasAndSteaksTextView.setVisibility(View.VISIBLE);
            }

            if (!mSpecialtiesText.isEmpty()) {
                ((MainActivity)mActivityReference.get()).mSpecialtiesCardView.setVisibility(View.VISIBLE);
                ((MainActivity)mActivityReference.get()).mSpecialtiesTextView.setText(mSpecialtiesText);
                ((MainActivity)mActivityReference.get()).mSpecialtiesTextView.setVisibility(View.VISIBLE);
            }

            if (!mMariscosText.isEmpty()) {
                ((MainActivity)mActivityReference.get()).mMariscosCardView.setVisibility(View.VISIBLE);
                ((MainActivity)mActivityReference.get()).mMariscosTextView.setText(mMariscosText);
                ((MainActivity)mActivityReference.get()).mMariscosTextView.setVisibility(View.VISIBLE);
            }

            if (!mCombinationsText.isEmpty()) {
                ((MainActivity)mActivityReference.get()).mCombinationsCardView.setVisibility(View.VISIBLE);
                ((MainActivity)mActivityReference.get()).mCombinationsTextView.setText(mCombinationsText);
                ((MainActivity)mActivityReference.get()).mCombinationsTextView.setVisibility(View.VISIBLE);
            }

            if (!mChimichangasText.isEmpty()) {
                ((MainActivity)mActivityReference.get()).mChimichangasCardView.setVisibility(View.VISIBLE);
                ((MainActivity)mActivityReference.get()).mChimichangasTextView.setText(mChimichangasText);
                ((MainActivity)mActivityReference.get()).mChimichangasTextView.setVisibility(View.VISIBLE);
            }

            if (!mQuesadillasText.isEmpty()) {
                ((MainActivity)mActivityReference.get()).mQuesadillasCardView.setVisibility(View.VISIBLE);
                ((MainActivity)mActivityReference.get()).mQuesadillasTextView.setText(mQuesadillasText);
                ((MainActivity)mActivityReference.get()).mQuesadillasTextView.setVisibility(View.VISIBLE);
            }

            if (!mPepesPlattersText.isEmpty()) {
                ((MainActivity)mActivityReference.get()).mPepesPlattersCardView.setVisibility(View.VISIBLE);
                ((MainActivity)mActivityReference.get()).mPepesPlattersTextView.setText(mPepesPlattersText);
                ((MainActivity)mActivityReference.get()).mPepesPlattersTextView.setVisibility(View.VISIBLE);
            }

            if (!mBurgersAndSandwichesText.isEmpty()) {
                ((MainActivity)mActivityReference.get()).mBurgersAndSandwichesCardView.setVisibility(View.VISIBLE);
                ((MainActivity)mActivityReference.get()).mBurgersAndSandwichesTextView.setText(mBurgersAndSandwichesText);
                ((MainActivity)mActivityReference.get()).mBurgersAndSandwichesTextView.setVisibility(View.VISIBLE);
            }

            if (!mLunchText.isEmpty()) {
                ((MainActivity)mActivityReference.get()).mLunchCardView.setVisibility(View.VISIBLE);
                ((MainActivity)mActivityReference.get()).mLunchTextView.setText(mLunchText);
                ((MainActivity)mActivityReference.get()).mLunchTextView.setVisibility(View.VISIBLE);
            }

            if (!mAppetizersText.isEmpty()) {
                ((MainActivity)mActivityReference.get()).mAppetizersCardView.setVisibility(View.VISIBLE);
                ((MainActivity)mActivityReference.get()).mAppetizersTextView.setText(mAppetizersText);
                ((MainActivity)mActivityReference.get()).mAppetizersTextView.setVisibility(View.VISIBLE);
            }

            if (!mDipsForYourChipsText.isEmpty()) {
                ((MainActivity)mActivityReference.get()).mDipsForYourChipsCardView.setVisibility(View.VISIBLE);
                ((MainActivity)mActivityReference.get()).mDipsForYourChipsTextView.setText(mDipsForYourChipsText);
                ((MainActivity)mActivityReference.get()).mDipsForYourChipsTextView.setVisibility(View.VISIBLE);
            }

            if (!mExtrasText.isEmpty()) {
                ((MainActivity)mActivityReference.get()).mExtrasCardView.setVisibility(View.VISIBLE);
                ((MainActivity)mActivityReference.get()).mExtrasTextView.setText(mExtrasText);
                ((MainActivity)mActivityReference.get()).mExtrasTextView.setVisibility(View.VISIBLE);
            }

            if (!mBeveragesText.isEmpty()) {
                ((MainActivity)mActivityReference.get()).mBeveragesCardView.setVisibility(View.VISIBLE);
                ((MainActivity)mActivityReference.get()).mBeveragesTextView.setText(mBeveragesText);
                ((MainActivity)mActivityReference.get()).mBeveragesTextView.setVisibility(View.VISIBLE);
            }

            if (!mDessertsText.isEmpty()) {
                ((MainActivity)mActivityReference.get()).mDessertsCardView.setVisibility(View.VISIBLE);
                ((MainActivity)mActivityReference.get()).mDessertsTextView.setText(mDessertsText);
                ((MainActivity)mActivityReference.get()).mDessertsTextView.setVisibility(View.VISIBLE);
            }
        }
    }

}
