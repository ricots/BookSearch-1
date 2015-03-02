package com.codepath.android.booksearch.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.codepath.android.booksearch.R;
import com.codepath.android.booksearch.models.Book;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class BookDetailActivity extends ActionBarActivity {
    private ImageView ivBookCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private Book result;
    private ShareActionProvider miShareAction;
    private ProgressBar pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        // Fetch views
        ivBookCover = (ImageView) findViewById(R.id.ivBookCover);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvAuthor = (TextView) findViewById(R.id.tvAuthor);

        // Extract book object from intent extras
        result = (Book) getIntent().getSerializableExtra("result");

        getSupportActionBar().setTitle(result.getTitle());

        pb = (ProgressBar) findViewById(R.id.pbLoading);
        showProgressBar();

        // Use book object to populate data into views
        ImageView ivBookCover = (ImageView) findViewById(R.id.ivBookCover);
        Log.i("DEBUG", "Load Image");
        Picasso.with(this).load(result.getLargeCoverUrl()).into(ivBookCover, new Callback(){

            @Override
            public void onSuccess() {
                setupShareIntent();
                hideProgressBar();
            }

            @Override
            public void onError() {

            }
        });

        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setText(result.getTitle());

        TextView tvAuthor = (TextView) findViewById(R.id.tvAuthor);
        tvAuthor.setText(result.getAuthor());



    }


    public void showProgressBar() {
        pb.setVisibility(ProgressBar.VISIBLE);
    }

    public void hideProgressBar() {
        pb.setVisibility(ProgressBar.INVISIBLE);
    }

    // Returns the URI path to the Bitmap displayed in specified ImageView
    public Uri getLocalBitmapUri(ImageView imageView) {
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = null;
        if (drawable instanceof BitmapDrawable){
            bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else {
            return null;
        }
        // Store image to default external storage directory
        Uri bmpUri = null;
        try {
            File file =  new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "share_image_" + System.currentTimeMillis() + ".png");
            file.getParentFile().mkdirs();
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_book_detail, menu);
        Log.i("DEBUG", "Create menu");
        MenuItem shareItem = (MenuItem) menu.findItem(R.id.menu_item_share);
        miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    // Gets the image URI and setup the associated share intent to hook into the provider
    public void setupShareIntent() {
        // Fetch Bitmap Uri locally
        Uri bmpUri = getLocalBitmapUri(ivBookCover); // see previous remote images section
        // Create share intent as described above
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/*");
        // Attach share event to the menu item provider
        miShareAction.setShareIntent(shareIntent);
    }
}
