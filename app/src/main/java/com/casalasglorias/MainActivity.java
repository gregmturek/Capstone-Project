package com.casalasglorias;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    NavigationView mNavigationView;
    ImageView mNavHeaderImageImageView;
    TextView mNavHeaderNameTextView, mNavHeaderEmailTextView, mMainContentTextView;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SearchDialogFragment().show(getSupportFragmentManager(), "SearchDialog");
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

        mNavigationView.setNavigationItemSelectedListener(this);
        mNavigationView.setCheckedItem(R.id.nav_home);
        mNavigationView.getMenu().performIdentifierAction(R.id.nav_home, 0);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            afterSignedIn(user);
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
            ConstraintLayout mainContent = findViewById(R.id.main_content);
            NestedScrollView menuContent = findViewById(R.id.menu_content);
            LinearLayout cateringContent = findViewById(R.id.catering_content);
            ConstraintLayout findContent = findViewById(R.id.find_content);

            FloatingActionButton floatingActionButton = findViewById(R.id.fab);

            mainContent.setVisibility(View.INVISIBLE);
            menuContent.setVisibility(View.INVISIBLE);
            cateringContent.setVisibility(View.INVISIBLE);
            findContent.setVisibility(View.INVISIBLE);
            floatingActionButton.setVisibility(View.INVISIBLE);

            if (id == R.id.nav_home) {
                mainContent.setVisibility(View.VISIBLE);
                WebView webView = findViewById(R.id.main_content_web_view);
                webView.loadUrl("http://www.casalasglorias.com/home.html");
            } else if (id == R.id.nav_menu) {
                menuContent.setVisibility(View.VISIBLE);
                floatingActionButton.setVisibility(View.VISIBLE);
            } else if (id == R.id.nav_catering) {
                cateringContent.setVisibility(View.VISIBLE);
                findViewById(R.id.catering_content_card_view).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(v.getContext(), CateringActivity.class));
                    }
                });
            } else if (id == R.id.nav_find) {
                findContent.setVisibility(View.VISIBLE);
                MapFragment mapFragment = (MapFragment) getFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(new OnMapReadyCallback() {
                    @Override
                    public void onMapReady(GoogleMap googleMap) {
                        LatLng latLng = new LatLng(42.0218595, -91.6577232);
                        googleMap.addMarker(new MarkerOptions().position(latLng)).setTitle("You're still here!");

                        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),
                                R.drawable.casalasglorias_pin);
                        bitmap.setHasAlpha(true);
                        bitmap = Bitmap.createScaledBitmap(bitmap, 256, 256, true);
                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(bitmap);

                        latLng = new LatLng(42.0342642, -91.6740979);
                        googleMap.addMarker(new MarkerOptions().position(latLng)
                                .icon(bitmapDescriptor)).setAnchor(0.5f,1f);
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                    }
                });
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

    private void afterSignedIn(FirebaseUser user) {
        mNavigationView.getMenu().findItem(R.id.nav_sign).setTitle(R.string.navigation_menu_sign_out);

        Uri uri = user.getPhotoUrl();
        GlideApp.with(this)
                .load(uri)
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
        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater layoutInflater = getActivity().getLayoutInflater();

            builder.setView(layoutInflater.inflate(R.layout.dialog_search, null))
                    .setTitle(getResources().getString(R.string.dialog_search_title))
                    .setPositiveButton(R.string.button_search, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String deviceId = ((EditText) getDialog()
                                    .findViewById(R.id.edit_text_keyword))
                                    .getText().toString();

                            Snackbar.make(getActivity().findViewById(R.id.menu_content),
                                        deviceId,
                                        Snackbar.LENGTH_LONG).show();
                        }
                    })
                    .setNegativeButton(R.string.button_cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            return builder.create();
        }
    }

}
