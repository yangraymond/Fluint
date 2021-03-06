package eia.fluint;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import java.util.Collection;
import java.util.List;


public class LoginSignUpActivity extends AppCompatActivity {

    static final String TAG = "LoginOrSignUp";

    private Toolbar toolbar;
    public static ViewPager mPager;
    private SlidingTabLayout mTabs;

    protected static final String POSITION_TAG = "position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_or_sign_up);

        // TODO: REPLACE ALL TOASTS WITH SNACKBARS

        // TODO: get reference to the toolbar
        toolbar = (Toolbar) findViewById(R.id.app_bar);

        // Tell Android we aren't using ActionBar
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        toolbar.setTitleTextColor(getResources().getColor(R.color.whiteColor));

        // TODO: Set logo
//        getSupportActionBar().setLogo(R.drawable.fluint_android_white);

        // TODO: Adjust logo size

        mPager = (ViewPager) findViewById(R.id.loginPager);
        mPager.setAdapter(new LoginPagerAdapter(getSupportFragmentManager()));
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    // Hide the keyboard.
                    ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                            .hideSoftInputFromWindow(mPager.getWindowToken(), 0);

                }
            }
        });
        mTabs = (SlidingTabLayout) findViewById(R.id.loginTabs);
        mTabs.setCustomTabView(R.layout.custom_tab_view_login, R.id.loginTabsText);
        mTabs.setDistributeEvenly(true);
        mTabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.primaryColorLight);
            }
        });
        mTabs.setViewPager(mPager);

        int defaultValue = 0;
        int page = getIntent().getIntExtra("page", defaultValue);
        mPager.setCurrentItem(page);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                Intent intent = new Intent(LoginSignUpActivity.this, SplashActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(ev);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + w.getLeft() - scrcoords[0];
            float y = ev.getRawY() + w.getTop() - scrcoords[1];

            Log.d("Activity", "Touch event " + ev.getRawX() + "," + ev.getRawY() + " " + x + "," + y
                    + " rect " + w.getLeft() + "," + w.getTop() + "," + w.getRight() + "," + w.getBottom()
                    + " coords " + scrcoords[0] + "," + scrcoords[1]);
            if (ev.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom())) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (ParseUser.getCurrentUser() == null) {
            Intent intent = new Intent(LoginSignUpActivity.this, SplashActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
    }

    class LoginPagerAdapter extends FragmentPagerAdapter {

        String[] tabs = getResources().getStringArray(R.array.tabs);

        public LoginPagerAdapter(FragmentManager fm) {
            super(fm);
            tabs = getResources().getStringArray(R.array.tabs);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    ExistingUserFragment existingUserFragment = ExistingUserFragment.getInstance(position);
                    return existingUserFragment;
                default:
                    NewUserFragment newUserFragment = NewUserFragment.getInstance(position);
                    return newUserFragment;

            }
        }

        @Override
        public int getCount() {
            return tabs.length;
        }

    }

    public static class NewUserFragment extends Fragment {

        private static final String TAG_NEW = "NewLoginOrSignup";
        private static final String USER_DATA = "user_data";

        protected android.support.v7.widget.AppCompatEditText etName;
        protected android.support.v7.widget.AppCompatEditText etEmailText;
        protected android.support.v7.widget.AppCompatEditText etPasswordText;
        protected android.support.v7.widget.AppCompatButton ibContinue;
        protected android.support.v7.widget.AppCompatButton ibFacebook;
        protected ConnectionDetector cd;
        protected boolean isInternetPresentNew;
        protected Collection<String> permissions;

        private TextInputLayout tilSignupName;
        private TextInputLayout tilSignupEmail;
        private TextInputLayout tilSignupPassword;


        public static NewUserFragment getInstance(int position) {
            NewUserFragment newUserFragment = new NewUserFragment();
            return newUserFragment;
        }


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            final View layout = inflater.inflate(R.layout.new_user_fragment, container, false);

            etName = (AppCompatEditText) layout.findViewById(R.id.etName);
            etEmailText = (AppCompatEditText) layout.findViewById(R.id.etEmailText);
            etPasswordText = (AppCompatEditText) layout.findViewById(R.id.etPasswordText);
            ibContinue = (AppCompatButton) layout.findViewById(R.id.ibContinue);
            ibFacebook = (AppCompatButton) layout.findViewById(R.id.ibFacebook);
            tilSignupName = (TextInputLayout) layout.findViewById(R.id.tilSignupName);
            tilSignupEmail = (TextInputLayout) layout.findViewById(R.id.tilSignupEmail);
            tilSignupPassword = (TextInputLayout) layout.findViewById(R.id.tilSignupPassword);
//            View coordinatorLayout = layout.findViewById(R.id.snackBarPosition);
            cd = new ConnectionDetector(getActivity());

            final View.OnClickListener clickListener = new View.OnClickListener() {
                public void onClick(View v) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                }
            };
//            if (coordinatorLayout != null) {
//                Snackbar.make(coordinatorLayout, "You are not connected to the internet.", Snackbar.LENGTH_LONG)
//                        .setAction("CONNECT", clickListener).show();
//            }


            etName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (etName.getText().length() > 0) {
                        tilSignupName.setError(null);
                    }
                }
            });

            etEmailText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (etEmailText.getText().length() > 0) {
                        tilSignupEmail.setError(null);
                    }
                }
            });

            etPasswordText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (etPasswordText.getText().length() > 0) {
                        tilSignupPassword.setError(null);
                    }
                }
            });

            ibContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final ProgressDialog pDialog = new ProgressDialog(getActivity(), R.style.AuthenticateDialog);
                    pDialog.setIndeterminate(true);
                    pDialog.setMessage("Signing up");
                    pDialog.show();

                    // TODO: Parse signup
                    // 1) Check if the email is already in the Parse database
                    //    Dialog: An account with that email already exists. Refer to UX forums to
                    //    see what users should be asked next to solve this issue
                    //    If so, display a Dialog, with the question: "Would you like to login?"
                    //    The answers are "No" and "Yes", with "Yes" sending the user to the other
                    //    PageView to login
                    // 2) Make sure to have users verify their email by having a boolean value
                    //    called hasVerifiedEmail to check if user has clicked on the link in their email

                    final String name = etName.getText().toString();
                    final String email = etEmailText.getText().toString();
                    final String password = etPasswordText.getText().toString();

                    if (name.equals("") || email.equals("") || password.equals("") || !email.contains("@")) {
                        pDialog.dismiss();
                        if (name.equals("") && email.equals("") && password.equals("")) {
                            tilSignupName.setError("Name cannot be left blank");
                            tilSignupEmail.setError("Email cannot be left blank");
                            tilSignupPassword.setError("Password cannot be left blank");
                            return;
                        } else if (name.equals("") && email.equals("")) {
                            tilSignupName.setError("Name cannot be left blank");
                            tilSignupEmail.setError("Email cannot be left blank");
                            return;
                        } else if (email.equals("") && password.equals("")) {
                            tilSignupEmail.setError("Email cannot be left blank");
                            tilSignupPassword.setError("Password cannot be left blank");
                            return;
                        } else if (name.equals("") && password.equals("")) {
                            tilSignupName.setError("Name cannot be left blank");
                            tilSignupPassword.setError("Password cannot be left blank");
                            return;
                        } else if (name.equals("")) {
                            tilSignupName.setError("Name cannot be left blank");
                            return;
                        } else if (email.equals("")) {
                            tilSignupEmail.setError("Email cannot be left blank");
                            return;
                        } else if (password.equals("")) {
                            tilSignupPassword.setError("Password cannot be left blank");
                            return;
                        } else if (!email.contains("@")) {
                            tilSignupEmail.setError("Please provide a valid email");
                            return;
                        }
                    }

                    isInternetPresentNew = cd.isConnectingToInternet();
                    if (isInternetPresentNew) {
                        // TODO: Sign user up
                        final String[] userData = new String[10];
                        userData[0] = name;
                        userData[1] = email;
                        userData[2] = password;

                        ParseQuery<ParseUser> query = ParseUser.getQuery();
                        query.whereEqualTo("email", email);
                        query.findInBackground(new FindCallback<ParseUser>() {
                            @Override
                            public void done(List<ParseUser> list, ParseException e) {
                                if (e == null && list.size() > 0) {
                                    pDialog.dismiss();
                                    // TODO: email is already taken. Show a dialog
                                    // should also check if list.size() > 0 ?
                                    android.support.v7.app.AlertDialog.Builder builder = new
                                            android.support.v7.app.AlertDialog.Builder(getActivity(),
                                            R.style.AppCompatAlertDialogStyle);
                                    builder.setTitle("Sign Up");
                                    builder.setMessage("This email is already registered. Want to login?");
                                    builder.setPositiveButton("LOGIN", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO: Take user to ExistingUserFragment
                                            mPager.setCurrentItem(1, true);
                                        }
                                    });
                                    builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            // TODO: Retrieve user password
                                            dialog.dismiss();
                                        }
                                    });
                                    builder.show();

                                } else {
                                    // Email is not taken. Allow user to continue

                                    ParseUser user = new ParseUser();
                                    user.setUsername(email);
                                    user.setPassword(password);
                                    user.put("name", name);
                                    user.signUpInBackground(new SignUpCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            // Sign up and store current user
                                            pDialog.dismiss();
                                            if (e == null) {
                                                Intent intent = new Intent(getActivity(), MainFeedActivity.class);
                                                intent.putExtra(USER_DATA, userData);
                                                startActivity(intent);
                                                getActivity().finish();
                                            } else {
                                                // TODO: Display a Snackbar saying signup failed

                                            }

                                        }
                                    });
                                }
                            }
                        });
                    } else {
                        pDialog.dismiss();
                        android.support.v7.app.AlertDialog.Builder builder2 = new
                                android.support.v7.app.AlertDialog.Builder(getActivity(),
                                R.style.AppCompatAlertDialogStyle);
                        builder2.setTitle("No Internet Connection");
                        builder2.setMessage("You are not connected to the internet.\nDo you want to check your Wifi settings?");
                        builder2.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            }
                        });
                        builder2.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder2.show();


                    }
                }
            });

            ibFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isInternetPresentNew = cd.isConnectingToInternet();
                    if (isInternetPresentNew) {
                        ParseFacebookUtils.logInWithReadPermissionsInBackground(NewUserFragment.this, permissions, new LogInCallback() {
                            @Override
                            public void done(ParseUser parseUser, ParseException e) {
                                if (parseUser == null) {
                                    // User cancelled the Facebook login. Do nothing
                                } else if (parseUser.isNew()) {
                                    // User just signed up and logged in through Facebook
                                    // If Parse doesn't automatically do it, link the fb account and ParseUser
                                    Intent intent = new Intent(getActivity(), MainFeedActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                } else {
                                    // User just logged in through Facebook
                                    Intent intent = new Intent(getActivity(), MainFeedActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            }
                        });
                    } else {
                        android.support.v7.app.AlertDialog.Builder builder3 = new
                                android.support.v7.app.AlertDialog.Builder(getActivity(),
                                R.style.AppCompatAlertDialogStyle);
                        builder3.setTitle("No Internet Connection");
                        builder3.setMessage("You are not connected to the internet.\nDo you want to check your Wifi settings?");
                        builder3.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            }
                        });
                        builder3.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builder3.show();
                    }
                }
            });

            return layout;
        }

        @Override
        public void onResume() {
            super.onResume();

            Log.d(TAG_NEW, "Entered new user onResume!");

        }

    }

    public static class ExistingUserFragment extends Fragment {

        private static final String TAG_EXISTING = "ExistingLoginOrSignup";

        // TODO: create class variables for our Views
        protected AppCompatEditText etLoginEmail;
        protected AppCompatEditText etLoginPassword;
        protected AppCompatButton ibLoginButton;
        protected AppCompatButton ibLoginFacebook;
        protected ConnectionDetector cd;
        protected boolean isInternetPresent;

        protected int failedLoginCount;

        // TODO: Update Facebook permissions
        protected Collection<String> permissions;

        private TextInputLayout tilLoginEmail;
        private TextInputLayout tilLoginPassword;

        public static ExistingUserFragment getInstance(int position) {
            ExistingUserFragment newUserFragment = new ExistingUserFragment();
            return newUserFragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            failedLoginCount = 0;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

            Log.d(TAG_EXISTING, "Getting references to widgets in ExistingUserFragment's onCreateView!");

            View layout = inflater.inflate(R.layout.existing_user_fragment, container, false);

            // TODO: get references to the different Views
            etLoginEmail = (AppCompatEditText) layout.findViewById(R.id.etLoginEmail);
            etLoginPassword = (AppCompatEditText) layout.findViewById(R.id.etLoginPassword);
            ibLoginButton = (AppCompatButton) layout.findViewById(R.id.ibLoginButton);
            ibLoginFacebook = (AppCompatButton) layout.findViewById(R.id.ibLoginFacebook);
            tilLoginEmail = (TextInputLayout) layout.findViewById(R.id.tilLoginEmail);
            tilLoginPassword = (TextInputLayout) layout.findViewById(R.id.tilLoginPassword);
            cd = new ConnectionDetector(getActivity());

            etLoginEmail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (etLoginEmail.getText().length() > 0) {
                        tilLoginEmail.setError(null);
                    }
                }
            });

            etLoginPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (etLoginPassword.getText().length() > 0) {
                        tilLoginPassword.setError(null);
                    }
                }
            });


            ibLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // TODO: set ProgressBar's visibility to View.VISIBLE
                    final ProgressDialog pDialog2 = new ProgressDialog(getActivity(), R.style.AuthenticateDialog);
                    pDialog2.setIndeterminate(true);
                    pDialog2.setMessage("Signing in");
                    pDialog2.show();

                    // Get the email and password from the user-inputted fields
                    String email = etLoginEmail.getText().toString();
                    String password = etLoginPassword.getText().toString();


                    if (email.equals("") || password.equals("")) {
                        pDialog2.dismiss();
                        if (email.equals("") && password.equals("")) {
                            tilLoginEmail.setError("Email cannot be left blank");
                            tilLoginPassword.setError("Password cannot be left blank");
                            return;
                        } else if (email.equals("")) {
                            tilLoginEmail.setError("Email cannot be left blank");
                            return;
                        } else if (password.equals("")) {
                            tilLoginPassword.setError("Password cannot be left blank");
                            return;
                        }
                    }

                    // TODO: verify. If Parse verified, create a new ParseUser and save to device
                    isInternetPresent = cd.isConnectingToInternet();
                    if (isInternetPresent) {
                        ParseUser.logInInBackground(email, password, new LogInCallback() {
                            @Override
                            public void done(ParseUser parseUser, ParseException e) {
                                pDialog2.dismiss();
                                if (e == null && parseUser != null) {
                                    Intent intent = new Intent(getActivity(), MainFeedActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                } else if (parseUser == null) {
                                    failedLoginCount++;
                                    android.support.v7.app.AlertDialog.Builder dialogBuilder = new
                                            android.support.v7.app.AlertDialog.Builder(getActivity(),
                                            R.style.AppCompatAlertDialogStyle);
                                    dialogBuilder.setTitle("Invalid Login");
                                    dialogBuilder.setMessage("The email and password do not match.");
                                    dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    dialogBuilder.show();
                                } else {
                                    // TODO: something else went wrong somewhere
                                    Toast.makeText(getActivity(), "An error occurred. Please try again later.",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        pDialog2.dismiss();
                        android.support.v7.app.AlertDialog.Builder builderDialog = new
                                android.support.v7.app.AlertDialog.Builder(getActivity(),
                                R.style.AppCompatAlertDialogStyle);
                        builderDialog.setTitle("No Internet Connection");
                        builderDialog.setMessage("You are not connected to the internet.\nDo you want to check your Wifi settings?");
                        builderDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            }
                        });
                        builderDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builderDialog.show();
                    }


                }
            });

            ibLoginFacebook.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean internet = cd.isConnectingToInternet();
                    if (internet) {
                        ParseFacebookUtils.logInWithReadPermissionsInBackground(ExistingUserFragment.this, permissions, new LogInCallback() {
                            @Override
                            public void done(ParseUser parseUser, ParseException e) {
                                if (parseUser == null) {
                                    // User canelled the Facebook login. Do nothing
                                } else if (parseUser.isNew()) {
                                    // User just signed up and logged in through Facebook
                                    // If Parse doesn't automatically do it, link the fb account and ParseUser
                                    Intent intent1 = new Intent(getActivity(), MainFeedActivity.class);
                                    startActivity(intent1);
                                    getActivity().finish();
                                } else {
                                    // User just logged in through Facebook
                                    Intent intent = new Intent(getActivity(), MainFeedActivity.class);
                                    startActivity(intent);
                                    getActivity().finish();
                                }
                            }
                        });
                    } else {
                        android.support.v7.app.AlertDialog.Builder builderDialog = new
                                android.support.v7.app.AlertDialog.Builder(getActivity(),
                                R.style.AppCompatAlertDialogStyle);
                        builderDialog.setTitle("No Internet Connection");
                        builderDialog.setMessage("You are not connected to the internet.\nDo you want to check your Wifi settings?");
                        builderDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                            }
                        });
                        builderDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        builderDialog.show();
                    }

                }
            });


            return layout;
        }

        @Override
        public void onResume() {
            super.onResume();

            Log.d(TAG_EXISTING, "Entered ExistingUserFragment's onResume!");


        }

    }

}
