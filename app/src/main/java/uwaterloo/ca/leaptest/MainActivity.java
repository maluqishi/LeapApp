package uwaterloo.ca.leaptest;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    public static int state;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        state = 1;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.aboutAuthor):
                new AlertDialog.Builder(this)
                        .setTitle("About Author of This App")
                        .setMessage("Ernest Wong, a second year Computer Engineering student at University of Waterloo. Learn more about him at: ernesternie.com")
                        .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                return true;
            case (R.id.aboutApp):
                new AlertDialog.Builder(this)
                        .setTitle("About This App")
                        .setMessage("This App is aimed to acquire and process sensor data from Leap Motion, and rapidly assess symptoms of concussion.")
                        .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                return true;
            case (R.id.reset):
                if (!BimanualFragment.isPlaying()) BimanualFragment.resetTrialNumber();
                else Toast.makeText(this,"You cannot change settings when doing trials",Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            switch (state) {
                case 1:
                    new AlertDialog.Builder(this)
                            .setTitle("Really Exit?")
                            .setMessage("All unsaved data will be lost. Are you sure you want to exit?")
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface arg0, int arg1) {
                                    MainActivity.super.onBackPressed();
                                }
                            }).create().show();
                    return true;
                case 2:
                    state = 1;
                    InfoFragment.backAnimation();
                    MainActivityFragment.backAnimation();
                    return true;
                case 3:
                    new AlertDialog.Builder(this)
                            .setTitle("Really Exit?")
                            .setMessage("All unsaved data will be lost. Are you sure you want to exit?")
                            .setNegativeButton(android.R.string.no, null)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface arg0, int arg1) {
                                    MainActivity.super.onBackPressed();
                                }
                            }).create().show();
            }
        }
        return false;
    }

}
