package com.example.asus.bussola.Bussola;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.bussola.API.Helper;
import com.example.asus.bussola.API.OpenWeatherMap;
import com.example.asus.bussola.R;

/**
 * Class MainActivity herda algumas das caracteristicas das Activities
 * caso contrario, seria apenas uma classe qualquer do Java. Foi herdado a AppCompatActivity.
 * Implementa o SensorEventListener (obtencao da orientacao) e LocationListener (obtencao da localizacao)
 */
public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {
    private OpenWeatherMap dadosRecebidos = null;
    private Helper conexao = null;
    private GPSTracker gps = null;
    private double lat, lng;
    Button botao;
    TextView txtCity, txtDescription, txtTemp, txtTempMin, txtTempMax, txtTimeSunrise, txtTimeSunset, txtDistancia;
    ImageView imageView, imageViewSeta;

    OpenWeatherMap openWeatherMap = new OpenWeatherMap();
    LocationManager locationManager;
    String provider;

    // ImageView para a bússola
    private ImageView imageBussola;

    // Guarda o ângulo currente da bússola
    private float anguloCurrente = 0f;

    // Device sensor manager
    private SensorManager mSensorManager;
    private float[] mGravity = new float[3];
    private float[] mGeomagnetic = new float[3];
    private float azimuth = 0f;
    private float currentAzimuth = 0f;
    static AlertDialog alert;

    int MY_PERMISSION = 0;

    /**
     * Método onCreate que recebe um parametro do tipo Bundle, é responsável por guardar o estado da Activity quando ela é reiniciada, como se fosse um cache.
     * O setContentView(R.layout.activity_main), é responsável por configurar o layout XML da Activity e definir todos os elementos de interface do usuário, tais como o EditText e Buttons.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtCity = (TextView) findViewById(R.id.txtCity);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtTemp = (TextView) findViewById(R.id.txtTemp);
        txtTempMax = (TextView) findViewById(R.id.txtTempMax);
        txtTempMin = (TextView) findViewById(R.id.txtTempMin);
        txtTimeSunrise = (TextView) findViewById(R.id.txtTimeSunrise);
        txtTimeSunset = (TextView) findViewById(R.id.txtTimeSunset);
        txtDistancia = (TextView) findViewById(R.id.txtViewDistancia);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageViewSeta = (ImageView) findViewById(R.id.imageViewSeta);
        imageBussola = (ImageView) findViewById(R.id.imageViewCompass);


        //se o botão para definir o destino for clicado, inicia outra atividade
        botao = (Button) findViewById(R.id.buttonDestino);
        botao.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(getBaseContext(), LocationActivity.class);
                startActivity(it);
            }
        });

        // Imagem com a bússola
        imageBussola = (ImageView) findViewById(R.id.imageViewCompass);
        // Inicializa as funcionalidades do sensor do Android
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location == null) {
            Log.e("LOCATION", "No location");
        }
        conexao = new Helper();


        if (gps == null) {
            // create class object
            gps = new GPSTracker(MainActivity.this, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    lat = gps.getLatitude();
                    lng = gps.getLongitude();

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        } else {
            gps.stopUsingGPS();
            gps = null;
        }

        Location l = new Location("");
        l.setLatitude(gps.latitude);
        l.setLongitude(gps.longitude);
        dadosRecebidos = conexao.getHTTPData(l);
        txtCity.setText(dadosRecebidos.getName());
        txtTemp.setText((dadosRecebidos.getMain().getTemp()));
        txtTempMin.setText(dadosRecebidos.getMain().getTemp_min());
        txtTempMax.setText(dadosRecebidos.getMain().getTemp_max());
        txtTimeSunrise.setText(dadosRecebidos.getSys().getSunrise());
        txtTimeSunset.setText(dadosRecebidos.getSys().getSunset());
        txtDescription.setText(dadosRecebidos.getWeather().getDescription());
        iconAPI(dadosRecebidos.getWeather().getIcon());
        calculaDistancia();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            alertaErroLocalizacao();

        // Regista listener para notificações de orientação
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_FASTEST);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
        }
        locationManager.requestLocationUpdates(provider, 400, 1, (LocationListener) this);
    }


    @Override
    protected void onPause() {
        super.onPause();
// Pára de receber notificações de forma a poupar a bateria
        mSensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mGravity[0] = alpha * mGravity[0] + (1 - alpha) * event.values[0];
            mGravity[1] = alpha * mGravity[1] + (1 - alpha) * event.values[1];
            mGravity[2] = alpha * mGravity[2] + (1 - alpha) * event.values[2];
        }
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            mGeomagnetic[0] = alpha * mGeomagnetic[0] + (1 - alpha) * event.values[0];
            mGeomagnetic[1] = alpha * mGeomagnetic[1] + (1 - alpha) * event.values[1];
            mGeomagnetic[2] = alpha * mGeomagnetic[2] + (1 - alpha) * event.values[2];
        }


        if (mGravity != null && mGeomagnetic != null) {
            float R[] = new float[9];
            float I[] = new float[9];

            boolean success = SensorManager.getRotationMatrix(
                    R, I, mGravity, mGeomagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = (float) Math.toDegrees(orientation[0]);
                azimuth = (azimuth + 360) % 360;

                //Animation
                Animation anim = new RotateAnimation(-currentAzimuth,
                        -azimuth,
                        Animation.RELATIVE_TO_SELF,
                        0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f);
                currentAzimuth = azimuth;

                anim.setDuration(500);
                anim.setRepeatCount(0);
                anim.setFillAfter(true);


                imageBussola.startAnimation(anim);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
// Não utilizado
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public void calculaDistancia() {
        Intent intent = getIntent();

        Bundle bundle2 = intent.getExtras();
        if (bundle2 != null) {
            String lat_receive = bundle2.getString("lat_map");
            String lon_receive = bundle2.getString("lon_map");
            if (!lat_receive.isEmpty() || !lon_receive.isEmpty()) {

                double lat_map = Double.parseDouble(lat_receive);
                double lon_map = Double.parseDouble(lon_receive);
                Location loc1 = new Location("");
                loc1.setLatitude(lat);
                loc1.setLongitude(lng);

                Location loc2 = new Location("");
                loc2.setLatitude(lat_map);
                loc2.setLongitude(lon_map);

                Double distanceInMeters = new Double(((loc1.distanceTo(loc2))));

                imageViewSeta.setVisibility(ImageView.VISIBLE);

                imageViewSeta.setRotation(-loc1.bearingTo(loc2));

                if (distanceInMeters < 1000.0) {
                    txtDistancia.setText(String.format("%1$,.2f", distanceInMeters) + " m");
                } else {
                    Double distanceinKm = distanceInMeters / 1000.0;
                    txtDistancia.setText(String.format("%1$,.2f", distanceinKm) + " Km");
                }
            } else {
                imageViewSeta.setVisibility(ImageView.INVISIBLE);
            }

        }
    }


    public void iconAPI(String icon) {
        switch (icon) {
            case "01d":
                imageView.setImageResource(R.drawable.image01d);
                break;
            case "01n":
                imageView.setImageResource(R.drawable.image01n);
                break;
            case "02d":
                imageView.setImageResource(R.drawable.image02d);
                break;
            case "02n":
                imageView.setImageResource(R.drawable.image02n);
                break;
            case "03d":
                imageView.setImageResource(R.drawable.image03d);
                break;
            case "03n":
                imageView.setImageResource(R.drawable.image03n);
                break;
            case "04d":
                imageView.setImageResource(R.drawable.image04d);
                break;
            case "04n":
                imageView.setImageResource(R.drawable.image04n);
                break;
            case "09d":
                imageView.setImageResource(R.drawable.image09d);
                break;
            case "09n":
                imageView.setImageResource(R.drawable.image09n);
                break;
            case "10d":
                imageView.setImageResource(R.drawable.image10d);
                break;
            case "10n":
                imageView.setImageResource(R.drawable.image10n);
                break;
            case "11d":
                imageView.setImageResource(R.drawable.image11d);
                break;
            case "11n":
                imageView.setImageResource(R.drawable.image11n);
                break;
            case "13d":
                imageView.setImageResource(R.drawable.image13d);
                break;
            case "13n":
                imageView.setImageResource(R.drawable.image13n);
                break;
            case "50d":
                imageView.setImageResource(R.drawable.image50d);
                break;
            case "50n":
                imageView.setImageResource(R.drawable.image50n);
                break;
        }

    }
    public void alertaErroLocalizacao(){
        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(MainActivity.this);

        // Titulo do dialogo
        alertDialog.setTitle("GPS");

        // Mensagem do dialogo
        alertDialog.setMessage("GPS não está habilitado. Deseja configurar?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Configurar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MainActivity.this.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // visualizacao do dialogo
        alertDialog.show();
    }
}
