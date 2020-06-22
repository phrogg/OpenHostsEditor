package me.vittorio_io.openhostseditor.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;

import me.vittorio_io.openhostseditor.R;
import me.vittorio_io.openhostseditor.model.HostRule;
import me.vittorio_io.openhostseditor.model.HostsManager;

public class EditRuleActivity extends BaseActivity {

    private boolean isEditing = false;
    private int ruleId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_rule);

        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);

        Bundle b = getIntent().getExtras();
        int value = -1; // or other values

        if (b != null)
            value = b.getInt("key");

        ruleId = value;
        isEditing = value != -1;

        if (isEditing) {
            HostRule rule = MainActivity.rules.get(value);

            EditText ip = (EditText) findViewById(R.id.editIp);
            EditText url = (EditText) findViewById(R.id.editUrl);

            ip.setText(rule.getIp().toString().substring(1));
            url.setText(rule.getUrl());

            setTitle(getString(R.string.action_edit_rule));
        } else {
            setTitle(getString(R.string.action_add_rule));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (isEditing)
            getMenuInflater().inflate(R.menu.menu_edit, menu);
        else
            getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        goBack();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                goBack();
                return true;
            case R.id.action_okay:
                updateRule();
                return true;
            case R.id.action_delete:
                removeRule();
                return true;

        }
        return false;
    }

    private void goBack() {
        // TODO check for unsaved changes
        //updateRule();
        finish();
    }

    private void removeRule() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.action_delete_rule)
                .setMessage(R.string.message_confirm_delete_rule)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        try {
                            if (!HostsManager.removeRule(getApplicationContext(), ruleId))
                                throw new RuntimeException();
                            haveASnack(getString(R.string.message_rule_removed));
                            finish();
                        } catch (Exception e) {
                            haveASnack(getString(R.string.message_rule_not_removed));
                        }

                    }
                })
                .setNegativeButton(android.R.string.no, null).show();

    }


    private void updateRule() {
        EditText ip = (EditText) findViewById(R.id.editIp);
        EditText url = (EditText) findViewById(R.id.editUrl);

        try {
            String toParse = ip.getText() + " " + url.getText();

            System.out.println(toParse);

            HostRule rule = HostRule.fromHostLine(toParse);

            if (isEditing) {
                HostsManager.editRule(getApplicationContext(), ruleId, rule);
            } else {
                HostsManager.addRule(getApplicationContext(), rule);
            }

            finish();
            return;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        haveASnack(getString(R.string.message_generic_error));
    }
}
