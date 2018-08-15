package com.redbluekey.sciodev.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.redbluekey.sciodev.R;
import com.redbluekey.sciodev.cameraGalleryPicker.GalleryImage;
import com.redbluekey.sciodev.cameraGalleryPicker.ImagesPickerManager;
import com.redbluekey.sciodev.dialog.ChooseDialog;
import com.redbluekey.sciodev.helpers.LocalStorage;
import com.redbluekey.sciodev.helpers.Retrofit.GetNoticeDataService;
import com.redbluekey.sciodev.helpers.Retrofit.RetrofitInstance;
import com.redbluekey.sciodev.models.PostImageResponse;
import com.redbluekey.sciodev.models.PostResponse;

import org.json.JSONObject;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostActivity extends AppCompatActivity implements ChooseDialog.ChooseDialogCallback{


    public static final int REQUEST_OPEN_CAMERA = 600;
    public static final int SELECT_REAL_IMAGE_REQUEST = 500;


    protected ProgressDialog      mProgressDialog;

    private int m_nPostMode;
    private String m_strSectionName;

    private TextView m_tvSection;
    private EditText m_etTitle;
    private EditText m_etBody;
    private ImageView m_ivAttachButton;
    private ImageView m_ivAttachImage;
    private PercentRelativeLayout m_rlAttach;

    private Bitmap m_bmpAttach;
    private Uri imageUri;
    private String m_strImgPath;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        initVariable();
        initUI();
        updateUI();
    }

    void initVariable() {

        m_nPostMode = getIntent().getIntExtra("post_mode", 0);
        m_strSectionName = getIntent().getStringExtra("section_name");
    }

    void initUI() {

        m_tvSection = findViewById(R.id.tv_section);
        m_etTitle = findViewById(R.id.et_title);
        m_etBody = findViewById(R.id.et_body);
        m_ivAttachButton = findViewById(R.id.iv_attach_button);
        m_ivAttachImage = findViewById(R.id.iv_attach_image);
        m_rlAttach = findViewById(R.id.rl_attach);

        m_ivAttachButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showChooseDialog();
            }
        });

    }

    void updateUI() {


        try {
            ActionBar actionBar = getSupportActionBar();
            actionBar.setDisplayHomeAsUpEnabled(true);

            if (m_nPostMode == 0) {     //Image

                actionBar.setTitle(R.string.post_image);
                m_etTitle.setHint(R.string.hint_title_image);
                m_etBody.setVisibility(View.GONE);
            } else if (m_nPostMode == 1) {     //Link

                actionBar.setTitle(R.string.post_link);
                m_rlAttach.setVisibility(View.GONE);
                m_etTitle.setHint(R.string.hint_title_link);
                m_etBody.setHint(R.string.hint_body_link);

            } else if (m_nPostMode == 2) {     //Text

                actionBar.setTitle(R.string.post_text);
                m_rlAttach.setVisibility(View.GONE);
                m_etTitle.setHint(R.string.hint_title_text);
                m_etBody.setHint(R.string.hint_body_text);

            }

            m_tvSection.setText(m_strSectionName.substring(0,1).toUpperCase() + m_strSectionName.substring(1));

        } catch (Exception e) {}

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_post, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;

            case R.id.action_post:

                goToPost();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_CANCELED)
            return;

        if (requestCode == SELECT_REAL_IMAGE_REQUEST){

            GalleryImage galleryImage = ImagesPickerManager.handlePickerResult(requestCode, resultCode, data);
            if (galleryImage != null) {

                m_bmpAttach = getBitmap(galleryImage.getImagePath());
                m_ivAttachImage.setImageBitmap(m_bmpAttach);

                m_strImgPath = galleryImage.getImagePath();

            }
        }  else if( requestCode == REQUEST_OPEN_CAMERA ) {

            if(imageUri == null)
                return;

            m_bmpAttach = getBitmap(imageUri.getPath());
            m_ivAttachImage.setImageBitmap(m_bmpAttach);
            m_strImgPath = imageUri.getPath();

        }
    }

    void showChooseDialog() {

        View contentView = LayoutInflater.from(this).inflate(R.layout.dlg_choose_image, null);
        ChooseDialog dlgChoose = new ChooseDialog(this,  contentView, this);
        dlgChoose.show();

    }

    void goToPost() {


        //Check Vailidation;

        String strTitle = m_etTitle.getText().toString();
        String strBody = m_etBody.getText().toString();

        if (strTitle.isEmpty()) {
            m_etTitle.setError(getText(R.string.alert_post_title));
            return;
        }

        if (m_nPostMode == 0) {             //Image

            if (m_bmpAttach == null || m_bmpAttach.isRecycled() || m_strImgPath == null) {

                Toast.makeText(this, R.string.alert_post_image, Toast.LENGTH_SHORT).show();
                return;
            }
        } else if (m_nPostMode == 1) {      //Link

            if (m_etBody.getText().toString().isEmpty()) {

                m_etTitle.setError(getText(R.string.alert_post_body));
                return;
            }
        }



        final String authToken = LocalStorage.getAuthData(this)[0];

        /*String strTokenAndUser[] = LocalStorage.getAuthData(this);
        if(strTokenAndUser == null || strTokenAndUser.length != 2 || strTokenAndUser[0] == null) {

            Toast.makeText(this, R.string.alert_token, Toast.LENGTH_SHORT).show();
            return;
        }*/

        //if the user is not logged in redirect to the login form
        //TODO

        showProgressDialog(getText(R.string.str_wait).toString());

        if (m_nPostMode == 0) {

            //post image;
            postImage(strTitle, strBody, m_strImgPath, authToken);

        } else
            post(strTitle, strBody, "", "", authToken);
    }

    void postImage(final String strTitle, final String strBody, String strImagePath, final String token) {

        File file = new File(strImagePath);
        RequestBody mFile = RequestBody.create(null, file);
        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", file.getName(), mFile);


        GetNoticeDataService service = RetrofitInstance.getRetrofitInstance().create(GetNoticeDataService.class);
        Call<PostImageResponse> call = service.PostCommentImage("Android", token, fileToUpload);

        call.enqueue(new Callback<PostImageResponse>() {
            @Override
            public void onResponse(Call<PostImageResponse> call, Response<PostImageResponse> response) {

                if (response.isSuccessful()) {

                    String strImageId = response.body().getMessage();
                    post(strTitle, strBody, strImageId, "", token);
                }
                else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PostActivity.this, R.string.error_post_image, Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<PostImageResponse> call, Throwable t) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(PostActivity.this, R.string.error_post_image, Toast.LENGTH_SHORT).show();
                        hideProgressDialog();
                    }
                });
            }
        });
    }

    void post(String title, String body, String imageId, String cIDr, String token) {

        //post ...
        GetNoticeDataService service = RetrofitInstance.getRetrofitInstance().create(GetNoticeDataService.class);
        JSONObject jsonBody = new JSONObject();

        try {

            jsonBody.put("tag", m_strSectionName);
            jsonBody.put("title", title);
            jsonBody.put("c", body);
            jsonBody.put("linkURL", body);
            jsonBody.put("imageID", imageId);
            jsonBody.put("cIDr", "");

            final RequestBody regBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonBody.toString());
            Call<PostResponse> call  = service.PostComment("Android", token, regBody);

            call.enqueue(new Callback<PostResponse>() {
                @Override
                public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {


                    if (response.isSuccessful()) {

                        //success
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                Toast.makeText(PostActivity.this, R.string.post_success, Toast.LENGTH_SHORT).show();
                                hideProgressDialog();
                                finish();
                            }
                        });

                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(PostActivity.this, R.string.error_post, Toast.LENGTH_SHORT).show();
                                hideProgressDialog();
                                finish();
                            }
                        });
                    }
                }

                @Override
                public void onFailure(Call<PostResponse> call, Throwable t) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(PostActivity.this, R.string.error_post, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

        } catch (Exception e) {

        }
    }


    @Override
    public void onChoose(int type) {

        //type;1 ==> Take Photo, type;2 ==> Choose Album

        if (type == 1) {

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photo = new File(Environment.getExternalStorageDirectory(),  "Scio.jpg");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));
            imageUri = Uri.fromFile(photo);

            startActivityForResult(intent, REQUEST_OPEN_CAMERA);

        } else  if (type == 2) {

            ImagesPickerManager.startPicker(this);
        }
    }

    public Bitmap getBitmap(String imgPath)
    {

        Bitmap bitmap = null;

        try {
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inPreferredConfig = Bitmap.Config.ARGB_8888;
            bitmap = BitmapFactory.decodeFile(imgPath, op);
        } catch (Exception e) {

        }
        return bitmap;
    }



    public void showProgressDialog(String message) {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
        }
        else {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
    }

    public void hideProgressDialog() {
        try {
            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
        }
        catch (Exception e) {
        }
    }
}
