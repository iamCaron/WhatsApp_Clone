package com.littleit.whatsappclone.view.activities.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.littleit.whatsappclone.managers.ChatService;

public class DeleteConfirmationDailog extends AppCompatDialogFragment {
    ChatService chatService;

    public DeleteConfirmationDailog(ChatService chatService) {
         this.chatService=chatService;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        setRetainInstance(true);


       return new AlertDialog.Builder(getContext())
                .setTitle("Confirm Delete Action")
                .setMessage("Do you really want to delete selected messages?")
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton("DELETE", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        chatService.deleteMessages();
                    }
                })

                .setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }
                } ).show();

    }
}
