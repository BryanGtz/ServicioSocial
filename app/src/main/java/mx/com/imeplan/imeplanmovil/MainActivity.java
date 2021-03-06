package mx.com.imeplan.imeplanmovil;

import android.Manifest;
import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import br.com.bloder.magic.view.MagicButton;

public class MainActivity extends AppCompatActivity{
    MagicButton mbtn01, mbtn02, mbtn03, mbtn04, mbtn05;
    ImageButton btn_creditos;
    ImageView logo_imeplan;
    Intent miIntent = null;
    int permissionCheckGPS;
    ConnectivityManager cm;
    NetworkInfo ni;
    public final int MY_PERMISSION_REQUEST_GPS = 1;
    public final int MY_GPS_ENABLEMENT_REQUEST = 2;
    private  String PREFS_KEY = "mispreferencias";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Mostrar OnBoarding activity 1 vez
        boolean muestra= getValuePreference(getApplicationContext());
        if(muestra){
            Intent intent = new Intent(MainActivity.this, OnBoarding.class);
            startActivity(intent);
            saveValuePreference(getApplicationContext(), false);
        }
        // Solicitar Permisos GPS
        permissionCheckGPS = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean isGPSEnabled = false;
        if (lm != null) {
            isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        if(isGPSEnabled){
            solicitarGPS();
        }
        else{
            String mensaje = "GPS apagado. ¿Desea habilitarlo?";
            String titulo = "Configuración del GPS";
            String intentName = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
            getSettingsDialog(mensaje,titulo,intentName).show();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        init();

        //Boton Plan de terreno
        mbtn01.setMagicButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miIntent = new Intent(MainActivity.this, PlanDeOrdenamientoActivity.class);
                startActivity(miIntent);
            }
        });
        //Boton Nuestros Proyectos
        mbtn02.setMagicButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miIntent = new Intent(MainActivity.this, SitiosInteres.class);
                startActivity(miIntent);
            }
        });
        //Boton Reporte ciudadano
        mbtn03.setMagicButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                permissionCheckGPS = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheckGPS != PackageManager.PERMISSION_GRANTED) {
                    DialogoExplicacionPermiso();
                }
                else {
                    miIntent = new Intent(MainActivity.this, ReporteCiudadano.class);
                    miIntent.putExtra("id", 10);
                    startActivity(miIntent);
                }
            }
        });
        //Boton rutas de transporte
        mbtn04.setMagicButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Revisar conexion a Internet
                cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                ni = cm.getActiveNetworkInfo();

                if (ni != null && ni.isConnected()) {
                    miIntent = new Intent(MainActivity.this, WebView_Imeplan.class);
                    //miIntent.putExtra("id", 2);
                    //miIntent = new Intent(MainActivity.this, CreditosActivity.class);
                    miIntent.putExtra("id", 1);
                    startActivity(miIntent);
                }
                else
                    Toast.makeText(getApplicationContext(), "Sin conexión a Internet", Toast.LENGTH_LONG).show();
            }
        });
        //Boton Calculadora Ambiental
        mbtn05.setMagicButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Revisar conexion a Internet
                cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                ni = cm.getActiveNetworkInfo();

                if (ni != null && ni.isConnected()) {
                    miIntent = new Intent(MainActivity.this, WebView_Imeplan.class);
                    miIntent.putExtra("id", 2);
                    startActivity(miIntent);
                }
                else
                    Toast.makeText(getApplicationContext(), "Sin conexión a Internet", Toast.LENGTH_LONG).show();
            }
        });
        // Botón para acceder a los créditos
        btn_creditos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreditosActivity.class));
            }
        });
        // Botón para abrir la página del Imeplan
        logo_imeplan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                miIntent = new Intent(MainActivity.this, WebView_Imeplan.class);
                miIntent.putExtra("id", 3);
                startActivity(miIntent);
            }
        });
    }

    private void DialogoExplicacionPermiso() {
        String msj = "Debes de conceder el permiso a la aplicación para acceder a tu ubicación.\n" +
                "Ve a <b>Configuración</b> > <b>Aplicaciones</b> > <b>Imeplan Movil</b> > <b>Permisos</b>";
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("¡IMPORTANTE!")
                .setMessage(Html.fromHtml(msj))
                .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(getApplicationContext(), "Permiso necesario", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void init() {
        mbtn01 = findViewById(R.id.magic_button01);
        mbtn02 = findViewById(R.id.magic_button02);
        mbtn03 = findViewById(R.id.magic_button03);
        mbtn04 = findViewById(R.id.magic_button04);
        mbtn05 = findViewById(R.id.magic_button05);
        btn_creditos = findViewById(R.id.btn_creditos);
        logo_imeplan = findViewById(R.id.logo_imeplan);
    }

    public AlertDialog getSettingsDialog(String message, String title, final String intentName){
        //Dialogo para activacion del GPS
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        // Setting Dialog Title
        alertDialog.setTitle(title)
        // Setting Dialog Message
        .setMessage(message)
        // Accion de Aceptacion button
        .setPositiveButton("Configurar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(intentName);
                startActivityForResult(intent,MY_GPS_ENABLEMENT_REQUEST);
            }
        });
        // Acción de cancelacion
        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return alertDialog.create();
    }

    /**
     * Método para solicitar permisos del gps al usuario
     */
    private void solicitarGPS() {
        if (permissionCheckGPS != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                //Han sido negados los permisos anteriormente
                // (Aparece la opcion de no volver a mostrar)
                // TODO: Decir porqué estamos solicitando permisos
                // TODO: Tomar accion si se vuelven a negar los permisos
                DialogoExplicacion();

            } else {
                //Es la primer vez que se están solicitando los permisos
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_REQUEST_GPS);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_REQUEST_GPS) {
            if (grantResults.length <= 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permiso Denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        permissionCheckGPS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheckGPS != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

        }
        else{
            Toast.makeText(getApplicationContext(), "Necesario activar GPS", Toast.LENGTH_LONG)
                    .show();
        }
    }*/

    public void DialogoExplicacion(){
        String msj = "Para enviar los reportes necesitamos conocer su ubicación, así la dependencia encargada localizará el reporte" +
                " y será atendido más rápido.";
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¿Por qué necesitamos acceder a tu Ubicación?")
               .setMessage(msj)
               .setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSION_REQUEST_GPS);
                    }
                })
        .show();
    }

    public void saveValuePreference(Context context, Boolean mostrar){
        SharedPreferences settings= context.getSharedPreferences(PREFS_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor;
        editor= settings.edit();
        editor.putBoolean("license", mostrar);
        editor.apply();
    }
    public boolean getValuePreference(Context context){
        SharedPreferences sp= context.getSharedPreferences(PREFS_KEY,MODE_PRIVATE);
        return sp.getBoolean("license",true);
    }
}

