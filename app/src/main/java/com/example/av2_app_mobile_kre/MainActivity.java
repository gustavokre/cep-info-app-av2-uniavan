package com.example.av2_app_mobile_kre;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private TextView Cep;
    private Long tempoResposta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        System.out.println("oi");
        Log.d("App", "oi");



    }

    public void pesquisa_cep(View v)
    {
        Log.d("App", "boa");
        get_cep_info();
    }

    public JSONObject filter_response(String s)
    {
        JSONObject obj = null;
        try {
            obj = new JSONObject(s);
            System.out.println(obj.getString("name")); //John
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;

    }

    private boolean is_cep_valid(String s)
    {
        return s.matches("[0-9]{8}");
    }

    private void get_cep_info()
    {
        Date tempo = new Date();
        tempoResposta = tempo.getTime();
        textView = (TextView) findViewById(R.id.textView2);
        Cep = (TextView) findViewById(R.id.cepInput);
        String cep_texto = Cep.getText().toString();

        if(!is_cep_valid(cep_texto))
        {
            textView.setText("Cep deve conter 8 números somente");
        }

        Log.d("Cep", cep_texto);

        OkHttpClient client = new OkHttpClient();
        String url = "https://viacep.com.br/ws/"+ cep_texto + "/json/";
        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final long dif = new Date().getTime() - tempoResposta;
                    final String myResponse = response.body().string();
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                String msg_final = "";

                                JSONObject obj = filter_response(myResponse);
                                msg_final += "<b>Cidade:</b> " + obj.getString("localidade") + "<br>";
                                msg_final += "<b>Estado:</b> " + obj.getString("uf") + "<br>";
                                msg_final += "<b>Logradouro:</b> " + obj.getString("logradouro") + "<br>";
                                msg_final += "<b>Bairro:</b> " + obj.getString("bairro") + "<br><br>" ;
                                msg_final += "<small><b>Tempo de Resposta:</b> " + dif + "ms</small>" ;
                                textView.setText(Html.fromHtml(msg_final));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

}