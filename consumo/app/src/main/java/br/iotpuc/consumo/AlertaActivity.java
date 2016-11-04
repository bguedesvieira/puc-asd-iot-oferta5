package br.iotpuc.consumo;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import br.iotpuc.consumo.util.HttpHandler;

public class AlertaActivity extends AppCompatActivity {

    private Button btnEnviar;
    private EditText txtAlertaMes, txtAlertaSemana, txtAlertaDia, txtAlertaHora, txtAlertaMinuto;
    private ProgressDialog pDialog;

    private static final String TAG = AlertaActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alerta);

        btnEnviar = (Button) findViewById(R.id.btn_enviar);
        txtAlertaMes = (EditText) findViewById(R.id.txt_alerta_mes);
        txtAlertaSemana = (EditText) findViewById(R.id.txt_alerta_semana);
        txtAlertaDia = (EditText) findViewById(R.id.txt_alerta_dia);
        txtAlertaHora = (EditText) findViewById(R.id.txt_alerta_hora);
        txtAlertaMinuto = (EditText) findViewById(R.id.txt_alerta_minuto);

        btnEnviar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                submeterAlertas();
            }
        });

    }

    private void submeterAlertas(){
        new EnviaAlertas().execute(txtAlertaMes.getText().toString().trim(),
                txtAlertaSemana.getText().toString().trim(),
                txtAlertaDia.getText().toString().trim(),
                txtAlertaHora.getText().toString().trim(),
                txtAlertaMinuto.getText().toString().trim());
    }

    private class EnviaAlertas extends AsyncTask<String,Void,Void>{
        private final static String ALERTA_URL = "http://192.168.56.1:8093/user/config";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(AlertaActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(String... params) {
            int i = 0;
            try {
                for (String valor : params) {
                    if (valor != null) {
                        submeterAlerta(++i, valor);
                    }
                }
            }catch (final JSONException e){
                Log.e(TAG, "Json parsing error: " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Json parsing error: " + e.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }catch (final IOException ioException){
                Log.e(TAG, "Erro de comunicacao com servidor: " + ioException.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Erro de comunicacao com servidor: " + ioException.getMessage(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Dismiss the progress dialog
            if (pDialog.isShowing()) {
                pDialog.dismiss();
            }

        }
        private void submeterAlerta(int temporalidade,final String valor) throws  JSONException, IOException{
            String jsonAlerta = montaJSon(temporalidade,valor);
            Map<String, String> headers = new HashMap<String,String>();
            headers.put("content-type", "application/json; charset=utf-8");
            if (jsonAlerta != null){
                HttpHandler sh = new HttpHandler();

                sh.makeHttpPostCall(ALERTA_URL,headers,jsonAlerta);
            }
        }

        private String montaJSon(int temporalidade, final String valor) throws  JSONException{
            if ("".equals(valor.trim())){
                return null;
            }
            FirebaseAuth auth = FirebaseAuth.getInstance();
            JSONObject configAlerta = new JSONObject();

            configAlerta.put("login", auth.getCurrentUser().getEmail());
            configAlerta.put("period",String.valueOf(temporalidade));
            configAlerta.put("maxValue",valor);

            return configAlerta.toString();
        }
    }
}
