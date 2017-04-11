package com.jfpdazey.pathoflowestcost;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowAlertDialog;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.robolectric.shadows.support.v4.SupportFragmentTestUtil.startFragment;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "src/main/AndroidManifest.xml", packageName = "com.jfpdazey.pathoflowestcost")
public class CalculatePathofLowestCostRefactorTest {

    private CalculatePathOfLowestCostFragment fragment;

    @Before
    public void setUp() {
        fragment = new CalculatePathOfLowestCostFragment();
        startFragment(fragment);
        assertNotNull(fragment);
    }

    @Test
    public void goButtonIsDisabledByDefault() {
        Button goButton = (Button) getViewFromFragment(R.id.go_button);
        assertThat(goButton.isEnabled(), is(false));
    }

    @Test
    public void enteringAnyTextIntoTheCustomGridContentsEnablesTheGoButton() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);
        Button goButton = (Button) getViewFromFragment(R.id.go_button);

        customGridContents.setText("a");

        assertThat(goButton.isEnabled(), is(true));
    }

    @Test
    public void removingAllTextFromTheCustomGridContentsDisablesTheGoButton() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);
        Button goButton = (Button) getViewFromFragment(R.id.go_button);
        customGridContents.setText("a");

        customGridContents.setText("");

        assertThat(goButton.isEnabled(), is(false));
    }

    @Test
    public void clickingGoWithLessThanFiveColumnsOfDataDisplaysErrorMessage() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);

        customGridContents.setText("1 2 3 4");
        getViewFromFragment(R.id.go_button).performClick();

        ShadowAlertDialog alertDialog = Shadows.shadowOf(ShadowAlertDialog.getLatestAlertDialog());
        assertThat(alertDialog.getTitle().toString(), equalTo("Invalid Grid"));
        assertThat(alertDialog.getMessage().toString(), equalTo(fragment.getResources().getString(R.string.invalid_grid_message)));
    }

    @Test
    public void clickingGoWithMoreThanOneHundredColumnsOfDataDisplaysErrorMessage() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);
        StringBuilder inputBuilder = new StringBuilder();
        for (int i = 1; i <= 101; i++) {
            inputBuilder.append(i).append(" ");
        }

        customGridContents.setText(inputBuilder.toString());
        getViewFromFragment(R.id.go_button).performClick();

        ShadowAlertDialog alertDialog = Shadows.shadowOf(ShadowAlertDialog.getLatestAlertDialog());
        assertThat(alertDialog.getTitle().toString(), equalTo("Invalid Grid"));
        assertThat(alertDialog.getMessage().toString(), equalTo(fragment.getResources().getString(R.string.invalid_grid_message)));
    }

    @Test
    public void clickingGoWithNonNumericDataDisplaysErrorMessage() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);

        customGridContents.setText("1 2 3 4 b");
        getViewFromFragment(R.id.go_button).performClick();

        ShadowAlertDialog alertDialog = Shadows.shadowOf(ShadowAlertDialog.getLatestAlertDialog());
        assertThat(alertDialog.getTitle().toString(), equalTo("Invalid Grid"));
        assertThat(alertDialog.getMessage().toString(), equalTo(fragment.getResources().getString(R.string.invalid_grid_message)));
    }

    @Test
    public void clickingGoWithValidDataLoadsTheGrid() {
        String expectedContents = GridUtils.EXAMPLE_GRID_1.asDelimitedString("\t");
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);

        customGridContents.setText(expectedContents);
        getViewFromFragment(R.id.go_button).performClick();

        String gridContents = ((TextView) getViewFromFragment(R.id.grid_contents)).getText().toString();
        assertThat(gridContents, equalTo(expectedContents));
    }

    @Test
    public void clickingGoWithValidDataDisplaysYesIfPathSuccessful() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);

        customGridContents.setText("1 2 3 4 5");
        getViewFromFragment(R.id.go_button).performClick();

        TextView resultsView = (TextView) getViewFromFragment(R.id.results_success);
        assertThat(resultsView.getText().toString(), equalTo("Yes"));
    }

    @Test
    public void clickingGoAfterClickingAGridButtonDisplaysNoIfPathNotSuccessful() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);

        customGridContents.setText("50 2 3 4 5");
        getViewFromFragment(R.id.go_button).performClick();

        TextView resultsView = (TextView) getViewFromFragment(R.id.results_success);
        assertThat(resultsView.getText().toString(), equalTo("No"));
    }

    @Test
    public void clickingGoAfterClickingAGridButtonDisplaysTotalCostOfPathOnSecondLineOfResults() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);

        customGridContents.setText("1 2 3 4 5");
        getViewFromFragment(R.id.go_button).performClick();

        TextView resultsView = (TextView) getViewFromFragment(R.id.results_total_cost);
        assertThat(resultsView.getText().toString(), equalTo("15"));
    }

    @Test
    public void clickingGoAfterClickingAGridButtonDisplaysPathTakenOnThirdLineOfResults() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);

        customGridContents.setText("1 2 3 4 5 6\n2 1 2 2 2 2\n3 3 1 3 3 3\n4 4 4 1 1 4\n5 5 5 5 5 1\n6 6 6 6 6 6");
        getViewFromFragment(R.id.go_button).performClick();

        TextView resultsView = (TextView) getViewFromFragment(R.id.results_path_taken);
        assertThat(resultsView.getText().toString(), equalTo("1\t2\t3\t4\t4\t5"));
    }

    //Sample 1
    @Test
    public void calculateCost6x5MatrixNormalFlow() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);

        customGridContents.setText("3 4 1 2 8 6\n6 1 8 2 7 4\n5 9 3 9 9 5\n8 4 1 3 2 6\n3 7 2 8 6 4");
        getViewFromFragment(R.id.go_button).performClick();

        TextView resultsView = (TextView) getViewFromFragment(R.id.results_total_cost);
        assertThat(resultsView.getText().toString(), equalTo("16"));
    }

    //Sample 2
    @Test
    public void calculateCost6x5MatrixNormalFlowSample2() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);

        customGridContents.setText("3 4 1 2 8 6\n6 1 8 2 7 4\n5 9 3 9 9 5\n8 4 1 3 2 6\n3 7 2 1 2 3");
        getViewFromFragment(R.id.go_button).performClick();

        TextView resultsView = (TextView) getViewFromFragment(R.id.results_total_cost);
        assertThat(resultsView.getText().toString(), equalTo("11"));
    }

    //Sample 3
    @Test
    public void calculateCost5X3matrixwithnopathLessThan50() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);

        customGridContents.setText("19 10 19 10 19\n21 23 20 19 12\n20 12 20 11 10");
        getViewFromFragment(R.id.go_button).performClick();

        TextView resultsView = (TextView) getViewFromFragment(R.id.results_total_cost);
        assertThat(resultsView.getText().toString(), equalTo("48"));
    }

    //Sample 4
    @Test
    public void calculateCost1x4Matrix() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);

        customGridContents.setText("5 8 5 3 5");
        getViewFromFragment(R.id.go_button).performClick();

        TextView resultsView = (TextView) getViewFromFragment(R.id.results_total_cost);
        assertThat(resultsView.getText().toString(), equalTo("26"));
    }

    //Sample 5
    @Test
    public void calculateCost5x1Matrix() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);

        customGridContents.setText("5\n8\n5\n3\n5");
        getViewFromFragment(R.id.go_button).performClick();

        TextView resultsView = (TextView) getViewFromFragment(R.id.results_total_cost);
        assertThat(resultsView.getText().toString(), equalTo("3"));
    }

    //Sample 6
    @Test
    public void calculateCostNonNumericInput() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);

        customGridContents.setText("5 4 H\n5 4 H\n5 7 5");
        getViewFromFragment(R.id.go_button).performClick();

        ShadowAlertDialog alertDialog = Shadows.shadowOf(ShadowAlertDialog.getLatestAlertDialog());
        assertThat(alertDialog.getTitle().toString(), equalTo("Invalid Grid"));
        assertThat(alertDialog.getMessage().toString(), equalTo(fragment.getResources().getString(R.string.invalid_grid_message)));
    }

    //Sample 7
    @Test
    public void calculateCostNoInput () {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);

        customGridContents.setText("");
        getViewFromFragment(R.id.go_button).performClick();

        ShadowAlertDialog alertDialog = Shadows.shadowOf(ShadowAlertDialog.getLatestAlertDialog());
        assertThat(alertDialog.getTitle().toString(), equalTo("Invalid Grid"));
        assertThat(alertDialog.getMessage().toString(), equalTo(fragment.getResources().getString(R.string.invalid_grid_message)));
    }

    //Sample 8
    @Test
    public void calculateCostStartingWithGreaterThan50() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);

        customGridContents.setText("69 10 19 10 19\n51 23 20 19 12\n60 12 20 11 10");
        getViewFromFragment(R.id.go_button).performClick();

        TextView resultsView = (TextView) getViewFromFragment(R.id.results_total_cost);
        assertThat(resultsView.getText().toString(), equalTo("0"));
    }

    //Sample 9
    @Test
    public void calculateCostOneValueGreaterThan50() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);

        customGridContents.setText("60 3 3 6\n6 3 7 9\n5 6 8 3");
        getViewFromFragment(R.id.go_button).performClick();

        TextView resultsView = (TextView) getViewFromFragment(R.id.results_total_cost);
        assertThat(resultsView.getText().toString(), equalTo("14"));
    }

    //Sample 10
    @Test
    public void calculateCostNegativeValues() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);

        customGridContents.setText("6 3 -5 9\n-5 2 4 10\n3 -2 6 10\n6 -1 -2 10");
        getViewFromFragment(R.id.go_button).performClick();

        TextView resultsView = (TextView) getViewFromFragment(R.id.results_total_cost);
        assertThat(resultsView.getText().toString(), equalTo("0"));
    }

    //Sample 11
    @Test
    public void calculateCostCompletePathVsLowerCostIncompletePath() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);

        customGridContents.setText("51 51\n0 51\n51 51\n5 5");
        getViewFromFragment(R.id.go_button).performClick();

        TextView resultsView = (TextView) getViewFromFragment(R.id.results_total_cost);
        assertThat(resultsView.getText().toString(), equalTo("10"));
    }

    //Sample 12
    @Test
    public void calculateCostLongerIncompletePathVsShorterLowerCostIncompletePath() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);

        customGridContents.setText("51 51 51\n0 51 51\n51 51 51\n5 5 51");
        getViewFromFragment(R.id.go_button).performClick();

        TextView resultsView = (TextView) getViewFromFragment(R.id.results_total_cost);
        assertThat(resultsView.getText().toString(), equalTo("10"));
    }

    //Sample 13
    @Test
    public void calculateCostLargeNumberOfColumns() {
        EditText customGridContents = (EditText) getViewFromFragment(R.id.custom_grid_contents);

        customGridContents.setText("1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1 1\n2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2 2\n");
        getViewFromFragment(R.id.go_button).performClick();

        TextView resultsView = (TextView) getViewFromFragment(R.id.results_total_cost);
        assertThat(resultsView.getText().toString(), equalTo("20"));
    }

    private View getViewFromFragment(int id) {
        return fragment.getView().findViewById(id);
    }
}