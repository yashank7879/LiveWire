package com.livewire.ui.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.livewire.R;
import com.livewire.adapter.FaqAdapter;
import com.livewire.adapter.SubCategoryAdapter;
import com.livewire.databinding.ActivityFaqsBinding;
import com.livewire.responce.FaqResponce;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FaqsActivity extends AppCompatActivity implements FaqAdapter.FaqInterface {
   ActivityFaqsBinding binding;
    private List<FaqResponce> countries;
    private FaqAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = DataBindingUtil.setContentView(this,R.layout.activity_faqs);
        //  getDatafromfile()

        binding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        loadCountries(this);
    }

    private void getDatafromfile() {
        /*try {
            BufferedReader jsonReader = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.country_code)));
            StringBuilder jsonBuilder = new StringBuilder();
            for (String line = null; (line = jsonReader.readLine()) != null; ) {
                jsonBuilder.append(line).append("\n");
            }
            //Parse Json
            JSONTokener tokener = new JSONTokener(jsonBuilder.toString());
        }catch (Exception e){

        }*/
    }


    public void loadCountries(Context context) {
        try {

            BufferedReader jsonReader = new BufferedReader(new InputStreamReader(context.getResources().openRawResource(R.raw.faqs)));
            StringBuilder jsonBuilder = new StringBuilder();
            for (String line = null; (line = jsonReader.readLine()) != null; ) {
                jsonBuilder.append(line).append("\n");
            }
            //Parse Json
            JSONTokener tokener = new JSONTokener(jsonBuilder.toString());
            JSONArray array = new JSONArray(tokener);

            countries = new ArrayList<>();
            for (int i = 0; i < array.length(); i++) {
                FaqResponce country = new FaqResponce();
                JSONObject jo_inside = array.getJSONObject(i);
                country.setParentId(jo_inside.getString("parentId"));
                country.setTittle(jo_inside.getString("tittle"));
                country.setTitle(jo_inside.getBoolean("isTitle"));
                country.setVisible(false);
                JSONArray jsonArray = jo_inside.getJSONArray("SubQue");
                ArrayList<FaqResponce.SubQueBean>subqueList = new ArrayList<>();

                for (int i1 = 0; i1 < jsonArray.length(); i1++) {

                    FaqResponce.SubQueBean subQueBean = new FaqResponce.SubQueBean();
                    JSONObject subque = jsonArray.getJSONObject(i1);
                    subQueBean.setSubId(subque.getString("subId"));
                    subQueBean.setSubQuestion(subque.getString("subQuestion"));
                    subQueBean.setAns(subque.getString("ans"));
                    subqueList.add(subQueBean);
                }
                country.setSubQue(subqueList);
                countries.add(country);

                LinearLayoutManager layoutManager1 = new LinearLayoutManager(this);
                binding.rvFaq.setLayoutManager(layoutManager1);
                adapter = new FaqAdapter(this,countries,this);
                binding.rvFaq.setAdapter(adapter);
            }
            Log.e( "loadCountries: ",""+countries.size());
        } catch (Exception e) {
            Log.e("ArrayAfterRead", e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void cellOnClick(boolean value ,FaqResponce model) {
        if (value){
           model.setVisible(false);
        }else model.setVisible(true);

        adapter.notifyDataSetChanged();

    }
}
