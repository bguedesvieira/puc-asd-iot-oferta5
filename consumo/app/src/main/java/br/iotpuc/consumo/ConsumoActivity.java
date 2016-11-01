package br.iotpuc.consumo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;

public class ConsumoActivity extends AppCompatActivity {

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumo);
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
        progressBar.setVisibility(View.VISIBLE);
        try {
            wait(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        progressBar.setVisibility(View.GONE);
    }

    private void sair(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(ConsumoActivity.this, LoginActivity.class));
        finish();
    }
}
