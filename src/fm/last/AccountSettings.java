package fm.last;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.os.Handler;
import androidx.util.GUITaskQueue;
import androidx.util.ResultReceiver;


import fm.last.api.MD5;
import fm.last.api.Session;
import fm.last.tasks.AuthenticationTask;


public class AccountSettings extends Activity implements ResultReceiver<Session>
{
	private EditText userField, passwordField;
//	private final Handler m_handler = new Handler();
	ProgressDialog m_progress = null;
	private String md5Password;
	private String username;

	public void onCreate(Bundle icicle)
	{
		super.onCreate( icicle );
		setContentView( R.layout.account_settings );

		final Button loginButton = (Button) findViewById( R.id.login_button );
		userField = (EditText) findViewById(R.id.login_username);
		passwordField = (EditText) findViewById(R.id.login_password);

		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
			  doLogin();
			}
		});
	}

	private void doLogin() {
      m_progress = ProgressDialog.show(AccountSettings.this,
          getResources().getString(R.string.authProgressTitle),
          getResources().getString(R.string.authProgressMessage),
          true);
      username = userField.getText().toString();
      md5Password = MD5.getInstance().hash(passwordField.getText().toString());
      GUITaskQueue.getInstance().addTask(
      new AuthenticationTask(username, md5Password, this));
	}
	

  public void handle_exception(Throwable t) {
    m_progress.dismiss();  	
    Dialog alert = (new AlertDialog.Builder( AccountSettings.this )
    .setTitle( R.string.badAuthTitle )
    .setIcon( R.drawable.icon )
    .setMessage( R.string.badAuth )
    ).create();
    alert.show();
  }
  
  private SharedPreferences getPrivatePreferences() {
  	return getSharedPreferences("Last.fm", Context.MODE_PRIVATE);
  }

  public void resultObtained(Session session) {
    m_progress.dismiss();  	
  	Log.i("We've got a session! session.key=" + session.getKey());
      SharedPreferences prefs = getPrivatePreferences();
      SharedPreferences.Editor prefEdit = prefs.edit();
      prefEdit.putString("username", username);
      prefEdit.putString("md5Password", md5Password);
      prefEdit.commit();
      this.finish();
  }
}
