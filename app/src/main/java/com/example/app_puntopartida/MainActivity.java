package com.example.app_puntopartida;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private static final long TIEMPO_MIN = 10 * 1000;
    private static final float DISTANCIA_MIN = 5;
    private static final String[] E = {"fuera de servicio", "temporalmente no disponible", "disponible"};
    private LocationManager manejador;
    private String proveedor;
    private TextView salida;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        salida = findViewById(R.id.salida);
        manejador = (LocationManager) getSystemService(LOCATION_SERVICE);


        // Configura los criterios de ubicación comunes
        Criteria criterio = new Criteria();
        criterio.setCostAllowed(false);
        criterio.setAltitudeRequired(false);
        criterio.setAccuracy(Criteria.ACCURACY_FINE);
        proveedor = LocationManager.GPS_PROVIDER;

        if (proveedor != null) {
            log("Mejor proveedor: " + proveedor + "\n");
            log("Comenzamos con la última localización conocida:");
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location localizacion = manejador.getLastKnownLocation(proveedor);
                muestraLocaliz(localizacion);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            }
        } else {
            log("No se encontró un proveedor de ubicación adecuado.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            manejador.requestLocationUpdates(proveedor, TIEMPO_MIN, DISTANCIA_MIN, (LocationListener) this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        manejador.removeUpdates((LocationListener) this);
    }

    @Override
    public void onLocationChanged(Location location) {
        log("Nueva localización: ");
        muestraLocaliz(location);
    }

    @Override
    public void onProviderDisabled(String proveedor) {
        log("Proveedor deshabilitado: " + proveedor + "\n");
    }

    @Override
    public void onProviderEnabled(String proveedor) {
        log("Proveedor habilitado: " + proveedor + "\n");
    }

    @Override
    public void onStatusChanged(String proveedor, int estado, Bundle extras) {
        log("Cambia estado proveedor: " + proveedor + ", estado=" + E[Math.max(0, estado)] + ", extras=" + extras + "\n");
    }

    private void log(String msg) {
        salida.append(msg + "\n");
    }

    private void muestraLocaliz(Location localizacion) {
        if (localizacion == null) {
            log("Localización desconocida\n");
        } else {
            log("Latitud: " + localizacion.getLatitude() + "\n" +
                    "Longitud: " + localizacion.getLongitude() + "\n" +
                    "Altitud: " + localizacion.getAltitude() + "\n" +
                    "Velocidad " + localizacion.getSpeed() + "\n" +
                    "Tiempo: " + localizacion.getTime());
        }
    }
}