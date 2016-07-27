package com.example.gui;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

public class PromptDialog {
	
	public static void showPromptDialog(Context context, String prompt){
		
		Builder builder = new Builder(context);
		builder.setTitle("Promption");
		builder.setMessage(prompt);
		
		builder.setPositiveButton("Confirm", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		builder.create();		
		builder.show();
	}

}
