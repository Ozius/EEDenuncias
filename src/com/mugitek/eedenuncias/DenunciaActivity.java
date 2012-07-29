package com.mugitek.eedenuncias;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.Html;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class DenunciaActivity extends SherlockActivity {
	private ActionBar mActionBar;
	private ImageView mImageView;
	//private Bitmap mImageBitmap;
	private String mCurrentPhotoPath;
	private File mFile;
	private String mEmail;
	private boolean mThumbnail;
	private Spinner mSpinner;
	private EditText mTextHora;
	private EditText mTextLetra;
	private EditText mTextNumero;
	private EditText mDesc;
	private View mViewAttachment;
	private View mViewSeparadorAttachment;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_denuncia);
		
		mActionBar = this.getSupportActionBar();
		mActionBar.setIcon(R.drawable.ic_actionbar);
		
		mSpinner = (Spinner) findViewById(R.id.spinnerMotivo);

		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.opciones_motivo, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		mSpinner.setAdapter(adapter);
		
		mTextHora = (EditText) findViewById(R.id.editTextHora);
		mTextLetra = (EditText) findViewById(R.id.editTextLetra);
		mTextNumero = (EditText) findViewById(R.id.editTextNumero);
		mDesc = (EditText) findViewById(R.id.editTextDescripcion);
		mViewAttachment = (View) findViewById(R.id.layoutAttachment);
		mViewSeparadorAttachment = (View) findViewById(R.id.layoutSeparadorAttachment);
		
		mTextHora.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
		
		
		mImageView = (ImageView) findViewById(R.id.imageViewDenuncia);
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		mEmail = preferences.getString("preference_email", "marcan@euskalencounter.org");
		mThumbnail = preferences.getBoolean("preference_thumbnail", false);
		
		mCurrentPhotoPath = "";
		
		//Toast.makeText(this, "onCreate", Toast.LENGTH_SHORT).show();
		
		//Cogemos el Intent por si se ha llamado a la Activity desde otra aplicación mediante el botón compartir
		Intent intent = getIntent();
	    String action = intent.getAction();
	    String type = intent.getType();
	    	    
	    if (Intent.ACTION_SEND.equals(action) && type != null) {
	        if (type.startsWith("image/")) {
	        	Uri imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
	            if (imageUri != null) {
	            	
	            	//Toast.makeText(this, getRealPathFromURI(imageUri), Toast.LENGTH_SHORT).show();
	                // Update UI to reflect image being shared
	            	
	            	mCurrentPhotoPath = getRealPathFromURI(imageUri);
	            	
	            	if(mThumbnail){
		            	setPic(mCurrentPhotoPath);
		    			mViewSeparadorAttachment.setVisibility(View.VISIBLE);
		    			mImageView.setVisibility(View.VISIBLE);
	            	}
	            	
	            	//Mostramos el nombre del fichero
	            	File f = new File(mCurrentPhotoPath);
	    			setAttachmentNameText(f.getName(), f.length());
	    			mViewAttachment.setVisibility(View.VISIBLE);
	            }
	        }
	    }
	    
	}
	
	private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }
	
	public void cancelClicked(View v)
    {
        switch(v.getId())
        {
            case R.id.buttonCancelar:
            	this.setResult(RESULT_CANCELED);
    			finish();
        }
    }
	
	public void removeAttachment(View v){
		if(mFile != null){
			mFile.delete();
			mFile = null;
		} else if(!mCurrentPhotoPath.equals("")){
			mCurrentPhotoPath = "";
		}
		
		mViewSeparadorAttachment.setVisibility(View.GONE);
		mImageView.setVisibility(View.GONE);
		mViewAttachment.setVisibility(View.GONE);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_denuncia, menu);
        return true;
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		/*case android.R.id.home:
			this.setResult(RESULT_CANCELED);
			finish();
			
			return true;*/
		case R.id.menu_send:
			if(validarFormulario()){
				//Toast.makeText(this, "Enviando a '" + mEmail + "'", Toast.LENGTH_SHORT).show();

				String body = "<p><strong>Hora: </strong>"+ mTextHora.getText().toString() +"</p>";
				
				if(!mTextLetra.getText().toString().trim().equals(""))
					body = body + "<p><strong>Mesa: </strong>"+ mTextLetra.getText().toString().toUpperCase() + mTextNumero.getText().toString() +"</p>";
				
				body = body + "<p><strong>Motivo: </strong>"+ mSpinner.getSelectedItem().toString() +"</p>";
				body = body + "<p><strong>Descripción:</strong></p>" + Html.toHtml(mDesc.getText());
				
				// Rellenamos la info del correo
				Intent i = new Intent(Intent.ACTION_SEND);
				i.putExtra(Intent.EXTRA_EMAIL, new String[] {mEmail});
				i.putExtra(Intent.EXTRA_SUBJECT, "Denuncia EE20");
				i.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(body));
				
				if(mFile != null)
					i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(mFile));
				else if(!mCurrentPhotoPath.equals("")){
					File f = new File(mCurrentPhotoPath);
					i.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(f));
				}
				
				i.setType("text/html");
				
				//Evitamos que se abra la ventana de selección de applicación de correo y abrimos directamente gmail
				final PackageManager pm = getPackageManager();
				final List<ResolveInfo> matches = pm.queryIntentActivities(i, 0);
				ResolveInfo best = null;
				for (final ResolveInfo info : matches){
					//Toast.makeText(getApplicationContext(), info.activityInfo.packageName + "\n" + info.activityInfo.name.toLowerCase(), Toast.LENGTH_SHORT).show();
				
					if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
						best = info;
				}
				
				if (best != null)
					i.setClassName(best.activityInfo.packageName, best.activityInfo.name);

				startActivity(i);
			}
			
			return true;
		case R.id.menu_add:
			mostrarDialogCamaraGaleria();
		    
			return true;
		case R.id.menu_settings:
			Intent intent1 = new Intent(this, SettingsActivity.class);
			startActivityForResult(intent1, 4);
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	private boolean validarFormulario(){
		if(mSpinner.getSelectedItemPosition() == 0){
			Toast.makeText(getApplicationContext(), this.getString(R.string.error_tipo), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(mDesc.getText().toString().trim().length() == 0){
			Toast.makeText(getApplicationContext(), this.getString(R.string.error_desc), Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	private void mostrarDialogCamaraGaleria(){
		final CharSequence[] items = { "Cámara", "Galería" };
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		AlertDialog alertDialog;

		builder.setTitle("Origen de la foto");
		
		builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (items[item] == "Cámara") {
					try {
						mFile = createImageFile();
						
						Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mFile));
						
						startActivityForResult(takePictureIntent, 0);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					dialog.dismiss();

				}

				else if (items[item] == "Galería") {
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					
					startActivityForResult(Intent.createChooser(intent,	"Select Picture"), 1);
					dialog.dismiss();

				}

			}

		});

		alertDialog = builder.create();
		alertDialog.show();
	}
	
	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("ddMMyyyy").format(new Date());
		String imageFileName = "EE_" + timeStamp;
		
		File image = File.createTempFile(
				imageFileName, 
				".jpg", 
				Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
		
		//Toast.makeText(this, image.getAbsolutePath(), Toast.LENGTH_SHORT).show();
		
		//mCurrentPhotoPath = image.getAbsolutePath();
		return image;
	}
	
	private void setPic(String imagePath) {
	    // Get the dimensions of the View
		//Toast.makeText(this, "Width: " + mDesc.getWidth(), Toast.LENGTH_SHORT).show();
		
		Bitmap bOriginal = BitmapFactory.decodeFile(imagePath);

		Display display = getWindowManager().getDefaultDisplay(); 
		int width = display.getWidth();
		Resources r = getResources();
		//float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, r.getDisplayMetrics());
		
		int targetW = width - ((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 32, r.getDisplayMetrics()));
		
		float scaleFactor;

		scaleFactor = (float) bOriginal.getHeight() / (float) bOriginal.getWidth();//targetW/bOriginal.getWidth();
		
		//Log.d("setPic", "Escala: " + Float.toString(scaleFactor));
		//Log.d("setPic", "Original: " + bOriginal.getWidth() + "x" + bOriginal.getHeight() + "\nEscalado: " + targetW + "x" + (targetW*scaleFactor));

		Bitmap bitmap = Bitmap.createScaledBitmap(bOriginal, targetW, (int) (targetW*scaleFactor), true);
		mImageView.setImageBitmap(bitmap);
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode != Activity.RESULT_OK) {
			if(requestCode == 0){
				//Borramos el fichero temporal
				mFile.delete();
			} else if(requestCode == 2){
				
			} else if(requestCode == 4){
				//Respuesta desde las opciones, recargamos la dirección de email
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
				mEmail = preferences.getString("preference_email", "marcan@euskalencounter.org");
				mThumbnail = preferences.getBoolean("preference_thumbnail", false);
			}
			
			//mCurrentPhotoPath = "";
			
			return;
		}

		if(requestCode == 0){
			//Imagen con la cámara de fotos
			
			//Mostramos un thumbnail
			if(mThumbnail){
				setPic(mFile.getAbsolutePath());
				mViewSeparadorAttachment.setVisibility(View.VISIBLE);
				mImageView.setVisibility(View.VISIBLE);
			}
			
			//Mostramos el nombre del fichero
			setAttachmentNameText(mFile.getName(), mFile.length());
			mViewAttachment.setVisibility(View.VISIBLE);
		} else if(requestCode == 1) {
			//Logramos el path de la imagen seleccionada en la galería

			mCurrentPhotoPath = getRealPathFromURI(data.getData());
			
			//Mostramos el nombre del fichero
			File f = new File(mCurrentPhotoPath);
			setAttachmentNameText(f.getName(), f.length());
			mViewAttachment.setVisibility(View.VISIBLE);
			
			//Mostramos un thumbnail
			if(mThumbnail){
				setPic(mCurrentPhotoPath);
				mViewSeparadorAttachment.setVisibility(View.VISIBLE);
				mImageView.setVisibility(View.VISIBLE);
			}
		} else if(requestCode == 4){
			//Respuesta desde las opciones, recargamos la dirección de email
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			mEmail = preferences.getString("preference_email", "marcan@euskalencounter.org");
			mThumbnail = preferences.getBoolean("preference_thumbnail", false);
		}
    }
	
	private void setAttachmentNameText(String fileName, long fileSize){
		TextView linea1 = (TextView) this.findViewById(R.id.textAttach1);
		TextView linea2 = (TextView) this.findViewById(R.id.textAttach2);
		
		linea1.setText(fileName);
		linea2.setText(Long.toString(fileSize/1024) + " Kb");
	}
	
	public static boolean isIntentAvailable(Context context, String action) {
	    final PackageManager packageManager = context.getPackageManager();
	    final Intent intent = new Intent(action);
	    List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
	    return list.size() > 0;
	}
	
	@Override
	public void onPause(){
		super.onPause();
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}

	@Override
    protected void onResume(){
    	super.onResume();
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}
