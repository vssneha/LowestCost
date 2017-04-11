package com.jfpdazey.pathoflowestcost;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CalculatePathOfLowestCostFragment extends Fragment {

    public Grid validGrid;
    public CalculatePathOfLowestCostFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_lowest_cost_custom_main, container, false);

        Button goButton = (Button) fragmentView.findViewById(R.id.go_button);
        goButton.setOnClickListener(new GoOnClickListener());

        EditText customGridContents = (EditText) fragmentView.findViewById(R.id.custom_grid_contents);
        customGridContents.addTextChangedListener(new GridContentsWatcher());

        setHasOptionsMenu(false);
        return fragmentView;
    }

    private String formatPath(PathState path) {
        StringBuilder builder = new StringBuilder();
        List<Integer> rows = path.getRowsTraversed();

        for (int i = 0; i < rows.size(); i++) {
            builder.append(rows.get(i));
            if (i < rows.size() - 1) {
                builder.append("\t");
            }
        }

        return builder.toString();
    }

    private boolean gridContentsAreValid(int[][] contents) {
        if (contents.length < 1 || contents.length > 10 || contents[0].length < 1 || contents[0].length > 100) {
            return false;
        } else {
            return true;
        }
    }

    private void loadGrid(int[][] contents) {
        validGrid = new Grid(contents);
        GridVisitor visitor = new GridVisitor(validGrid);
        PathState bestPath = visitor.getBestPathForGrid();

        if (bestPath.isSuccessful()) {
            ((TextView) getView().findViewById(R.id.results_success)).setText("Yes");
        } else {
            ((TextView) getView().findViewById(R.id.results_success)).setText("No");
        }
        ((TextView) getView().findViewById(R.id.results_total_cost)).setText(Integer.toString(bestPath.getTotalCost()));
        ((TextView) getView().findViewById(R.id.results_path_taken)).setText(formatPath(bestPath));
        ((TextView) getView().findViewById(R.id.grid_contents)).setText(validGrid.asDelimitedString("\t"));
    }

    class GridContentsWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            Button goButton = (Button) getView().findViewById(R.id.go_button);
            goButton.setEnabled(!s.toString().trim().isEmpty());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    class GoOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

            String gridString = ((EditText) getView().findViewById(R.id.custom_grid_contents)).getText().toString();
            int[][] potentialGridContents = GridUtils.gridArrayFromString(gridString);
            if (gridContentsAreValid(potentialGridContents)) {
                //loadGrid(potentialGridContents);
               new MyAsyncTask(getActivity()).execute(potentialGridContents);
            } else {
                new AlertDialog.Builder(getContext())
                        .setTitle("Invalid Grid")
                        .setMessage(R.string.invalid_grid_message)
                        .setPositiveButton("OK", null)
                        .show();
            }
        }
    }

    class MyAsyncTask extends AsyncTask<int[][], String, PathState> {
        Activity mContex;
        private ProgressDialog dialog = new ProgressDialog(getActivity());

        public MyAsyncTask(Activity contex) {
            this.mContex = contex;
            this.dialog.setMessage("Calculating..");
            this.dialog.show();
        }

        protected PathState doInBackground(int[][]... params) {
            validGrid = new Grid(params[0]);
            GridVisitor visitor = new GridVisitor(validGrid);
            PathState bestPath = visitor.getBestPathForGrid();
            return bestPath;
        }

        @Override
        protected void onPostExecute(PathState bestPath) {
            {
                dialog.dismiss();
                Toast.makeText(mContex,"Calculation Complete",Toast.LENGTH_SHORT).show();
                if (bestPath.isSuccessful()) {
                    ((TextView) getView().findViewById(R.id.results_success)).setText("Yes");
                } else {
                    ((TextView) getView().findViewById(R.id.results_success)).setText("No");
                }
                ((TextView) getView().findViewById(R.id.results_total_cost)).setText(Integer.toString(bestPath.getTotalCost()));
                ((TextView) getView().findViewById(R.id.results_path_taken)).setText(formatPath(bestPath));
                ((TextView) getView().findViewById(R.id.grid_contents)).setText(validGrid.asDelimitedString("\t"));
            }
        }
    }
}
