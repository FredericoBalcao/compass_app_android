package com.example.asus.bussola.Bussola;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.asus.bussola.R;

public class LocationActivity extends Activity {

    private GPSTracker gps = null;
    final Bundle bundle = new Bundle();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_coord);

        Button botaoConfirmar;

        final EditText editTextLat = (EditText) findViewById(R.id.txtLatitude);
        final EditText editTextLong = (EditText) findViewById(R.id.txtLongitude);

        editTextLat.setText("");
        editTextLong.setText("");

        botaoConfirmar = (Button) findViewById(R.id.buttonConfirmar);
        botaoConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                final Bundle bundle2 = new Bundle();

                bundle2.putString("lat_map", editTextLat.getText().toString());
                bundle2.putString("lon_map", editTextLong.getText().toString());
                retornaMainActivity(arg0, bundle2);
            }
        });
    }

    public void retornaMainActivity(View arg0, Bundle bundle) {
        Intent intent = new Intent(LocationActivity.this, MainActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}
