package com.dywa.e_doc;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import com.dywa.e_doc.data.request.DocumentDetailRequest;
import com.dywa.e_doc.data.service.ApiService;
import com.rajat.pdfviewer.PdfQuality;
import com.rajat.pdfviewer.PdfRendererView;
import com.rajat.pdfviewer.PdfViewerActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DocumentDetailActivity extends AppCompatActivity {
    private static Object Companion;
    private final String TAG = "SDKSDK";

    DocumentDetailRequest documentDetailRequest;
    public static final String SHARED_PREFS = "shared_prefs";
    SharedPreferences sharedpreferences;
    String token;

    ImageButton back_btn;
    TextView doc_name;
    PdfRendererView pdfRendererView;
    PdfQuality pdfQuality;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_detail);

        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString("token_key", null);

        String patient_document_id = getIntent().getStringExtra("intent_patient_document_id");
        String docName = getIntent().getStringExtra("intent_doc_name");

        Log.d(TAG, patient_document_id);
        documentDetailRequest = new DocumentDetailRequest(Integer.parseInt(patient_document_id));
        getDocumentDetail(documentDetailRequest, token);

    }

    private void getDocumentDetail(DocumentDetailRequest documentDetailRequest, String token) {
        ApiService.endpoint(token).getDocumentView(documentDetailRequest)
            .enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if(response.isSuccessful()) {
                        Log.d(TAG, "Success");
                        new AsyncTask<Void, Long, Void>() {
                            @Override
                            protected Void doInBackground(Void... voids) {
                                saveToDisk(response.body());
                                return null;
                            }
                        }.execute();
                    } else {
                        try {
                            Log.d(TAG, "error " + response.errorBody().string());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        //new SweetAlert().error(DocumentDetailActivity.this, "Oops...", "Error ...");
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Log.d( TAG, "error : " + t);
                    //new SweetAlert().error(DocumentDetailActivity.this, "Oops...", "Error ...");
                }
            });
    }

    public void saveToDisk(ResponseBody body) {
        try {
            new File("/data/data/" + getPackageName() + "/pdfFile").mkdir();
            File destinationFile = new File("/data/data/" + getPackageName() + "/pdfFile/downloadedFile.pdf");

            InputStream is = null;
            OutputStream os = null;

            try {
                Log.d(TAG, "File Size=" + body.contentLength());

                is = body.byteStream();
                os = new FileOutputStream(destinationFile);

                byte data[] = new byte[4096];
                int count;
                int progress = 0;
                while ((count = is.read(data)) != -1) {
                    os.write(data, 0, count);
                    progress +=count;
                    Log.d(TAG, "Progress: " + progress + "/" + body.contentLength() + " >>>> " + (float) progress/body.contentLength());
                }

                os.flush();

                Log.d(TAG, "File saved successfully!");
                return;
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Failed to save the file!");
                return;
            } finally {
                startActivity(
                        PdfViewerActivity.Companion.launchPdfFromPath(
                                DocumentDetailActivity.this,
                                "/data/data/" + getPackageName() + "/pdfFile/downloadedFile.pdf",
                                "Pdf title/name",
                                "",
                                false,
                                false
                        )
                );

                if (is != null) is.close();
                if (os != null) os.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to save the file!");
            return;
        }
    }
}