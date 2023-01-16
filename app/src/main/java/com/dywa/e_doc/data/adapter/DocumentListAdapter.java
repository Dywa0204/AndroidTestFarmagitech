package com.dywa.e_doc.data.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dywa.e_doc.DocumentListActivity;
import com.dywa.e_doc.R;
import com.dywa.e_doc.data.response.model.DocumentData;
import com.dywa.e_doc.data.response.model.PatientData;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DocumentListAdapter extends RecyclerView.Adapter<DocumentListAdapter.ViewHolder> {

    private List<DocumentData> results = new ArrayList<>();
    private OnAdapterListener listener;
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String TOKEN_KEY = "token_key";
    SharedPreferences sharedpreferences;
    String token;
    Context context;

    public DocumentListAdapter(List<DocumentData> results, OnAdapterListener listener) {
        this.results = results;
        this.listener = listener;
    }

    @NonNull
    @Override
    public DocumentListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        return new DocumentListAdapter.ViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_document_list, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull DocumentListAdapter.ViewHolder holder, int position) {
        DocumentData result = results.get(position);
        holder.doc_name.setText(result.getTitle());
        holder.doc_created.setText(getDate(result.getCreated_time()));

        sharedpreferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString("token_key", null);

        String thumUrl = "https://androidtest.farmagitechs.co.id/api/patient_document/serve_doc_thumbnail/" + result.getPatient_document_id();

        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request newRequest  = chain.request().newBuilder()
                        .addHeader("Authorization", "Bearer " + token)
                        .build();
                return chain.proceed(newRequest);
            }
        }).build();

        Picasso picasso = new Picasso.Builder(context)
            .downloader(new OkHttp3Downloader(client))
            .build();

        picasso
            .load( thumUrl )
            .placeholder(R.drawable.img_doc)
            .error(R.drawable.img_error)
            .fit(). centerCrop()
            .into(holder.doc_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick(result);
            }
        });
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView doc_image;
        TextView doc_name;
        TextView doc_created;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            doc_image = itemView.findViewById(R.id.doc_image);
            doc_name = itemView.findViewById(R.id.doc_name);
            doc_created = itemView.findViewById(R.id.doc_created);
        }
    }

    public void setData(List<DocumentData> data) {
        results.clear();
        results.addAll(data);
        notifyDataSetChanged();
    }

    public interface OnAdapterListener {
        void onClick(DocumentData documentData);
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time * 1000);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }
}
