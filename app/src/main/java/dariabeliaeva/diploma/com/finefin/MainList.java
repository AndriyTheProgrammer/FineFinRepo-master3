package dariabeliaeva.diploma.com.finefin;


import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import dariabeliaeva.diploma.com.finefin.dao.CategoriesDAO;
import dariabeliaeva.diploma.com.finefin.data_models.Categories;
import dariabeliaeva.diploma.com.finefin.data_models.Spendings;
import io.realm.Realm;
import io.realm.RealmResults;


/**
 * A simple {@link Fragment} subclass.
 */
public class MainList extends Fragment {


    public MainList() {
        // Required empty public constructor
    }

    FinListAdapter finListAdapter;
    private Realm realm;
    Date dPicker = new Date();
    View rootView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        getSupportActionBar().setHideOnContentScrollEnabled(true);
        //inflater.inflate(R.layout.fragment_main_list, container, false);

        rootView = inflater.inflate(R.layout.fragment_main_list, container, false);
        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //buildAndShowInputDialog();
                Intent myIntent = new Intent(getActivity(), AddNoteActivity.class);
                startActivity(myIntent);
            }

            //Snackbar.make(view, "Add later", Snackbar.LENGTH_LONG)
            //.setAction("Action", null).show();
            //}
        });

        realm = Realm.getDefaultInstance();

        List<Spendings> spendings = new ArrayList<Spendings>();

        dPicker.getDate();

        spendings.add(new Spendings(1, "no transport ever", 10, dPicker, "Transport"));
        spendings.add(new Spendings(2, "salary is mine now", 12, dPicker, "Salary"));
        //cats.add(new Spendings(3, "soda", 9.5, "09-23-2015", ));
        //cats.add(new Spendings(4, "juice", 20, "09-23-2012", ));
        //cats.add(new Spendings(5, "vodka", 90, "12-23-2012", ));

        realm.beginTransaction();
//        for (Spendings sp : cats) {
        realm.copyToRealmOrUpdate(spendings);
//
        realm.commitTransaction();

        CategoriesDAO cat = new CategoriesDAO();
        realm.beginTransaction();
        realm.clear(Categories.class);
        realm.commitTransaction();
        cat.addCategory("Transport", "outcome");
        cat.addCategory("Food", "outcome");
        cat.addCategory("Fun", "outcome");
        cat.addCategory("Shopping", "outcome");
        cat.addCategory("Salary", "income");
        cat.addCategory("Lottery", "income");
        cat.addCategory("Gift", "income");
        // Inflate the layout for this fragment
        return rootView;

    }


    @Override
    public void onResume() {
        super.onResume();

        provideListInitialization();
    }

    private void provideListInitialization() {
        RealmResults<Spendings> spenItems = realm
                .where(Spendings.class)
                .findAll();
        ArrayList<Spendings> spenItemsArrayList = new ArrayList<>();
        spenItemsArrayList.addAll(spenItems);
        long rezult;

        finListAdapter = new FinListAdapter();
        Collections.reverse(spenItemsArrayList);
        finListAdapter.setSpendings(spenItemsArrayList);
        RecyclerView realmRecyclerView = (RecyclerView) rootView.findViewById(R.id.fin_list);
        realmRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        realmRecyclerView.setAdapter(finListAdapter);
        rezult = finListAdapter.UpdateBalance();
        TextView txtBal = (TextView) rootView.findViewById(R.id.tvBalance);
        txtBal.setText("Balance: " + rezult);


        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                realm.beginTransaction();
                finListAdapter.getSpendings().get(viewHolder.getAdapterPosition()).removeFromRealm();
                realm.commitTransaction();
                finListAdapter.removeSpending(viewHolder.getAdapterPosition());


            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(realmRecyclerView);
    }


    private void buildAndShowInputDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Create New");

        LayoutInflater li = LayoutInflater.from(getActivity());
        View dialogView = li.inflate(R.layout.to_do_dialog_view, null);
        final EditText input = (EditText) dialogView.findViewById(R.id.input);
        final EditText input1 = (EditText) dialogView.findViewById(R.id.input1);

        builder.setView(dialogView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addSpenItem(input.getText().toString(), Integer.parseInt(input1.getText().toString()));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.show();
        input.setOnEditorActionListener(
                new EditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE ||
                                (event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                            dialog.dismiss();
                            addSpenItem(input.getText().toString(), Integer.parseInt(input1.getText().toString()));
                            return true;
                        }
                        return false;
                    }
                });
    }

    private void addSpenItem(String spenItemText, int spenSum) {
        if (spenItemText == null || spenItemText.length() == 0) {

            Toast
                    .makeText(getActivity(), "Could not be empty!", Toast.LENGTH_SHORT)
                    .show();
            return;

        }

        realm.beginTransaction();
        Spendings spenItem = realm.createObject(Spendings.class);
        spenItem.setId(System.currentTimeMillis());
        spenItem.setName(spenItemText);
        spenItem.setPrice(spenSum);
        spenItem.setDate(dPicker);

        realm.commitTransaction();

        finListAdapter.addSpending(spenItem);
    }


}