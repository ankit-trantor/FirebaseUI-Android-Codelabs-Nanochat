package com.kiewic.nanochat;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseListAdapter;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import android.view.View;
import java.util.Map;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import com.firebase.client.AuthData;
import android.widget.ImageButton;


// Tutor: kkam

public class MainActivity extends ListActivity {

    private Firebase mFirebaseRef;
    FirebaseListAdapter<ChatMessage> mListAdapter;
    String mUsername;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Firebase.setAndroidContext(this);
        //mFirebaseRef = new Firebase("https://hyperchat.firebaseio.com");
        mFirebaseRef = new Firebase("https://kiewicchat.firebaseio.com");

        final EditText textEdit = (EditText) this.findViewById(R.id.text_edit);
        Button sendButton = (Button) this.findViewById(R.id.send_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = textEdit.getText().toString();
                //ChatMessage message = new ChatMessage("Android User", text);
                //mFirebaseRef.push().setValue(message);
                mFirebaseRef.push().setValue(new ChatMessage(MainActivity.this.mUsername, text, null));
                textEdit.setText("");
            }
        });

        ImageButton cameraButton = (ImageButton) this.findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capture a photo.
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });

        //mListAdapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
        //    android.R.layout.two_line_list_item, mFirebaseRef) {
        mListAdapter = new FirebaseListAdapter<ChatMessage>(this, ChatMessage.class,
            R.layout.chat_message, mFirebaseRef) {
            @Override
            protected void populateView(View v, ChatMessage model) {
                ((TextView)v.findViewById(R.id.text1)).setText(model.getName());

                String text = model.getText();
                TextView text2 = (TextView)v.findViewById(R.id.text2);
                if (text == null || text == "") {
                    text2.setVisibility(View.GONE);
                }
                else {
                    text2.setText(text);
                }

                String base64String = model.getImage();
                ImageView imageView2 = (ImageView)v.findViewById(R.id.imageView2);
                if (base64String == null || base64String == "") {
                    imageView2.setVisibility(View.GONE);
                }
                else {
                    imageView2.setImageBitmap(BitmapConverter.getBitmap((base64String)));
                }
            }
        };
        setListAdapter(mListAdapter);

        Button loginButton = (Button) findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MainActivity.this)
                    .setMessage("Enter your email address and password")
                    .setTitle("Log in")
                    .setView(MainActivity.this.getLayoutInflater().inflate(R.layout.dialog_signin, null))
                    .setNegativeButton("Cancel", null)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AlertDialog dlg = (AlertDialog) dialog;
                            final String email = ((TextView) dlg.findViewById(R.id.email)).getText().toString();
                            final String password = ((TextView) dlg.findViewById(R.id.password)).getText().toString();

                            // Sign in to Firebase.
                            mFirebaseRef.createUser(email, password, new Firebase.ResultHandler() {
                                @Override
                                public void onSuccess() {
                                    mFirebaseRef.authWithPassword(email, password, null);
                                }
                                @Override
                                public void onError(FirebaseError firebaseError) {
                                    mFirebaseRef.authWithPassword(email, password, null);
                                }
                            });

                        }
                    })
                    .create()
                    .show();
            }
        });

        mFirebaseRef.addAuthStateListener(new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                if (authData != null) {
                    mUsername = ((String) authData.getProviderData().get("email"));
                    findViewById(R.id.login).setVisibility(View.INVISIBLE);
                } else {
                    mUsername = null;
                    findViewById(R.id.login).setVisibility(View.VISIBLE);
                }
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            mFirebaseRef.push().setValue(new ChatMessage(
                MainActivity.this.mUsername,
                "",
                BitmapConverter.getBase64(imageBitmap)));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListAdapter.cleanup();

        //mFirebaseRef.unauth();
    }
}
