package com.example.swiftcheckin;
/**
 * This is the promotional QR
 */
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

//Citation: The following code for sharing a QR code, 2024, Youtube, "Send Image To Other Apps in Android Studio (Updated) || Android 11 onwards", Android Tutorials, https://www.youtube.com/watch?v=eSi28xqGjbE
public class PromotionQR extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.promotion_qr_activity);
        ImageView imageView = findViewById(R.id.image_view);
        Button shareImage = findViewById(R.id.btn_share);
        shareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();
                shareImageAndText(bitmap);

            }
        });
    }
    private void shareImageAndText(Bitmap bitmap){
        Uri uri = getImageToShare(bitmap);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM,uri);
        intent.putExtra(Intent.EXTRA_TEXT,"Image Text");
        intent.putExtra(Intent.EXTRA_SUBJECT,"Image Subject");
        intent.setType("image/*");
        startActivity(Intent.createChooser(intent,"Share via"));
    }
    private Uri getImageToShare(Bitmap bitmap){
        File folder = new File(getCacheDir(),"images");
        Uri uri = null;
        try{
            folder.mkdirs();
            File file = new File(folder, "image.jpg");
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,90,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();

            uri = FileProvider.getUriForFile(this,"com.example.swiftcheckin",file);

        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return uri;

    }
}
