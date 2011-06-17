package sw805a.cardgame.ui;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import sw805a.cardgame.R;


public class ClientAdapter extends ArrayAdapter<LobbyView.ClientListItem> {
    private Context ctx;
    private int layout;
    
    
    public ClientAdapter(Context context, int textViewResourceId, ArrayList<LobbyView.ClientListItem> items) {
            super(context, textViewResourceId, items);
            ctx = context;
            layout = textViewResourceId;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
            	LayoutInflater vi = (LayoutInflater)ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(layout, null);
            }
            LobbyView.ClientListItem item = getItem(position);
            
           	TextView tv = (TextView)v.findViewById(R.id.ClientName);
           	tv.setText(item.Client.getName());
           	CheckBoxListItem cbv = (CheckBoxListItem)v.findViewById(R.id.ClientChecked);
           	cbv.setChecked(getItem(position).Checked);
           	
           	
            return v;
    }	
    
    
    

}
