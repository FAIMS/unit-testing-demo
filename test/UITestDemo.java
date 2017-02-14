package au.org.intersect.faims.android.test;

import au.org.intersect.faims.android.ui.activity.SplashActivity;
import com.robotium.solo.*;

import android.test.ActivityInstrumentationTestCase2;

import au.org.intersect.faims.android.util.AppModuleUtil;
import au.org.intersect.faims.android.util.ModuleHelper;

/**
 * Created by christian on 9/02/17.
 */
public class UnitTestDemo extends ActivityInstrumentationTestCase2<SplashActivity> {
    private Solo solo;
    public static final String MODULE_NAME = "unit-testing-demo";

    public UnitTestDemo() {
        super(SplashActivity.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        solo = new Solo(getInstrumentation(), getActivity());
    }

    /*
        Test: Start app, set server and module, and create 1000 child entities
        Requires: FAIMS server
     */
    public void testRun1() {
        AppModuleUtil.roboConnectToServer(solo, AppModuleUtil.SERVER_NAME_DEV, AppModuleUtil.SERVER_PORT_80);
        AppModuleUtil.roboLoadModule(solo, MODULE_NAME);

        // Select username
        android.view.View loggerId = solo.getView((Object) "Login/Login/Logger_ID");
        while(!solo.waitForText(AppModuleUtil.USER_FAIMS_ADMIN)) {
            solo.clickOnView(loggerId);
        }
        solo.clickOnText(AppModuleUtil.USER_FAIMS_ADMIN);

        // Create Ent
        ModuleHelper.clickButton(solo, "Create Ent");
        assertTrue(
                "Ent not created",
                solo.searchText("Ent ID", true) /* Lets us know that Ent/Ent/Ent_ID is visible */
        );

        // Trigger autosave
        android.widget.EditText entId = (android.widget.EditText) solo.getView((Object) "Ent/Ent/Ent_ID");
        solo.enterText(entId, "dummy");

        // Navigate to "Children" tab
        solo.clickOnText("Children");

        // Create Child entity
        for (int i = 1; i < 1000; i++) {
            // Create Child Ent
            String makeChildEnt = "Make Child Ent";
            while(solo.searchText(makeChildEnt))
                solo.clickOnText(makeChildEnt);

            // Get ID of child Ent
            android.widget.EditText childEntId = (android.widget.EditText) solo.getView((Object) "Child_Ent/Child_Ent/Child_Ent_ID");
            String childEntIdVal = childEntId.getText().toString();

            // Return to parent tab group and check if the child ent is displayed now
            solo.goBack();
            solo.clickOnText("Children");
            if (!solo.waitForText(childEntIdVal, 1, Timeout.getLargeTimeout(), false)) {
                solo.takeScreenshot();
                fail("Child Entity " + childEntIdVal + " not displayed");
            }
        }
    }

    /*
        Test: Start app and load named module
        Requires: Server previously set and module "CSIRO Geochemistry Sampling" loaded on server
     */
    public void testRun2() {
        AppModuleUtil.roboUseCurrentServer(solo);
        AppModuleUtil.roboLoadModule(solo, MODULE_NAME);
    }

    /*
        Test: Start app and load most recent module
        Requires: Existing server and module "CSIRO Geochemistry Sampling" previously selected
     */
    public void testRun3() {
        AppModuleUtil.roboContinueLastSession(solo);
    }

    @Override
    public void tearDown() throws Exception {
        solo.finishOpenedActivities();
        super.tearDown();
    }
}
