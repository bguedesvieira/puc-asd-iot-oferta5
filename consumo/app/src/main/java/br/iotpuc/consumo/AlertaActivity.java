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
    protected final static String ALERTA_URL = "http://192.168.56.1:8093/user/config";

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

        new GetAlertas().execute();

    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetAlertas().execute();
    }

    private void submeterAlertas(){
        new EnviaAlertas().execute(txtAlertaMes.getText().toString().trim(),
                txtAlertaSemana.getText().toString().trim(),
                txtAlertaDia.getText().toString().trim(),
                txtAlertaHora.getText().toString().trim(),
                txtAlertaMinuto.getText().toString().trim());
    }

    private class GetAlertas extends AsyncTask<Void,Void,JSONObject>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            //pDialog = new ProgressDialog(AlertaActivity.this);
            //pDialog.setMessage("Please wait...");
            //pDialog.setCancelable(true);
            //pDialog = ProgressDialog.show(getApplicationContext(),"Aguarde","Carregando Valores...",false,true ) ;

        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(ALERTA_URL);
            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    Object mensagem = jsonObj.get("message");
                    JSONObject result = jsonObj.getJSONObject("result");
                    if (result == null){
                        Log.e(TAG, mensagem != null ? mensagem.toString() : "Servidor não retornou informacoes.");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(),
                                        "Não foi possível retornar os dados de consumo!",
                                        Toast.LENGTH_LONG)
                                        .show();
                            }
                        });
                        return null;
                    }
                    return result;
                } catch (final JSONException e) {
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

                }
            }else {
                Log.e(TAG, "Servidor não retornou informacoes.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(),
                                "Não foi possível retornar os dados de consumo!",
                                Toast.LENGTH_LONG)
                                .show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
//            pDialog.dismiss();

            if (result != null){
                try {

                    String temporalidade = result.getString("period");
                    if (temporalidade != null && !temporalidade.isEmpty()){
                        int valorInt = Integer.parseInt(temporalidade);
                        switch (valorInt) {
                            case 1:
                                txtAlertaMinuto.setText(result.getString("maxValue"));
                                break;
                            case 2:
                                txtAlertaHora.setText(result.getString("maxValue"));
                                break;
                            case 3:
                                txtAlertaDia.setText(result.getString("maxValue"));
                                break;
                            case 4:
                                txtAlertaSemana.setText(result.getString("maxValue"));
                                break;
                            case 5:
                                txtAlertaMes.setText(result.getString("maxValue"));
                                break;
                            default:
                                Log.e(TAG,"Nao conseguiu definir tipo de alerta" + result.toString());
                                break;
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
                }
            }

            // Dismiss the progress dialog
//            if (pDialog.isShowing() || pDialog.isIndeterminate()) {
//                pDialog.dismiss();
//            }

        }
    }

    private class EnviaAlertas extends AsyncTask<String,Void,Void>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(AlertaActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
//                pDialog = ProgressDialog.show(getApplicationContext(),"Aguarde","Carregando Valores...",false,false ) ;

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
            Toast.makeText(getApplicationContext(),"Dados Atualizados!",Toast.LENGTH_LONG);

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
