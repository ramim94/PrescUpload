package com.mobodreamers.prescupload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mobodreamers.prescupload.model.Prescription;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//task left
//create back end
//send to server
//retrieve from server


public class UploadHome extends AppCompatActivity {
    private final String prescriptionUploadURL ="http://10.0.3.2/Hellopathy/upload_prescription.php";
    private final String prescriptionDownloadURL="http://10.0.3.2/Hellopathy/download_prescription.php";
    private static final int GALLERY_INTENT_REQUEST_CODE=5;
    private static final int CAMERA_INTENT_REQUEST_CODE=7;

    List<Prescription> prescriptions;
    Intent cameraIntent,galleryIntent;

    RecyclerView prescriptionRecycler;
    PrescriptionRecyclerAdapter myAdapter;

    RequestQueue uploader;
    ImageView selectedImagePlaceholder;
    Button upload;
    EditText prescriptionName,prescriptionNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        prescriptions=new ArrayList<>();
        prescriptionRecycler= (RecyclerView)findViewById(R.id.recycler_prescription_view);
        myAdapter=new PrescriptionRecyclerAdapter(prescriptions,this);
        RecyclerView.LayoutManager llm= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);

        prescriptionRecycler.setLayoutManager(llm);

        prescriptionRecycler.setItemAnimator(new DefaultItemAnimator());
        prescriptionRecycler.setAdapter(myAdapter);

        uploader= Volley.newRequestQueue(this);
        getImagesFromServer();
        selectedImagePlaceholder=(ImageView)findViewById(R.id.selected_image_placeholder);
        upload=(Button) findViewById(R.id.button_upload);
        prescriptionName=(EditText) findViewById(R.id.input_prescription_name);
        prescriptionNotes=(EditText) findViewById(R.id.input_prescription_notes);

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap img=((BitmapDrawable)selectedImagePlaceholder.getDrawable()).getBitmap();

            String imageNotes,imageName,encodedImage;
                imageName=prescriptionName.getText().toString();
                imageNotes=prescriptionNotes.getText().toString();

                if(imageName!=null && !imageName.isEmpty()){
                    encodedImage=getEncodedString(imageName,img);
                    imageToServer(imageName,imageNotes,encodedImage);
                }
            }
        });

    }

    private void getImagesFromServer() {
        StringRequest download=new StringRequest(Request.Method.POST, prescriptionDownloadURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    Prescription newPrescription=new Prescription();
                    JSONArray fullData=new JSONArray(response);
                    for(int i=0; i< fullData.length();i++){
                        JSONObject perData=fullData.getJSONObject(i);
                        newPrescription.setName(perData.getString("name"));
                        newPrescription.setImage(perData.getString("image_dir"));
                        prescriptions.add(newPrescription);
                    }
                    myAdapter=new PrescriptionRecyclerAdapter(prescriptions,UploadHome.this);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                myAdapter.notifyDataSetChanged();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UploadHome.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        uploader.add(download);
    }

    private void imageToServer(final String imageName,final String imageNotes, final String encodedImage) {
        StringRequest img2server= new StringRequest(Request.Method.POST, prescriptionUploadURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(UploadHome.this, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(UploadHome.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> image=new HashMap<>();
                image.put("image_name",imageName);
                image.put("image_note",imageNotes);
                image.put("encoded_image",encodedImage);
                return image;
            }
        };
        uploader.add(img2server);
    }

    public String getEncodedString(String imageName, Bitmap image){
        String encodedImage;
        ByteArrayOutputStream os= new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG,100,os);
        byte[] imagebyte=os.toByteArray();
        encodedImage= Base64.encodeToString(imagebyte,Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_upload_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_camera) {
            return true;
        }
        if (id == R.id.action_gallery) {
            galleryIntent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent,GALLERY_INTENT_REQUEST_CODE);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==RESULT_OK && data!= null && requestCode==GALLERY_INTENT_REQUEST_CODE){
            Uri selectedImage=data.getData();
            selectedImagePlaceholder.setImageURI(selectedImage);
        }
    }
}
