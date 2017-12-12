package javahelps.com.test3database;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by admin on 11/3/2017.
 */

public class PersonListAdapter extends ArrayAdapter<HouseDetails> {

    private static final String TAG = "personListAdapter";

    private Context mContext;
    int mResource;
    private int lastposition = -1;

    static class ViewHolder {
        TextView address;
        TextView county;
        TextView state;
        TextView zipcode;
        TextView price;
    }

    public PersonListAdapter(Context context, int resource, ArrayList<HouseDetails> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //get the persons information
        String address = getItem(position).getAddress();
        String county = getItem(position).getCounty();
        String state = getItem(position).getState();
        String zipcode = getItem(position).getZipcode();
        String price = getItem(position).getPrice();

        //Create a person object with the information
        HouseDetails housedet = new HouseDetails(address,county,state,zipcode,price);

        final View result;
        ViewHolder holder = new ViewHolder();

        if (convertView == null) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource,parent,false);

            holder.address = (TextView) convertView.findViewById(R.id.textView1);
            holder.county = (TextView) convertView.findViewById(R.id.textView2);
            holder.state = (TextView) convertView.findViewById(R.id.textView3);
            holder.zipcode = (TextView) convertView.findViewById(R.id.textView4);
            holder.price = (TextView) convertView.findViewById(R.id.textView5);

            result = convertView;
            convertView.setTag(holder);
        }

        else {
            holder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext,(position > lastposition) ? R.anim.load_down_anim : R.anim.load_up_anim);
        result.startAnimation(animation);
        lastposition = position;


        holder.address.setText(housedet.getAddress());
        holder.county.setText(housedet.getCounty());
        holder.state.setText(housedet.getState());
        holder.zipcode.setText(housedet.getZipcode());
        holder.price.setText(housedet.getPrice());

        return convertView;

    }
}
