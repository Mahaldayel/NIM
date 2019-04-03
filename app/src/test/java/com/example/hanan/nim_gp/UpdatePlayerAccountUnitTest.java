package com.example.hanan.nim_gp;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.hanan.nim_gp.AccountActivity.UpdateAccountActivity;
import com.example.hanan.nim_gp.Game.SelectGameLevelActivity;
import com.example.hanan.nim_gp.ManageDevices.ManageDevicesActivity;
import com.google.firebase.FirebaseApp;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class UpdatePlayerAccountUnitTest {

    @Test
    public void testEmailNotEmpty() {

        UpdateAccountActivity updateAccountActivity = new UpdateAccountActivity();

        assertEquals(false, updateAccountActivity.emailNotEmpty(""));
    }

    @Test
    public void testEmailEmpty() {

        UpdateAccountActivity updateAccountActivity = new UpdateAccountActivity();

        assertEquals(true, updateAccountActivity.emailNotEmpty("H@gmail.con"));
    }

    @Test
    public void testNameNotEmpty() {

        UpdateAccountActivity updateAccountActivity = new UpdateAccountActivity();

        assertEquals(false, updateAccountActivity.nameNotEmpty(""));
    }

    @Test
    public void testBirthDateNotEmpty() {

        UpdateAccountActivity updateAccountActivity = new UpdateAccountActivity();

        assertEquals(true, updateAccountActivity.birthDateNotEmpty("Hanan"));
    }

    @Test
public void testBirthDateEmpty() {

    UpdateAccountActivity updateAccountActivity = new UpdateAccountActivity();

    assertEquals(false, updateAccountActivity.nameNotEmpty(""));
}

    @Test
    public void testNameEmpty() {

        UpdateAccountActivity updateAccountActivity = new UpdateAccountActivity();

        assertEquals(true, updateAccountActivity.nameNotEmpty("21/4/1997"));
    }

    @Test
    public void testUpdateWithEmptyFields() {

        UpdateAccountActivity updateAccountActivity = new UpdateAccountActivity();
        String name = "";
        String email = "";
        String bDate = "";
        boolean checkUsernameAvailability = true;
        boolean forTest = true;

        assertEquals(false, updateAccountActivity.update(checkUsernameAvailability,name,email,bDate,forTest));
    }


    @Test
    public void testUpdateWithDuplicateUserName() {

        UpdateAccountActivity updateAccountActivity = new UpdateAccountActivity();
        String name = "Hanan";
        String email = "H@gmail.com";
        String bDate = "12/12/2000";
        boolean checkUsernameAvailability = true;
        boolean forTest = true;

        updateAccountActivity.setAvailable(false);
        updateAccountActivity.setAvailableIsUpdatede(true);

        assertEquals(false, updateAccountActivity.update(checkUsernameAvailability,name,email,bDate,forTest));
    }


    @Test
    public void testUpdate() {

        UpdateAccountActivity updateAccountActivity = new UpdateAccountActivity();
        String name = "Hanan";
        String email = "H@gmail.com";
        String bDate = "12/12/2000";
        boolean checkUsernameAvailability = true;
        boolean forTest = true;
        updateAccountActivity.setAvailable(true);
        updateAccountActivity.setAvailableIsUpdatede(true);

        assertEquals(true, updateAccountActivity.update(checkUsernameAvailability,name,email,bDate,forTest));
    }

}










