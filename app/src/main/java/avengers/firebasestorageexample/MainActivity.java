 package avengers.firebasestorageexample;

 import android.app.ProgressDialog;
 import android.content.Intent;
 import android.graphics.Bitmap;
 import android.net.Uri;
 //import android.net.rtp.AudioCodec;
 import android.provider.MediaStore;
 import android.support.annotation.NonNull;
 import android.support.v7.app.AppCompatActivity;
 import android.os.Bundle;
 import android.util.Log;
 import android.view.View;
 import android.widget.Button;
 import android.widget.EditText;
 import android.widget.ImageView;
 import android.widget.TextView;
 import android.widget.Toast;


 import com.google.android.gms.tasks.OnFailureListener;
 import com.google.android.gms.tasks.OnSuccessListener;
 import com.google.android.gms.tasks.Task;
 import com.google.firebase.storage.FirebaseStorage;
 import com.google.firebase.storage.OnProgressListener;
 import com.google.firebase.storage.StorageReference;
 import com.google.firebase.storage.UploadTask;

 import java.io.IOException;

 public class MainActivity extends AppCompatActivity implements View.OnClickListener /*  implementing click listener */ {
     //a constant to track the file chooser intent
     private static final int PICK_IMAGE_REQUEST = 234;

     //Buttons
     private Button buttonChoose;
     private Button buttonUpload;

     //ImageView
     private ImageView imageView;

     //a Uri object to store file path
     private Uri filePath;
     private StorageReference storageReference;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
         super.onCreate(savedInstanceState);
         setContentView(R.layout.activity_main);

         storageReference = FirebaseStorage.getInstance().getReference();
         //getting views from layout
         buttonChoose = (Button) findViewById(R.id.buttonChoose);
         buttonUpload = (Button) findViewById(R.id.buttonUpload);

         //imageView = (ImageView) findViewById(R.id.imageView);

         //attaching listener
         buttonChoose.setOnClickListener(this);
         buttonUpload.setOnClickListener(this);
     }

     //method to show file chooser
     private void showFileChooser() {
         Intent intent = new Intent();
         intent.setType("audio/*");
         intent.setAction(Intent.ACTION_GET_CONTENT);
         startActivityForResult(Intent.createChooser(intent, "Select an audio file"), PICK_IMAGE_REQUEST);
     }

     private void uploadAudio() {
         if (filePath != null) {


             EditText name = (EditText) findViewById(R.id.audioname);
             String y = name.getText().toString();
             if (y.length() == 0) {
                 name.setError("Cannot be empty!");
             } else {
                 final ProgressDialog progressDialog = new ProgressDialog(this);
                 progressDialog.setTitle("Uploading...");
                 progressDialog.show();
                 final StorageReference riversRef = storageReference.child("audios/" + y + ".mp3");

                 riversRef.putFile(filePath)
                         .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                             @Override
                             public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                 progressDialog.dismiss();
                                 // Get a URL to the uploaded content
                                 Task<Uri> downloadUrl = riversRef.getDownloadUrl();
                                 TextView imgurl = (TextView) findViewById(R.id.imageurl);
                                 imgurl.setText(downloadUrl.toString());
                                 Log.d("KEY", downloadUrl.toString());
                                 Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();

                             }
                         })
                         .addOnFailureListener(new OnFailureListener() {

                             @Override
                             public void onFailure(@NonNull Exception exception) {
                                 // Handle unsuccessful uploads
                                 // ...
                                 progressDialog.dismiss();
                                 Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                             }
                         })
                         .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                             @Override
                             public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                 double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                                 progressDialog.setMessage(((int) progress) + "% Uploaded");
                                 //progressDialog.setMessage("Uploaded " + ((int) progress) + "%...");
                             }
                         });

             }


         } else {
             //display aerror toast
             Toast.makeText(getApplicationContext(), "Invalid file Path ", Toast.LENGTH_LONG).show();
         }
     }

     //handling the image chooser activity result
     @Override
     protected void onActivityResult(int requestCode, int resultCode, Intent data) {
         super.onActivityResult(requestCode, resultCode, data);
         if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
             filePath = data.getData();
             /*try {
                 Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                 imageView.setImageBitmap(bitmap);

             } catch (IOException e) {
                 e.printStackTrace();
             }*/
         }
     }

     @Override
     public void onClick(View view) {
         //if the clicked button is choose
         if (view == buttonChoose) {
             showFileChooser();
         }
         //if the clicked button is upload
         else if (view == buttonUpload) {
             uploadAudio();
         }
     }
 }