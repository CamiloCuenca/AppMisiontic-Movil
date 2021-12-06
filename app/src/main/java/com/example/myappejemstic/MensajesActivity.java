package com.example.myappejemstic;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myappejemstic.adapters.AdapterChats;
import com.example.myappejemstic.fragments.chatsFragment;
import com.example.myappejemstic.pojos.Chats;
import com.example.myappejemstic.pojos.Estado;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MensajesActivity extends AppCompatActivity {

    CircleImageView img_user;
    TextView username;
    ImageView ic_conectado,ic_desconectado;
    SharedPreferences mpref;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref_estado = database.getReference("Estado").child(user.getUid());
    // se cambio estado por Estado
    DatabaseReference ref_chat = database.getReference("chats");

    EditText et_mensaje_txt;
    ImageButton btn_enviar_msj;


    // ID CHAT GlOBAL

    String  id_chat_global;
    Boolean amigoonline = false;

    RecyclerView rv_chats;
    AdapterChats adapter;
    ArrayList<Chats>  chatList;





    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mensajes2);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mpref = getApplicationContext().getSharedPreferences("usuario_sp",MODE_PRIVATE);
        img_user = findViewById(R.id.img_usuario);
        username = findViewById(R.id.tv_userM);
        ic_conectado = findViewById(R.id.icon_conectadoM);
        ic_desconectado = findViewById(R.id.icon_desconectadoM);

        String usuario = getIntent().getExtras().getString("nombre");
        String foto = getIntent().getExtras().getString("img_user");
        String id_user = getIntent().getExtras().getString("id_user");
        id_chat_global = getIntent().getExtras().getString("id_unico");

        colocarenvisto();

        et_mensaje_txt = findViewById(R.id.et_tx_mensaje);
        btn_enviar_msj = findViewById(R.id.btn_enviar_msj);
        btn_enviar_msj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msj = et_mensaje_txt.getText().toString();
                if(!msj.isEmpty()){
                    final Calendar c = Calendar.getInstance();
                    final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String idpsuh = ref_chat.push().getKey();

                    if(amigoonline){
                        Chats chatmsj = new Chats(idpsuh,user.getUid(),id_user,msj,"si",dateFormat.format(c.getTime()),timeFormat.format(c.getTime()));
                        ref_chat.child(id_chat_global).child(idpsuh).setValue(chatmsj);
                        et_mensaje_txt.setText("");
                    }else{
                        Chats chatmsj = new Chats(idpsuh,user.getUid(),id_user,msj,"no",dateFormat.format(c.getTime()),timeFormat.format(c.getTime()));
                        ref_chat.child(id_chat_global).child(idpsuh).setValue(chatmsj);
                        et_mensaje_txt.setText("");
                    }

                }else{
                    Toast.makeText(MensajesActivity.this, "El mensaje esta vacio ", Toast.LENGTH_SHORT).show();
                }

            }
        });



        final String id_user_sp = mpref.getString("usuario_sp","");

        username.setText(usuario);
        Glide.with(this).load(foto).into(img_user);

        final DatabaseReference ref = database.getReference("estado").child(id_user_sp).child("chatcon");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String chatcon = snapshot.getValue(String.class);
                if(snapshot.exists()){
                    if(chatcon.equals(user.getUid())){
                        amigoonline = true;
                        ic_conectado.setVisibility(View.VISIBLE);
                        ic_desconectado.setVisibility(View.GONE);
                    }else{
                        amigoonline = false;
                        ic_conectado.setVisibility(View.GONE);
                        ic_desconectado.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        rv_chats= findViewById(R.id.rv);
        rv_chats.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        rv_chats.setLayoutManager(linearLayoutManager);


        chatList= new ArrayList<>();
        adapter=new AdapterChats(chatList,this);
        rv_chats.setAdapter(adapter);

        Leermensajes();




    }//Fin del onCreate

    private void colocarenvisto() {
        ref_chat.child(id_chat_global).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snapshot1: snapshot.getChildren()){
                    Chats chats = snapshot1.getValue(Chats.class);
                    if(chats.getRecibe().equals(user.getUid())){
                        ref_chat.child(id_chat_global).child(chats.getId()).child("visto").setValue("si");
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void Leermensajes() {

        ref_chat.child(id_chat_global).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    chatList.removeAll(chatList);
                    for (DataSnapshot snapshot1:snapshot.getChildren()){
                        Chats chat = snapshot1.getValue(Chats.class);
                        chatList.add(chat);
                        setScroll();
                    }
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void setScroll() {

        rv_chats.scrollToPosition(adapter.getItemCount()-1);

    }


    private void estadousuario(String estado) {
        ref_estado.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                final String id_user_sp = mpref.getString("usuario_sp","");

                Estado est = new Estado(estado, "","",id_user_sp);
                ref_estado.setValue(est);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        estadousuario("conectado");
    }

    @Override
    protected void onPause() {
        super.onPause();
        estadousuario("desconectado");
        dameultimafecha();

    }

    private void dameultimafecha() {
        final Calendar c = Calendar.getInstance();
        SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");
        final SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");

        ref_estado.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                ref_estado.child("fecha").setValue(dateformat.format(c.getTime()));
                ref_estado.child("hora").setValue(timeformat.format(c.getTime()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}