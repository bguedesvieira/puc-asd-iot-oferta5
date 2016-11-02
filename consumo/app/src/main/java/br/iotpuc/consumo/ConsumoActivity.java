package br.iotpuc.consumo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import br.iotpuc.consumo.util.HttpHandler;

public class ConsumoActivity extends AppCompatActivity {

    private static final String FIELD_VALOR = "field1";
    private static final String FIELD_TEMPORALIDADE = "field2";

    private static final String CAMPO_TIPO_TEMPORALIADE = "temporalidade";
    private static final String CAMPO_VALOR="valor";

    private String TAG = ConsumoActivity.class.getSimpleName();

    private ProgressBar progressBar;
    private ProgressDialog pDialog;
    ArrayList<HashMap<String, String>> consumoList;
    private ListView lv;

    private final static String CONSUMO_URL = "https://thingspeak.com/channels/173362/feeds.json?api_key=7IFEE5OYI37ZX8GR&results=5";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressBar = (ProgressBar) findViewById(br.iotpuc.consumo.R.id.progressBar);
        setContentView(R.layout.activity_consumo);


        lv = (ListView) findViewById(R.id.list);


        new GetConsumo().execute();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.consumo_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.consumo:
                return true;
            case R.id.opcoes:
                Intent intent = new Intent(ConsumoActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.atualizar:
                atualizar();
                return true;
            case R.id.mn_sair:
                sair();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void atualizar(){
//        progressBar.setVisibility(View.VISIBLE);

            new GetConsumo().execute();

//        progressBar.setVisibility(View.GONE);
    }

    private void sair(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(ConsumoActivity.this, LoginActivity.class));
        finish();
    }

    /**
     *  Task para recuperar valores de consumo
     */

    private class GetConsumo extends AsyncTask<Void,Void,Void>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(ConsumoActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            HttpHandler sh = new HttpHandler();
            consumoList = new ArrayList<>();
            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(CONSUMO_URL);
            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {

                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    JSONArray feeds = jsonObj.getJSONArray("feeds");

                    String[] temporalidadeArray = getResources().getStringArray(R.array.temporalidade);

                    for (int i = 0; i < feeds.length(); i++) {
                        JSONObject feed = feeds.getJSONObject(i);
                        String temporalidade = feed.getString(FIELD_TEMPORALIDADE );
                        double valor = Double.valueOf(feed.getString(FIELD_VALOR)) / 10;
                        String valorStr = String.valueOf(valor);
                        // tmp hash map for single contact
                        HashMap<String, String> consumo = new HashMap<>();

                        consumo.put(CAMPO_TIPO_TEMPORALIADE,temporalidadeArray[Integer.parseInt(temporalidade)]);
                        consumo.put(CAMPO_VALOR,valorStr);

                        consumoList.add(consumo);
                    }
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
                Log.e(TAG, "Couldn't get json from server.");
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
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            ListAdapter adapter = new SimpleAdapter(
                    ConsumoActivity.this, consumoList,
                    R.layout.consumo_list_item , new String[]{"temporalidade", "valor"},
                    new int[]{R.id.temporalidade,
                    R.id.valor });

            lv.setAdapter(adapter);
        }
    }
}
