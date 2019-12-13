package test.tuto_passport;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import com.squareup.picasso.Picasso;
import retrofit2.Callback;
import retrofit2.Response;
import test.tuto_passport.entities.MenuResponse;
import test.tuto_passport.entities.Menu;
import test.tuto_passport.network.ApiService;
import test.tuto_passport.network.RetrofitBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
public class PostActivity extends AppCompatActivity {

    private static final String TAG = "PostActivity";


    ApiService service;
    TokenManager tokenManager;
    Call<MenuResponse> call;

    @BindView(R.id.ListViewXml)
    ListView listveiw;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        ButterKnife.bind(this);
        tokenManager = TokenManager.getInstance(getSharedPreferences("prefs", MODE_PRIVATE));

        if(tokenManager.getToken() == null){
            startActivity(new Intent(PostActivity.this, LoginActivity.class));
            finish();
        }

        service = RetrofitBuilder.createServiceWithAuth(ApiService.class, tokenManager);
        getPosts();
    }
   /* @OnClick(R.id.btn_menus)
            void clik(){
startActivity(new Intent(PostActivity.this, Activity_map.class));}*/
     void getPosts(){

        call = service.menus();
        call.enqueue(new Callback<MenuResponse>() {
            @Override
            public void onResponse(Call<MenuResponse> call, Response<MenuResponse> response) {
                Log.w(TAG, "onResponse: " + response );

                if(response.isSuccessful()){
                    final   ArrayList<Menu> Items=new  ArrayList<Menu> (response.body().getData());
                    final MyCustomAdapter myadpter= new MyCustomAdapter(Items);
                    listveiw.setAdapter(myadpter);
                    //title.setText(response.body().getData().get(0).getLibelle());
                }else {
                    tokenManager.deleteToken();
                    startActivity(new Intent(PostActivity.this, LoginActivity.class));
                    finish();

                }
            }

            @Override
            public void onFailure(Call<MenuResponse> call, Throwable t) {
                Log.w(TAG, "onFailure: " + t.getMessage() );
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(call != null){
            call.cancel();
            call = null;
        }
    }


    class MyCustomAdapter extends BaseAdapter
    {
        ArrayList<Menu> Items=new ArrayList<Menu>();
        MyCustomAdapter(ArrayList<Menu> Items ) {
            this.Items=Items;

        }


        @Override
        public int getCount() {
            return Items.size();
        }

        @Override
        public String getItem(int position) {
            return Items.get(position).getLibelle();

        }

        @Override
        public long getItemId(int position) {
            return  position;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            LayoutInflater linflater =getLayoutInflater();
            View view1=linflater.inflate(R.layout.row_view, null);

            TextView txtname =(TextView) view1.findViewById(R.id.libelle_menu);
            TextView txtdes =(TextView) view1.findViewById(R.id.desc_menu);
            ImageView img = view1.findViewById(R.id.img_menu);
            String imageUrl = "http://172.17.9.183:8000/"+Items.get(i).getImg();

            //Loading image using Picasso
            Picasso.get().load(imageUrl).into(img);
            txtname.setText(Items.get(i).getLibelle());
            txtdes.setText(Items.get(i).getDesc());
            return view1;

        }



    }
}
