package com.example.myappejemstic.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myappejemstic.MensajesActivity;
import com.example.myappejemstic.R;
import com.example.myappejemstic.pojos.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class adapterChatLista extends RecyclerView.Adapter<adapterChatLista.viewHolderAdapterChatlist> {
    List<Users> usersList;
    Context context;
    SharedPreferences mPref;


    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    public adapterChatLista(List<Users> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolderAdapterChatlist onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_chatlista,parent,false);
        viewHolderAdapterChatlist holder = new viewHolderAdapterChatlist(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolderAdapterChatlist holder, int position) {

           final  Users userss = usersList.get(position);

        final Vibrator vibrator = (Vibrator)context.getSystemService(context.VIBRATOR_SERVICE);

        holder.tv_usuario.setText(userss.getNombre());
        Glide.with(context).load(userss.getFoto()).into(holder.im_user);

        DatabaseReference ref_mis_solicitudes = database.getReference("Solicitudes").child(user.getUid());
        ref_mis_solicitudes.child(userss.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String estado = snapshot.child("estado").getValue(String.class);

                if(snapshot.exists()){
                    if(estado.equals("amigos")){
                        holder.cardView.setVisibility(View.VISIBLE);
                    }else {
                        holder.cardView.setVisibility(View.GONE);
                    }
                }else{
                    holder.cardView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Calendar c = Calendar.getInstance();
        final SimpleDateFormat dateformat = new SimpleDateFormat("dd/MM/yyyy");

        DatabaseReference ref_Estado = database.getReference("Estado").child(userss.getId());

        // se cambio estado por Estado
        ref_Estado.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                String estado = snapshot.child("estado").getValue(String.class);
                String fecha = snapshot.child("fecha").getValue(String.class);
                String hora = snapshot.child("hora").getValue(String.class);

                if(snapshot.exists()){
                    if(estado.equals("conectado")){
                        holder.tv_conectado.setVisibility(View.VISIBLE);
                        holder.icon_conectado.setVisibility(View.VISIBLE);
                        holder.tv_desconectado.setVisibility(View.GONE);
                        holder.icon_desconectado.setVisibility(View.GONE);
                    }else{
                        holder.tv_desconectado.setVisibility(View.VISIBLE);
                        holder.icon_desconectado.setVisibility(View.VISIBLE);
                        holder.tv_conectado.setVisibility(View.GONE);
                        holder.icon_conectado.setVisibility(View.GONE);

                        if(fecha.equals(dateformat.format(c.getTime()))){
                            holder.tv_desconectado.setText("últ.vez hoy a las" + hora);
                        }else{
                            holder.tv_desconectado.setText("últ.vez " +fecha +"a las "+hora);
                        }

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                mPref=v.getContext().getSharedPreferences("usuario_sp",Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = mPref.edit();


                final DatabaseReference ref = database.getReference("Solicitudes").child(user.getUid()).child(userss.getId()).child("idechat");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String id_unico=snapshot.getValue(String.class);
                        if(snapshot.exists()){
                            Intent intent=new Intent(v.getContext(), MensajesActivity.class);
                            intent.putExtra("nombre",userss.getNombre());
                            intent.putExtra("img_user",userss.getFoto());
                            intent.putExtra("id_user",userss.getId());
                            intent.putExtra("id_unico",id_unico);
                            editor.putString("usuario_sp",userss.getId());
                            editor.apply();
                            v.getContext().startActivity(intent);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });







            }
        });









    }//findel onbindViewHolder

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class viewHolderAdapterChatlist extends RecyclerView.ViewHolder {

        TextView tv_usuario;
        ImageView im_user;
        CardView cardView;
        TextView tv_conectado , tv_desconectado;
        ImageView icon_conectado , icon_desconectado;



        public viewHolderAdapterChatlist(@NonNull View itemView) {
            super(itemView);
            tv_usuario=itemView.findViewById(R.id.tv_userchat);
            im_user = itemView.findViewById(R.id.img_usuariochat);
            cardView = itemView.findViewById(R.id.carviewchat);
            tv_conectado = itemView.findViewById(R.id.tv_conectado);
            tv_desconectado = itemView.findViewById(R.id.tv_desconectado);
            icon_conectado= itemView.findViewById(R.id.icon_conectado);
            icon_desconectado = itemView.findViewById(R.id.icon_desconectado);


        }
    }
}
