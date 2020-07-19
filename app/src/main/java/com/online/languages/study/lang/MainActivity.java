package com.online.languages.study.lang;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.online.languages.study.lang.adapters.MenuListAdapter;
import com.online.languages.study.lang.adapters.NavigationDialog;
import com.online.languages.study.lang.adapters.OpenActivity;
import com.online.languages.study.lang.adapters.ThemeAdapter;
import com.online.languages.study.lang.data.DataFromJson;
import com.online.languages.study.lang.data.DataItem;
import com.online.languages.study.lang.data.DataManager;
import com.online.languages.study.lang.data.NavCategory;
import com.online.languages.study.lang.data.NavSection;
import com.online.languages.study.lang.data.NavStructure;
import com.online.languages.study.lang.fragments.ContactFragment;
import com.online.languages.study.lang.fragments.GalleryFragment;
import com.online.languages.study.lang.fragments.HomeFragment;
import com.online.languages.study.lang.fragments.HomeTabsFragment;
import com.online.languages.study.lang.fragments.InfoFragment;
import com.online.languages.study.lang.fragments.NotesFragment;
import com.online.languages.study.lang.fragments.PrefsFragment;
import com.online.languages.study.lang.fragments.SectionFragment;
import com.online.languages.study.lang.fragments.StarredFragment;
import com.online.languages.study.lang.fragments.StatsFragment;
import com.online.languages.study.lang.util.IabHelper;
import com.online.languages.study.lang.util.IabResult;
import com.online.languages.study.lang.util.Inventory;

import java.util.ArrayList;

import static com.online.languages.study.lang.Constants.EXTRA_CAT_ID;
import static com.online.languages.study.lang.Constants.EXTRA_SECTION_ID;
import static com.online.languages.study.lang.Constants.GALLERY_REQUESTCODE;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener {


    Boolean multipane;

    ListView listView = null;

    NavStructure navStructure;

    MenuListAdapter mMenuListAdapter;
    String[] menuTitles;

    private static final String ACITVE_MENU_ITEM = "ACTIVE_ITEM";
    int menuActiveItem = 0;

    NavigationView navigationView;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;

    BottomNavigationView bottomNav;
    View bottomNavBox;
    Boolean bottomNavDisplay;

    Boolean shouldBack = false;

    HomeFragment homeFragment;
    HomeTabsFragment homeTabsFragment;
    StarredFragment starredFragment;
    StatsFragment statsFragment;
    PrefsFragment prefsFragment;
    InfoFragment infoFragment;
    ContactFragment contactFragment;
    SectionFragment sectionFragment;
    GalleryFragment galleryFragment;
    NotesFragment notesFragment;

    FragmentTransaction fPages;
    FragmentManager fragmentManager;

    ThemeAdapter themeAdapter;
    SharedPreferences appSettings;
    public String themeTitle;

    OpenActivity openActivity;

    DataManager dataManager;

    Toolbar toolbar;

    String btmNavState = "";
    boolean btmOnly = false;


    public static ArrayList<DataItem> errorsList;
    public static ArrayList<DataItem> allDataList;
    public static ArrayList<DataItem> oldestDataList;

    private Boolean PRO_VERSION;

    Boolean fullVersion;

    public static Boolean hasPrivilege;

    FloatingActionButton fab;
    View appbar;
    String homeFrag = "all";
    View panelShadow;


    public static final String SKU_PREMIUM = BuildConfig.SKU;

    public static final String PUBLIC_KEY_1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuHpMkp0RqQJ4CzsokYgM0kkzAXgffvRI9aQCS5KYGsSe6RIAZHqvZurxjWjQN";
    public static final String PUBLIC_KEY_2 = "kpKHwRa5IdDHZD7BUsA1+pooJtyPRZLEvCAlXf1XNY93clF9UEHz77fp29RuzTEGtlZysUk56Do5++FKKcwa4UEJkobyObTCzo6e+jVMy23";
    public static final String PUBLIC_KEY_3 = "gFX2A0HP25aP4RUfX5UsPjFpPMydGgGyLZoD5TcQVB1aLvUSCCdTuQpgkEEB4Cdqd09VlYXSrgJqDg4f0+ZbDM06UvE63XEmtnj79ivu5adm9";
    public static final String PUBLIC_KEY_4 = "S1NLNHkFor9DbTrMgNWvvcFCIMPkMxlcRYS0ewdFptA8mKmDABMBEq12rCkjOFSSwIDAQAB";

    IabHelper mHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        appSettings = PreferenceManager.getDefaultSharedPreferences(this);
        themeTitle= appSettings.getString("theme", Constants.SET_THEME_DEFAULT);

        themeAdapter = new ThemeAdapter(this, themeTitle, true);
        themeAdapter.getTheme();

        menuTitles = getResources().getStringArray(R.array.menu_titles);

        multipane = getResources().getBoolean(R.bool.multipane);

        homeFrag = "all";


        openActivity = new OpenActivity(this);
        dataManager = new DataManager(this, true);

        openActivity.setOrientation();

        if (multipane) {
            setContentView(R.layout.main_multipane);
        } else {

            if (Build.VERSION.SDK_INT >= 21) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getResources().getColor(R.color.colorTransparent));
            }

            setContentView(R.layout.activity_main);
        }




        String base64EncodedPublicKey = PUBLIC_KEY_1+PUBLIC_KEY_2+PUBLIC_KEY_3+PUBLIC_KEY_4;

        base64EncodedPublicKey = getString(R.string.encoded_key);

        mHelper = new IabHelper(this, base64EncodedPublicKey);

        PRO_VERSION = Constants.PRO;


        hasPrivilege = false;


        if (Constants.DEBUG) {

            fullVersion = appSettings.getBoolean(Constants.SET_VERSION_TXT, false);

            if (Constants.PRO) {
                changeVersion(true);
                changeShowAd(false);
                fullVersion = true;
            } else {

                changeVersion(false);
                changeShowAd(true);
                fullVersion = false;
            }

        } else {

            fullVersion = appSettings.getBoolean(Constants.SET_VERSION_TXT, false);

            if (fullVersion) {
                changeVersion(true);
                changeShowAd(false);
            } else {
                changeVersion(false);
                changeShowAd(true);
            }
        }



        if (!fullVersion) { checkPremium(); } // TODO turn on in release: check for any version

        // if ( needCheck() ) checkPremium();

        // if (Constants.DEBUG) checkPremium();


        hasPrivilege = checkPrivilege();

        if (PRO_VERSION) {
            hasPrivilege = true;
        }

        appbar =  findViewById(R.id.app_bar);

        panelShadow = findViewById(R.id.app_bar_shadow);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        fab = findViewById(R.id.fab_add);




        if (multipane) {
            listView = findViewById(R.id.menu_list);
            mMenuListAdapter = new MenuListAdapter(this, menuTitles, menuActiveItem);
            listView.setAdapter(mMenuListAdapter);
            listView.setOnItemClickListener(this);

        } else {

            drawer = findViewById(R.id.drawer_layout);
            toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

             bottomNavDisplay();


        }

        fragmentManager = getSupportFragmentManager();
        homeFragment = new HomeFragment();
        homeTabsFragment = new HomeTabsFragment();
        starredFragment = new StarredFragment();
        infoFragment = new InfoFragment();
        prefsFragment = new PrefsFragment();
        sectionFragment = new SectionFragment();
        contactFragment = new ContactFragment();
        statsFragment = new StatsFragment();
        galleryFragment = new GalleryFragment();
        notesFragment = new NotesFragment();

        if (savedInstanceState != null) {
            menuActiveItem = savedInstanceState.getInt(ACITVE_MENU_ITEM, 0);
        } else {

            fPages = fragmentManager.beginTransaction();
           // fPages.replace(R.id.content_fragment, homeFragment, "home");  /// TODO


            if (homeFrag.equals("all")) fPages.replace(R.id.content_fragment, homeTabsFragment, "home");
            else fPages.replace(R.id.content_fragment, homeFragment, "home");

            fPages.disallowAddToBackStack();
            fPages.commit();
        }

        updateMenuList(menuActiveItem);

        errorsList= new ArrayList<>();


        /// check premium
        // premiumVersion  = premiumHelper; // for debug

        navStructure = new NavStructure(this);
        DataFromJson dataFromJson = new DataFromJson(this);
        navStructure = dataFromJson.getStructure();

        Bundle bundle = new Bundle();

        bundle.putParcelable("structure", navStructure);

        bundle.putString(EXTRA_SECTION_ID, "root");
        bundle.putString(EXTRA_CAT_ID, "root");

        homeFragment.setArguments(bundle);
        homeTabsFragment.setArguments(bundle);
        statsFragment.setArguments(bundle);
        galleryFragment.setArguments(bundle);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               noteFabClick();
            }
        });

    }





    private boolean needCheck() {

        boolean check = false;

        SharedPreferences mLaunches = getSharedPreferences(AppStart.APP_LAUNCHES, Context.MODE_PRIVATE);
        int launchesNum = mLaunches.getInt(AppStart.LAUNCHES_NUM, 0);

        if ( (launchesNum % 10 == 0) &&  isNetworkAvailable()) check = true;

        return check;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void bottomNavDisplay() {

        String btmSetting = appSettings.getString("btm_nav", getString(R.string.set_btm_nav_value_default));

        btmOnly = btmSetting.equals(getString(R.string.set_btm_nav_value_4));

        boolean sameTypeWithMenu = false;

        sameTypeWithMenu =  (btmNavState.equals(getString(R.string.set_btm_nav_value_1)) || btmNavState.equals(getString(R.string.set_btm_nav_value_2)) )
                        &&
                        ( btmSetting.equals(getString(R.string.set_btm_nav_value_1)) || btmSetting.equals(getString(R.string.set_btm_nav_value_2)));



        if ( !btmNavState.equals(btmSetting)  && !sameTypeWithMenu )  {


          //  bottomNav = new BottomNavigationView(this);

                setTitle("");

                if (btmOnly) {

                    bottomNav = findViewById(R.id.navigation1);
                    bottomNav.setVisibility(View.VISIBLE);
                    findViewById(R.id.navigation).setVisibility(View.GONE);
                    setDrawerState(false);

                } else {
                    bottomNav = findViewById(R.id.navigation);
                    bottomNav.setVisibility(View.VISIBLE);
                    findViewById(R.id.navigation1).setVisibility(View.GONE);
                    setDrawerState(true);

                }

                setToolbarTitle(menuActiveItem);


              bottomNav.getMenu().clear();

                if (dataManager.gallerySection) {
                    if (btmOnly) bottomNav.inflateMenu(R.menu.bottom_nav_gallery_more);
                    else bottomNav.inflateMenu(R.menu.bottom_nav_gallery);

                } else {
                    if (btmOnly) {

                        if (!dataManager.statsSection) {
                            bottomNav.inflateMenu(R.menu.bottom_nav_base_more);
                        } else {
                            bottomNav.inflateMenu(R.menu.bottom_nav_more);
                        }
                    }
                    else bottomNav.inflateMenu(R.menu.bottom_nav);
                }

                btmNavState = btmSetting;


        }

        bottomNav.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavBox = findViewById(R.id.bottomNavBox);


        Boolean display = btmSetting.equals(getResources().getString(R.string.set_btm_nav_value_1)) || btmSetting.equals(getString(R.string.set_btm_nav_value_2)) || btmSetting.equals(getString(R.string.set_btm_nav_value_4));

        if (Build.VERSION.SDK_INT < 21) display = false;

        if (bottomNavBox != null) {
            bottomNavDisplay = display;
            if (display)  {
                bottomNavBox.setVisibility(View.VISIBLE);

                if (navigationView != null) {
                   if (btmSetting.equals(getString(R.string.set_btm_nav_value_1))) {
                       navigationView.getMenu().setGroupVisible(R.id.grp1, false);
                   } else {
                       navigationView.getMenu().setGroupVisible(R.id.grp1, true);
                   }
                    final View wrap = findViewById(R.id.fragmentWrapper);

                    bottomNavBox.getViewTreeObserver().addOnGlobalLayoutListener(
                            new ViewTreeObserver.OnGlobalLayoutListener(){
                                @Override
                                public void onGlobalLayout() {
                                    int mHeight = bottomNavBox.getHeight();
                                    bottomNavBox.getViewTreeObserver().removeOnGlobalLayoutListener( this );
                                    wrap.setPadding(0, 0, 0, (mHeight - 5));
                                }

                            });
                }
            }  else {
                bottomNavBox.setVisibility(View.GONE);
                if (navigationView != null) {
                    navigationView.getMenu().setGroupVisible(R.id.grp1, true);
                    View wrap = findViewById(R.id.fragmentWrapper);
                    wrap.setPadding(0, 0, 0, 0);
                }
            }
        }

        checkGalleryNavItem(navigationView);
        updateMenuList(menuActiveItem);
    }


    private void checkGalleryNavItem(NavigationView navigation) {

        String btmSetting = appSettings.getString("btm_nav", getString(R.string.set_btm_nav_value_default));

        if (navigation != null) {

            if (dataManager.gallerySection) {

                if (btmSetting.equals(getString(R.string.set_btm_nav_value_1))) {
                    navigation.getMenu().findItem(R.id.nav_gallery).setVisible(false);
                } else {
                    navigation.getMenu().findItem(R.id.nav_gallery).setVisible(true);
                }
            }
            else {
                navigation.getMenu().findItem(R.id.nav_gallery).setVisible(false);
            }

            if (!dataManager.statsSection) navigation.getMenu().findItem(R.id.nav_statistic).setVisible(false);


        }
    }


    private void checkPremium() {
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                if (!result.isSuccess()) {
                    Log.d("LogInap", "Problem setting up In-app Billing: " + result);
                }

                if (mHelper == null) return;

                Log.d("Inapp", "Setup successful. Querying inventory.");

                if(mHelper.isSetupDone() && !mHelper.isAsyncInProgress()) {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                }
            }
        });
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {

            if (result.isFailure()) {
                //showRes("Feilure to connect Google service API");
                Log.d("Inapp", "Feilure to connect Google service API");
            }
            else {
                Log.d("Inapp", "Success inventory.");

                if (inventory.hasPurchase(SKU_PREMIUM)) {
                    changeVersion(true);
                    changeShowAd(false);
                } else {
                    changeShowAd(true);
                }

                updateMenuList(menuActiveItem);
            }
        }
    };


    public Boolean checkPrivilege() {
        SharedPreferences mLaunches = getSharedPreferences(AppStart.APP_LAUNCHES, Context.MODE_PRIVATE);
        int launchesNum = mLaunches.getInt(AppStart.LAUNCHES_NUM, 0);

        if (launchesNum < 3 ) {
            hasPrivilege = true;
        }
        return hasPrivilege;
    }

    //////// dealing with fragments

    public void openPage(int position) {

        String[] tags = {"home", "gallery", "starred", "stats", "notes", "prefs", "desc", "contact"};
        String tag = tags[position];

        fPages = fragmentManager.beginTransaction();
        //fragmentManager.popBackStack(null, 0);

        if (fragmentManager.getBackStackEntryCount() > 0) {
            if (position == 1) {
                fragmentManager.popBackStack(null, 0);
            } else {
                fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }

        }  else {
            fragmentManager.popBackStack(null, 0);
        }


        fPages.setCustomAnimations(R.anim.fade_in, 0, R.anim.fade_in, 0);


        if (position == 0) {

            if (homeFrag.equals("all")) fPages.replace(R.id.content_fragment, homeTabsFragment, tag);
            else fPages.replace(R.id.content_fragment, homeFragment, tag); // TODO check fragment

        } else if (position == 1) {
            fPages.replace(R.id.content_fragment, galleryFragment, tag);
        } else if (position == 2) {
            fPages.replace(R.id.content_fragment, starredFragment, tag);
        } else if (position == 3) {
            fPages.replace(R.id.content_fragment, statsFragment, tag);
        } else if (position == 4) {
            fPages.replace(R.id.content_fragment, notesFragment, tag);
        } else if (position == 5) {
            fPages.replace(R.id.content_fragment, prefsFragment, tag);
        } else if (position == 6) {
            fPages.replace(R.id.content_fragment, infoFragment, tag);
        } else if (position == 7) {
            fPages.replace(R.id.content_fragment, contactFragment, tag);
        }

        fPages.disallowAddToBackStack();

        fPages.commit();

    }


    public void updateMenuList(int activePosition) {
        int[] menuItemsPosition = {0, 1, 2, 3, 4, 5, 6, 7, 8};
        menuActiveItem = activePosition;

        if (multipane) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

            if (activePosition == -1) {
                mMenuListAdapter = new MenuListAdapter(this, menuTitles, -1 );
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            } else {
                mMenuListAdapter = new MenuListAdapter(this, menuTitles, menuItemsPosition[activePosition]);
                setToolbarTitle(activePosition);
            }

            listView.setAdapter(mMenuListAdapter);

        } else {

            int size = navigationView.getMenu().size();
            for (int i = 0; i < size; i++) {
                navigationView.getMenu().getItem(i).setChecked(false);
            }

            bottomNav.getMenu().setGroupCheckable(0, true, false);

            for (int i = 0; i < bottomNav.getMenu().size(); i++) {
                bottomNav.getMenu().getItem(i).setChecked(false);
            }


            bottomNav.getMenu().setGroupCheckable(0, true, true);

            if (activePosition != -1) {

                int pos = menuItemsPosition[activePosition];

                navigationView.getMenu().getItem(pos).setChecked(true);
                setToolbarTitle(activePosition);

                if (dataManager.gallerySection) {
                    if (activePosition < 4 ) bottomNav.getMenu().getItem(activePosition).setChecked(true);
                } else {
                    if (activePosition < 3 ) bottomNav.getMenu().getItem(activePosition).setChecked(true);
                }

                //Toast.makeText(MainActivity.this, "Num: " + menuActiveItem + " : "+ activePosition, Toast.LENGTH_SHORT).show();

                checkGalleryNavItem(navigationView);

                //// enable drawer indicator
                shouldBack = false;
                //toggle.setDrawerIndicatorEnabled(true);

            } else {
                shouldBack=true;
                /// change drawer indicator to arrow up
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                //toggle.setDrawerIndicatorEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }

            fullVersion = appSettings.getBoolean(Constants.SET_VERSION_TXT, false);
            if (fullVersion) {
                findViewById(R.id.nav_footer).setVisibility(View.GONE);
            } else {
                findViewById(R.id.nav_footer).setVisibility(View.VISIBLE);
            }



            manageBarShadow(activePosition);

        }

        if (fab != null) {
            manageNoteFab(activePosition);
        }

    }


    private void manageBarShadow(int position) {


        if (multipane) panelShadow.setVisibility(View.GONE);

        if (!homeFrag.equals("all")){
            panelShadow.setAlpha(1f);
            return;
        }

        if (position == 0) {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    panelShadow.animate().alpha(0f).setDuration(200);
                }
            }, 150);


        } else {

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    panelShadow.animate().alpha(1f).setDuration(250);
                }
            }, 300);
        }




    }


    private void manageNoteFab(int position) {

        if (position == 4 ) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fab.show();
                }
            }, 350);
        }
        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    fab.hide();
                }
            }, 100);
        }

    }

    private void noteFabClick() {

        NotesFragment fragment = (NotesFragment)fragmentManager.findFragmentByTag("notes");
        if (fragment!=null) fragment.fabClick();

    }


    public void setDrawerState(boolean isEnabled) {

        if ( isEnabled ) {
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_UNLOCKED);
            toggle.setDrawerIndicatorEnabled(true);
            toggle.syncState();
        }
        else {

            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.onDrawerStateChanged(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            toggle.setDrawerIndicatorEnabled(false);
            toggle.syncState();

           /// setTitle("    " + menuTitles[menuActiveItem]);

        }
    }


    public void setToolbarTitle(int position) {

        String title = menuTitles[position];

        if (position==0) title = getString(R.string.title_main_txt);

        if (btmOnly) title = "   " + title;

        setTitle(title);
    }

    public void onMenuItemClicker(int position) {
        openPageFromMenu(position);
        updateMenuList(position);
    }


    private void openPageFromMenu(int position) {
        if (menuActiveItem != position) {
            menuActiveItem = position;
            final int act = position;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openPage(act);
                }
            }, 390);
        }
        updateMenuList(menuActiveItem);
    }


    public void openCatActivity(View view, int position) {

        NavSection navSection = navStructure.sections.get(position);

        if (navSection.type.equals("simple")) {

            NavCategory cat = navSection.navCategories.get(0);

            openActivity.openCat(cat.id, cat.spec, cat.title, navSection.id);

        } else {

            if (navSection.type.equals("gallery")) {

                if (navSection.spec.equals("gallery_simple")) {

                    NavCategory cat = navSection.navCategories.get(0);

                    openActivity.openImageList(navStructure, navSection.id, cat.id, cat.title);

                    return;

                } else {
                    openGallery(navSection);
                    return;
                }
            }

            Intent i = new Intent(MainActivity.this, SectionActivity.class);
            openActivity.openSection(i, navStructure, navSection.id, "root");
        }

    }

    public void openGallery(NavSection navSection) {

        Intent i;
        i = new Intent(MainActivity.this, GalleryActivity.class);
        i.putExtra(Constants.EXTRA_CAT_ID, "root");
        i.putExtra(Constants.EXTRA_SECTION_ID, navSection.id);
        i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        startActivityForResult(i, 1);
        openActivity.pageTransition();

    }


    public void openSectionStats(View view, int position) {
        Intent i = new Intent(MainActivity.this, SectionStatsActivity.class);
        i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        i.putExtra(Constants.EXTRA_SECTION_ID, navStructure.sections.get(position).id);
        i.putExtra(Constants.EXTRA_SECTION_NUM, position);
        startActivity(i);
        openActivity.pageTransition();
    }


    public void openProgressStats(View view) {
        Intent i = new Intent(MainActivity.this, ProgressStatsActivity.class);
        i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        startActivity(i);
        openActivity.pageTransition();
    }

    public void openStudiedBySections (View view) {
        openDataTypeBySections (0);
    }

    public void openKnownBySections (View view) {
        openDataTypeBySections (1);
    }

    public void openUnknownBySections (View view) {
        openDataTypeBySections (2);
    }


    public void openDataTypeBySections (int type) {
        Intent i = new Intent(MainActivity.this, SectionStatsListActivity.class);
        i.putExtra(Constants.EXTRA_DATA_TYPE, type);
        startActivity(i);
        openActivity.pageTransition();
    }


    public void openErrors(View view) {
        Intent i = new Intent(MainActivity.this, CustomDataListActivity.class);
        i.putParcelableArrayListExtra("dataItems", errorsList);
        i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        i.putExtra(Constants.EXTRA_SECTION_ID, "errors");
        startActivity(i);
        openActivity.pageTransition();
    }


    public void testAllPage(View view) {

        Intent i = new Intent(MainActivity.this, ExerciseActivity.class);
        i.putExtra("ex_type", 1);
        i.putExtra(Constants.EXTRA_CAT_TAG, "all");
        i.putParcelableArrayListExtra("dataItems", allDataList);
        startActivity(i);
        openActivity.pageTransition();
    }

    public void testOldstPage(View view) {
        if (oldestDataList.size() < 1) {
            displayEmtySection();
        } else {
            openOldPage();
        }
    }

    public void displayEmtySection() {
        Toast.makeText(this, R.string.no_data_msg, Toast.LENGTH_SHORT).show();
    }

    private void openOldPage() {
        Intent i = new Intent(MainActivity.this, ExerciseActivity.class) ;
        i.putExtra("ex_type", 1);
        i.putExtra(Constants.EXTRA_CAT_TAG, Constants.REVISE_CAT_TAG);
        i.putParcelableArrayListExtra("dataItems", oldestDataList);
        startActivity(i);
        openActivity.pageTransition();
    }

    public void openCatFromGallery(final View view) {
        ViewGroup v = (ViewGroup) view.getParent();
        View tagged = v.findViewById(R.id.tagged);
        final String tag = (String) tagged.getTag();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                GalleryFragment fragment = (GalleryFragment)fragmentManager.findFragmentByTag("gallery");
               if (fragment!=null) fragment.openCatActivity(tag);

            }
        }, 50);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ACITVE_MENU_ITEM, menuActiveItem);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        int position = 0;

        if (id == R.id.nav_home) {
            position  = 0;
        } else if (id == R.id.nav_gallery) {
            position  = 1;
        } else if (id == R.id.nav_starred) {
            position  = 2;
        } else if (id == R.id.nav_statistic) {
            position  = 3;
        } else if (id == R.id.nav_notes) {
            position  = 4;
        } else if (id == R.id.nav_settings) {
            position  = 5;
        } else if (id == R.id.nav_info) {
            position  = 6;
        } else if (id == R.id.nav_contact) {
            position  = 7;
        }

        onMenuItemClicker(position);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

           // updateMenuList(menuActiveItem);



            int id = item.getItemId();
            int position = 0;

            if (id == R.id.nav_gallery) {
                 position  = 1;
            } else if (id == R.id.nav_starred) {
                position  = 2;
            } else if (id == R.id.nav_statistic) {
                position  = 3;
            } else if (id == R.id.nav_more) {
                position  = 100;
            }


            if (position == 100) {

                openNavDialog();
                return false;

            } else {
                onMenuItemClicker(position);
                return true;
            }


        }
    };


    public void openNavDialog() {

        NavigationDialog navDialog = new NavigationDialog(this, MainActivity.this);
        navDialog.openInfoDialog("Navigation");

    }

    @Override
    public void onBackPressed() {

        if (multipane) {
            goBack();
        } else {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                goBack();
            }
        }
    }


    private void goBack() {

        if (menuActiveItem!=0) {
            openPage(0);
            updateMenuList(0);

        } else {
            super.onBackPressed();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search) {
            openSearch();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void openSearch() {
        Intent i = new Intent(MainActivity.this, SearchActivity.class);
        i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        startActivityForResult(i, 10);
    }


    private void changeVersion(Boolean full_version) {
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putBoolean(Constants.SET_VERSION_TXT, full_version);
        editor.apply();
    }

    private void changeShowAd(Boolean show_ad) {
        SharedPreferences.Editor editor = appSettings.edit();
        editor.putBoolean(Constants.SET_SHOW_AD, show_ad);
        editor.apply();
    }


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        onMenuItemClicker(i);
    }


    public void openStarred(View view) {
        Intent i = new Intent(MainActivity.this, UserListActivity.class);
        startActivity(i);
        openActivity.pageTransition();
    }

    public void openStarredBookmarks(View view) {
        Intent i = new Intent(MainActivity.this, StarredBookmarksActivity.class);
        i.putExtra(Constants.EXTRA_CAT_ID, "01010");
        i.putExtra(Constants.EXTRA_SECTION_ID, "01010");
        i.putExtra(Constants.EXTRA_NAV_STRUCTURE, navStructure);
        startActivity(i);
        openActivity.pageTransition();
    }

    public void openReference(View view) {
        Intent i = new Intent(MainActivity.this, ReferenceActivity.class);
        startActivity(i);
        openActivity.pageTransition();
    }

    public void openGetPremium(View view) {
        Intent i = new Intent(MainActivity.this, GetPremium.class);
        startActivityForResult(i, 1);
    }

    public void sendFeedback(View view) {
        sendMail(0);
    }

    public void sendReport(View view) {
        sendMail(1);
    }

    private void sendMail(int type) {

        String recepientEmail = getString(R.string.mail_address);

        String subject = getString(R.string.msg_mail_subject);
        if (type == 1 ) subject = getString(R.string.msg_mail_subject_error);

        String versionName = BuildConfig.VERSION_NAME;
        if (fullVersion) versionName += "+";

        String version = String.format(getString(R.string.msg_version_name), versionName);

        String mailSubject = subject + " " + version;

        Intent i = new Intent(Intent.ACTION_SENDTO);

        String mailto = "mailto:" + recepientEmail +
                "?subject=" + Uri.encode(mailSubject) +
                "&body=" + Uri.encode("");


        i.setData(Uri.parse(mailto));

        try {
            startActivity(Intent.createChooser(i, getString(R.string.msg_sending_mail)));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, R.string.msg_no_mail_client, Toast.LENGTH_SHORT).show();
        }
    }

    public void visitSite(View view) {
        openWebsite();
    }

    private void openWebsite() {

        String url = "";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        try {
            startActivity(i);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(MainActivity.this, R.string.msg_no_browser, Toast.LENGTH_SHORT).show();
        }
    }


    public void rateApp(View view) {
        rateApplication();
    }

    private void rateApplication() {

        String pack = getString(R.string.app_market_link);

        //pack = context.getPackageName();
        Uri uri = Uri.parse("market://details?id=" + pack);
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + pack)));
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        }

        if (requestCode == 10) {
            Fragment fragment = fragmentManager.findFragmentByTag("starred");
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }

            Fragment fragmentNote = fragmentManager.findFragmentByTag("notes");
            if (fragmentNote != null) {
                fragmentNote.onActivityResult(requestCode, resultCode, data);
            }
        }

        if (requestCode == GALLERY_REQUESTCODE) {
            Fragment fragment = fragmentManager.findFragmentByTag("gallery");
            if (fragment != null) {
                fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
    }


}
