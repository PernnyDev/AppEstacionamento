package com.example.appestacionamento;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.appestacionamento.model.Proprietario;
import com.example.appestacionamento.model.Veiculo;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import cz.msebera.android.httpclient.Header;

public class VeiculosActivity extends AppCompatActivity {
    Spinner spinnerProprietarios;
    ListView lvVeiculos;
    AsyncHttpClient cliente;
    ArrayList<Proprietario> proprietarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_veiculos);

        spinnerProprietarios = findViewById(R.id.spinnerProprietarios);
        lvVeiculos = findViewById(R.id.lvVeiculos);
        cliente = new AsyncHttpClient();

        carregaProprietarios();
    }

    public void carregaProprietarios() {
        String url = "http://192.168.200.8:8081/proprietario";
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    listarTodosProprietarios(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(VeiculosActivity.this, "Erro ao carregar proprietários: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void listarTodosProprietarios(String resposta) {
        proprietarios = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(resposta);
            for (int i = 0; i < jsonArray.length(); i++) {
                Proprietario p = new Proprietario();
                p.setId(jsonArray.getJSONObject(i).getInt("id"));
                p.setNome(jsonArray.getJSONObject(i).getString("nome"));
                p.setCpf(jsonArray.getJSONObject(i).getString("cpf"));
                proprietarios.add(p);
            }
            ArrayAdapter<Proprietario> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, proprietarios);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerProprietarios.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        spinnerProprietarios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Proprietario proprietario = (Proprietario) parent.getSelectedItem();
                carregaVeiculos(proprietario.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void carregaVeiculos(int proprietarioId) {
        String url = "http://192.168.200.8:8081/veiculo?proprietarioId=" + proprietarioId;
        cliente.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200) {
                    listarTodosVeiculos(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(VeiculosActivity.this, "Erro ao carregar veículos: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void listarTodosVeiculos(String resposta) {
        ArrayList<Veiculo> lista = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(resposta);
            for (int i = 0; i < jsonArray.length(); i++) {
                Veiculo v = new Veiculo();
                v.setId(jsonArray.getJSONObject(i).getInt("id"));
                v.setPlaca(jsonArray.getJSONObject(i).getString("placa"));
                v.setModelo(jsonArray.getJSONObject(i).getString("modelo"));
                v.setCor(jsonArray.getJSONObject(i).getString("cor"));
                v.setProprietarioId(jsonArray.getJSONObject(i).getInt("proprietarioId"));
                lista.add(v);
            }
            AdapterVeiculo adapter = new AdapterVeiculo(this, R.layout.adapter_veiculo, R.id.id_veiculo, lista);
            lvVeiculos.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
