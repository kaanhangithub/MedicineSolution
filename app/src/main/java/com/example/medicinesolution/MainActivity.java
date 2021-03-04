package com.example.medicinesolution;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity  {
    private ImageView camera;
    private ImageView image;
    private Button getMedicine;
    private Bitmap imageBitmap;
    private Uri imageUri;
    private String txt;
    private EditText textEdit;
    private TextView prescription;
    private String medicines[];
    private TextToSpeech tts;
    private String polysporin,retinA,blexten,nurofen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Medical Solution");
        getWindow().setStatusBarColor(ContextCompat.getColor(MainActivity.this,R.color.appcolor));
        tts= new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status==TextToSpeech.SUCCESS){
                   int result = tts.setLanguage(Locale.US);
                   if(result==TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                       Log.e("TTS","language not supported");
                   }else{
                       //getMedicine.setEnabled(true);
                   }
                }else{
                    Log.e("TTS","failed");
                }


            }
        });
        camera = findViewById(R.id.imageView4);
        image  = findViewById(R.id.imageView3);
        getMedicine = findViewById(R.id.button);
        textEdit = findViewById(R.id.editTextTextPersonName);
        prescription = findViewById(R.id.textView);
        polysporin = getResources().getString(R.string.Polysporin);
        retinA = getResources().getString(R.string.Retin_A);
        blexten = getResources().getString(R.string.blexten);
        nurofen = getResources().getString(R.string.nurofen);
        medicines = new String[]{"Polysporin","Retin-A","BLEXTEN","NUROFEN"};
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().start(MainActivity.this);
            }
        });
        getMedicine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakPrescription();
            }
        });


    }

    @Override
    protected void onDestroy() {
        if(tts!=null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private void speakPrescription() {
        String str = textEdit.getText().toString();
        if(str.contains(medicines[0])){
            prescription.setText(polysporin);
            tts.speak(polysporin,TextToSpeech.QUEUE_FLUSH,null,null);
        }
        if(str.contains(medicines[1])){
            prescription.setText(retinA);
            tts.speak(retinA,TextToSpeech.QUEUE_FLUSH,null,null);
        }
        if(str.contains(medicines[2])){
            prescription.setText(blexten);
            tts.speak(blexten,TextToSpeech.QUEUE_FLUSH,null,null);
        }
        if(str.contains(medicines[3])){
            prescription.setText(nurofen);
            tts.speak(nurofen,TextToSpeech.QUEUE_FLUSH,null,null);
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                imageUri=result.getUri();

                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                image.setImageBitmap(imageBitmap);
               opticalCharacterRecognition();
            }
            else if(resultCode== CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception e = result.getError();
                Toast.makeText(this,"Error :" +e,Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void opticalCharacterRecognition(){
        FirebaseVisionImage visionImage = FirebaseVisionImage.fromBitmap(imageBitmap);
        FirebaseVisionTextRecognizer textDetector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        textDetector.processImage(visionImage).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                textFromOcr(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("Problem has occurred : ",e.getMessage());
                Toast.makeText(MainActivity.this,"Error :"+e,Toast.LENGTH_SHORT).show();

            }
        });

    }
    private void textFromOcr(FirebaseVisionText firebaseVisionText){
        List<FirebaseVisionText.TextBlock> blocks = firebaseVisionText.getTextBlocks();
        if(blocks.size()==0){
            Toast.makeText(this,"Nothing to Recognize",Toast.LENGTH_SHORT).show();

        }
        else{
            txt = "";
            for (int i =0; i<blocks.size(); i++ ){
                List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
                for(int j=0 ; j<lines.size(); j++){
                    List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                    for(int k=0 ; k<elements.size(); k++){
                        txt = txt +" "+ elements.get(k).getText();
                    }
                }
            }
            textEdit.setText(txt);
        }
    }
    public void setTitle(String title){
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setTextSize(23);

        textView.setTypeface(null, Typeface.BOLD);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(textView);
    }



}



