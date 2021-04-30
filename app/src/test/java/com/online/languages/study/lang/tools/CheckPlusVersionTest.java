package com.online.languages.study.lang.tools;

import android.content.Context;
import android.os.Build;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

@Config(sdk = {Build.VERSION_CODES.O_MR1})
@RunWith(RobolectricTestRunner.class)
public class CheckPlusVersionTest {

    CheckPlusVersion SUT;
    private Context mockContext;


    @Before
    public void setUp() throws Exception {
        mockContext = RuntimeEnvironment.application.getApplicationContext();
        SUT = new CheckPlusVersion(mockContext);
    }

    @Test
    public void checkPlusVersion_isPlus_returnTrue() {
        SUT.setPlusVersion(true);
        boolean result = SUT.isPlusVersion();
        Assert.assertThat(result, is(true));

    }

    @Test
    public void checkPlusVersion_isNotPlus_returnFalse() {
        SUT.setPlusVersion(false);
        boolean result = SUT.isPlusVersion();
        Assert.assertThat(result, is(false));

    }

}