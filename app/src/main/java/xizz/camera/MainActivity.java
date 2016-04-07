package xizz.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;

public class MainActivity extends AppCompatActivity {

	private static final int REQUEST_TAKE_PHOTO = 1;
	private static final int REQUEST_PICK_PHOTO = 2;

	private ImageView imageView;
	private File photoFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		imageView = (ImageView) findViewById(R.id.image);
	}

	public void takePhoto(View view) {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		photoFile = createImageFile();
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
		startActivityForResult(intent, REQUEST_TAKE_PHOTO);
	}

	private File createImageFile() {
		String imageFileName = "JPEG_" + System.currentTimeMillis() + ".jpg";
		File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		return new File(storageDir.getAbsolutePath(), imageFileName);
	}

	public void pickPhoto(View view) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intent, REQUEST_PICK_PHOTO);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;

		if (requestCode == REQUEST_TAKE_PHOTO) {
			try {
				decodeUri(Uri.parse(photoFile.toURI().toString()));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else if (requestCode == REQUEST_PICK_PHOTO) {
			try {
				decodeUri(data.getData());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	public void decodeUri(Uri uri) throws FileNotFoundException {
		// Get the dimensions of the View
		int targetW = imageView.getWidth();
		int targetH = imageView.getHeight();

		// Get the dimensions of the bitmap
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();

		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;

		// Determine how much to scale down the image
		int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;

		Bitmap image = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, bmOptions);
		imageView.setImageBitmap(image);
	}
}
