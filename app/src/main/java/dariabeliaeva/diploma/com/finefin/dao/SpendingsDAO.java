package dariabeliaeva.diploma.com.finefin.dao;

import dariabeliaeva.diploma.com.finefin.MyApplication;
import dariabeliaeva.diploma.com.finefin.data_models.Categories;
import dariabeliaeva.diploma.com.finefin.data_models.Spendings;
import dariabeliaeva.diploma.com.finefin.data_models.User;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.internal.Context;

import java.lang.Object;
import java.util.Date;


/**
 * Created by Dari on 3/20/2016.
 */
public class SpendingsDAO {

    private Realm pRealm;


    //pRealm = Realm.getInstance(this);

    public void addSpendings(String description, int price, Date date, String catName, Categories cat)
    {
        Spendings spen = new Spendings();
        pRealm = Realm.getDefaultInstance();
        spen.setId(System.currentTimeMillis());
        spen.setName(description);
        spen.setPrice(price);
        spen.setDate(date);
        spen.setCategory(catName);

        pRealm.beginTransaction();
        cat.getSpendings().add(spen);
        pRealm.copyToRealmOrUpdate(spen);
        pRealm.copyToRealmOrUpdate(cat);
        pRealm.commitTransaction();

    }


    public Long sumByCat (String catName) {
        long sum = pRealm.where(Spendings.class).equalTo("category", catName).sum("price").longValue();
        return sum;
    }


    //all the crud and more
    /*
    Realm realm = Realm.getInstance(
            new RealmConfiguration.Builder(context)
                    .name("myOtherRealm.realm").build());

    public void AddSpending(int id, String name, double price, Date date, Categories category) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Spendings spen = realm.createObject(Spendings.class);
                spen.setId(id);
                spen.
            }

        });
    }*/

}