package uwaterloo.ca.leaptest;

import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            case (R.id.inquiry):
                new AlertDialog.Builder(this)
                        .setTitle("About Author")
                        .setMessage("Ernest Wong, a second year Computer Engineering student at University of Waterloo. Learn more about him on: ernesternie.com")
                        .setPositiveButton("Back", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .show();
                return true;
        }
        if (MainActivityFragment.isConnect) {
            switch (item.getItemId()) {
                case (R.id.pause):
                    MainActivityFragment.isPause = true;
                    Toast.makeText(this, "Paused",
                            Toast.LENGTH_SHORT).show();
                    return true;
                case (R.id.resume):
                    MainActivityFragment.isPause = false;
                    Toast.makeText(this, "Resumed",
                            Toast.LENGTH_SHORT).show();
                    return true;
                case (R.id.save):
                    MainActivityFragment.saveFile();
                    Toast.makeText(getBaseContext(),
                            "Data Saved on SD card",
                            Toast.LENGTH_SHORT).show();
                    return true;
                case (R.id.startStreaming):
                    MainActivityFragment.startStreaming();
                    Toast.makeText(getBaseContext(),
                            "Start Streaming...",
                            Toast.LENGTH_SHORT).show();
                    return true;
                case (R.id.stopStreaming):
                    MainActivityFragment.stopStreaming();
                    Toast.makeText(getBaseContext(),
                            "Stream Data Saved on SD card",
                            Toast.LENGTH_SHORT).show();
                    return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
