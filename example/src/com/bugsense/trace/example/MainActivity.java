package com.bugsense.trace.example;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.bugsense.trace.BugSenseHandler;

public class MainActivity extends Activity {

	private static final int DIALOG_SUBMITTING_EXCEPTIONS = 1;
	private static final String BUGSENSE_KEY = "4eb2a615";

	private Dialog mExceptionSubmitDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		BugSenseHandler.setup(this, BUGSENSE_KEY);
		/*
        ExceptionHandler.setMinDelay(4000);
        ExceptionHandler.setHttpTimeout(10000);
        ExceptionHandler.setup(this, new ExceptionHandler.Processor() {
        	@Override
			public boolean beginSubmit() {
				showDialog(DIALOG_SUBMITTING_EXCEPTIONS);
				return true;
			}

			@Override
			public void submitDone() {
				mExceptionSubmitDialog.cancel();
			}

			@Override
			public void handlerInstalled() {}
		});
		*/
    	findViewById(R.id.crash_button).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int ar = 1/0;
				throw new RuntimeException("exception requested by user");
			}
		});
    }

    @Override
	protected void onDestroy() {
		//ExceptionHandler.notifyContextGone();
		super.onDestroy();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		if (id == DIALOG_SUBMITTING_EXCEPTIONS) {
			mExceptionSubmitDialog = new AlertDialog.Builder(this)
				.setTitle("Please wait...")
				.setMessage("The application has previously crashed "+
						"unexpectedly. Please wait while I am submitting "+
						"some information that will help us fix the problem.")
				.setCancelable(false).create();
			return mExceptionSubmitDialog;
		}
		else
			return super.onCreateDialog(id);
	}
}