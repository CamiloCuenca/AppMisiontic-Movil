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
import com.example.myappejemstic.pojos.Solicitudes;
import com.example.myappejemstic.pojos.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class adapterUsuarios extends RecyclerView.Adapter<adapterUsuarios.viewHolderAdapter> {
    List<Users> usersList;
    Context context;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    FirebaseDatabase database = FirebaseDatabase.getInstance();

    SharedPreferences mPref;





    public adapterUsuarios(List<Users> usersList, Context context) {
        this.usersList = usersList;
        this.context = context;
    }




    @NonNull
    @Override
    public viewHolderAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_usuarios,parent,false);
        viewHolderAdapter holder = new viewHolderAdapter(v);
        return holder;
    }





    @Override
    public void onBindViewHolder(@NonNull viewHolderAdapter holder, int position) {

        Users userss = usersList.get(position);

        final Vibrator vibrator = (Vibrator)context.getSystemService(context.VIBRATOR_SERVICE);

        Glide.with(context).load(userss.getFoto()).into(holder.img_user);
        holder.tv_usuario.setText(userss.getNombre());

        if(userss.getId().equals(user.getUid())){
            holder.cardView.setVisibility(View.GONE);
        }else{
            holder.cardView.setVisibility(View.VISIBLE);
        }

        DatabaseReference ref_mis_botones = database.getReference("Solicitudes").child(user.getUid());

        ref_mis_botones.child(userss.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String estado = snapshot.child("estado").getValue(String.class);
                // se cambio Estado por estado // se volvio a cambiar Estado por estado



                if(snapshot.exists()){
                    if(estado.equals("enviado")){
                        holder.send.setVisibility(View.VISIBLE);
                        holder.amigos.setVisibility(View.GONE);
                        holder.tengosolicitud.setVisibility(View.GONE);
                        holder.add.setVisibility(View.GONE);
                        holder.progressBar.setVisibility(View.GONE);
                    }
                    if(estado.equals("amigos")) {
                        holder.send.setVisibility(View.GONE);
                        holder.amigos.setVisibility(View.VISIBLE);
                        holder.tengosolicitud.setVisibility(View.GONE);
                        holder.add.setVisibility(View.GONE);
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    if(estado.equals("solicitud")) {
                        holder.send.setVisibility(View.GONE);
                        holder.amigos.setVisibility(View.GONE);
                        holder.tengosolicitud.setVisibility(View.VISIBLE);
                        holder.add.setVisibility(View.GONE);
                        holder.progressBar.setVisibility(View.GONE);
                    }




                    }else{
                    holder.send.setVisibility(View.GONE);
                    holder.amigos.setVisibility(View.GONE);
                    holder.tengosolicitud.setVisibility(View.GONE);
                    holder.add.setVisibility(View.VISIBLE);
                    holder.progressBar.setVisibility(View.GONE);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });







        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                 final DatabaseReference A = database.getReference("Solicitudes").child(user.getUid());
                A.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Solicitudes sol = new Solicitudes("enviado","");
                            A.child(userss.getId()).setValue(sol);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                final DatabaseReference B = database.getReference("Solicitudes").child(userss.getId());
                B.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Solicitudes sol = new Solicitudes("solicitud","");
                        B.child(user.getUid()).setValue(sol);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                DatabaseReference count =database.getReference("Contador").child(userss.getId());

                count.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            Integer val = snapshot.getValue(Integer.class);
                            if(val==0){
                                count.setValue(1);

                            }else{
                                count.setValue(val+1);
                            }
                        }else{
                            count.setValue(1);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                vibrator.vibrate(300);


            }//Fin del oneClick

        });






        holder.tengosolicitud.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                final String idchat = ref_mis_botones.push().getKey();

                final DatabaseReference A = database.getReference("Solicitudes").child(userss.getId()).child(user.getUid())  ;
                A.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Solicitudes sol = new Solicitudes("amigos",idchat);
                        A.setValue(sol);


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


                final DatabaseReference B = database.getReference("Solicitudes").child(user.getUid()).child(userss.getId());
                B.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Solicitudes sol = new Solicitudes("amigos",idchat);
                        B.setValue(sol);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                vibrator.vibrate(300);
            }
        });

        holder.amigos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPref=v.getContext().getSharedPreferences("usuario_sp",Context.MODE_PRIVATE);
                final SharedPreferences.Editor editor = mPref.edit();



                final DatabaseReference ref = database.getReference("Solicitudes").child(user.getUid()).child(userss.getId()).child("idchat");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String id_unico =snapshot.getValue(String.class);
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

    public class viewHolderAdapter extends RecyclerView.ViewHolder {

        TextView tv_usuario;
        ImageView img_user;
        CardView cardView;
        Button add,send,amigos,tengosolicitud;
        ProgressBar progressBar;

        public viewHolderAdapter(@NonNull View itemView) {
            super(itemView);
            tv_usuario=itemView.findViewById(R.id.tv_userrow);
            img_user = itemView.findViewById(R.id.img_usuariorow);
            cardView = itemView.findViewById(R.id.carviewrow);
            add=itemView.findViewById(R.id.btn_addrow);
            send=itemView.findViewById(R.id.btn_sendrow);
            amigos=itemView.findViewById(R.id.btn_amigosrow);
            tengosolicitud=itemView.findViewById(R.id.btn_tengosolicitudrow);
            progressBar = itemView.findViewById(R.id.progressbarrow);

        }
    }
}
