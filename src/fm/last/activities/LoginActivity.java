package fm.last.activities;

import com.google.gwt.user.client.rpc.AsyncCallback;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import androidx.util.DialogUtil;
import androidx.util.FinishLaterTask;
import androidx.util.GUITaskQueue;
import androidx.util.ProgressIndicator;
import androidx.util.ResultReceiver;

import fm.last.R;
import fm.last.Log;
import fm.last.LastFmApplication;
import fm.last.LastfmRadio;
import fm.last.api.MD5;
import fm.last.api.Session;

public class LoginActivity extends Activity implements
 AsyncCallback<Session>, ProgressIndicator {
	private EditText userField, passwordField;
	ProgressDialog progressDialog = null;
	private String md5Password;
	private String username;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.account_settings);

		final Button loginButton = (Button) findViewById(R.id.login_button);
		userField = (EditText) findViewById(R.id.login_username);
		passwordField = (EditText) findViewById(R.id.login_password);

		loginButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				doLogin();
			}
		});
	}

	public void showProgressIndicator() {
		String title = getResources().getString(R.string.authProgressTitle);
		String message = getResources().getString(R.string.authProgressMessage);
		progressDialog = ProgressDialog.show(this, title, message, true);
	}
	
	public void hideProgressIndicator() {
		progressDialog.dismiss();
	}
	
	private void doLogin() {
		username = userField.getText().toString();
		md5Password = MD5.getInstance().hash(passwordField.getText().toString());
		LastfmRadio.getInstance().obtainSession(this, username, md5Password, this);
	}

	public void onFailure(Throwable t) {
		// show an alert dialog for 2 seconds
		DialogUtil.showAlertDialog(this, R.string.badAuthTitle, R.string.badAuth, R.drawable.icon, 2000);
		// call finish on this activity after the alert dialog is dismissed
		GUITaskQueue.getInstance().addTask(new FinishLaterTask(this, RESULT_CANCELED, 0));
	}

	public void onSuccess(Session session) {
		Log.i("LoginActivity: We've got a session! session.key=" + session.getKey());
		
		// Save our credentials to our SharedPreferences
		LastFmApplication.instance().saveCredentials(username, md5Password);
		setResult(RESULT_OK);
		finish();
	}

}
